package com.capa_de_negocio.adm_universidad.dto;

/** Resultado de subir un archivo: la URL es la que se persiste luego en la entrega. */
public record RespuestaArchivo(String url, String nombreOriginal, long tamanoBytes) {
}
