package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.entidad.Rol;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.repositorio.RolRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioUsuarios {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private PasswordEncoder codificadorContrasena;

    @Transactional
    public List<UsuarioDto> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream().map((ServicioUsuarios::de)).toList();
    }

    @Transactional
    public List<UsuarioDto> obtenerUsuariosMedianteRol(String rolName) {
        return usuarioRepository.findByRolNombre(rolName).stream().map((ServicioUsuarios::de)).toList();
    }


    public Usuario guardarUsuario(Usuario usuario) {
        if(usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        Rol rol = rolRepositorio.findRolById(usuario.getRol().getId());
        usuario.setRol(rol);
        usuario.setPasswordHash(codificadorContrasena.encode(usuario.getPasswordHash()));
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(Math.toIntExact(id));
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuarioExistente.setNombres(usuarioActualizado.getNombres());
        usuarioExistente.setApellidos(usuarioActualizado.getApellidos());

        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
            throw new RuntimeException("El nuevo correo ya está en uso por otro usuario");
        }
        usuarioExistente.setEmail(usuarioActualizado.getEmail());

        if (usuarioActualizado.getRol() != null && usuarioActualizado.getRol().getId() != null) {
            usuarioExistente.setRol(usuarioActualizado.getRol());
        }


        if (usuarioActualizado.getPasswordHash() != null && !usuarioActualizado.getPasswordHash().trim().isEmpty()) {
             usuarioExistente.setPasswordHash(codificadorContrasena.encode(usuarioActualizado.getPasswordHash()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public static UsuarioDto de(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.getMatriculaEmpleadoId(),
                usuario.getTelefono(),
                usuario.getEstado() == null ? null : usuario.getEstado().getValor());
    }
}
