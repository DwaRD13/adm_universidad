package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;

public interface InscripcionRepositorio extends JpaRepository<Inscripcion, Integer> {

    /**
     * Inscripciones vigentes (no retiradas) de un estudiante en un periodo, con toda
     * la cadena seccion -> materia -> profesor resuelta en una sola consulta.
     */
    @Query("""
            select i from Inscripcion i
              join fetch i.seccion s
              join fetch s.materia m
              left join fetch m.carrera
              join fetch s.profesor p
            where i.estudiante.id = :estudianteId
              and s.periodo = :periodo
              and i.estado <> com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion.RETIRADO
            order by m.nombre
            """)
    List<Inscripcion> buscarVigentesPorEstudianteYPeriodo(@Param("estudianteId") Integer estudianteId,
            @Param("periodo") String periodo);

    /** Historial completo del estudiante, incluidas las secciones de periodos anteriores. */
    @Query("""
            select i from Inscripcion i
              join fetch i.seccion s
              join fetch s.materia m
              left join fetch m.carrera
              join fetch s.profesor p
            where i.estudiante.id = :estudianteId
            order by s.periodo desc, m.nombre
            """)
    List<Inscripcion> buscarTodasPorEstudiante(@Param("estudianteId") Integer estudianteId);

    Optional<Inscripcion> findByIdAndEstudianteId(Integer id, Integer estudianteId);

    Optional<Inscripcion> findByEstudianteIdAndSeccionId(Integer estudianteId, Integer seccionId);

    /** Cupo ocupado: las inscripciones retiradas liberan plaza. */
    @Query("""
            select i.seccion.id, count(i) from Inscripcion i
            where i.seccion.id in :seccionIds
              and i.estado <> com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion.RETIRADO
            group by i.seccion.id
            """)
    List<Object[]> contarOcupadasPorSeccion(@Param("seccionIds") List<Integer> seccionIds);

    long countBySeccionIdAndEstadoNot(Integer seccionId,
        com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion estado);

@org.springframework.data.jpa.repository.Query(
    "SELECT i FROM Inscripcion i JOIN FETCH i.estudiante WHERE i.seccion.id IN :seccionIds AND i.estado <> :excluido")
List<com.capa_de_negocio.adm_universidad.entidad.Inscripcion> buscarPorSeccionesVigentes(
        @org.springframework.data.repository.query.Param("seccionIds") List<Integer> seccionIds,
        @org.springframework.data.repository.query.Param("excluido") com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion excluido);




}
