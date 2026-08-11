package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.CalificacionFinal;
import org.springframework.stereotype.Repository;

@Repository
public interface CalificacionFinalRepositorio extends JpaRepository<CalificacionFinal, Integer> {

    /** Notas publicadas del estudiante, con la materia y el periodo ya resueltos. */
    @Query("""
            select c from CalificacionFinal c
              join fetch c.inscripcion i
              join fetch i.seccion s
              join fetch s.materia m
            where i.estudiante.id = :estudianteId
            order by s.periodo desc, m.nombre
            """)
    List<CalificacionFinal> buscarPorEstudiante(@Param("estudianteId") Integer estudianteId);
}
