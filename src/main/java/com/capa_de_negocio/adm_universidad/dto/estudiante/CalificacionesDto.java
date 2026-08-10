package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Calificaciones publicadas del estudiante, con el promedio ya calculado. */
public record CalificacionesDto(
        BigDecimal promedioGeneral,
        int materiasAprobadas,
        int materiasReprobadas,
        List<CalificacionDto> calificaciones) {

    public record CalificacionDto(
            Integer id,
            Integer inscripcionId,
            String materia,
            String codigoMateria,
            Integer creditos,
            String profesor,
            String periodo,
            BigDecimal nota,
            String literal,
            /** Estado de la inscripcion: Aprobado, Reprobado, Inscrito... */
            String estadoInscripcion,
            LocalDateTime fechaPublicacion) {
    }
}
