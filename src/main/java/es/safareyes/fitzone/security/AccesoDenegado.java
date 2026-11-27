
package es.safareyes.fitzone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccesoDenegado implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public AccesoDenegado(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest peticion,
                       HttpServletResponse respuesta,
                       AccessDeniedException excepcion) throws IOException {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Tu cuenta no tiene permisos para esta operación");

        problema.setTitle("Acceso denegado");
        problema.setProperty("ruta", peticion.getRequestURI());

        respuesta.setStatus(HttpStatus.FORBIDDEN.value());
        respuesta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        objectMapper.writeValue(respuesta.getOutputStream(), problema);
    }
}
