package com.capa_de_negocio.adm_universidad.entidad;

/**
 * Refleja ENUM('Abierta', 'Cerrada', 'En Curso', 'Finalizada') de la tabla secciones.
 * 'En Curso' lleva espacio, por eso el valor de MySQL no puede ser el nombre de la constante.
 */
public enum EstadoSeccion {

    ABIERTA("Abierta"),
    CERRADA("Cerrada"),
    EN_CURSO("En Curso"),
    FINALIZADA("Finalizada");

    private final String valor;

    EstadoSeccion(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoSeccion desde(String valor) {
        if (valor == null) {
            return null;
        }
        for (EstadoSeccion estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de seccion desconocido: " + valor);
    }
}
