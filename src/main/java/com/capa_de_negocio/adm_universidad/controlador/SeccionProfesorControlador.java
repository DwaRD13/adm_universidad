package com.capa_de_negocio.adm_universidad.controlador;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.ResumenDashboardProfesorDto;
import com.capa_de_negocio.adm_universidad.dto.profesor.SeccionProfesorDto;
import com.capa_de_negocio.adm_universidad.seguridad.UsuarioAutenticado;
import com.capa_de_negocio.adm_universidad.servicio.ServicioProfesor;

@RestController
@RequestMapping("/api/profesor")
public class SeccionProfesorControlador {

    private final ServicioProfesor servicioProfesor;

    public SeccionProfesorControlador(ServicioProfesor servicioProfesor) {
        this.servicioProfesor = servicioProfesor;
    }

    @GetMapping("/dashboard")
    public ResumenDashboardProfesorDto dashboard(Authentication authentication) {
        return servicioProfesor.resumen(idDe(authentication));
    }

    @GetMapping("/secciones")
    public List<SeccionProfesorDto> secciones(Authentication authentication) {
        return servicioProfesor.secciones(idDe(authentication));
    }

    @GetMapping("/contactos")
    public List<UsuarioDto> contactos(Authentication authentication) {
        return servicioProfesor.contactos(idDe(authentication));
    }

    /** Extrae el id del profesor autenticado. La usan también los otros controladores de /api/profesor. */
    static Integer idDe(Authentication authentication) {
        return ((UsuarioAutenticado) authentication.getPrincipal()).getUsuario().getId();
    }
}