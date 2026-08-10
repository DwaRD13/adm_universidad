package com.capa_de_negocio.adm_universidad.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.AsistenciaDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.CalificacionesDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.ClaseHorarioDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.ConversacionDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.InscripcionDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.MaterialDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.MensajeDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.ResumenDashboardDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SeccionDisponibleDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudEntrega;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudInscripcion;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudMensaje;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudPerfil;
import com.capa_de_negocio.adm_universidad.dto.estudiante.TareaDto;
import com.capa_de_negocio.adm_universidad.seguridad.UsuarioAutenticado;
import com.capa_de_negocio.adm_universidad.servicio.ServicioAsistencia;
import com.capa_de_negocio.adm_universidad.servicio.ServicioCalificaciones;
import com.capa_de_negocio.adm_universidad.servicio.ServicioDashboard;
import com.capa_de_negocio.adm_universidad.servicio.ServicioInscripcion;
import com.capa_de_negocio.adm_universidad.servicio.ServicioMateriales;
import com.capa_de_negocio.adm_universidad.servicio.ServicioMensajes;
import com.capa_de_negocio.adm_universidad.servicio.ServicioPerfil;
import com.capa_de_negocio.adm_universidad.servicio.ServicioTareas;

import jakarta.validation.Valid;

/**
 * Panel del estudiante. Todos los metodos operan sobre el usuario del token
 * ({@link UsuarioAutenticado#idActual()}): ningun endpoint acepta un id de estudiante
 * como parametro, de forma que un estudiante nunca puede leer datos de otro.
 */
@RestController
@RequestMapping("/api/estudiante")
@PreAuthorize("hasRole('Estudiante')")
public class ControladorEstudiante {

    private final ServicioDashboard servicioDashboard;
    private final ServicioInscripcion servicioInscripcion;
    private final ServicioCalificaciones servicioCalificaciones;
    private final ServicioAsistencia servicioAsistencia;
    private final ServicioTareas servicioTareas;
    private final ServicioMateriales servicioMateriales;
    private final ServicioMensajes servicioMensajes;
    private final ServicioPerfil servicioPerfil;

    public ControladorEstudiante(ServicioDashboard servicioDashboard,
            ServicioInscripcion servicioInscripcion,
            ServicioCalificaciones servicioCalificaciones,
            ServicioAsistencia servicioAsistencia,
            ServicioTareas servicioTareas,
            ServicioMateriales servicioMateriales,
            ServicioMensajes servicioMensajes,
            ServicioPerfil servicioPerfil) {
        this.servicioDashboard = servicioDashboard;
        this.servicioInscripcion = servicioInscripcion;
        this.servicioCalificaciones = servicioCalificaciones;
        this.servicioAsistencia = servicioAsistencia;
        this.servicioTareas = servicioTareas;
        this.servicioMateriales = servicioMateriales;
        this.servicioMensajes = servicioMensajes;
        this.servicioPerfil = servicioPerfil;
    }

    // ---------- Dashboard ----------

    @GetMapping("/resumen")
    public ResumenDashboardDto resumen() {
        return servicioDashboard.resumen(UsuarioAutenticado.idActual());
    }

    // ---------- Horario e inscripcion ----------

    @GetMapping("/horario")
    public List<ClaseHorarioDto> horario() {
        return servicioInscripcion.horario(UsuarioAutenticado.idActual());
    }

    @GetMapping("/inscripciones")
    public List<InscripcionDto> inscripciones() {
        return servicioInscripcion.misInscripciones(UsuarioAutenticado.idActual());
    }

    @GetMapping("/secciones-disponibles")
    public List<SeccionDisponibleDto> seccionesDisponibles() {
        return servicioInscripcion.seccionesDisponibles(UsuarioAutenticado.idActual());
    }

    @PostMapping("/inscripciones")
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionDto inscribir(@Valid @RequestBody SolicitudInscripcion solicitud) {
        return servicioInscripcion.inscribir(UsuarioAutenticado.idActual(), solicitud.seccionId());
    }

    @DeleteMapping("/inscripciones/{id}")
    public InscripcionDto retirar(@PathVariable Integer id) {
        return servicioInscripcion.retirar(UsuarioAutenticado.idActual(), id);
    }

    // ---------- Calificaciones y asistencia ----------

    @GetMapping("/calificaciones")
    public CalificacionesDto calificaciones() {
        return servicioCalificaciones.calificaciones(UsuarioAutenticado.idActual());
    }

    @GetMapping("/asistencias")
    public AsistenciaDto asistencias() {
        return servicioAsistencia.asistencia(UsuarioAutenticado.idActual());
    }

    // ---------- Tareas y materiales ----------

    @GetMapping("/tareas")
    public List<TareaDto> tareas() {
        return servicioTareas.tareas(UsuarioAutenticado.idActual());
    }

    @PostMapping("/tareas/{id}/entrega")
    @ResponseStatus(HttpStatus.CREATED)
    public TareaDto entregar(@PathVariable Integer id, @Valid @RequestBody SolicitudEntrega solicitud) {
        return servicioTareas.entregar(UsuarioAutenticado.idActual(), id, solicitud.archivoUrl());
    }

    @GetMapping("/materiales")
    public List<MaterialDto> materiales() {
        return servicioMateriales.materiales(UsuarioAutenticado.idActual());
    }

    // ---------- Mensajes ----------

    @GetMapping("/mensajes")
    public List<ConversacionDto> conversaciones() {
        return servicioMensajes.conversaciones(UsuarioAutenticado.idActual());
    }

    @GetMapping("/mensajes/{usuarioId}")
    public List<MensajeDto> hilo(@PathVariable Integer usuarioId) {
        return servicioMensajes.hilo(UsuarioAutenticado.idActual(), usuarioId);
    }

    @PostMapping("/mensajes")
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeDto enviarMensaje(@Valid @RequestBody SolicitudMensaje solicitud) {
        return servicioMensajes.enviar(UsuarioAutenticado.idActual(), solicitud);
    }

    /** Profesores de sus secciones: los unicos destinatarios que ofrece la pantalla. */
    @GetMapping("/contactos")
    public List<UsuarioDto> contactos() {
        return servicioMensajes.contactos(UsuarioAutenticado.idActual());
    }

    // ---------- Perfil ----------

    @GetMapping("/perfil")
    public UsuarioDto perfil() {
        return servicioPerfil.perfil(UsuarioAutenticado.idActual());
    }

    @PutMapping("/perfil")
    public UsuarioDto actualizarPerfil(@Valid @RequestBody SolicitudPerfil solicitud) {
        return servicioPerfil.actualizar(UsuarioAutenticado.idActual(), solicitud);
    }
}
