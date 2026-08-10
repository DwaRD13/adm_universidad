package com.capa_de_negocio.adm_universidad.dto.estudiante;

/** Seccion del catalogo de inscripcion, con el cupo y la situacion del estudiante. */
public record SeccionDisponibleDto(
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer creditos,
        String carrera,
        String profesor,
        String aula,
        String periodo,
        String horarioDescripcion,
        int cupoMaximo,
        int cupoOcupado,
        int cupoDisponible,
        boolean yaInscrito) {
}
