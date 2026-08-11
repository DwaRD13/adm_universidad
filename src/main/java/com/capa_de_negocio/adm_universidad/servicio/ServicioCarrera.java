package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.entidad.Carrera;
import com.capa_de_negocio.adm_universidad.repositorio.CarreraRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioCarrera {

    @Autowired
    private CarreraRepositorio carreraRepository;

    public List<Carrera> listarTodas() {
        return carreraRepository.findAll();
    }

    public Carrera guardar(Carrera carrera) {
        if (carreraRepository.existsByCodigo(carrera.getCodigo())) {
            throw new RuntimeException("El código de la carrera ya existe");
        }
        return carreraRepository.save(carrera);
    }

    public Carrera actualizar(Long id, Carrera carrera) {
        Carrera exist = carreraRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));
        exist.setNombre(carrera.getNombre());
        exist.setDescripcion(carrera.getDescripcion());
        exist.setDuracionPeriodos(carrera.getDuracionPeriodos());
        return carreraRepository.save(exist);
    }

    public void eliminar(Long id) {
        carreraRepository.deleteById(Math.toIntExact(id));
    }
}
