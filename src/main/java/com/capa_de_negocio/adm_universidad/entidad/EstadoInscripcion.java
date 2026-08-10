package com.capa_de_negocio.adm_universidad.entidad;

/**
 * Refleja ENUM('Inscrito', 'Retirado', 'Aprobado', 'Reprobado') de la tabla inscripciones.
 */
public enum EstadoInscripcion {

    INSCRITO("Inscrito"),
    RETIRADO("Retirado"),
    APROBADO("Aprobado"),
    REPROBADO("Reprobado");

    private final String valor;

    EstadoInscripcion(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    /** Una inscripcion cuenta para cupo, horario y notas mientras no este retirada. */
    public boolean estaVigente() {
        return this != RETIRADO;
    }

    public static EstadoInscripcion desde(String valor) {
        if (valor == null) {
            return null;
        }
        for (EstadoInscripcion estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de inscripcion desconocido: " + valor);
    }
}
