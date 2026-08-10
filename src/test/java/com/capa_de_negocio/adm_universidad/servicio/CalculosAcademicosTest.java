package com.capa_de_negocio.adm_universidad.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.capa_de_negocio.adm_universidad.entidad.CalificacionFinal;
import com.capa_de_negocio.adm_universidad.entidad.Inscripcion;
import com.capa_de_negocio.adm_universidad.entidad.Materia;
import com.capa_de_negocio.adm_universidad.entidad.Seccion;

/** Comprueba los dos calculos que el estudiante ve en pantalla: promedio y asistencia. */
class CalculosAcademicosTest {

    private CalificacionFinal nota(String valor, int creditos) {
        Materia materia = new Materia();
        materia.setCreditos(creditos);

        Seccion seccion = new Seccion();
        seccion.setMateria(materia);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setSeccion(seccion);

        CalificacionFinal calificacion = new CalificacionFinal();
        calificacion.setInscripcion(inscripcion);
        calificacion.setNotaNumerica(new BigDecimal(valor));
        return calificacion;
    }

    @Test
    @DisplayName("El promedio pondera por creditos, no es una media simple")
    void promedioPonderadoPorCreditos() {
        // (90*4 + 60*2) / 6 = 80.00. La media simple daria 75.
        BigDecimal promedio = ServicioCalificaciones.promedio(
                List.of(nota("90.00", 4), nota("60.00", 2)));

        assertEquals(0, new BigDecimal("80.00").compareTo(promedio));
    }

    @Test
    @DisplayName("Sin notas publicadas no hay promedio")
    void sinNotasNoHayPromedio() {
        assertNull(ServicioCalificaciones.promedio(List.of()));
    }

    @Test
    @DisplayName("El porcentaje de asistencia cuenta todo lo que no sea ausencia")
    void porcentajeDeAsistencia() {
        assertEquals(0, new BigDecimal("80.0")
                .compareTo(ServicioAsistencia.porcentaje(8, 10)));
        assertEquals(0, new BigDecimal("100.0")
                .compareTo(ServicioAsistencia.porcentaje(5, 5)));
    }

    @Test
    @DisplayName("Sin clases registradas no se inventa un porcentaje")
    void sinClasesNoHayPorcentaje() {
        assertNull(ServicioAsistencia.porcentaje(0, 0));
    }
}
