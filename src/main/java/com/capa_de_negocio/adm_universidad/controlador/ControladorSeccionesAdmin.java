package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.dto.administrador.CrearSeccionRequest;
import com.capa_de_negocio.adm_universidad.dto.administrador.SeccionDTO;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.servicio.ServicioSeccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/secciones")
public class ControladorSeccionesAdmin {

    @Autowired
    private ServicioSeccion seccionService;

    @GetMapping
    public ResponseEntity<List<SeccionDTO>> listar() {
        return ResponseEntity.ok(seccionService.listarTodas());
    }
    @GetMapping("/{id}")
    public ResponseEntity<SeccionDTO> getPorId(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(seccionService.getById(id));
    }

    @GetMapping("/periodo/{periodo}")
    public ResponseEntity<List<Seccion>> listarPorPeriodo(@PathVariable String periodo) {
        return ResponseEntity.ok(seccionService.listarPorPeriodo(periodo));
    }

    @PostMapping
    public ResponseEntity<SeccionDTO> crear(@RequestBody CrearSeccionRequest request) {
        return ResponseEntity.ok(seccionService.guardarDesdeRequest(request));
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
