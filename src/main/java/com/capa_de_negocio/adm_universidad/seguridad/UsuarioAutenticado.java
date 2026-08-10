package com.capa_de_negocio.adm_universidad.seguridad;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.capa_de_negocio.adm_universidad.entidad.EstadoUsuario;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;

/**
 * Principal de Spring Security. Envuelve al usuario de la base de datos y expone
 * su rol como authority ROLE_&lt;nombre&gt; (ej. ROLE_Estudiante).
 */
public class UsuarioAutenticado implements UserDetails {

    private final transient Usuario usuario;

    public UsuarioAutenticado(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Id del usuario de la peticion en curso. Es la unica fuente valida de identidad:
     * los servicios del estudiante filtran por este id y nunca por uno recibido en la URL.
     */
    public static Integer idActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UsuarioAutenticado autenticado) {
            return autenticado.getUsuario().getId();
        }
        throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad.");
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()));
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getEstado() != EstadoUsuario.SUSPENDIDO;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getEstado() == EstadoUsuario.ACTIVO;
    }
}
