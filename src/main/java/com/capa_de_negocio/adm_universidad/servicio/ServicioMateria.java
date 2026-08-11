package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.entidad.Materia;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.MateriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioMateria {

    @Autowired
    private MateriaRepositorio materiaRepository;

    public List<Materia> listarTodas() {
        return materiaRepository.findAll();
    }

    public List<Materia> listarPorCarrera(Long carreraId) {
        return materiaRepository.findByCarreraId(carreraId);
    }

    public Materia guardar(Materia materia) {
        if (materiaRepository.existsByCodigo(materia.getCodigo())) {
            throw new RecursoNoEncontradoExcepcion("El código de materia ya existe");
        }
        return materiaRepository.save(materia);
    }

    public Materia actualizar(Long id, Materia materia) {
        Materia exist = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Materia no encontrada"));
        exist.setNombre(materia.getNombre());
        exist.setCreditos(materia.getCreditos());
        exist.setCarrera(materia.getCarrera());
        return materiaRepository.save(exist);
    }

    public void eliminar(Long id) {
        materiaRepository.deleteById(id);
    }
}
