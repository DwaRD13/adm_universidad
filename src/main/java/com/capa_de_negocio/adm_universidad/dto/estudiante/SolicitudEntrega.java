package com.capa_de_negocio.adm_universidad.dto.estudiante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** La URL sale de POST /api/archivos o es un enlace externo pegado por el estudiante. */
public record SolicitudEntrega(
        @NotBlank(message = "Adjunta un archivo o pega un enlace para entregar la tarea.")
        @Size(max = 255, message = "La URL del archivo es demasiado larga.")
        String archivoUrl) {
}
