package com.capa_de_negocio.adm_universidad.entidad;

/**
 * Refleja ENUM('Activo', 'Inactivo', 'Suspendido') de la tabla usuarios.
 * El valor guardado en MySQL es {@link #getValor()}, no el nombre de la constante.
 */
public enum EstadoUsuario {

    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    SUSPENDIDO("Suspendido");

    private final String valor;

    EstadoUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoUsuario desde(String valor) {
        if (valor == null) {
            return null;
        }
        for (EstadoUsuario estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de usuario desconocido: " + valor);
    }
}
