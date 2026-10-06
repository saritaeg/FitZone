package es.safareyes.fitzone.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {
    public enum Rol{
        ADMIN,
        RECEPCION
    }
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column( name = "nom_contraseña",length = 100, nullable = false)
    private String nomContraseña;

    @Column( length = 100, nullable = false)
    private String contraseña;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 50, nullable = false)
    private Rol rol;

    @OneToOne(mappedBy = "usuario")
    private Socio socio;


}
