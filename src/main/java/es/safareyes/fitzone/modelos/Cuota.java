
package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Entity
@Table(name = "cuota")
public class Cuota {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    public enum EstadoCuota {
        PAGADO,
        PENDIENTE
    }

    @Column(nullable = false)
    private Integer mes;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoCuota estado;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;
}
