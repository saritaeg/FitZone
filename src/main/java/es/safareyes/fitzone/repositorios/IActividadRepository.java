package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.dto.ActividadDTO;
import es.safareyes.fitzone.modelos.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface IActividadRepository extends JpaRepository<Actividad, Integer> {

    // GET 7
    @Query(value =
            "SELECT a.id AS id, a.nombre AS nombre, a.dia AS dia, " +
                    "a.hora AS hora, a.sala AS sala, 50 AS aforoMax, " +
                    "m.nombre || ' ' || m.apellidos AS monitor, " +
                    "COUNT(r.id) AS plazasOcupadas, " +
                    "50 - COUNT(r.id) AS plazasLibres " +
                    "FROM actividad a " +
                    "JOIN monitor m ON a.id_monitor = m.id " +
                    "LEFT JOIN reserva r ON r.id_actividad = a.id " +
                    "AND CAST(r.fecha_reserva AS DATE) = :fecha " +
                    "AND r.estado = 'ACTIVA' " +
                    "GROUP BY a.id, a.nombre, a.dia, a.hora, a.sala, " +
                    "m.nombre, m.apellidos",
            nativeQuery = true)
    List<ActividadDTO> buscarActividadesPorFecha(
            @Param("fecha") LocalDate fecha
    );

}
