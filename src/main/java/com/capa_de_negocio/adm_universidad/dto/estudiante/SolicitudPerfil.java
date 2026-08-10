package com.capa_de_negocio.adm_universidad.dto.estudiante;

import jakarta.validation.constraints.Size;

/**
 * Edicion del perfil propio. Los tres campos son opcionales: se envia solo lo que
 * se quiere cambiar. Para cambiar la contraseña hay que mandar la actual y la nueva.
 */
public record SolicitudPerfil(
        @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres.")
        String telefono,

        String passwordActual,

        @Size(min = 6, max = 100, message = "La nueva contraseña debe tener al menos 6 caracteres.")
        String passwordNueva) {
}
