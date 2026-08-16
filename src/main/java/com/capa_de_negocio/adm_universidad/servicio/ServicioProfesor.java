package com.capa_de_negocio.adm_universidad.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import com.capa_de_negocio.adm_universidad.dto.profesor.*;
import com.capa_de_negocio.adm_universidad.entidad.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.repositorio.EntregaTareaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.InscripcionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.TareaRepositorio;
import com.capa_de_negocio.adm_universidad.servicio.ParseadorHorario.Horario;
import com.capa_de_negocio.adm_universidad.repositorio.AsistenciaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.CalificacionFinalRepositorio;
import com.capa_de_negocio.adm_universidad.entidad.CalificacionFinal;

/** Secciones del profesor, resumen del dashboard y directorio de sus estudiantes. */
@Service
public class ServicioProfesor {

    private final SeccionRepositorio seccionRepositorio;
    private final InscripcionRepositorio inscripcionRepositorio;
    private final TareaRepositorio tareaRepositorio;
    private final EntregaTareaRepositorio entregaRepositorio;
    private final AsistenciaRepositorio asistenciaRepositorio;
    private final CalificacionFinalRepositorio calificacionRepositorio;
    private final ServicioMensajes servicioMensajes;
    private final ServicioPeriodo servicioPeriodo;

    public ServicioProfesor(SeccionRepositorio seccionRepositorio,
            InscripcionRepositorio inscripcionRepositorio,
            TareaRepositorio tareaRepositorio,
            EntregaTareaRepositorio entregaRepositorio,
            AsistenciaRepositorio asistenciaRepositorio,
            CalificacionFinalRepositorio calificacionRepositorio,
            ServicioMensajes servicioMensajes,
            ServicioPeriodo servicioPeriodo) {
        this.seccionRepositorio = seccionRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.tareaRepositorio = tareaRepositorio;
        this.entregaRepositorio = entregaRepositorio;
        this.asistenciaRepositorio = asistenciaRepositorio;
        this.calificacionRepositorio = calificacionRepositorio;
        this.servicioMensajes = servicioMensajes;
        this.servicioPeriodo = servicioPeriodo;
    }

