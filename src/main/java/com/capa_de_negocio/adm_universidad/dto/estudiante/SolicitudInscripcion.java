package com.capa_de_negocio.adm_universidad.dto.estudiante;

import jakarta.validation.constraints.NotNull;

public record SolicitudInscripcion(
        @NotNull(message = "Indica la sección en la que deseas inscribirte.")
        Integer seccionId) {
}
