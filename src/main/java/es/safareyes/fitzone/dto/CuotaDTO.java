package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuotaDTO {

    private Integer id;
    private Integer mes;
    private Integer anio;
    private BigDecimal importe;
    private String estado;
    private LocalDateTime fechaPago;
    private String plan;
}