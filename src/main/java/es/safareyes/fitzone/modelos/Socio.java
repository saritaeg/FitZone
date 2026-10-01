package es.safareyes.fitzone.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "socio")
public class Socio {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    public enum EstadoSocio{
        ACTIVO,
        INACTIVO
    }

    @Column ( length=100, nullable = false)
    private String nombre;

    @Column (length=150, nullable = false)
    private String apellidos;

    @Column (length=20, nullable = true)
    private String matricula;

    @Column (length=150,nullable = false)
    private String email;

    @Column (length=20, nullable = false)
    private String dni;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Enumerated(EnumType.STRING)
    @Column(length=20,nullable = false)
    private EstadoSocio activo = EstadoSocio.ACTIVO;


    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
