package com.capa_de_negocio.adm_universidad.repositorio;

import com.capa_de_negocio.adm_universidad.entidad.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepositorio extends JpaRepository<Carrera, Integer> {
    boolean existsByCodigo(String codigo);
}
