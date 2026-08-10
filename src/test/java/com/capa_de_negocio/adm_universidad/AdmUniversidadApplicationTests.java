package com.capa_de_negocio.adm_universidad;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integracion: levanta el contexto completo, lo que incluye conectarse a
 * MySQL y validar el esquema contra las entidades (ddl-auto=validate).
 *
 * <p>Esta deshabilitado porque necesita una base de datos accesible con las
 * credenciales del archivo .env. Quita la anotacion @Disabled cuando el usuario
 * de la base de datos tenga permisos sobre uniconnect_db: si el contexto carga,
 * es la prueba de que las 13 entidades calzan con el DDL.
 */
@Disabled("Requiere acceso real a MySQL. Ver el comentario de la clase.")
@SpringBootTest
class AdmUniversidadApplicationTests {

    @Test
    void contextLoads() {
    }
}
