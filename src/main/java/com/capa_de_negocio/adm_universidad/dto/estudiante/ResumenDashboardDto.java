package com.capa_de_negocio.adm_universidad.dto.estudiante;

import java.math.BigDecimal;
import java.util.List;

/** Todo lo que necesita el dashboard en una sola llamada. */
public record ResumenDashboardDto(
        String periodoActivo,
        int materiasInscritas,
        int creditosInscritos,
        /** Promedio general de las notas ya publicadas; null si aun no hay ninguna. */
        BigDecimal promedioGeneral,
        /** Porcentaje de asistencia efectiva (0-100); null si no hay registros. */
        BigDecimal porcentajeAsistencia,
        int tareasPendientes,
        int mensajesSinLeer,
        /** Las tres entregas mas proximas, para la tarjeta de recordatorio. */
        List<TareaDto> proximasEntregas,
        /** Promedio por materia, para el grafico de barras. */
        List<PromedioMateriaDto> promediosPorMateria) {

    public record PromedioMateriaDto(String materia, String codigoMateria, BigDecimal promedio) {
    }
}
