package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EntregaProfesorDto(
        Integer id,
        Integer estudianteId,
        String estudianteNombre,
        String archivoUrl,
        LocalDateTime fechaEnvio,
        BigDecimal calificacion,
        String comentariosProfesor) {
}