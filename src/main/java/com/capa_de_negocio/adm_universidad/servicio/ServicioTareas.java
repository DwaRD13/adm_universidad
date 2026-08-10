package com.capa_de_negocio.adm_universidad.servicio;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.TareaDto;
import com.capa_de_negocio.adm_universidad.entidad.EntregaTarea;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.entidad.Tarea;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.EntregaTareaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.TareaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;

/** Tareas de las secciones del estudiante y envio de sus entregas. */
@Service
public class ServicioTareas {

    private final TareaRepositorio tareaRepositorio;
    private final EntregaTareaRepositorio entregaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ServicioInscripcion servicioInscripcion;

    public ServicioTareas(TareaRepositorio tareaRepositorio,
            EntregaTareaRepositorio entregaRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            ServicioInscripcion servicioInscripcion) {
        this.tareaRepositorio = tareaRepositorio;
        this.entregaRepositorio = entregaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.servicioInscripcion = servicioInscripcion;
    }

    @Transactional(readOnly = true)
    public List<TareaDto> tareas(Integer estudianteId) {
        List<Integer> seccionIds = seccionesDelEstudiante(estudianteId);
        if (seccionIds.isEmpty()) {
            return List.of();
        }

        List<Tarea> tareas = tareaRepositorio.buscarPorSecciones(seccionIds);
        if (tareas.isEmpty()) {
            return List.of();
        }

        Map<Integer, EntregaTarea> entregas = entregasVigentes(estudianteId,
                tareas.stream().map(Tarea::getId).toList());

        return tareas.stream().map(tarea -> aDto(tarea, entregas.get(tarea.getId()))).toList();
    }

    /**
     * Registra una entrega. Se permite entregar despues de la fecha limite (queda como
     * entrega tardia y el profesor decide); lo que no se permite es entregar en una
     * seccion en la que el estudiante no esta inscrito.
     */
    @Transactional
    public TareaDto entregar(Integer estudianteId, Integer tareaId, String archivoUrl) {
        Tarea tarea = tareaRepositorio.buscarPorIdCompleta(tareaId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La tarea indicada no existe."));

        if (!seccionesDelEstudiante(estudianteId).contains(tarea.getSeccion().getId())) {
            throw new RecursoNoEncontradoExcepcion("La tarea indicada no existe.");
        }

        Usuario estudiante = usuarioRepositorio.getReferenceById(estudianteId);

        // El esquema no impide varias entregas por tarea: se reutiliza la ultima para no
        // acumular filas y para que el estudiante pueda corregir su envio.
        EntregaTarea entrega = entregasVigentes(estudianteId, List.of(tareaId)).get(tareaId);
        if (entrega == null) {
            entrega = new EntregaTarea();
            entrega.setTarea(tarea);
            entrega.setEstudiante(estudiante);
        }
        entrega.setArchivoUrl(archivoUrl);
        entrega.setFechaEnvio(LocalDateTime.now());

        return aDto(tarea, entregaRepositorio.save(entrega));
    }

    private List<Integer> seccionesDelEstudiante(Integer estudianteId) {
        return servicioInscripcion.inscripcionesVigentes(estudianteId).stream()
                .map(Inscripcion::getSeccion)
                .map(Seccion::getId)
                .toList();
    }

    /** La entrega vigente de cada tarea es la mas reciente (el repositorio ya las ordena). */
    private Map<Integer, EntregaTarea> entregasVigentes(Integer estudianteId, List<Integer> tareaIds) {
        Map<Integer, EntregaTarea> porTarea = new HashMap<>();
        for (EntregaTarea entrega : entregaRepositorio.buscarPorEstudianteYTareas(estudianteId, tareaIds)) {
            porTarea.putIfAbsent(entrega.getTarea().getId(), entrega);
        }
        return porTarea;
    }

    private static TareaDto aDto(Tarea tarea, EntregaTarea entrega) {
        boolean entregada = entrega != null;
        boolean calificada = entregada && entrega.getCalificacion() != null;
        boolean vencida = tarea.getFechaEntrega().isBefore(LocalDateTime.now());

        String estado;
        if (calificada) {
            estado = TareaDto.CALIFICADA;
        } else if (entregada) {
            estado = TareaDto.ENTREGADA;
        } else if (vencida) {
            estado = TareaDto.VENCIDA;
        } else {
            estado = TareaDto.PENDIENTE;
        }

        return new TareaDto(
                tarea.getId(),
                tarea.getSeccion().getId(),
                tarea.getSeccion().getMateria().getNombre(),
                tarea.getSeccion().getMateria().getCodigo(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getFechaEntrega(),
                tarea.getArchivoAdjuntoUrl(),
                estado,
                entregada,
                entregada ? entrega.getFechaEnvio() : null,
                entregada ? entrega.getArchivoUrl() : null,
                entregada ? entrega.getCalificacion() : null,
                entregada ? entrega.getComentariosProfesor() : null);
    }
}
