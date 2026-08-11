package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.Tarea;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepositorio extends JpaRepository<Tarea, Integer> {

    @Query("""
            select t from Tarea t
              join fetch t.seccion s
              join fetch s.materia m
            where s.id in :seccionIds
            order by t.fechaEntrega asc
            """)
    List<Tarea> buscarPorSecciones(@Param("seccionIds") List<Integer> seccionIds);

    @Query("""
            select t from Tarea t
              join fetch t.seccion s
              join fetch s.materia m
            where t.id = :id
            """)
    Optional<Tarea> buscarPorIdCompleta(@Param("id") Integer id);
}
