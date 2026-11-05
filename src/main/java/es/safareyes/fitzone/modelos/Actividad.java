
package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(length = 50, nullable = false)
    private String sala;

    @Column(name = "aforo_max", nullable = false)
    private Integer aforoMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_monitor", nullable = false)
    private Monitor monitor;
}
