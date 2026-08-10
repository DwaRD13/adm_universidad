package com.capa_de_negocio.adm_universidad.dto;

import com.capa_de_negocio.adm_universidad.entidad.Usuario;

/** Datos publicos del usuario. Nunca incluye el hash de la contraseña. */
public record UsuarioDto(
        Integer id,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String email,
        String rol,
        String matricula,
        String telefono,
        String estado) {

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
