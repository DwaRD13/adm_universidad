package com.capa_de_negocio.adm_universidad.entidad.convertidor;

import com.capa_de_negocio.adm_universidad.entidad.EstadoAsistencia;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Traduce entre la constante Java y el texto exacto del ENUM de MySQL. */
@Converter(autoApply = true)
public class EstadoAsistenciaConvertidor implements AttributeConverter<EstadoAsistencia, String> {

    @Override
    public String convertToDatabaseColumn(EstadoAsistencia estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoAsistencia convertToEntityAttribute(String valor) {
        return EstadoAsistencia.desde(valor);
    }
}
