package com.capa_de_negocio.adm_universidad.dto;

import java.util.Map;

/**
 * Formato unico de error de la API. El cliente Flutter muestra {@code mensaje}
 * directamente al usuario, por eso siempre va redactado en español.
 */
public record RespuestaError(String mensaje, int codigo, Map<String, String> campos) {

    public static RespuestaError de(String mensaje, int codigo) {
        return new RespuestaError(mensaje, codigo, Map.of());
    }
}
