package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.servicio.ServicioDashboardAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

public class ControladorDashboardAdmin {

    @Autowired
    private ServicioDashboardAdmin dashboardService;

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Long>> getEstadisticas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasPrincipales());
    }
}
