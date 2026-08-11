package com.capa_de_negocio.adm_universidad.controlador;

import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.servicio.ServicioAdminInscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inscripciones")
@PreAuthorize("hasRole('Administrativo')")
public class ControladorInscripcionAdmin {

    @Autowired
    private ServicioAdminInscripcion inscripcionService;

    @GetMapping
    public ResponseEntity<List<Inscripcion>> listar() {
        return ResponseEntity.ok(inscripcionService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Inscripcion> inscribir(@RequestBody Inscripcion inscripcion) {
        return ResponseEntity.ok(inscripcionService.inscribirEstudiante(inscripcion));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoInscripcion estado) {
        inscripcionService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
