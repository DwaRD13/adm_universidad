package com.capa_de_negocio.adm_universidad.entidad.convertidor;

import com.capa_de_negocio.adm_universidad.entidad.EstadoSeccion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Traduce entre la constante Java y el texto exacto del ENUM de MySQL ('En Curso' lleva espacio). */
@Converter(autoApply = true)
public class EstadoSeccionConvertidor implements AttributeConverter<EstadoSeccion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoSeccion estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoSeccion convertToEntityAttribute(String valor) {
        return EstadoSeccion.desde(valor);
    }
}
