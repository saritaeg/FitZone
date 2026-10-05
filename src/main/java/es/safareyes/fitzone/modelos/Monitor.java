package es.safareyes.fitzone.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "monitor")
public class Monitor {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column ( length=100, nullable = false)
    private String nombre;

    @Column (length=150, nullable = false)
    private String apellidos;
    public Monitor() {
    }

    public Monitor(String nombre, String apellidos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
    }


}
