package com.capa_de_negocio.adm_universidad.controlador;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capa_de_negocio.adm_universidad.dto.estudiante.MaterialDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.SolicitudMaterial;
import com.capa_de_negocio.adm_universidad.servicio.ServicioMaterialesProfesor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profesor/materiales")
public class MaterialProfesorControlador {

    private final ServicioMaterialesProfesor servicioMaterialesProfesor;

    public MaterialProfesorControlador(ServicioMaterialesProfesor servicioMaterialesProfesor) {
        this.servicioMaterialesProfesor = servicioMaterialesProfesor;
    }

    @GetMapping
    public List<MaterialDto> materiales(Authentication authentication) {
        return servicioMaterialesProfesor.materiales(SeccionProfesorControlador.idDe(authentication));
    }

    @PostMapping
    public MaterialDto crear(Authentication authentication, @Valid @RequestBody SolicitudMaterial solicitud) {
        return servicioMaterialesProfesor.crear(SeccionProfesorControlador.idDe(authentication), solicitud);
    }

    @PutMapping("/{materialId}")
    public MaterialDto actualizar(Authentication authentication, @PathVariable Integer materialId,
            @Valid @RequestBody SolicitudMaterial solicitud) {
        return servicioMaterialesProfesor.actualizar(
                SeccionProfesorControlador.idDe(authentication), materialId, solicitud);
    }

    @DeleteMapping("/{materialId}")
    public void eliminar(Authentication authentication, @PathVariable Integer materialId) {
        servicioMaterialesProfesor.eliminar(SeccionProfesorControlador.idDe(authentication), materialId);
    }
}