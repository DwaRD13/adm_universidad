package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.time.LocalDate;
import java.util.List;

public record RegistrarAsistenciaDto(
        Integer seccionId,
        LocalDate fecha,
        List<RegistroAsistenciaDto> registros) {
}