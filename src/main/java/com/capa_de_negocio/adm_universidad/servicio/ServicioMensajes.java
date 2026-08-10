package com.capa_de_negocio.adm_universidad.servicio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.ConversacionDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.MensajeDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudMensaje;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Mensaje;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.MensajeRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;

/** Mensajeria del estudiante: bandeja, hilos y envio. */
@Service
public class ServicioMensajes {

    private final MensajeRepositorio mensajeRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ServicioInscripcion servicioInscripcion;

    public ServicioMensajes(MensajeRepositorio mensajeRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            ServicioInscripcion servicioInscripcion) {
        this.mensajeRepositorio = mensajeRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.servicioInscripcion = servicioInscripcion;
    }

    /** Bandeja agrupada por interlocutor, ordenada por el mensaje mas reciente. */
    @Transactional(readOnly = true)
    public List<ConversacionDto> conversaciones(Integer usuarioId) {
        Map<Integer, ConversacionDto> porInterlocutor = new LinkedHashMap<>();

        for (Mensaje mensaje : mensajeRepositorio.buscarBandeja(usuarioId)) {
            Usuario otro = mensaje.getRemitente().getId().equals(usuarioId)
                    ? mensaje.getDestinatario()
                    : mensaje.getRemitente();

            boolean sinLeer = !mensaje.getRemitente().getId().equals(usuarioId)
                    && !Boolean.TRUE.equals(mensaje.getLeido());

            ConversacionDto actual = porInterlocutor.get(otro.getId());
            if (actual == null) {
                // El primero que aparece es el mas reciente: define el resumen de la conversacion.
                porInterlocutor.put(otro.getId(), new ConversacionDto(
                        otro.getId(),
                        otro.getNombreCompleto(),
                        otro.getRol().getNombre(),
                        mensaje.getCuerpo(),
                        mensaje.getFechaEnvio(),
                        sinLeer ? 1 : 0));
            } else if (sinLeer) {
                porInterlocutor.put(otro.getId(), new ConversacionDto(
                        actual.usuarioId(), actual.nombre(), actual.rol(),
                        actual.ultimoMensaje(), actual.fechaUltimoMensaje(),
                        actual.sinLeer() + 1));
            }
        }

        return new ArrayList<>(porInterlocutor.values());
    }

    /** Devuelve el hilo con otro usuario y marca como leidos los que le llegaron al actual. */
    @Transactional
    public List<MensajeDto> hilo(Integer usuarioId, Integer otroId) {
        if (usuarioId.equals(otroId)) {
            throw new ReglaNegocioExcepcion("No puedes abrir una conversación contigo mismo.");
        }

        mensajeRepositorio.marcarHiloComoLeido(usuarioId, otroId);

        return mensajeRepositorio.buscarHilo(usuarioId, otroId).stream()
                .map(mensaje -> aDto(mensaje, usuarioId))
                .toList();
    }

    @Transactional
    public MensajeDto enviar(Integer usuarioId, SolicitudMensaje solicitud) {
        if (usuarioId.equals(solicitud.destinatarioId())) {
            throw new ReglaNegocioExcepcion("No puedes enviarte un mensaje a ti mismo.");
        }

        Usuario destinatario = usuarioRepositorio.buscarPorIdConRol(solicitud.destinatarioId())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("El destinatario no existe."));
        Usuario remitente = usuarioRepositorio.buscarPorIdConRol(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Tu usuario ya no existe."));

        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setAsunto(solicitud.asunto());
        mensaje.setCuerpo(solicitud.cuerpo());
        mensaje.setLeido(false);
        mensaje.setFechaEnvio(java.time.LocalDateTime.now());

        return aDto(mensajeRepositorio.save(mensaje), usuarioId);
    }

    /**
     * Destinatarios validos para un estudiante: los profesores de las secciones en las
     * que esta inscrito. Evita que la pantalla de mensajes exponga el directorio completo.
     */
    @Transactional(readOnly = true)
    public List<UsuarioDto> contactos(Integer estudianteId) {
        return servicioInscripcion.inscripcionesVigentes(estudianteId).stream()
                .map(Inscripcion::getSeccion)
                .map(seccion -> seccion.getProfesor())
                .collect(java.util.stream.Collectors.toMap(Usuario::getId, u -> u, (a, b) -> a,
                        LinkedHashMap::new))
                .values().stream()
                .map(UsuarioDto::de)
                .toList();
    }

    public long sinLeer(Integer usuarioId) {
        return mensajeRepositorio.countByDestinatarioIdAndLeidoFalse(usuarioId);
    }

    private static MensajeDto aDto(Mensaje mensaje, Integer usuarioId) {
        return new MensajeDto(
                mensaje.getId(),
                mensaje.getRemitente().getId(),
                mensaje.getRemitente().getNombreCompleto(),
                mensaje.getDestinatario().getId(),
                mensaje.getDestinatario().getNombreCompleto(),
                mensaje.getAsunto(),
                mensaje.getCuerpo(),
                Boolean.TRUE.equals(mensaje.getLeido()),
                mensaje.getRemitente().getId().equals(usuarioId),
                mensaje.getFechaEnvio());
    }
}
