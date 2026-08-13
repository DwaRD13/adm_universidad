package com.capa_de_negocio.adm_universidad.servicio;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.profesor.EntregaProfesorDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.SolicitudCalificacion;
import com.capa_de_negocio.adm_universidad.dto.profesor.SolicitudTarea;
import com.capa_de_negocio.adm_universidad.dto.profesor.TareaProfesorDto;
import com.capa_de_negocio.adm_universidad.entidad.EntregaTarea;
import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.entidad.Tarea;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.EntregaTareaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.InscripcionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.TareaRepositorio;

/** Tareas creadas por el profesor en sus secciones: alta, listado y calificacion de entregas. */
@Service
public class ServicioTareasProfesor {

    private final TareaRepositorio tareaRepositorio;
    private final EntregaTareaRepositorio entregaRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final InscripcionRepositorio inscripcionRepositorio;
    private final ServicioPeriodo servicioPeriodo;

    public ServicioTareasProfesor(TareaRepositorio tareaRepositorio,
            EntregaTareaRepositorio entregaRepositorio,
            SeccionRepositorio seccionRepositorio,
            InscripcionRepositorio inscripcionRepositorio,
            ServicioPeriodo servicioPeriodo) {
        this.tareaRepositorio = tareaRepositorio;
        this.entregaRepositorio = entregaRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.servicioPeriodo = servicioPeriodo;
    }

    @Transactional(readOnly = true)
    public List<TareaProfesorDto> tareas(Integer profesorId) {
        List<Integer> seccionIds = seccionesDelProfesor(profesorId);
        if (seccionIds.isEmpty()) {
            return List.of();
        }
        return tareaRepositorio.buscarPorSecciones(seccionIds).stream().map(this::aDto).toList();
    }

    @Transactional
    public TareaProfesorDto crear(Integer profesorId, SolicitudTarea solicitud) {
        Seccion seccion = seccionDelProfesor(profesorId, solicitud.seccionId());

        Tarea tarea = new Tarea();
        tarea.setSeccion(seccion);
        tarea.setTitulo(solicitud.titulo());
        tarea.setDescripcion(solicitud.descripcion());
        tarea.setFechaEntrega(solicitud.fechaEntrega());
        tarea.setArchivoAdjuntoUrl(solicitud.archivoAdjuntoUrl());

        return aDto(tareaRepositorio.save(tarea));
    }

    @Transactional(readOnly = true)
    public List<EntregaProfesorDto> entregas(Integer profesorId, Integer tareaId) {
        Tarea tarea = tareaDelProfesor(profesorId, tareaId);

        Map<Integer, EntregaTarea> masReciente = new LinkedHashMap<>();
        for (EntregaTarea entrega : entregaRepositorio.findByTareaIdOrderByFechaEnvioDesc(tarea.getId())) {
            masReciente.putIfAbsent(entrega.getEstudiante().getId(), entrega);
        }

        return masReciente.values().stream().map(ServicioTareasProfesor::aEntregaDto).toList();
    }

    @Transactional
    public EntregaProfesorDto calificar(Integer profesorId, Integer entregaId, SolicitudCalificacion solicitud) {
        EntregaTarea entrega = entregaRepositorio.findById(entregaId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La entrega indicada no existe."));

        // Verifica que la tarea de esa entrega pertenezca a una seccion de este profesor.
        tareaDelProfesor(profesorId, entrega.getTarea().getId());

        entrega.setCalificacion(solicitud.calificacion());
        entrega.setComentariosProfesor(solicitud.comentarios());

        return aEntregaDto(entregaRepositorio.save(entrega));
    }

    private List<Integer> seccionesDelProfesor(Integer profesorId) {
        return seccionRepositorio.buscarPorProfesorYPeriodo(profesorId, servicioPeriodo.periodoActivo())
                .stream().map(Seccion::getId).toList();
    }

    private Seccion seccionDelProfesor(Integer profesorId, Integer seccionId) {
        Seccion seccion = seccionRepositorio.findById(seccionId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La sección indicada no existe."));
        if (!seccion.getProfesor().getId().equals(profesorId)) {
            throw new ReglaNegocioExcepcion("Esa sección no pertenece a tus clases.");
        }
        return seccion;
    }

    private Tarea tareaDelProfesor(Integer profesorId, Integer tareaId) {
        Tarea tarea = tareaRepositorio.findById(tareaId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La tarea indicada no existe."));
        if (!tarea.getSeccion().getProfesor().getId().equals(profesorId)) {
            throw new RecursoNoEncontradoExcepcion("La tarea indicada no existe.");
        }
        return tarea;
    }

    private TareaProfesorDto aDto(Tarea tarea) {
        long totalEstudiantes = inscripcionRepositorio.countBySeccionIdAndEstadoNot(
                tarea.getSeccion().getId(), EstadoInscripcion.RETIRADO);

        Map<Integer, EntregaTarea> masReciente = new LinkedHashMap<>();
        for (EntregaTarea entrega : entregaRepositorio.findByTareaIdOrderByFechaEnvioDesc(tarea.getId())) {
            masReciente.putIfAbsent(entrega.getEstudiante().getId(), entrega);
        }

        int entregadas = masReciente.size();
        int pendientesPorCalificar = (int) masReciente.values().stream()
                .filter(e -> e.getCalificacion() == null)
                .count();

        return new TareaProfesorDto(
                tarea.getId(),
                tarea.getSeccion().getId(),
                tarea.getSeccion().getMateria().getNombre(),
                tarea.getSeccion().getMateria().getCodigo(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getFechaEntrega(),
                tarea.getArchivoAdjuntoUrl(),
                (int) totalEstudiantes,
                entregadas,
                pendientesPorCalificar);
    }

    private static EntregaProfesorDto aEntregaDto(EntregaTarea entrega) {
        return new EntregaProfesorDto(
                entrega.getId(),
                entrega.getEstudiante().getId(),
                entrega.getEstudiante().getNombreCompleto(),
                entrega.getArchivoUrl(),
                entrega.getFechaEnvio(),
                entrega.getCalificacion(),
                entrega.getComentariosProfesor());
    }

    @Transactional
    public TareaProfesorDto actualizar(Integer profesorId, Integer tareaId, SolicitudTarea solicitud) {
        Tarea tarea = tareaDelProfesor(profesorId, tareaId);
        Seccion seccion = seccionDelProfesor(profesorId, solicitud.seccionId());

        tarea.setSeccion(seccion);
        tarea.setTitulo(solicitud.titulo());
        tarea.setDescripcion(solicitud.descripcion());
        tarea.setFechaEntrega(solicitud.fechaEntrega());
        tarea.setArchivoAdjuntoUrl(solicitud.archivoAdjuntoUrl());

        return aDto(tareaRepositorio.save(tarea));
    }

    @Transactional
    public void eliminar(Integer profesorId, Integer tareaId) {
        Tarea tarea = tareaDelProfesor(profesorId, tareaId);
        // El esquema tiene entregas_tareas.tarea_id con ON DELETE CASCADE: borrar la
        // tarea borra automáticamente sus entregas, no hace falta limpiarlas a mano.
        tareaRepositorio.delete(tarea);
    }


}