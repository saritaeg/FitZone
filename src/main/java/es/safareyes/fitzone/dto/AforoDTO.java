package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AforoDTO {

    private long personasDentro;
    private int aforoMaximo;
}