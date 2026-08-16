package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

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
    @Query("""
        select a from Asistencia a
          join fetch a.inscripcion i
          join fetch i.estudiante
          join fetch i.seccion s
          join fetch s.materia
        where s.id in :seccionIds
        """)
    List<Asistencia> buscarPorSecciones(
            @Param("seccionIds") List<Integer> seccionIds);

    Optional<Asistencia> findByInscripcionIdAndFecha(
            Integer inscripcionId,
            LocalDate fecha);
}
