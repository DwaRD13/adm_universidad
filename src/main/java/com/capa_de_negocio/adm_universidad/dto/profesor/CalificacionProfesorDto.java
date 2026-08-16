package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.math.BigDecimal;

public record CalificacionProfesorDto(
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer estudiantes,
        Integer aprobados,
        Integer reprobados,
        BigDecimal promedio) {
}