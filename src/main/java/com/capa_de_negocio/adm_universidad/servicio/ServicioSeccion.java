package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.dto.administrador.CrearSeccionRequest;
import com.capa_de_negocio.adm_universidad.dto.administrador.SeccionDTO;
import com.capa_de_negocio.adm_universidad.entidad.EstadoSeccion;
import com.capa_de_negocio.adm_universidad.entidad.Materia;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.repositorio.MateriaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioSeccion {

    @Autowired
    private SeccionRepositorio seccionRepository;

    @Autowired
    private MateriaRepositorio materiaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public List<SeccionDTO> listarTodas() {
        return seccionRepository.findAllConMateriaYProfesor().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional
    public SeccionDTO getById(Long id) {
        return convertirADTO(seccionRepository.findById(id));
    }

    public List<Seccion> listarPorPeriodo(String periodo) {
        return seccionRepository.findByPeriodo(periodo);
    }

    public SeccionDTO guardarDesdeRequest(CrearSeccionRequest req) {
        Materia materia = materiaRepositorio.findById(Long.valueOf(req.getMateriaId()))
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        Usuario profesor = usuarioRepositorio.findById(req.getProfesorId())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Seccion seccion = new Seccion();
        seccion.setMateria(materia);
        seccion.setProfesor(profesor);
        seccion.setPeriodo(req.getPeriodo());
        seccion.setCupoMaximo(req.getCupoMaximo());
        seccion.setAula(req.getAula());
        seccion.setHorarioDescripcion(req.getHorarioDescripcion());
        seccion.setEstado(EstadoSeccion.valueOf(
                (req.getEstado() != null ? req.getEstado() : "ABIERTA").toUpperCase()
        ));

        Seccion guardada = seccionRepository.save(seccion);
        return convertirADTO(guardada);
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

    private SeccionDTO convertirADTO(Seccion s) {
        SeccionDTO dto = new SeccionDTO();
        dto.setId(s.getId());
        if (s.getMateria() != null) {
            dto.setMateriaId(s.getMateria().getId());
            dto.setMateriaNombre(s.getMateria().getNombre());
        }
        if (s.getProfesor() != null) {
            dto.setProfesorId(s.getProfesor().getId());
            dto.setProfesorNombre(s.getProfesor().getNombres() + " " + s.getProfesor().getApellidos());
        }
        dto.setPeriodo(s.getPeriodo());
        dto.setCupoMaximo(s.getCupoMaximo());
        dto.setAula(s.getAula());
        dto.setHorarioDescripcion(s.getHorarioDescripcion());
        dto.setEstado(s.getEstado().name());
        dto.setInscritos(s.getCantidadInscritos() != null ? s.getCantidadInscritos() : 0);
        return dto;
    }
}
