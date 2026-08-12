package com.capa_de_negocio.adm_universidad.repositorio;

import com.capa_de_negocio.adm_universidad.entidad.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {

    Rol findRolByNombre(String nombre);
    Rol findRolById(Integer rolId);

}
