package com.capa_de_negocio.adm_universidad.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.AsistenciaDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.AsistenciaDto.MateriaAsistenciaDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.AsistenciaDto.RegistroDto;
import com.capa_de_negocio.adm_universidad.entidad.Asistencia;
import com.capa_de_negocio.adm_universidad.entidad.EstadoAsistencia;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.repositorio.AsistenciaRepositorio;

/** Asistencia del estudiante agrupada por materia, en solo lectura. */
@Service
public class ServicioAsistencia {

    private final AsistenciaRepositorio asistenciaRepositorio;
    private final ServicioInscripcion servicioInscripcion;

    public ServicioAsistencia(AsistenciaRepositorio asistenciaRepositorio,
            ServicioInscripcion servicioInscripcion) {
        this.asistenciaRepositorio = asistenciaRepositorio;
        this.servicioInscripcion = servicioInscripcion;
    }

    @Transactional(readOnly = true)
    public AsistenciaDto asistencia(Integer estudianteId) {
        List<Inscripcion> inscripciones = servicioInscripcion.inscripcionesVigentes(estudianteId);
        if (inscripciones.isEmpty()) {
            return new AsistenciaDto(null, 0, List.of());
        }

        List<Asistencia> registros = asistenciaRepositorio.buscarPorInscripciones(
                inscripciones.stream().map(Inscripcion::getId).toList());

        // Se agrupa por inscripcion conservando el orden de las materias.
        Map<Integer, List<Asistencia>> porInscripcion = new LinkedHashMap<>();
        for (Inscripcion inscripcion : inscripciones) {
            porInscripcion.put(inscripcion.getId(), new ArrayList<>());
        }
        for (Asistencia registro : registros) {
            porInscripcion.computeIfAbsent(registro.getInscripcion().getId(), k -> new ArrayList<>())
                    .add(registro);
        }

        List<MateriaAsistenciaDto> materias = new ArrayList<>();
        int totalClases = 0;
        int totalAsistidas = 0;

        for (Inscripcion inscripcion : inscripciones) {
            List<Asistencia> deLaMateria = porInscripcion.getOrDefault(inscripcion.getId(), List.of());

            int presentes = contar(deLaMateria, EstadoAsistencia.PRESENTE);
            int ausentes = contar(deLaMateria, EstadoAsistencia.AUSENTE);
            int tardanzas = contar(deLaMateria, EstadoAsistencia.TARDANZA);
            int excusas = contar(deLaMateria, EstadoAsistencia.EXCUSA);
            int clases = deLaMateria.size();
            int asistidas = clases - ausentes;

            totalClases += clases;
            totalAsistidas += asistidas;

            materias.add(new MateriaAsistenciaDto(
                    inscripcion.getId(),
                    inscripcion.getSeccion().getMateria().getNombre(),
                    inscripcion.getSeccion().getMateria().getCodigo(),
                    inscripcion.getSeccion().getProfesor().getNombreCompleto(),
                    clases,
                    presentes,
                    ausentes,
                    tardanzas,
                    excusas,
                    porcentaje(asistidas, clases),
                    deLaMateria.stream()
                            .map(a -> new RegistroDto(a.getId(), a.getFecha(),
                                    a.getEstado().getValor(), a.getObservaciones()))
                            .toList()));
        }

        return new AsistenciaDto(porcentaje(totalAsistidas, totalClases), totalClases, materias);
    }

    private static int contar(List<Asistencia> registros, EstadoAsistencia estado) {
        return (int) registros.stream().filter(a -> a.getEstado() == estado).count();
    }

    /** Porcentaje de clases no ausentes. Null cuando aun no hay clases registradas. */
    static BigDecimal porcentaje(int asistidas, int total) {
        if (total == 0) {
            return null;
        }
        return BigDecimal.valueOf(asistidas)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
    }
}
