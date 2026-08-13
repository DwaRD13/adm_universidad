package com.capa_de_negocio.adm_universidad.dto.profesor;

import java.util.List;

public record ResumenDashboardProfesorDto(
        String periodo,
        int totalSecciones,
        int totalEstudiantes,
        int tareasPendientesPorCalificar,
        int mensajesSinLeer,
        List<SeccionProfesorDto> clasesHoy) {
}