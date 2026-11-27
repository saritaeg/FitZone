
package es.safareyes.fitzone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EntradaNoAutenticada implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public EntradaNoAutenticada(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest peticion,
                         HttpServletResponse respuesta,
                         AuthenticationException excepcion) throws IOException {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Necesitas iniciar sesión para acceder a este recurso");

        problema.setTitle("No autenticado");
        problema.setProperty("ruta", peticion.getRequestURI());

        respuesta.setStatus(HttpStatus.UNAUTHORIZED.value());
        respuesta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        objectMapper.writeValue(respuesta.getOutputStream(), problema);
    }
}