    @Transactional(readOnly = true)
    public List<SeccionProfesorDto> secciones(Integer profesorId) {
        String periodo = servicioPeriodo.periodoActivo();
        return seccionRepositorio.buscarPorProfesorYPeriodo(profesorId, periodo)
                .stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<MateriaProfesorDto> materias(Integer profesorId) {

        return secciones(profesorId)
                .stream()
                .map(seccion -> new MateriaProfesorDto(
                        seccion.seccionId(),
                        seccion.materia(),
                        seccion.codigoMateria(),
                        seccion.creditos(),
                        seccion.periodo(),
                        seccion.aula(),
                        seccion.estado(),
                        seccion.inscritos()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsistenciaProfesorDto> asistencia(Integer profesorId) {

        List<SeccionProfesorDto> secciones = secciones(profesorId);

        if (secciones.isEmpty()) {
            return List.of();
        }

        List<Integer> seccionIds = secciones.stream()
                .map(SeccionProfesorDto::seccionId)
                .toList();

        List<Asistencia> asistencias =
                asistenciaRepositorio.buscarPorSecciones(seccionIds);

        return secciones.stream()
                .map(seccion -> {

                    List<Asistencia> registros = asistencias.stream()
                            .filter(a -> a.getInscripcion()
                                    .getSeccion()
                                    .getId()
                                    .equals(seccion.seccionId()))
                            .toList();

                    int totalRegistros = registros.size();

                    long asistidos = registros.stream()
                            .filter(a -> a.getEstado().cuentaComoAsistido())
                            .count();

                    BigDecimal porcentaje = null;

                    if (totalRegistros > 0) {
                        porcentaje = BigDecimal.valueOf(asistidos)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(
                                        BigDecimal.valueOf(totalRegistros),
                                        1,
                                        RoundingMode.HALF_UP);
                    }

                    return new AsistenciaProfesorDto(
                            seccion.seccionId(),
                            seccion.materia(),
                            seccion.codigoMateria(),
                            seccion.inscritos(),
                            porcentaje);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CalificacionProfesorDto> calificaciones(Integer profesorId) {

        List<SeccionProfesorDto> secciones = secciones(profesorId);

        if (secciones.isEmpty()) {
            return List.of();
        }

        List<Integer> seccionIds = secciones.stream()
                .map(SeccionProfesorDto::seccionId)
                .toList();

        List<CalificacionFinal> calificaciones =
                calificacionRepositorio.buscarPorSecciones(seccionIds);

        return secciones.stream()
                .map(seccion -> {

                    List<CalificacionFinal> notas = calificaciones.stream()
                            .filter(c -> c.getInscripcion()
                                    .getSeccion()
                                    .getId()
                                    .equals(seccion.seccionId()))
                            .toList();

                    int aprobados = (int) notas.stream()
                            .filter(c -> c.getInscripcion().getEstado()
                                    == EstadoInscripcion.APROBADO)
                            .count();

                    int reprobados = (int) notas.stream()
                            .filter(c -> c.getInscripcion().getEstado()
                                    == EstadoInscripcion.REPROBADO)
                            .count();

                    BigDecimal promedio = null;

                    if (!notas.isEmpty()) {

                        BigDecimal suma = notas.stream()
                                .map(CalificacionFinal::getNotaNumerica)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        promedio = suma.divide(
                                BigDecimal.valueOf(notas.size()),
                                2,
                                RoundingMode.HALF_UP);
                    }

                    return new CalificacionProfesorDto(
                            seccion.seccionId(),
                            seccion.materia(),
                            seccion.codigoMateria(),
                            seccion.inscritos(),
                            aprobados,
                            reprobados,
                            promedio);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstudianteAsistenciaDto> estudiantesSeccion(
            Integer profesorId,
            Integer seccionId,
            LocalDate fecha) {

        boolean pertenece = secciones(profesorId)
                .stream()
                .anyMatch(s -> s.seccionId().equals(seccionId));

        if (!pertenece) {
            throw new IllegalArgumentException(
                    "La sección no pertenece al profesor");
        }
        LocalDate hoy = fecha;
        return inscripcionRepositorio
                .buscarPorSeccionesVigentes(
                        List.of(seccionId),
                        EstadoInscripcion.RETIRADO)
                .stream()
                .map(i -> {

                    Asistencia asistencia =
                            asistenciaRepositorio
                                    .findByInscripcionIdAndFecha(
                                            i.getId(),
                                            hoy)
                                    .orElse(null);

                    return new EstudianteAsistenciaDto(
                            i.getId(),
                            i.getEstudiante().getId(),
                            i.getEstudiante().getNombreCompleto(),
                            asistencia == null
                                    ? "PRESENTE"
                                    : asistencia.getEstado().name(),
                            asistencia == null
                                    ? null
                                    : asistencia.getObservaciones());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstudianteCalificacionDto> calificacionesSeccion(
            Integer profesorId,
            Integer seccionId) {

        boolean pertenece = secciones(profesorId)
                .stream()
                .anyMatch(s ->
                        s.seccionId().equals(seccionId));

        if (!pertenece) {
            throw new IllegalArgumentException(
                    "La sección no pertenece al profesor");
        }

        return inscripcionRepositorio
                .buscarPorSeccionesVigentes(
                        List.of(seccionId),
                        EstadoInscripcion.RETIRADO)
                .stream()
                .map(i -> {

                    CalificacionFinal calificacion =
                            calificacionRepositorio
                                    .findByInscripcionId(
                                            i.getId())
                                    .orElse(null);

                    return new EstudianteCalificacionDto(
                            i.getId(),
                            i.getEstudiante().getId(),
                            i.getEstudiante().getNombreCompleto(),
                            calificacion == null
                                    ? null
                                    : calificacion.getNotaNumerica());
                })
                .toList();
    }

    @Transactional
    public void registrarCalificaciones(
            Integer profesorId,
            RegistrarCalificacionesDto dto) {

        boolean pertenece = secciones(profesorId)
                .stream()
                .anyMatch(s ->
                        s.seccionId().equals(dto.seccionId()));

        if (!pertenece) {
            throw new IllegalArgumentException(
                    "La sección no pertenece al profesor");
        }

        for (RegistroCalificacionDto registro : dto.registros()) {

            Inscripcion inscripcion =
                    inscripcionRepositorio
                            .findById(
                                    registro.inscripcionId())
                            .orElseThrow();

            if (!inscripcion.getSeccion()
                    .getId()
                    .equals(dto.seccionId())) {

                throw new IllegalArgumentException(
                        "La inscripción no pertenece a la sección");
            }

            CalificacionFinal calificacion =
                    calificacionRepositorio
                            .findByInscripcionId(
                                    registro.inscripcionId())
                            .orElseGet(CalificacionFinal::new);

            calificacion.setInscripcion(inscripcion);

            calificacion.setNotaNumerica(
                    registro.nota());

            calificacionRepositorio.save(
                    calificacion);
        }
    }

    @Transactional
    public void registrarAsistencia(
            Integer profesorId,
            RegistrarAsistenciaDto dto) {

        boolean pertenece = secciones(profesorId)
                .stream()
                .anyMatch(s ->
                        s.seccionId().equals(dto.seccionId()));

        if (!pertenece) {
            throw new IllegalArgumentException(
                    "La sección no pertenece al profesor");
        }

        for (RegistroAsistenciaDto registro : dto.registros()) {

            Inscripcion inscripcion =
                    inscripcionRepositorio
                            .findById(
                                    registro.inscripcionId())
                            .orElseThrow();

            if (!inscripcion.getSeccion()
                    .getId()
                    .equals(dto.seccionId())) {

                throw new IllegalArgumentException(
                        "La inscripción no pertenece a la sección");
            }

            Asistencia asistencia =
                    asistenciaRepositorio
                            .findByInscripcionIdAndFecha(
                                    registro.inscripcionId(),
                                    dto.fecha())
                            .orElseGet(Asistencia::new);

            asistencia.setInscripcion(inscripcion);

            asistencia.setFecha(dto.fecha());

            asistencia.setEstado(
                    EstadoAsistencia.valueOf(
                            registro.estado().toUpperCase()));

            asistencia.setObservaciones(
                    registro.observaciones());

            asistenciaRepositorio.save(asistencia);
        }
    }

    @Transactional(readOnly = true)
    public ResumenDashboardProfesorDto resumen(Integer profesorId) {
        List<SeccionProfesorDto> secciones = secciones(profesorId);
        int totalEstudiantes = secciones.stream().mapToInt(SeccionProfesorDto::inscritos).sum();

        List<Integer> seccionIds = secciones.stream().map(SeccionProfesorDto::seccionId).toList();
        int pendientesPorCalificar = 0;
        if (!seccionIds.isEmpty()) {
            for (var tarea : tareaRepositorio.buscarPorSecciones(seccionIds)) {
                var entregas = entregaRepositorio.findByTareaIdOrderByFechaEnvioDesc(tarea.getId());
                var vistos = new java.util.HashSet<Integer>();
                for (var entrega : entregas) {
                    // Solo la entrega mas reciente de cada estudiante cuenta.
                    if (vistos.add(entrega.getEstudiante().getId()) && entrega.getCalificacion() == null) {
                        pendientesPorCalificar++;
                    }
                }
            }
        }

        String hoy = codigoDia(LocalDate.now().getDayOfWeek());
        List<SeccionProfesorDto> clasesHoy = secciones.stream()
                .filter(s -> s.dias().contains(hoy))
                .toList();

        return new ResumenDashboardProfesorDto(
                servicioPeriodo.periodoActivo(),
                secciones.size(),
                totalEstudiantes,
                pendientesPorCalificar,
                (int) servicioMensajes.sinLeer(profesorId),
                clasesHoy);
    }

    /** Estudiantes inscritos (vigentes) en cualquier seccion que imparte el profesor este periodo. */
    @Transactional(readOnly = true)
    public List<UsuarioDto> contactos(Integer profesorId) {
        List<Integer> seccionIds = secciones(profesorId).stream()
                .map(SeccionProfesorDto::seccionId).toList();
        if (seccionIds.isEmpty()) {
            return List.of();
        }

        return inscripcionRepositorio.buscarPorSeccionesVigentes(seccionIds, EstadoInscripcion.RETIRADO)
                .stream()
                .map(Inscripcion::getEstudiante)
                .collect(java.util.stream.Collectors.toMap(Usuario::getId, u -> u, (a, b) -> a,
                        LinkedHashMap::new))
                .values().stream()
                .map(UsuarioDto::de)
                .toList();
    }

    private SeccionProfesorDto aDto(Seccion seccion) {
        Horario horario = ParseadorHorario.analizar(seccion.getHorarioDescripcion());
        long inscritos = inscripcionRepositorio.countBySeccionIdAndEstadoNot(
                seccion.getId(), EstadoInscripcion.RETIRADO);

        return new SeccionProfesorDto(
                seccion.getId(),
                seccion.getMateria().getNombre(),
                seccion.getMateria().getCodigo(),
                seccion.getMateria().getCreditos(),
                seccion.getPeriodo(),
                seccion.getAula(),
                seccion.getHorarioDescripcion(),
                horario.dias(),
                horario.horaInicio(),
                horario.horaFin(),
                seccion.getEstado() == null ? null : seccion.getEstado().getValor(),
                seccion.getCupoMaximo(),
                (int) inscritos);
    }

    private static String codigoDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Lu";
            case TUESDAY -> "Ma";
            case WEDNESDAY -> "Mi";
            case THURSDAY -> "Ju";
            case FRIDAY -> "Vi";
            case SATURDAY -> "Sa";
            case SUNDAY -> "Do";
        };
    }
}