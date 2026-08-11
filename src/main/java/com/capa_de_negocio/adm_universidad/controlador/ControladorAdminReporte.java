package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.servicio.ServicioReportAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reportes")
@PreAuthorize("hasRole('Administrativo')")
public class ControladorAdminReporte {

    @Autowired
    private ServicioReportAdmin reporteService;

    @GetMapping("/resumen-academico")
    public ResponseEntity<Map<String, Object>> obtenerResumenAcademico() {
        return ResponseEntity.ok(reporteService.obtenerResumenAcademicoGlobal());
    }
}
