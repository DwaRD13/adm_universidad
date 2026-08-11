package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.entidad.Carrera;
import com.capa_de_negocio.adm_universidad.servicio.ServicioCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/carreras")
@PreAuthorize("hasRole('Administrativo')")
public class ControladorAdminCarrera {

    @Autowired
    private ServicioCarrera carreraService;

    @GetMapping
    public ResponseEntity<List<Carrera>> listar() {
        return ResponseEntity.ok(carreraService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Carrera> crear(@RequestBody Carrera carrera) {
        return ResponseEntity.ok(carreraService.guardar(carrera));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrera> actualizar(@PathVariable Long id, @RequestBody Carrera carrera) {
        return ResponseEntity.ok(carreraService.actualizar(id, carrera));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
