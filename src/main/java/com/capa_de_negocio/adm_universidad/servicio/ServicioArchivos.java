package com.capa_de_negocio.adm_universidad.servicio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capa_de_negocio.adm_universidad.dto.RespuestaArchivo;
import com.capa_de_negocio.adm_universidad.excepcion.RecursoNoEncontradoExcepcion;
import com.capa_de_negocio.adm_universidad.excepcion.ReglaNegocioExcepcion;

/**
 * Almacenamiento de los archivos que suben los estudiantes en sus entregas.
 *
 * <p>Los ficheros se guardan en disco con un nombre generado (UUID + extension) y en la
 * base de datos solo viaja la URL, tal como espera el esquema (entregas_tareas.archivo_url).
 */
@Service
public class ServicioArchivos {

    /** Extensiones aceptadas para una entrega academica. */
    private static final java.util.Set<String> EXTENSIONES_PERMITIDAS = java.util.Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "zip", "rar",
            "png", "jpg", "jpeg");

    private final Path directorio;

    public ServicioArchivos(@Value("${app.archivos.directorio}") String directorioConfigurado) {
        this.directorio = Paths.get(directorioConfigurado).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.directorio);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "No se pudo crear la carpeta de archivos: " + this.directorio, e);
        }
    }

    public RespuestaArchivo guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ReglaNegocioExcepcion("Selecciona un archivo antes de enviarlo.");
        }

        String nombreOriginal = archivo.getOriginalFilename() == null
                ? "archivo"
                : Paths.get(archivo.getOriginalFilename()).getFileName().toString();

        String extension = extension(nombreOriginal);
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new ReglaNegocioExcepcion(
                    "Formato no permitido. Acepta: PDF, Word, Excel, PowerPoint, texto, "
                            + "imágenes o archivos comprimidos.");
        }

        // El nombre en disco lo genera el servidor: el que llega del cliente no se usa
        // como ruta, para que no pueda escapar de la carpeta de subidas.
        String nombreEnDisco = UUID.randomUUID() + "." + extension;
        Path destino = directorio.resolve(nombreEnDisco);

        try (var entrada = archivo.getInputStream()) {
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo.", e);
        }

        return new RespuestaArchivo("/api/archivos/" + nombreEnDisco, nombreOriginal, archivo.getSize());
    }

    public Resource cargar(String nombre) {
        // Se resuelve solo el nombre de fichero y se comprueba que el resultado siga
        // dentro de la carpeta configurada.
        Path ruta = directorio.resolve(Paths.get(nombre).getFileName().toString()).normalize();
        if (!ruta.startsWith(directorio) || !Files.exists(ruta)) {
            throw new RecursoNoEncontradoExcepcion("El archivo solicitado no existe.");
        }

        try {
            return new UrlResource(ruta.toUri());
        } catch (IOException e) {
            throw new RecursoNoEncontradoExcepcion("El archivo solicitado no existe.");
        }
    }

    private static String extension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
    }
}
