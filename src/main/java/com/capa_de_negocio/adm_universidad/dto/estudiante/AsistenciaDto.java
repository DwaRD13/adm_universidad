package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Asistencia del estudiante agrupada por materia, con porcentajes ya calculados. */
public record AsistenciaDto(
        BigDecimal porcentajeGeneral,
        int totalClases,
        List<MateriaAsistenciaDto> materias) {

    public record MateriaAsistenciaDto(
            Integer inscripcionId,
            String materia,
            String codigoMateria,
            String profesor,
            int totalClases,
            int presentes,
            int ausentes,
            int tardanzas,
            int excusas,
            /** Porcentaje de clases no ausentes (0-100). */
            BigDecimal porcentaje,
            List<RegistroDto> registros) {
    }

    public record RegistroDto(
            Integer id,
            LocalDate fecha,
            String estado,
            String observaciones) {
    }
}
