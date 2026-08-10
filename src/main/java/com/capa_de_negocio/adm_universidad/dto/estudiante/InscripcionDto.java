package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.time.LocalDateTime;

/** Una inscripcion del estudiante, con la seccion resuelta para mostrarla en lista. */
public record InscripcionDto(
        Integer id,
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer creditos,
        String profesor,
        String aula,
        String periodo,
        String horarioDescripcion,
        String estado,
        LocalDateTime fechaInscripcion) {
}
