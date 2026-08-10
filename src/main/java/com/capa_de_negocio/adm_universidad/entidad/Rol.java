package com.capa_de_negocio.adm_universidad.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Tabla roles: 'Administrativo', 'Profesor', 'Estudiante'. */
@Entity
@Table(name = "roles")
public class Rol {

    /** Nombres tal como estan sembrados en la tabla; se usan como authority de Spring Security. */
    public static final String ADMINISTRATIVO = "Administrativo";
    public static final String PROFESOR = "Profesor";
    public static final String ESTUDIANTE = "Estudiante";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
