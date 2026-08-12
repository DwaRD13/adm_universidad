package com.capa_de_negocio.adm_universidad.dto.administrador;

public class CrearSeccionRequest {
    private Integer materiaId;
    private Integer profesorId;
    private String periodo;
    private Integer cupoMaximo;
    private String aula;
    private String horarioDescripcion;
    private String estado;

    public Integer getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Integer materiaId) {
        this.materiaId = materiaId;
    }

    public Integer getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Integer profesorId) {
        this.profesorId = profesorId;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
