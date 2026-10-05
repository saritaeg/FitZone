package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    public enum TipoPlan{
        BASICO,
        PREMIUM
    }

    @Column( length = 50, nullable = false)
    private String nombre;

    @Column(name="precio_mensual", length =20, nullable = false)
    private String precioMensual;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo", length = 50, nullable = false)
    private TipoPlan tipo;






}
