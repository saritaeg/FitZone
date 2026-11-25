package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoReservaDTO {

    private boolean reservada;
    private String mensaje;
}