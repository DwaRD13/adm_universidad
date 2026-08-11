package com.capa_de_negocio.adm_universidad.servicio;

import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServicioDashboardAdmin {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    public Map<String, Long> obtenerEstadisticasPrincipales() {
        Map<String, Long> estadisticas = new HashMap<>();

        estadisticas.put("totalUsuarios", usuarioRepository.count());
        estadisticas.put("totalEstudiantes", usuarioRepository.countByRolNombre("Estudiante"));
        estadisticas.put("totalProfesores", usuarioRepository.countByRolNombre("Profesor"));
        estadisticas.put("totalAdministrativos", usuarioRepository.countByRolNombre("Administrativo"));

        return estadisticas;
    }
}
