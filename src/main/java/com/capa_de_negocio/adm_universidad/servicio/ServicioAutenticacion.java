package com.capa_de_negocio.adm_universidad.servicio;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.capa_de_negocio.adm_universidad.dto.RespuestaLogin;
import com.capa_de_negocio.adm_universidad.dto.SolicitudLogin;
import com.capa_de_negocio.adm_universidad.dto.UsuarioDto;
import com.capa_de_negocio.adm_universidad.entidad.Usuario;
import com.capa_de_negocio.adm_universidad.seguridad.ServicioJwt;
import com.capa_de_negocio.adm_universidad.seguridad.UsuarioAutenticado;

/** Login unico para los tres perfiles: el rol devuelto decide a que panel entra el cliente. */
@Service
public class ServicioAutenticacion {

    private final AuthenticationManager gestorAutenticacion;
    private final ServicioJwt servicioJwt;

    public ServicioAutenticacion(AuthenticationManager gestorAutenticacion, ServicioJwt servicioJwt) {
        this.gestorAutenticacion = gestorAutenticacion;
        this.servicioJwt = servicioJwt;
    }

    public RespuestaLogin iniciarSesion(SolicitudLogin solicitud) {
        // Las credenciales invalidas o la cuenta inactiva salen como excepcion de Spring
        // Security y las traduce ManejadorGlobalErrores.
        Authentication autenticacion = gestorAutenticacion.authenticate(
                new UsernamePasswordAuthenticationToken(solicitud.email(), solicitud.password()));

        Usuario usuario = ((UsuarioAutenticado) autenticacion.getPrincipal()).getUsuario();

        return new RespuestaLogin(
                servicioJwt.generarToken(usuario),
                servicioJwt.getExpiracionMs(),
                UsuarioDto.de(usuario));
    }
}
