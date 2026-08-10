package com.capa_de_negocio.adm_universidad.entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Tabla calificaciones_finales: relacion 1:1 con inscripciones (inscripcion_id es UNIQUE). */
@Entity
@Table(name = "calificaciones_finales")
public class CalificacionFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false, unique = true)
    private Inscripcion inscripcion;

    @Column(name = "nota_numerica", nullable = false, precision = 5, scale = 2)
    private BigDecimal notaNumerica;

    @Column(name = "literal", length = 2)
    private String literal;

    @Column(name = "fecha_publicacion", insertable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }

    public BigDecimal getNotaNumerica() {
        return notaNumerica;
    }

    public void setNotaNumerica(BigDecimal notaNumerica) {
        this.notaNumerica = notaNumerica;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }
}
