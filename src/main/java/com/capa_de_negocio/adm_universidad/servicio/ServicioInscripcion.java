package com.capa_de_negocio.adm_universidad.servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.ClaseHorarioDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.InscripcionDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.SeccionDisponibleDto;
import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;
import com.capa_de_negocio.adm_universidad.entidad.EstadoSeccion;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;
import com.capa_de_negocio.adm_universidad.repositorio.InscripcionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.UsuarioRepositorio;
import com.capa_de_negocio.adm_universidad.servicio.ParseadorHorario.Horario;

/**
 * Horario, catalogo de secciones e inscripcion/retiro del estudiante.
 *
 * <p>Todos los metodos reciben el id del estudiante autenticado desde el controlador;
 * ninguno acepta un id arbitrario de la URL. Ese es el limite de aislamiento de datos.
 */
@Service
public class ServicioInscripcion {

    private final InscripcionRepositorio inscripcionRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ServicioPeriodo servicioPeriodo;

    public ServicioInscripcion(InscripcionRepositorio inscripcionRepositorio,
            SeccionRepositorio seccionRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            ServicioPeriodo servicioPeriodo) {
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.servicioPeriodo = servicioPeriodo;
    }

    /** Inscripciones vigentes del periodo activo. Base de horario, tareas y materiales. */
    @Transactional(readOnly = true)
    public List<Inscripcion> inscripcionesVigentes(Integer estudianteId) {
        return inscripcionRepositorio.buscarVigentesPorEstudianteYPeriodo(
                estudianteId, servicioPeriodo.periodoActivo());
    }

    @Transactional(readOnly = true)
    public List<ClaseHorarioDto> horario(Integer estudianteId) {
        List<ClaseHorarioDto> clases = new ArrayList<>();

        for (Inscripcion inscripcion : inscripcionesVigentes(estudianteId)) {
            Seccion seccion = inscripcion.getSeccion();
            Horario horario = ParseadorHorario.analizar(seccion.getHorarioDescripcion());

            clases.add(new ClaseHorarioDto(
                    inscripcion.getId(),
                    seccion.getId(),
                    seccion.getMateria().getNombre(),
                    seccion.getMateria().getCodigo(),
                    seccion.getMateria().getCreditos(),
                    seccion.getProfesor().getNombreCompleto(),
                    seccion.getAula(),
                    seccion.getPeriodo(),
                    seccion.getHorarioDescripcion(),
                    horario.dias(),
                    horario.horaInicio(),
                    horario.horaFin()));
        }

        // Ordenadas por hora de inicio: asi el cliente pinta el dia de arriba abajo sin reordenar.
        clases.sort((a, b) -> {
            String horaA = a.horaInicio() == null ? "99:99" : a.horaInicio();
            String horaB = b.horaInicio() == null ? "99:99" : b.horaInicio();
            return horaA.compareTo(horaB);
        });

        return clases;
    }

    @Transactional(readOnly = true)
    public List<InscripcionDto> misInscripciones(Integer estudianteId) {
        return inscripcionRepositorio.buscarTodasPorEstudiante(estudianteId).stream()
                .map(ServicioInscripcion::aDto)
                .toList();
    }

    /** Catalogo del periodo activo con el cupo real y la marca de ya-inscrito. */
    @Transactional(readOnly = true)
    public List<SeccionDisponibleDto> seccionesDisponibles(Integer estudianteId) {
        String periodo = servicioPeriodo.periodoActivo();
        List<Seccion> secciones = seccionRepositorio.buscarAbiertasPorPeriodo(periodo);
        if (secciones.isEmpty()) {
            return List.of();
        }

        Map<Integer, Integer> ocupadasPorSeccion = contarOcupadas(
                secciones.stream().map(Seccion::getId).toList());

        Set<Integer> yaInscritas = new HashSet<>();
        for (Inscripcion inscripcion : inscripcionRepositorio.buscarTodasPorEstudiante(estudianteId)) {
            if (inscripcion.getEstado() == null || inscripcion.getEstado().estaVigente()) {
                yaInscritas.add(inscripcion.getSeccion().getId());
            }
        }

        List<SeccionDisponibleDto> disponibles = new ArrayList<>();
        for (Seccion seccion : secciones) {
            int ocupadas = ocupadasPorSeccion.getOrDefault(seccion.getId(), 0);
            disponibles.add(new SeccionDisponibleDto(
                    seccion.getId(),
                    seccion.getMateria().getNombre(),
                    seccion.getMateria().getCodigo(),
                    seccion.getMateria().getCreditos(),
                    seccion.getMateria().getCarrera() == null
                            ? null
                            : seccion.getMateria().getCarrera().getNombre(),
                    seccion.getProfesor().getNombreCompleto(),
                    seccion.getAula(),
                    seccion.getPeriodo(),
                    seccion.getHorarioDescripcion(),
                    seccion.getCupoMaximo(),
                    ocupadas,
                    Math.max(0, seccion.getCupoMaximo() - ocupadas),
                    yaInscritas.contains(seccion.getId())));
        }

        return disponibles;
    }

