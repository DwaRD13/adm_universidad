package com.capa_de_negocio.adm_universidad.controlador;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capa_de_negocio.adm_universidad.dto.estudiante.ConversacionDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.MensajeDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudMensaje;
import com.capa_de_negocio.adm_universidad.servicio.ServicioMensajes;

import jakarta.validation.Valid;

/** Mismo motor de mensajeria que Estudiante (ServicioMensajes es generico por usuarioId). */
@RestController
@RequestMapping("/api/profesor/mensajes")
public class MensajeProfesorControlador {

    private final ServicioMensajes servicioMensajes;

    public MensajeProfesorControlador(ServicioMensajes servicioMensajes) {
        this.servicioMensajes = servicioMensajes;
    }

    @GetMapping
    public List<ConversacionDto> conversaciones(Authentication authentication) {
        return servicioMensajes.conversaciones(SeccionProfesorControlador.idDe(authentication));
    }

    @GetMapping("/{otroId}")
    public List<MensajeDto> hilo(Authentication authentication, @PathVariable Integer otroId) {
        return servicioMensajes.hilo(SeccionProfesorControlador.idDe(authentication), otroId);
    }

    @PostMapping
    public MensajeDto enviar(Authentication authentication, @Valid @RequestBody SolicitudMensaje solicitud) {
        return servicioMensajes.enviar(SeccionProfesorControlador.idDe(authentication), solicitud);
    }
}