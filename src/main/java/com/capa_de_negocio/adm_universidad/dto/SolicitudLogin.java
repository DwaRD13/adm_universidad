package com.capa_de_negocio.adm_universidad.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudLogin(
        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "Escribe un correo válido.")
        String email,

        @NotBlank(message = "La contraseña es obligatoria.")
        String password) {
}
