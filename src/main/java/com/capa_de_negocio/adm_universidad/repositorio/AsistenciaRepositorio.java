package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.Asistencia;

public interface AsistenciaRepositorio extends JpaRepository<Asistencia, Integer> {

    /** Todos los registros del estudiante, ordenados de la clase mas reciente a la mas antigua. */
    @Query("""
            select a from Asistencia a
              join fetch a.inscripcion i
            where i.id in :inscripcionIds
            order by a.fecha desc
            """)
    List<Asistencia> buscarPorInscripciones(@Param("inscripcionIds") List<Integer> inscripcionIds);
}
