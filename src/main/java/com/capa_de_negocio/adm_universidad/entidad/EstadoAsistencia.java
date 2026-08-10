package com.capa_de_negocio.adm_universidad.entidad;

/**
 * Refleja ENUM('Presente', 'Ausente', 'Tardanza', 'Excusa') de la tabla asistencias.
 */
public enum EstadoAsistencia {

    PRESENTE("Presente"),
    AUSENTE("Ausente"),
    TARDANZA("Tardanza"),
    EXCUSA("Excusa");

    private final String valor;

    EstadoAsistencia(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    /** Presente, tardanza y excusa cuentan como asistencia efectiva en el porcentaje. */
    public boolean cuentaComoAsistido() {
        return this != AUSENTE;
    }

    public static EstadoAsistencia desde(String valor) {
        if (valor == null) {
            return null;
        }
        for (EstadoAsistencia estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de asistencia desconocido: " + valor);
    }
}
