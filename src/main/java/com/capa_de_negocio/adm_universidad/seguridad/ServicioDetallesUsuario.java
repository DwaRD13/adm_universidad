package com.capa_de_negocio.adm_universidad.seguridad;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;

/** Carga el usuario por email para el proceso de login. */
@Service
public class ServicioDetallesUsuario implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public ServicioDetallesUsuario(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return usuarioRepositorio.buscarPorEmailConRol(email)
                .map(UsuarioAutenticado::new)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas."));
    }
}
