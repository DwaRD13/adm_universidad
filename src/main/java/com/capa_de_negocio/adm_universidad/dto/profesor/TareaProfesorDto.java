package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.time.LocalDateTime;

public record TareaProfesorDto(
        Integer id,
        Integer seccionId,
        String materia,
        String codigoMateria,
        String titulo,
        String descripcion,
        LocalDateTime fechaEntrega,
        String archivoAdjuntoUrl,
        int totalEstudiantes,
        int entregadas,
        int pendientesPorCalificar) {
}