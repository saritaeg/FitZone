package es.safareyes.fitzone.modelos;


import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "actividad")
public class Actividad {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column( length=100, nullable = false)
    private String nombre;

    @Column( length=20, nullable = false)
    private String dia;

    @Column( length=20, nullable = false)
    private LocalDateTime hora;

    @Column( length=20, nullable = false)
    private String duracion;

    @Column( length=50, nullable = false)
    private String sala;

    @Column( name="aforo_max", nullable = false)
    private Integer aforoMax;

    
}
