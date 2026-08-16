package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.math.BigDecimal;

public record EstudianteCalificacionDto(
        Integer inscripcionId,
        Integer estudianteId,
        String nombre,
        BigDecimal nota) {
}