package es.safareyes.fitzone.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
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
    @Column(length = 20, nullable = false)
    private String telefono;

    @Column (length=150,nullable = false)
    private String email;

    @Column (length=20, nullable = false)
    private String dni;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Enumerated(EnumType.STRING)
    @Column(length=20,nullable = false)
    private EstadoSocio activo = EstadoSocio.ACTIVO;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

}
