package com.capa_de_negocio.adm_universidad.servicio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SolicitudPerfil;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;

/** Consulta y edicion del perfil propio. Nunca opera sobre el perfil de otro usuario. */
@Service
public class ServicioPerfil {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificador;

    public ServicioPerfil(UsuarioRepositorio usuarioRepositorio, PasswordEncoder codificador) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificador = codificador;
    }

    @Transactional(readOnly = true)
    public UsuarioDto perfil(Integer usuarioId) {
        return UsuarioDto.de(cargar(usuarioId));
    }

    @Transactional
    public UsuarioDto actualizar(Integer usuarioId, SolicitudPerfil solicitud) {
        Usuario usuario = cargar(usuarioId);

        if (solicitud.telefono() != null) {
            usuario.setTelefono(solicitud.telefono().isBlank() ? null : solicitud.telefono().trim());
        }

        if (solicitud.passwordNueva() != null && !solicitud.passwordNueva().isBlank()) {
            if (solicitud.passwordActual() == null || solicitud.passwordActual().isBlank()) {
                throw new ReglaNegocioExcepcion(
                        "Escribe tu contraseña actual para poder cambiarla.");
            }
            if (!codificador.matches(solicitud.passwordActual(), usuario.getPasswordHash())) {
                throw new ReglaNegocioExcepcion("La contraseña actual no es correcta.");
            }
            usuario.setPasswordHash(codificador.encode(solicitud.passwordNueva()));
        }

        return UsuarioDto.de(usuarioRepositorio.save(usuario));
    }

    private Usuario cargar(Integer usuarioId) {
        return usuarioRepositorio.buscarPorIdConRol(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Tu usuario ya no existe."));
    }
}
