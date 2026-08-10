package com.capa_de_negocio.adm_universidad.entidad.convertidor;

import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Traduce entre la constante Java y el texto exacto del ENUM de MySQL. */
@Converter(autoApply = true)
public class EstadoInscripcionConvertidor implements AttributeConverter<EstadoInscripcion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoInscripcion estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoInscripcion convertToEntityAttribute(String valor) {
        return EstadoInscripcion.desde(valor);
    }
}
