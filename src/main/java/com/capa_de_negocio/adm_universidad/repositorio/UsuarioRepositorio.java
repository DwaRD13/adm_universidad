package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    /** El rol se trae en la misma consulta porque el login siempre lo necesita. */
    @Query("select u from Usuario u join fetch u.rol where u.email = :email")
    Optional<Usuario> buscarPorEmailConRol(String email);

    @Query("select u from Usuario u join fetch u.rol where u.id = :id")
    Optional<Usuario> buscarPorIdConRol(Integer id);

    boolean existsByEmail(String email);

    long countByRolNombre(String nombreRol);

    List<Usuario> findByRolNombre(String nombreRol);


}
