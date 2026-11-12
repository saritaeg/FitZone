package es.safareyes.fitzone.dto;

import lombok.Data;

@Data
public class AltaSocioDTO {

    private String nombre;
    private String apellidos;
    private String matricula;
    private String telefono;
    private String email;
    private String dni;
    private Integer idPlan;
}