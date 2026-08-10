package com.capa_de_negocio.adm_universidad.dto;

/** El cliente guarda el token y usa el rol del usuario para decidir a que panel entrar. */
public record RespuestaLogin(String token, long expiraEnMs, UsuarioDto usuario) {
}
