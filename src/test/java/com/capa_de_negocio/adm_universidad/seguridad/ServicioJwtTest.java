package com.capa_de_negocio.adm_universidad.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.capa_de_negocio.adm_universidad.entidad.Rol;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;

class ServicioJwtTest {

    private static final String SECRETO = "clave-de-pruebas-suficientemente-larga-para-hs256";

    private Usuario usuarioDemo() {
        Rol rol = new Rol();
        rol.setId(3);
        rol.setNombre(Rol.ESTUDIANTE);

        Usuario usuario = new Usuario();
        usuario.setId(42);
        usuario.setNombres("Ana");
        usuario.setApellidos("Martínez");
        usuario.setEmail("ana.martinez@uniconnect.edu.do");
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    @DisplayName("El token generado devuelve el id del usuario al verificarlo")
    void generaYVerificaToken() {
        ServicioJwt servicio = new ServicioJwt(SECRETO, 3_600_000);

        String token = servicio.generarToken(usuarioDemo());

        assertEquals(42, servicio.extraerIdUsuario(token));
    }

    @Test
    @DisplayName("Un token firmado con otra clave no se acepta")
    void rechazaTokenDeOtraClave() {
        String token = new ServicioJwt(SECRETO, 3_600_000).generarToken(usuarioDemo());
        ServicioJwt otroServicio = new ServicioJwt(
                "otra-clave-distinta-igualmente-larga-para-hs256", 3_600_000);

        assertNull(otroServicio.extraerIdUsuario(token));
    }

    @Test
    @DisplayName("Un token vencido deja de valer")
    void rechazaTokenVencido() {
        // Expiracion negativa: nace ya caducado.
        ServicioJwt servicio = new ServicioJwt(SECRETO, -1000);

        assertNull(servicio.extraerIdUsuario(servicio.generarToken(usuarioDemo())));
    }

    @Test
    @DisplayName("Un texto que no es un token no rompe la aplicacion")
    void toleraTokenBasura() {
        ServicioJwt servicio = new ServicioJwt(SECRETO, 3_600_000);

        assertNull(servicio.extraerIdUsuario("esto-no-es-un-jwt"));
        assertNull(servicio.extraerIdUsuario(""));
    }

    @Test
    @DisplayName("Un secreto corto se rechaza al arrancar, no en la primera peticion")
    void exigeSecretoLargo() {
        assertThrows(IllegalStateException.class, () -> new ServicioJwt("corto", 3_600_000));
    }
}
