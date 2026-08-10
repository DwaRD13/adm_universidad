package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Una tarea vista desde el estudiante, con el estado de su propia entrega ya resuelto. */
public record TareaDto(
        Integer id,
        Integer seccionId,
        String materia,
        String codigoMateria,
        String titulo,
        String descripcion,
        LocalDateTime fechaEntrega,
        String archivoAdjuntoUrl,
        /** PENDIENTE, ENTREGADA, CALIFICADA o VENCIDA. */
        String estado,
        boolean entregada,
        LocalDateTime fechaEnvio,
        String archivoEntregadoUrl,
        BigDecimal calificacion,
        String comentariosProfesor) {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ENTREGADA = "ENTREGADA";
    public static final String CALIFICADA = "CALIFICADA";
    public static final String VENCIDA = "VENCIDA";
}
