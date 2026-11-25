package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoEntradaDTO {

    private boolean permitido;
    private String mensaje;
    private String motivo;
}