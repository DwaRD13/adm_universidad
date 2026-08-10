package com.capa_de_negocio.adm_universidad.seguridad;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lee la cabecera Authorization: Bearer &lt;token&gt; y, si el token es valido,
 * coloca al usuario en el contexto de seguridad para el resto de la peticion.
 */
@Component
public class FiltroAutenticacionJwt extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final ServicioJwt servicioJwt;
    private final UsuarioRepositorio usuarioRepositorio;

    public FiltroAutenticacionJwt(ServicioJwt servicioJwt, UsuarioRepositorio usuarioRepositorio) {
        this.servicioJwt = servicioJwt;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta,
            FilterChain cadena) throws ServletException, IOException {

        String cabecera = peticion.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith(PREFIJO_BEARER)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            Integer idUsuario = servicioJwt.extraerIdUsuario(cabecera.substring(PREFIJO_BEARER.length()));
            if (idUsuario != null) {
                usuarioRepositorio.buscarPorIdConRol(idUsuario)
                        .map(UsuarioAutenticado::new)
                        .filter(UsuarioAutenticado::isEnabled)
                        .ifPresent(autenticado -> {
                            var autenticacion = new UsernamePasswordAuthenticationToken(
                                    autenticado, null, autenticado.getAuthorities());
                            autenticacion.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(peticion));
                            SecurityContextHolder.getContext().setAuthentication(autenticacion);
                        });
            }
        }

        cadena.doFilter(peticion, respuesta);
    }
}
