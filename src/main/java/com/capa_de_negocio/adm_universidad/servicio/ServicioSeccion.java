package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioSeccion {

    @Autowired
    private SeccionRepositorio seccionRepository;

    public List<Seccion> listarTodas() {
        return seccionRepository.findAll();
    }

    public List<Seccion> listarPorPeriodo(String periodo) {
        return seccionRepository.findByPeriodo(periodo);
    }

    public Seccion guardar(Seccion seccion) {
        return seccionRepository.save(seccion);
    }

    public Seccion actualizar(Long id, Seccion seccion) {
        Seccion exist = seccionRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Sección no encontrada"));
        exist.setAula(seccion.getAula());
        exist.setCupoMaximo(seccion.getCupoMaximo());
        exist.setHorarioDescripcion(seccion.getHorarioDescripcion());
        exist.setEstado(seccion.getEstado());
        exist.setProfesor(seccion.getProfesor());
        exist.setPeriodo(seccion.getPeriodo());
        return seccionRepository.save(exist);
    }

    public void eliminar(Long id) {
        seccionRepository.deleteById(Math.toIntExact(id));
    }
}
