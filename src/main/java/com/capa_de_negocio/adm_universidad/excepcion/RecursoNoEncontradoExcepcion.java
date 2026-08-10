package com.capa_de_negocio.adm_universidad.excepcion;

/** El recurso no existe o no pertenece al usuario autenticado. Se traduce a HTTP 404. */
public class RecursoNoEncontradoExcepcion extends RuntimeException {

    public RecursoNoEncontradoExcepcion(String mensaje) {
        super(mensaje);
    }
}
