package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.time.LocalDateTime;

/** Material de apoyo de una seccion en la que el estudiante esta inscrito. */
public record MaterialDto(
        Integer id,
        Integer seccionId,
        String materia,
        String codigoMateria,
        String titulo,
        String descripcion,
        String tipoArchivo,
        String urlArchivo,
        LocalDateTime fechaSubida) {
}
