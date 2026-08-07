package com.capa_de_negocio.adm_universidad;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/status")
    public String checkStatus() {
        return "¡El backend de Spring Boot está funcionando y listo para Flutter!";
    }
}