
package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    public enum TipoPlan {
        BASICO,
        PREMIUM
    }

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(name = "precio_mensual", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioMensual;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50, nullable = false)
    private TipoPlan tipo;
}
