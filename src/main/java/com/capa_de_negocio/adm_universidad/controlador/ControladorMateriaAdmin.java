package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.entidad.Materia;
import com.capa_de_negocio.adm_universidad.servicio.ServicioMateria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/materias")
@PreAuthorize("hasRole('Administrativo')")
public class ControladorMateriaAdmin {

    @Autowired
    private ServicioMateria materiaService;

    @GetMapping
    public ResponseEntity<List<Materia>> listar() {
        return ResponseEntity.ok(materiaService.listarTodas());
    }

    @GetMapping("/carrera/{carreraId}")
    public ResponseEntity<List<Materia>> listarPorCarrera(@PathVariable Long carreraId) {
        return ResponseEntity.ok(materiaService.listarPorCarrera(carreraId));
    }

    @PostMapping
    public ResponseEntity<Materia> crear(@RequestBody Materia materia) {
        return ResponseEntity.ok(materiaService.guardar(materia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> actualizar(@PathVariable Long id, @RequestBody Materia materia) {
        return ResponseEntity.ok(materiaService.actualizar(id, materia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
