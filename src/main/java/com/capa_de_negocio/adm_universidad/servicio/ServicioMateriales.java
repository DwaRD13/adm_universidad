package com.capa_de_negocio.adm_universidad.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.MaterialDto;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.repositorio.MaterialRepositorio;

/** Materiales de apoyo de las secciones del estudiante. Solo lectura. */
@Service
public class ServicioMateriales {

    private final MaterialRepositorio materialRepositorio;
    private final ServicioInscripcion servicioInscripcion;

    public ServicioMateriales(MaterialRepositorio materialRepositorio,
            ServicioInscripcion servicioInscripcion) {
        this.materialRepositorio = materialRepositorio;
        this.servicioInscripcion = servicioInscripcion;
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> materiales(Integer estudianteId) {
        List<Integer> seccionIds = servicioInscripcion.inscripcionesVigentes(estudianteId).stream()
                .map(Inscripcion::getSeccion)
                .map(Seccion::getId)
                .toList();

        if (seccionIds.isEmpty()) {
            return List.of();
        }

        return materialRepositorio.buscarPorSecciones(seccionIds).stream()
                .map(material -> new MaterialDto(
                        material.getId(),
                        material.getSeccion().getId(),
                        material.getSeccion().getMateria().getNombre(),
                        material.getSeccion().getMateria().getCodigo(),
                        material.getTitulo(),
                        material.getDescripcion(),
                        material.getTipoArchivo(),
                        material.getUrlArchivo(),
                        material.getFechaSubida()))
                .toList();
    }
}
