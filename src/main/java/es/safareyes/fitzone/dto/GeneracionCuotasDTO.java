package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneracionCuotasDTO {

    private int cuotasCreadas;
    private int sociosInactivos;
}