package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.InscripcionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioAdminInscripcion {

    @Autowired
    private InscripcionRepositorio inscripcionRepository;

    @Autowired
    private SeccionRepositorio seccionRepository;

    public List<Inscripcion> listarTodas() {
        return inscripcionRepository.findAll();
    }

    public Inscripcion inscribirEstudiante(Inscripcion inscripcion) {
        Long seccionId = Long.valueOf(inscripcion.getSeccion().getId());
        Long estudianteId = Long.valueOf(inscripcion.getEstudiante().getId());

        if (inscripcionRepository.existsByEstudianteIdAndSeccionId(estudianteId, seccionId)) {
            throw new ReglaNegocioExcepcion("El estudiante ya está inscrito en esta sección");
        }

        // 2. Validar cupo disponible
        Seccion seccion = seccionRepository.findById(Math.toIntExact(seccionId))
                .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        long inscritosActuales = inscripcionRepository.countBySeccionIdAndEstado(seccionId, EstadoInscripcion.INSCRITO);

        if (inscritosActuales >= seccion.getCupoMaximo()) {
            throw new RuntimeException("No hay cupo disponible en esta sección");
        }

        return inscripcionRepository.save(inscripcion);
    }

    public void cambiarEstado(Long id, EstadoInscripcion nuevoEstado) {
        Inscripcion inscripcion = inscripcionRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        inscripcion.setEstado(nuevoEstado);
        inscripcionRepository.save(inscripcion);
    }

    public void eliminar(Long id) {
        inscripcionRepository.deleteById(Math.toIntExact(id));
    }
}
