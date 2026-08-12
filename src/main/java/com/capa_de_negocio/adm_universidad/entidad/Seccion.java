package com.capa_de_negocio.adm_universidad.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Formula;

/** Tabla secciones: materia + profesor + periodo (ej. '2026-C3'). */
@Entity
@Table(name = "secciones")
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profesor_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario profesor;

    @Column(name = "periodo", nullable = false, length = 20)
    private String periodo;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Column(name = "aula", length = 50)
    private String aula;

    /** Texto libre tipo 'Lu-Mi 18:00 - 20:00'; el backend lo interpreta para la vista de horario. */
    @Column(name = "horario_descripcion", length = 150)
    private String horarioDescripcion;

    @Column(name = "estado")
    private EstadoSeccion estado;

    @Formula("(SELECT COUNT(*) FROM inscripciones i WHERE i.seccion_id = id AND i.estado = 'Inscrito')")
    private Integer cantidadInscritos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Usuario getProfesor() {
        return profesor;
    }

    public void setProfesor(Usuario profesor) {
        this.profesor = profesor;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getHorarioDescripcion() {
        return horarioDescripcion;
    }

    public void setHorarioDescripcion(String horarioDescripcion) {
        this.horarioDescripcion = horarioDescripcion;
    }

    public EstadoSeccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSeccion estado) {
        this.estado = estado;
    }

    public Integer getCantidadInscritos() {
        return cantidadInscritos;
    }

    public void setCantidadInscritos(Integer cantidadInscritos) {
        this.cantidadInscritos = cantidadInscritos;
    }
}
