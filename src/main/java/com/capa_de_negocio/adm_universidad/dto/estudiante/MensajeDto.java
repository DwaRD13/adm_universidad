package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.time.LocalDateTime;

/** Un mensaje del hilo. {@code propio} indica si lo envio el usuario autenticado. */
public record MensajeDto(
        Integer id,
        Integer remitenteId,
        String remitente,
        Integer destinatarioId,
        String destinatario,
        String asunto,
        String cuerpo,
        boolean leido,
        boolean propio,
        LocalDateTime fechaEnvio) {
}
