package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record SolicitudCalificacion(
        @NotNull(message = "La calificación es obligatoria.")
        @DecimalMin(value = "0.0", message = "La calificación no puede ser negativa.")
        @DecimalMax(value = "100.0", message = "La calificación no puede pasar de 100.")
        BigDecimal calificacion,
        String comentarios) {
}