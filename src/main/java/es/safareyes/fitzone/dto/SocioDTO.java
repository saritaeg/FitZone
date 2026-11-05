package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocioDTO {

    private Integer id;
    private String nombre;
    private String apellidos;
    private String matricula;
    private String telefono;
    private String email;
    private String dni;
    private LocalDateTime fechaAlta;
    private String activo;
}