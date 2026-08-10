package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.EntregaTarea;

public interface EntregaTareaRepositorio extends JpaRepository<EntregaTarea, Integer> {

    /**
     * Entregas del estudiante para un conjunto de tareas. Se ordenan de mas nueva a
     * mas vieja para que el servicio se quede con la vigente de cada tarea.
     */
    @Query("""
            select e from EntregaTarea e
            where e.estudiante.id = :estudianteId
              and e.tarea.id in :tareaIds
            order by e.fechaEnvio desc, e.id desc
            """)
    List<EntregaTarea> buscarPorEstudianteYTareas(@Param("estudianteId") Integer estudianteId,
            @Param("tareaIds") List<Integer> tareaIds);
}
