package com.capa_de_negocio.adm_universidad.entidad.convertidor;

import com.capa_de_negocio.adm_universidad.entidad.EstadoUsuario;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Traduce entre la constante Java y el texto exacto del ENUM de MySQL. */
@Converter(autoApply = true)
public class EstadoUsuarioConvertidor implements AttributeConverter<EstadoUsuario, String> {

    @Override
    public String convertToDatabaseColumn(EstadoUsuario estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoUsuario convertToEntityAttribute(String valor) {
        return EstadoUsuario.desde(valor);
    }
}
