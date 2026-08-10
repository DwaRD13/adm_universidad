package com.capa_de_negocio.adm_universidad.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.dto.estudiante.CalificacionesDto;
import com.capa_de_negocio.adm_universidad.dto.estudiante.CalificacionesDto.CalificacionDto;
import com.capa_de_negocio.adm_universidad.entidad.CalificacionFinal;
import com.capa_de_negocio.adm_universidad.entidad.EstadoInscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;
import com.capa_de_negocio.adm_universidad.repositorio.CalificacionFinalRepositorio;

/** Calificaciones finales publicadas del estudiante, con el promedio ya calculado. */
@Service
public class ServicioCalificaciones {

    private final CalificacionFinalRepositorio calificacionRepositorio;

    public ServicioCalificaciones(CalificacionFinalRepositorio calificacionRepositorio) {
        this.calificacionRepositorio = calificacionRepositorio;
    }

    @Transactional(readOnly = true)
    public CalificacionesDto calificaciones(Integer estudianteId) {
        List<CalificacionFinal> notas = calificacionRepositorio.buscarPorEstudiante(estudianteId);

        List<CalificacionDto> detalle = notas.stream().map(nota -> {
            Inscripcion inscripcion = nota.getInscripcion();
            Seccion seccion = inscripcion.getSeccion();
            return new CalificacionDto(
                    nota.getId(),
                    inscripcion.getId(),
                    seccion.getMateria().getNombre(),
                    seccion.getMateria().getCodigo(),
                    seccion.getMateria().getCreditos(),
                    seccion.getProfesor().getNombreCompleto(),
                    seccion.getPeriodo(),
                    nota.getNotaNumerica(),
                    nota.getLiteral(),
                    inscripcion.getEstado() == null ? null : inscripcion.getEstado().getValor(),
                    nota.getFechaPublicacion());
        }).toList();

        int aprobadas = (int) notas.stream()
                .filter(n -> n.getInscripcion().getEstado() == EstadoInscripcion.APROBADO)
                .count();
        int reprobadas = (int) notas.stream()
                .filter(n -> n.getInscripcion().getEstado() == EstadoInscripcion.REPROBADO)
                .count();

        return new CalificacionesDto(promedio(notas), aprobadas, reprobadas, detalle);
    }

    /**
     * Promedio ponderado por creditos: es como lo calcula la universidad, no una media simple.
     * Devuelve null si el estudiante aun no tiene notas publicadas.
     */
    static BigDecimal promedio(List<CalificacionFinal> notas) {
        if (notas.isEmpty()) {
            return null;
        }

        BigDecimal sumaPonderada = BigDecimal.ZERO;
        int totalCreditos = 0;

        for (CalificacionFinal nota : notas) {
            int creditos = nota.getInscripcion().getSeccion().getMateria().getCreditos();
            sumaPonderada = sumaPonderada.add(nota.getNotaNumerica().multiply(BigDecimal.valueOf(creditos)));
            totalCreditos += creditos;
        }

        if (totalCreditos == 0) {
            return null;
        }
        return sumaPonderada.divide(BigDecimal.valueOf(totalCreditos), 2, RoundingMode.HALF_UP);
    }
}
