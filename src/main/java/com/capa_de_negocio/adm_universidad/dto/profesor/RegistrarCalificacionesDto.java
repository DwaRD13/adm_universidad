package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.util.List;

public record RegistrarCalificacionesDto(
        Integer seccionId,
        List<RegistroCalificacionDto> registros) {
}