package com.capa_de_negocio.adm_universidad.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.capa_de_negocio.adm_universidad.servicio.ParseadorHorario.Horario;

/**
 * El horario se guarda como texto libre en secciones.horario_descripcion, asi que
 * el parseo tiene que aguantar formatos raros sin tumbar la peticion.
 */
class ParseadorHorarioTest {

    @Test
    @DisplayName("Interpreta el formato habitual 'Lu-Mi 18:00 - 20:00'")
    void interpretaFormatoHabitual() {
        Horario horario = ParseadorHorario.analizar("Lu-Mi 18:00 - 20:00");

        assertEquals(List.of("Lu", "Mi"), horario.dias());
        assertEquals("18:00", horario.horaInicio());
        assertEquals("20:00", horario.horaFin());
    }

    @Test
    @DisplayName("Ordena los dias segun la semana aunque vengan al reves")
    void ordenaLosDias() {
        assertEquals(List.of("Ma", "Ju"), ParseadorHorario.analizar("Ju-Ma 10:00 - 12:00").dias());
    }

    @Test
    @DisplayName("Acepta un solo dia")
    void aceptaUnSoloDia() {
        Horario horario = ParseadorHorario.analizar("Vi 08:00 - 12:00");

        assertEquals(List.of("Vi"), horario.dias());
        assertEquals("08:00", horario.horaInicio());
    }

    @Test
    @DisplayName("Normaliza las horas a HH:mm")
    void normalizaLasHoras() {
        Horario horario = ParseadorHorario.analizar("Lu 8:5 - 9:30");

        assertEquals("08:05", horario.horaInicio());
        assertEquals("09:30", horario.horaFin());
    }

    @Test
    @DisplayName("Devuelve vacio en lugar de fallar cuando el texto no encaja")
    void toleraTextoInesperado() {
        assertTrue(ParseadorHorario.analizar(null).dias().isEmpty());
        assertTrue(ParseadorHorario.analizar("").dias().isEmpty());
        assertTrue(ParseadorHorario.analizar("Por definir").dias().isEmpty());

        Horario soloDias = ParseadorHorario.analizar("Lu-Mi");
        assertEquals(List.of("Lu", "Mi"), soloDias.dias());
        assertNull(soloDias.horaInicio());
    }
}
