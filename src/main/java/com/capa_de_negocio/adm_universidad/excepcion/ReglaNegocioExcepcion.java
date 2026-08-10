package com.capa_de_negocio.adm_universidad.excepcion;

/**
 * La operacion es valida en forma pero choca con una regla del dominio
 * (seccion sin cupo, inscripcion duplicada, tarea vencida). Se traduce a HTTP 409.
 */
public class ReglaNegocioExcepcion extends RuntimeException {

    public ReglaNegocioExcepcion(String mensaje) {
        super(mensaje);
    }
}
