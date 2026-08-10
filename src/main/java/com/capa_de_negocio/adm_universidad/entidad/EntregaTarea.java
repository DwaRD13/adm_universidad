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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Tabla entregas_tareas. No hay UNIQUE(tarea, estudiante): el esquema permite
 * varias entregas de la misma tarea, y se considera vigente la mas reciente.
 */
@Entity
@Table(name = "entregas_tareas")
public class EntregaTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarea_id", nullable = false)
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @Column(name = "archivo_url", nullable = false, length = 255)
    private String archivoUrl;

    /**
     * MySQL tiene DEFAULT CURRENT_TIMESTAMP, pero la aplicacion la escribe explicitamente:
     * al reenviar una entrega hay que actualizar la fecha, y ademas asi la respuesta del
     * POST ya trae el dato relleno.
     */
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "calificacion", precision = 5, scale = 2)
    private BigDecimal calificacion;

    @Column(name = "comentarios_profesor", columnDefinition = "TEXT")
    private String comentariosProfesor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Usuario estudiante) {
        this.estudiante = estudiante;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentariosProfesor() {
        return comentariosProfesor;
    }

    public void setComentariosProfesor(String comentariosProfesor) {
        this.comentariosProfesor = comentariosProfesor;
    }
}
