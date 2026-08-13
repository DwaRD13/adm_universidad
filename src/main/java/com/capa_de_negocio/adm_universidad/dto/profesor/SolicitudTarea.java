package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitudTarea(
        @NotNull(message = "Selecciona la sección.") Integer seccionId,
        @NotBlank(message = "El título es obligatorio.") String titulo,
        String descripcion,
        @NotNull(message = "La fecha de entrega es obligatoria.") LocalDateTime fechaEntrega,
        String archivoAdjuntoUrl) {
}