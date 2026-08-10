package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.util.List;

/**
 * Una clase del horario semanal. Los dias y las horas vienen ya interpretados desde
 * el backend: el cliente no analiza la cadena libre de horario_descripcion.
 */
public record ClaseHorarioDto(
        Integer inscripcionId,
        Integer seccionId,
        String materia,
        String codigoMateria,
        Integer creditos,
        String profesor,
        String aula,
        String periodo,
        String horarioDescripcion,
        /** Codigos de dia: Lu, Ma, Mi, Ju, Vi, Sa. Vacio si el texto no se pudo interpretar. */
        List<String> dias,
        String horaInicio,
        String horaFin) {
}