    @Transactional
    public InscripcionDto inscribir(Integer estudianteId, Integer seccionId) {
        Seccion seccion = seccionRepositorio.buscarPorIdCompleta(seccionId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("La sección indicada no existe."));

        if (seccion.getEstado() != EstadoSeccion.ABIERTA) {
            throw new ReglaNegocioExcepcion(
                    "La sección no admite inscripciones porque está " + seccion.getEstado().getValor() + ".");
        }

        var existente = inscripcionRepositorio.findByEstudianteIdAndSeccionId(estudianteId, seccionId);
        if (existente.isPresent() && existente.get().getEstado() != EstadoInscripcion.RETIRADO) {
            throw new ReglaNegocioExcepcion("Ya estás inscrito en esta sección.");
        }

        int ocupadas = contarOcupadas(List.of(seccionId)).getOrDefault(seccionId, 0);
        if (ocupadas >= seccion.getCupoMaximo()) {
            throw new ReglaNegocioExcepcion("La sección ya no tiene cupo disponible.");
        }

        // Si estuvo retirada se reactiva la fila: el UNIQUE(estudiante, seccion) impide crear otra.
        Inscripcion inscripcion = existente.orElseGet(() -> {
            Usuario estudiante = usuarioRepositorio.getReferenceById(estudianteId);
            Inscripcion nueva = new Inscripcion();
            nueva.setEstudiante(estudiante);
            nueva.setSeccion(seccion);
            return nueva;
        });
        inscripcion.setEstado(EstadoInscripcion.INSCRITO);

        return aDto(inscripcionRepositorio.save(inscripcion));
    }

    /** Retirarse no borra la fila: cambia el estado a Retirado y conserva el historial. */
    @Transactional
    public InscripcionDto retirar(Integer estudianteId, Integer inscripcionId) {
        Inscripcion inscripcion = inscripcionRepositorio
                .findByIdAndEstudianteId(inscripcionId, estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion(
                        "No encontramos esa inscripción en tu expediente."));

        if (inscripcion.getEstado() == EstadoInscripcion.RETIRADO) {
            throw new ReglaNegocioExcepcion("Ya te habías retirado de esta sección.");
        }
        if (inscripcion.getEstado() == EstadoInscripcion.APROBADO
                || inscripcion.getEstado() == EstadoInscripcion.REPROBADO) {
            throw new ReglaNegocioExcepcion(
                    "No puedes retirarte de una materia que ya tiene calificación final.");
        }

        inscripcion.setEstado(EstadoInscripcion.RETIRADO);
        return aDto(inscripcionRepositorio.save(inscripcion));
    }

    private Map<Integer, Integer> contarOcupadas(List<Integer> seccionIds) {
        Map<Integer, Integer> conteo = new HashMap<>();
        for (Object[] fila : inscripcionRepositorio.contarOcupadasPorSeccion(seccionIds)) {
            conteo.put((Integer) fila[0], ((Number) fila[1]).intValue());
        }
        return conteo;
    }

    static InscripcionDto aDto(Inscripcion inscripcion) {
        Seccion seccion = inscripcion.getSeccion();
        return new InscripcionDto(
                inscripcion.getId(),
                seccion.getId(),
                seccion.getMateria().getNombre(),
                seccion.getMateria().getCodigo(),
                seccion.getMateria().getCreditos(),
                seccion.getProfesor().getNombreCompleto(),
                seccion.getAula(),
                seccion.getPeriodo(),
                seccion.getHorarioDescripcion(),
                inscripcion.getEstado() == null ? null : inscripcion.getEstado().getValor(),
                inscripcion.getFechaInscripcion());
    }
}
