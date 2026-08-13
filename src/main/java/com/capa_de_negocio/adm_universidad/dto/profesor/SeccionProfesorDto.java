package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.util.List;

public record SeccionProfesorDto(
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer creditos,
        String periodo,
        String aula,
        String horarioDescripcion,
        List<String> dias,
        String horaInicio,
        String horaFin,
        String estado,
        Integer cupoMaximo,
        Integer inscritos) {
}