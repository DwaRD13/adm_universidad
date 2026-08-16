package com.capa_de_negocio.adm_universidad.dto.profesor;

public record EstudianteAsistenciaDto(
        Integer inscripcionId,
        Integer estudianteId,
        String nombre,
        String estado,
        String observaciones) {
}