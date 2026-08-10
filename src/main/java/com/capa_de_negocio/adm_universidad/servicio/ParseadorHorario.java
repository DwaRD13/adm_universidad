package com.capa_de_negocio.adm_universidad.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Interpreta la columna libre secciones.horario_descripcion (ej. 'Lu-Mi 18:00 - 20:00')
 * y la convierte en dias y horas estructurados.
 *
 * <p>El analisis vive en el backend a proposito: el cliente recibe datos ya listos y
 * no tiene que replicar esta logica. Si el texto no encaja con el formato esperado se
 * devuelven listas vacias en lugar de fallar, porque el campo admite texto arbitrario.
 */
public final class ParseadorHorario {

    /** Codigos de dia reconocidos, en orden de la semana. */
    public static final List<String> DIAS = List.of("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do");

    private ParseadorHorario() {
    }

    public record Horario(List<String> dias, String horaInicio, String horaFin) {

        public static Horario vacio() {
            return new Horario(List.of(), null, null);
        }
    }

    public static Horario analizar(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return Horario.vacio();
        }

        String texto = descripcion.trim();
        int primerEspacio = texto.indexOf(' ');
        if (primerEspacio < 0) {
            // Solo dias, sin horas: 'Lu-Mi'
            return new Horario(extraerDias(texto), null, null);
        }

        List<String> dias = extraerDias(texto.substring(0, primerEspacio));
        String[] horas = texto.substring(primerEspacio + 1).split("-");

        String inicio = horas.length > 0 ? normalizarHora(horas[0]) : null;
        String fin = horas.length > 1 ? normalizarHora(horas[1]) : null;

        return new Horario(dias, inicio, fin);
    }

    /** Orden semanal de un dia; los codigos desconocidos van al final. */
    public static int ordenDia(String codigo) {
        int indice = DIAS.indexOf(codigo);
        return indice < 0 ? Integer.MAX_VALUE : indice;
    }

    private static List<String> extraerDias(String fragmento) {
        List<String> dias = new ArrayList<>();
        for (String parte : fragmento.split("[-/,]")) {
            String codigo = capitalizar(parte.trim());
            if (DIAS.contains(codigo) && !dias.contains(codigo)) {
                dias.add(codigo);
            }
        }
        dias.sort(java.util.Comparator.comparingInt(ParseadorHorario::ordenDia));
        return dias;
    }

    /** Deja las horas siempre como HH:mm para que el cliente no tenga que normalizarlas. */
    private static String normalizarHora(String hora) {
        String limpia = hora.trim();
        if (limpia.isEmpty()) {
            return null;
        }
        String[] partes = limpia.split(":");
        if (partes.length < 2) {
            return limpia;
        }
        try {
            return String.format("%02d:%02d",
                    Integer.parseInt(partes[0].trim()),
                    Integer.parseInt(partes[1].trim().replaceAll("\\D", "")));
        } catch (NumberFormatException e) {
            return limpia;
        }
    }

    private static String capitalizar(String texto) {
        if (texto.length() < 2) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase(Locale.ROOT)
                + texto.substring(1, 2).toLowerCase(Locale.ROOT);
    }
}
