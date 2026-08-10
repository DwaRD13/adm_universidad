package com.capa_de_negocio.adm_universidad.seguridad;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.capa_de_negocio.adm_universidad.entidad.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Firma y verifica los tokens JWT (HS256) con los que viaja la sesion del usuario. */
@Service
public class ServicioJwt {

    private final SecretKey clave;
    private final long expiracionMs;

    public ServicioJwt(@Value("${app.jwt.secreto}") String secreto,
            @Value("${app.jwt.expiracion-ms}") long expiracionMs) {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRETO debe tener al menos 32 caracteres. Revisa el archivo .env.");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    /** El subject es el id del usuario: es lo unico en lo que confia el resto de la API. */
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("rol", usuario.getRol().getNombre())
                .claim("nombre", usuario.getNombreCompleto())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /** Devuelve el id del usuario, o null si el token es invalido o esta vencido. */
    public Integer extraerIdUsuario(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Integer.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public long getExpiracionMs() {
        return expiracionMs;
    }
}
