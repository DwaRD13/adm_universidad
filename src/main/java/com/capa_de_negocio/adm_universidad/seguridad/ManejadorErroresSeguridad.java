package com.capa_de_negocio.adm_universidad.seguridad;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.capa_de_negocio.adm_universidad.dto.RespuestaError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Spring Boot 4 usa Jackson 3: el paquete es tools.jackson, no com.fasterxml.jackson.
import tools.jackson.databind.ObjectMapper;

/**
 * Los errores 401 y 403 se producen dentro de la cadena de filtros, antes de llegar
 * a los controladores, asi que no los atrapa el @RestControllerAdvice. Este componente
 * les da el mismo formato JSON en español que el resto de la API.
 */
@Component
public class ManejadorErroresSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ManejadorErroresSeguridad(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest peticion, HttpServletResponse respuesta,
            AuthenticationException excepcion) throws IOException {
        escribir(respuesta, HttpServletResponse.SC_UNAUTHORIZED,
                "Debes iniciar sesión para acceder a este recurso.");
    }

    @Override
    public void handle(HttpServletRequest peticion, HttpServletResponse respuesta,
            AccessDeniedException excepcion) throws IOException {
        escribir(respuesta, HttpServletResponse.SC_FORBIDDEN,
                "Tu rol no tiene permiso para acceder a este recurso.");
    }

    private void escribir(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(respuesta.getWriter(), RespuestaError.de(mensaje, estado));
    }
}
