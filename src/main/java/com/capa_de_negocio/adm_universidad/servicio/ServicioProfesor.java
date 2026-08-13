package com.capa_de_negocio.adm_universidad.servicio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.ResumenDashboardProfesorDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.SeccionProfesorDto;
import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.repositorio.EntregaTareaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.InscripcionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.TareaRepositorio;
import com.capa_de_negocio.adm_universidad.servicio.ParseadorHorario.Horario;

/** Secciones del profesor, resumen del dashboard y directorio de sus estudiantes. */
@Service
public class ServicioProfesor {

    private final SeccionRepositorio seccionRepositorio;
    private final InscripcionRepositorio inscripcionRepositorio;
    private final TareaRepositorio tareaRepositorio;
    private final EntregaTareaRepositorio entregaRepositorio;
    private final ServicioMensajes servicioMensajes;
    private final ServicioPeriodo servicioPeriodo;

    public ServicioProfesor(SeccionRepositorio seccionRepositorio,
            InscripcionRepositorio inscripcionRepositorio,
            TareaRepositorio tareaRepositorio,
            EntregaTareaRepositorio entregaRepositorio,
            ServicioMensajes servicioMensajes,
            ServicioPeriodo servicioPeriodo) {
        this.seccionRepositorio = seccionRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.tareaRepositorio = tareaRepositorio;
        this.entregaRepositorio = entregaRepositorio;
        this.servicioMensajes = servicioMensajes;
        this.servicioPeriodo = servicioPeriodo;
    }

    @Transactional(readOnly = true)
    public List<SeccionProfesorDto> secciones(Integer profesorId) {
        String periodo = servicioPeriodo.periodoActivo();
        return seccionRepositorio.buscarPorProfesorYPeriodo(profesorId, periodo)
                .stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public ResumenDashboardProfesorDto resumen(Integer profesorId) {
        List<SeccionProfesorDto> secciones = secciones(profesorId);
        int totalEstudiantes = secciones.stream().mapToInt(SeccionProfesorDto::inscritos).sum();

        List<Integer> seccionIds = secciones.stream().map(SeccionProfesorDto::seccionId).toList();
        int pendientesPorCalificar = 0;
        if (!seccionIds.isEmpty()) {
            for (var tarea : tareaRepositorio.buscarPorSecciones(seccionIds)) {
                var entregas = entregaRepositorio.findByTareaIdOrderByFechaEnvioDesc(tarea.getId());
                var vistos = new java.util.HashSet<Integer>();
                for (var entrega : entregas) {
                    // Solo la entrega mas reciente de cada estudiante cuenta.
                    if (vistos.add(entrega.getEstudiante().getId()) && entrega.getCalificacion() == null) {
                        pendientesPorCalificar++;
                    }
                }
            }
        }

        String hoy = codigoDia(LocalDate.now().getDayOfWeek());
        List<SeccionProfesorDto> clasesHoy = secciones.stream()
                .filter(s -> s.dias().contains(hoy))
                .toList();

        return new ResumenDashboardProfesorDto(
                servicioPeriodo.periodoActivo(),
                secciones.size(),
                totalEstudiantes,
                pendientesPorCalificar,
                (int) servicioMensajes.sinLeer(profesorId),
                clasesHoy);
    }

    /** Estudiantes inscritos (vigentes) en cualquier seccion que imparte el profesor este periodo. */
    @Transactional(readOnly = true)
    public List<UsuarioDto> contactos(Integer profesorId) {
        List<Integer> seccionIds = secciones(profesorId).stream()
                .map(SeccionProfesorDto::seccionId).toList();
        if (seccionIds.isEmpty()) {
            return List.of();
        }

        return inscripcionRepositorio.buscarPorSeccionesVigentes(seccionIds, EstadoInscripcion.RETIRADO)
                .stream()
                .map(Inscripcion::getEstudiante)
                .collect(java.util.stream.Collectors.toMap(Usuario::getId, u -> u, (a, b) -> a,
                        LinkedHashMap::new))
                .values().stream()
                .map(UsuarioDto::de)
                .toList();
    }

    private SeccionProfesorDto aDto(Seccion seccion) {
        Horario horario = ParseadorHorario.analizar(seccion.getHorarioDescripcion());
        long inscritos = inscripcionRepositorio.countBySeccionIdAndEstadoNot(
                seccion.getId(), EstadoInscripcion.RETIRADO);

        return new SeccionProfesorDto(
                seccion.getId(),
                seccion.getMateria().getNombre(),
                seccion.getMateria().getCodigo(),
                seccion.getMateria().getCreditos(),
                seccion.getPeriodo(),
                seccion.getAula(),
                seccion.getHorarioDescripcion(),
                horario.dias(),
                horario.horaInicio(),
                horario.horaFin(),
                seccion.getEstado() == null ? null : seccion.getEstado().getValor(),
                seccion.getCupoMaximo(),
                (int) inscritos);
    }

    private static String codigoDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Lu";
            case TUESDAY -> "Ma";
            case WEDNESDAY -> "Mi";
            case THURSDAY -> "Ju";
            case FRIDAY -> "Vi";
            case SATURDAY -> "Sa";
            case SUNDAY -> "Do";
        };
    }
}