package com.capa_de_negocio.adm_universidad.dto.profesor;

public record MateriaProfesorDto(
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer creditos,
        String periodo,
        String aula,
        String estado,
        Integer inscritos
) {
}