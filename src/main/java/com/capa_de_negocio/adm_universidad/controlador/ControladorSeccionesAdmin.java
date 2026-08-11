package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.servicio.ServicioSeccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/secciones")
@PreAuthorize("hasRole('Administrativo')")
public class ControladorSeccionesAdmin {

    @Autowired
    private ServicioSeccion seccionService;

    @GetMapping
    public ResponseEntity<List<Seccion>> listar() {
        return ResponseEntity.ok(seccionService.listarTodas());
    }

    @GetMapping("/periodo/{periodo}")
    public ResponseEntity<List<Seccion>> listarPorPeriodo(@PathVariable String periodo) {
        return ResponseEntity.ok(seccionService.listarPorPeriodo(periodo));
    }

    @PostMapping
    public ResponseEntity<Seccion> crear(@RequestBody Seccion seccion) {
        return ResponseEntity.ok(seccionService.guardar(seccion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seccion> actualizar(@PathVariable Long id, @RequestBody Seccion seccion) {
        return ResponseEntity.ok(seccionService.actualizar(id, seccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        seccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
