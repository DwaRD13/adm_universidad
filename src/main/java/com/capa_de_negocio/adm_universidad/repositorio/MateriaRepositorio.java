package com.capa_de_negocio.adm_universidad.repositorio;

import com.capa_de_negocio.adm_universidad.entidad.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepositorio extends JpaRepository<Materia, Long> {
    List<Materia> findByCarreraId(Long carreraId);
    boolean existsByCodigo(String codigo);
}
