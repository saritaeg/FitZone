
package es.safareyes.fitzone.security;

import es.safareyes.fitzone.servicios.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UsuarioDetailsService usuarioDetailsService) {
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion,
                                    HttpServletResponse respuesta,
                                    FilterChain cadena)
            throws ServletException, IOException {

        String cabecera = peticion.getHeader(HttpHeaders.AUTHORIZATION);

        if (cabecera == null || !cabecera.startsWith(PREFIJO)) {
            cadena.doFilter(peticion, respuesta);
            return;
        }

        try {
            String nombreUsuario = jwtService.extraerUsuario(
                    cabecera.substring(PREFIJO.length())
            );

            UserDetails detalles =
                    usuarioDetailsService.loadUserByUsername(nombreUsuario);

            var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                    detalles,
                    null,
                    detalles.getAuthorities()
            );

            autenticacion.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(peticion)
            );

            SecurityContext contexto = SecurityContextHolder.createEmptyContext();
            contexto.setAuthentication(autenticacion);
            SecurityContextHolder.setContext(contexto);

        } catch (JwtException | UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
        }

        cadena.doFilter(peticion, respuesta);
    }
}
