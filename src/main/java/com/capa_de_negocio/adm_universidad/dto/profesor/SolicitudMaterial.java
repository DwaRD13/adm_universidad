package com.capa_de_negocio.adm_universidad.dto.profesor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitudMaterial(
        @NotNull(message = "Selecciona la sección.") Integer seccionId,
        @NotBlank(message = "El título es obligatorio.") String titulo,
        String descripcion,
        String tipoArchivo,
        @NotBlank(message = "Sube el archivo antes de guardar.") String urlArchivo) {
}