
package es.safareyes.fitzone.security;

import es.safareyes.fitzone.servicios.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final EntradaNoAutenticada entradaNoAutenticada;
    private final AccesoDenegado accesoDenegado;

    public SecurityConfig(EntradaNoAutenticada entradaNoAutenticada,
                          AccesoDenegado accesoDenegado) {
        this.entradaNoAutenticada = entradaNoAutenticada;
        this.accesoDenegado = accesoDenegado;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UsuarioDetailsService usuarioDetailsService) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()

                        // Rutas públicas
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/planes",
                                "/api/v1/actividades").permitAll()

                        // Socios
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/socios")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(HttpMethod.GET, "/api/v1/socios").hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(HttpMethod.GET, "/api/v1/socios/*").hasAnyRole("ADMIN", "RECEPCION", "SOCIO")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/socios/*")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/socios/*/estado")
                        .hasRole("ADMIN")

                        // Cuotas
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/cuotas/generar")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/cuotas/pagar")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/cuotas/socio/*")
                        .hasAnyRole("ADMIN", "RECEPCION", "SOCIO")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/cuotas/pendientes")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        // Accesos
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/accesos/dentro")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/accesos/entrada",
                                "/api/v1/accesos/salida")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        // Actividades
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/actividades/*/reservas")
                        .hasRole("SOCIO")

                        // Informes
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/informes/ingresos")
                        .hasRole("ADMIN")

                        // Swagger y errores
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, usuarioDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entradaNoAutenticada)
                        .accessDeniedHandler(accesoDenegado))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UsuarioDetailsService usuarioDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider proveedor =
                new DaoAuthenticationProvider(usuarioDetailsService);

        proveedor.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(proveedor);
    }
}
