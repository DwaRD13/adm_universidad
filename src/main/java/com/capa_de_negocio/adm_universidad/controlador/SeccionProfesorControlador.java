package com.capa_de_negocio.adm_universidad.controlador;

import java.time.LocalDate;
import java.util.List;

import com.capa_de_negocio.adm_universidad.dto.profesor.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
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

    @GetMapping("/materias")
    public List<MateriaProfesorDto> materias(
            Authentication authentication) {

        return servicioProfesor.materias(
                idDe(authentication));
    }

    @GetMapping("/asistencia")
    public List<AsistenciaProfesorDto> asistencia(
            Authentication authentication) {

        return servicioProfesor.asistencia(
                idDe(authentication));
    }

    @GetMapping("/secciones/{seccionId}/estudiantes")
    public List<EstudianteAsistenciaDto> estudiantes(
            @PathVariable Integer seccionId,
            @RequestParam(required = false) LocalDate fecha,
            Authentication authentication) {

        return servicioProfesor.estudiantesSeccion(
                idDe(authentication),
                seccionId,
                fecha == null
                        ? LocalDate.now()
                        : fecha);
    }

    @GetMapping("/secciones/{seccionId}/calificaciones")
    public List<EstudianteCalificacionDto> calificacionesSeccion(
            @PathVariable Integer seccionId,
            Authentication authentication) {

        return servicioProfesor.calificacionesSeccion(
                idDe(authentication),
                seccionId);
    }

    @PostMapping("/asistencia")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registrarAsistencia(
            @RequestBody RegistrarAsistenciaDto dto,
            Authentication authentication) {

        servicioProfesor.registrarAsistencia(
                idDe(authentication),
                dto);
    }

    @PostMapping("/calificaciones")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registrarCalificaciones(
            @RequestBody RegistrarCalificacionesDto dto,
            Authentication authentication) {

        servicioProfesor.registrarCalificaciones(
                idDe(authentication),
                dto);
    }

    @GetMapping("/calificaciones")
    public List<CalificacionProfesorDto> calificaciones(
            Authentication authentication) {

        return servicioProfesor.calificaciones(
                idDe(authentication));
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