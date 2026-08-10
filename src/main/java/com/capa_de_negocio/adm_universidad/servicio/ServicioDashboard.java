package com.capa_de_negocio.adm_universidad.servicio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.CalificacionesDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.ResumenDashboardDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.ResumenDashboardDto.PromedioMateriaDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.TareaDto;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;

/**
 * Compone en una sola llamada todo lo que pinta el dashboard, para que la pantalla
 * de inicio no tenga que encadenar siete peticiones.
 */
@Service
public class ServicioDashboard {

    /** Cuantas entregas proximas se muestran en la tarjeta de recordatorio. */
    private static final int MAXIMO_PROXIMAS_ENTREGAS = 3;

    private final ServicioInscripcion servicioInscripcion;
    private final ServicioCalificaciones servicioCalificaciones;
    private final ServicioAsistencia servicioAsistencia;
    private final ServicioTareas servicioTareas;
    private final ServicioMensajes servicioMensajes;
    private final ServicioPeriodo servicioPeriodo;

    public ServicioDashboard(ServicioInscripcion servicioInscripcion,
            ServicioCalificaciones servicioCalificaciones,
            ServicioAsistencia servicioAsistencia,
            ServicioTareas servicioTareas,
            ServicioMensajes servicioMensajes,
            ServicioPeriodo servicioPeriodo) {
        this.servicioInscripcion = servicioInscripcion;
        this.servicioCalificaciones = servicioCalificaciones;
        this.servicioAsistencia = servicioAsistencia;
        this.servicioTareas = servicioTareas;
        this.servicioMensajes = servicioMensajes;
        this.servicioPeriodo = servicioPeriodo;
    }

    @Transactional(readOnly = true)
    public ResumenDashboardDto resumen(Integer estudianteId) {
        List<Inscripcion> inscripciones = servicioInscripcion.inscripcionesVigentes(estudianteId);
        int creditos = inscripciones.stream()
                .mapToInt(i -> i.getSeccion().getMateria().getCreditos())
                .sum();

        CalificacionesDto calificaciones = servicioCalificaciones.calificaciones(estudianteId);
        List<TareaDto> tareas = servicioTareas.tareas(estudianteId);

        LocalDateTime ahora = LocalDateTime.now();
        List<TareaDto> proximas = tareas.stream()
                .filter(t -> !t.entregada() && t.fechaEntrega().isAfter(ahora))
                .sorted(Comparator.comparing(TareaDto::fechaEntrega))
                .limit(MAXIMO_PROXIMAS_ENTREGAS)
                .toList();

        int pendientes = (int) tareas.stream()
                .filter(t -> TareaDto.PENDIENTE.equals(t.estado()))
                .count();

        List<PromedioMateriaDto> promedios = calificaciones.calificaciones().stream()
                .map(c -> new PromedioMateriaDto(c.materia(), c.codigoMateria(), c.nota()))
                .toList();

        return new ResumenDashboardDto(
                servicioPeriodo.periodoActivo(),
                inscripciones.size(),
                creditos,
                calificaciones.promedioGeneral(),
                servicioAsistencia.asistencia(estudianteId).porcentajeGeneral(),
                pendientes,
                (int) servicioMensajes.sinLeer(estudianteId),
                proximas,
                promedios);
    }
}
