package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.math.BigDecimal;

public record RegistroCalificacionDto(
        Integer inscripcionId,
        BigDecimal nota) {
}