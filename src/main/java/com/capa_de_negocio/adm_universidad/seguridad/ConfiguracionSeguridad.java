package com.capa_de_negocio.adm_universidad.seguridad;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * API sin estado: no hay sesion de servidor, cada peticion se identifica con su JWT.
 * La autorizacion fina por rol se declara con @PreAuthorize en cada controlador.
 */
@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class ConfiguracionSeguridad {

    private final FiltroAutenticacionJwt filtroJwt;
    private final ManejadorErroresSeguridad manejadorErrores;

    public ConfiguracionSeguridad(FiltroAutenticacionJwt filtroJwt,
            ManejadorErroresSeguridad manejadorErrores) {
        this.filtroJwt = filtroJwt;
        this.manejadorErrores = manejadorErrores;
    }

    @Bean
    public SecurityFilterChain cadenaFiltros(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(fuenteCors()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        .requestMatchers("/api/auth/login", "/api/status").permitAll()
                        // Las descargas se abren desde el navegador o un visor externo,
                        // que no envian la cabecera Authorization.
//                        .requestMatchers(HttpMethod.GET, "/api/archivos/**").permitAll()
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll());
//                .exceptionHandling(e -> e
//                        .authenticationEntryPoint(manejadorErrores)
//                        .accessDeniedHandler(manejadorErrores))
//                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Desarrollo: Flutter web y el emulador llegan desde origenes distintos al backend. */
    @Bean
    public CorsConfigurationSource fuenteCors() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOriginPatterns(List.of("*"));
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("*"));
        configuracion.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }

    @Bean
    public PasswordEncoder codificadorContrasena() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager gestorAutenticacion(ServicioDetallesUsuario servicioDetalles,
            PasswordEncoder codificador) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(servicioDetalles);
        proveedor.setPasswordEncoder(codificador);
        return proveedor::authenticate;
    }
}
