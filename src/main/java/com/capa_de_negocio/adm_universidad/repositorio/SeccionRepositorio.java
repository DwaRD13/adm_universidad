package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.Seccion;

public interface SeccionRepositorio extends JpaRepository<Seccion, Integer> {

    /** Catalogo de inscripcion: secciones abiertas del periodo, con materia, carrera y profesor. */
    @Query("""
            select s from Seccion s
              join fetch s.materia m
              left join fetch m.carrera
              join fetch s.profesor p
            where s.periodo = :periodo
              and s.estado = com.capa_de_negocio.adm_universidad.entidad.EstadoSeccion.ABIERTA
            order by m.nombre
            """)
    List<Seccion> buscarAbiertasPorPeriodo(@Param("periodo") String periodo);

    @Query("""
            select s from Seccion s
              join fetch s.materia m
              left join fetch m.carrera
              join fetch s.profesor p
            where s.id = :id
            """)
    Optional<Seccion> buscarPorIdCompleta(@Param("id") Integer id);

    /** Reserva cuando configuracion_sistema no tiene definido el periodo activo. */
    @Query("select max(s.periodo) from Seccion s")
    Optional<String> buscarPeriodoMasReciente();
    
    @org.springframework.data.jpa.repository.Query(
    "SELECT s FROM Seccion s JOIN FETCH s.materia m WHERE s.profesor.id = :profesorId AND s.periodo = :periodo ORDER BY m.nombre")
List<com.capa_de_negocio.adm_universidad.entidad.Seccion> buscarPorProfesorYPeriodo(
        @org.springframework.data.repository.query.Param("profesorId") Integer profesorId,
        @org.springframework.data.repository.query.Param("periodo") String periodo);


}
