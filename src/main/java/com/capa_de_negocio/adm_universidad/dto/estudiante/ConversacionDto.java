package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.time.LocalDateTime;

/** Resumen de la conversacion con un interlocutor, para la lista de mensajes. */
public record ConversacionDto(
        Integer usuarioId,
        String nombre,
        String rol,
        String ultimoMensaje,
        LocalDateTime fechaUltimoMensaje,
        long sinLeer) {
}
