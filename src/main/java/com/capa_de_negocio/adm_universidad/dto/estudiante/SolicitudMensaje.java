package com.capa_de_negocio.adm_universidad.dto.estudiante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitudMensaje(
        @NotNull(message = "Elige a quién quieres escribir.")
        Integer destinatarioId,

        @Size(max = 150, message = "El asunto no puede superar los 150 caracteres.")
        String asunto,

        @NotBlank(message = "El mensaje no puede estar vacío.")
        String cuerpo) {
}
