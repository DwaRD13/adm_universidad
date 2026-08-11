package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capa_de_negocio.adm_universidad.entidad.ConfiguracionSistema;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionSistemaRepositorio extends JpaRepository<ConfiguracionSistema, Integer> {

    Optional<ConfiguracionSistema> findByClave(String clave);
}
