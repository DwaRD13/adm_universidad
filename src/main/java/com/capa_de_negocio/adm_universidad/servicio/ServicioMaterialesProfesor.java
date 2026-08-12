package com.capa_de_negocio.adm_universidad.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.MaterialDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.SolicitudMaterial;
import com.capa_de_negocio.adm_universidad.entidad.Material;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.MaterialRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;

/** Materiales que el profesor publica en sus secciones: listado y alta. */
@Service
public class ServicioMaterialesProfesor {

    private final MaterialRepositorio materialRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final ServicioPeriodo servicioPeriodo;

    public ServicioMaterialesProfesor(MaterialRepositorio materialRepositorio,
            SeccionRepositorio seccionRepositorio,
            ServicioPeriodo servicioPeriodo) {
        this.materialRepositorio = materialRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.servicioPeriodo = servicioPeriodo;
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> materiales(Integer profesorId) {
        List<Integer> seccionIds = seccionRepositorio
                .buscarPorProfesorYPeriodo(profesorId, servicioPeriodo.periodoActivo())
                .stream().map(Seccion::getId).toList();

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

    @Transactional
    public MaterialDto crear(Integer profesorId, SolicitudMaterial solicitud) {
        Seccion seccion = seccionRepositorio.findById(solicitud.seccionId())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La sección indicada no existe."));

        if (!seccion.getProfesor().getId().equals(profesorId)) {
            throw new ReglaNegocioExcepcion("Esa sección no pertenece a tus clases.");
        }

        Material material = new Material();
        material.setSeccion(seccion);
        material.setTitulo(solicitud.titulo());
        material.setDescripcion(solicitud.descripcion());
        material.setTipoArchivo(solicitud.tipoArchivo());
        material.setUrlArchivo(solicitud.urlArchivo());


       return aDto(materialRepositorio.save(material));
    }

    @Transactional
    public MaterialDto actualizar(Integer profesorId, Integer materialId, SolicitudMaterial solicitud) {
        Material material = materialDelProfesor(profesorId, materialId);
        Seccion seccion = seccionRepositorio.findById(solicitud.seccionId())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La sección indicada no existe."));

        if (!seccion.getProfesor().getId().equals(profesorId)) {
            throw new ReglaNegocioExcepcion("Esa sección no pertenece a tus clases.");
        }

        material.setSeccion(seccion);
        material.setTitulo(solicitud.titulo());
        material.setDescripcion(solicitud.descripcion());
        material.setTipoArchivo(solicitud.tipoArchivo());
        material.setUrlArchivo(solicitud.urlArchivo());

        return aDto(materialRepositorio.save(material));
    }

    @Transactional
    public void eliminar(Integer profesorId, Integer materialId) {
        materialRepositorio.delete(materialDelProfesor(profesorId, materialId));
    }

    private Material materialDelProfesor(Integer profesorId, Integer materialId) {
        Material material = materialRepositorio.findById(materialId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("El material indicado no existe."));
        if (!material.getSeccion().getProfesor().getId().equals(profesorId)) {
            throw new RecursoNoEncontradoExcepcion("El material indicado no existe.");
        }
        return material;
    }

    private static MaterialDto aDto(Material material) {
        return new MaterialDto(
                material.getId(),
                material.getSeccion().getId(),
                material.getSeccion().getMateria().getNombre(),
                material.getSeccion().getMateria().getCodigo(),
                material.getTitulo(),
                material.getDescripcion(),
                material.getTipoArchivo(),
                material.getUrlArchivo(),
                material.getFechaSubida());
    }

}