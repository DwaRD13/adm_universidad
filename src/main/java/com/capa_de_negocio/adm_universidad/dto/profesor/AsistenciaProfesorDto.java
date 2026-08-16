package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.math.BigDecimal;

public record AsistenciaProfesorDto(
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer estudiantes,
        BigDecimal porcentajePromedio) {
}