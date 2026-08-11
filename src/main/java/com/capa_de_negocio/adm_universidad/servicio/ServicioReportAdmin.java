package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServicioReportAdmin {

    @Autowired
    private CarreraRepositorio carreraRepository;

    @Autowired
    private MateriaRepositorio materiaRepository;

    @Autowired
    private SeccionRepositorio seccionRepository;

    @Autowired
    private InscripcionRepositorio inscripcionRepository;

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    public Map<String, Object> obtenerResumenAcademicoGlobal() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalCarreras", carreraRepository.count());
        resumen.put("totalMaterias", materiaRepository.count());
        resumen.put("totalSecciones", seccionRepository.count());
        resumen.put("totalInscripciones", inscripcionRepository.count());
        resumen.put("totalEstudiantesActivos", usuarioRepository.countByRolNombre("Estudiante"));
        resumen.put("totalProfesores", usuarioRepository.countByRolNombre("Profesor"));
        return resumen;
    }
}
