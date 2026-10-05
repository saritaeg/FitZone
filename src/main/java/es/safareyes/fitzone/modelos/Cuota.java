package es.safareyes.fitzone.modelos;


import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "cuota")
public class Cuota {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    public enum estadoCuota{
        PAGADO,
        PENDIENTE
    }
    @Column (nullable = false)
    private Integer mes;

    @Column (nullable = false)
    private Integer año;

    @Column ( length=20, nullable = false)
    private String importe;

    @Enumerated(EnumType.STRING)
    @Column ( name="estado",length=20, nullable = false)
    private estadoCuota estado;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;

}
