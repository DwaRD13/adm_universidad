package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.Material;

public interface MaterialRepositorio extends JpaRepository<Material, Integer> {

    @Query("""
            select mat from Material mat
              join fetch mat.seccion s
              join fetch s.materia m
            where s.id in :seccionIds
            order by mat.fechaSubida desc
            """)
    List<Material> buscarPorSecciones(@Param("seccionIds") List<Integer> seccionIds);
}
