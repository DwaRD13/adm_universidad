package com.capa_de_negocio.adm_universidad.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capa_de_negocio.adm_universidad.dto.RespuestaLogin;
import com.capa_de_negocio.adm_universidad.dto.SolicitudLogin;
import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.seguridad.UsuarioAutenticado;
import com.capa_de_negocio.adm_universidad.servicio.ServicioAutenticacion;
import com.capa_de_negocio.adm_universidad.servicio.ServicioPerfil;

import jakarta.validation.Valid;

/** Puerta de entrada unica: los tres perfiles inician sesion por aqui. */
@RestController
@RequestMapping("/api/auth")
public class ControladorAutenticacion {

    private final ServicioAutenticacion servicioAutenticacion;
    private final ServicioPerfil servicioPerfil;

    public ControladorAutenticacion(ServicioAutenticacion servicioAutenticacion,
            ServicioPerfil servicioPerfil) {
        this.servicioAutenticacion = servicioAutenticacion;
        this.servicioPerfil = servicioPerfil;
    }

    @PostMapping("/login")
    public RespuestaLogin login(@Valid @RequestBody SolicitudLogin solicitud) {
        return servicioAutenticacion.iniciarSesion(solicitud);
    }

    /** Permite al cliente validar el token guardado y refrescar los datos del usuario. */
    @GetMapping("/yo")
    public UsuarioDto yo() {
        return servicioPerfil.perfil(UsuarioAutenticado.idActual());
    }
}
