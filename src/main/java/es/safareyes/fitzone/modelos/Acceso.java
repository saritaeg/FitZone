
package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Entity
@Table(name = "acceso")
public class Acceso {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    public enum TipoResultado {
        PERMITIDO,
        DENEGADO
    }

    public enum MotivoDenegacion {
        INACTIVO,
        CUOTA_PENDIENTE,
        YA_DENTRO,
        AFORO_COMPLETO
    }

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada", nullable = false)
    private LocalDateTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resultado", length = 20, nullable = false)
    private TipoResultado tipoResultado;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_denegacion", length = 50)
    private MotivoDenegacion motivoDenegacion;

    @Column(name = "ticket_parking", length = 50)
    private String ticketParking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;
}
