package com.capa_de_negocio.adm_universidad.controlador;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capa_de_negocio.adm_universidad.dto.profesor.EntregaProfesorDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.SolicitudCalificacion;
import com.capa_de_negocio.adm_universidad.dto.profesor.SolicitudTarea;
import com.capa_de_negocio.adm_universidad.dto.profesor.TareaProfesorDto;
import com.capa_de_negocio.adm_universidad.servicio.ServicioTareasProfesor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profesor")
public class TareaProfesorControlador {

    private final ServicioTareasProfesor servicioTareasProfesor;

    public TareaProfesorControlador(ServicioTareasProfesor servicioTareasProfesor) {
        this.servicioTareasProfesor = servicioTareasProfesor;
    }

    @GetMapping("/tareas")
    public List<TareaProfesorDto> tareas(Authentication authentication) {
        return servicioTareasProfesor.tareas(SeccionProfesorControlador.idDe(authentication));
    }

    @PostMapping("/tareas")
    public TareaProfesorDto crear(Authentication authentication, @Valid @RequestBody SolicitudTarea solicitud) {
        return servicioTareasProfesor.crear(SeccionProfesorControlador.idDe(authentication), solicitud);
    }

    @GetMapping("/tareas/{tareaId}/entregas")
    public List<EntregaProfesorDto> entregas(Authentication authentication, @PathVariable Integer tareaId) {
        return servicioTareasProfesor.entregas(SeccionProfesorControlador.idDe(authentication), tareaId);
    }

    @PutMapping("/entregas/{entregaId}/calificar")
    public EntregaProfesorDto calificar(Authentication authentication, @PathVariable Integer entregaId,
            @Valid @RequestBody SolicitudCalificacion solicitud) {
        return servicioTareasProfesor.calificar(
                SeccionProfesorControlador.idDe(authentication), entregaId, solicitud);
    }

    @PutMapping("/tareas/{tareaId}")
    public TareaProfesorDto actualizar(Authentication authentication, @PathVariable Integer tareaId,
            @Valid @RequestBody SolicitudTarea solicitud) {
        return servicioTareasProfesor.actualizar(
                SeccionProfesorControlador.idDe(authentication), tareaId, solicitud);
    }

    @DeleteMapping("/tareas/{tareaId}")
    public void eliminar(Authentication authentication, @PathVariable Integer tareaId) {
        servicioTareasProfesor.eliminar(SeccionProfesorControlador.idDe(authentication), tareaId);
    }
}