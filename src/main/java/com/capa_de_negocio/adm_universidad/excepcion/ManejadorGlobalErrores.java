package com.capa_de_negocio.adm_universidad.excepcion;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.capa_de_negocio.adm_universidad.dto.RespuestaError;

/** Traduce las excepciones de la aplicacion al formato de error unico de la API. */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<RespuestaError> noEncontrado(RecursoNoEncontradoExcepcion e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespuestaError.de(e.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ReglaNegocioExcepcion.class)
    public ResponseEntity<RespuestaError> reglaNegocio(ReglaNegocioExcepcion e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RespuestaError.de(e.getMessage(), HttpStatus.CONFLICT.value()));
    }

    /** Credenciales incorrectas y cuentas no activas comparten respuesta para no filtrar informacion. */
    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<RespuestaError> credenciales() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespuestaError.de("Correo o contraseña incorrectos.",
                        HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler({ DisabledException.class, LockedException.class })
    public ResponseEntity<RespuestaError> cuentaNoActiva() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaError.de(
                        "Tu cuenta no está activa. Comunícate con el departamento administrativo.",
                        HttpStatus.FORBIDDEN.value()));
    }

    /** Errores de @Valid: se devuelve el detalle campo a campo para pintarlo bajo cada input. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(new RespuestaError(
                "Revisa los datos enviados.", HttpStatus.BAD_REQUEST.value(), campos));
    }

    /**
     * @PreAuthorize lanza AccessDeniedException dentro del controlador, asi que llega
     * aqui antes que al ExceptionTranslationFilter. Sin este manejador acabaria como 500.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> accesoDenegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaError.de("Tu rol no tiene permiso para acceder a este recurso.",
                        HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RespuestaError> archivoDemasiadoGrande() {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                .body(RespuestaError.de("El archivo supera el límite de 10 MB.",
                        HttpStatus.CONTENT_TOO_LARGE.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> errorInesperado(Exception e) {
        // El detalle tecnico queda en el log del servidor, no viaja al cliente.
        org.slf4j.LoggerFactory.getLogger(ManejadorGlobalErrores.class)
                .error("Error no controlado en la API", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaError.de("Ocurrió un error inesperado. Inténtalo de nuevo.",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
