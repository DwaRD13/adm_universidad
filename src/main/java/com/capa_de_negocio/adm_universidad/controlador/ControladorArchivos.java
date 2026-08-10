package com.capa_de_negocio.adm_universidad.controlador;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.capa_de_negocio.adm_universidad.dto.RespuestaArchivo;
import com.capa_de_negocio.adm_universidad.servicio.ServicioArchivos;

/**
 * Subida y descarga de archivos. La subida exige sesion iniciada; la descarga es
 * publica porque se abre desde el navegador o un visor externo, que no envian el token.
 */
@RestController
@RequestMapping("/api/archivos")
public class ControladorArchivos {

    private final ServicioArchivos servicioArchivos;

    public ControladorArchivos(ServicioArchivos servicioArchivos) {
        this.servicioArchivos = servicioArchivos;
    }

    @PostMapping
    public RespuestaArchivo subir(@RequestParam("archivo") MultipartFile archivo) {
        return servicioArchivos.guardar(archivo);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<Resource> descargar(@PathVariable String nombre) {
        Resource recurso = servicioArchivos.cargar(nombre);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombre + "\"")
                .body(recurso);
    }
}
