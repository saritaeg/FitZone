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

    // GET 7: Consultar actividades y plazas disponibles
    @Query(value =
            "SELECT a.id AS id, " +
                    "a.nombre AS nombre, " +
                    "TO_CHAR(a.fecha_hora_inicio, 'YYYY-MM-DD') AS dia, " +
                    "TO_CHAR(a.fecha_hora_inicio, 'HH24:MI') AS hora, " +
                    "a.sala AS sala, " +
                    "a.aforo_max AS \"aforoMax\", " +
                    "m.nombre || ' ' || m.apellidos AS monitor, " +
                    "COUNT(r.id) AS \"plazasOcupadas\", " +
                    "a.aforo_max - COUNT(r.id) AS \"plazasLibres\" " +
                    "FROM actividad a " +
                    "JOIN monitor m ON a.id_monitor = m.id " +
                    "LEFT JOIN reserva r ON r.id_actividad = a.id " +
                    "AND r.estado = 'ACTIVA' " +
                    "WHERE CAST(a.fecha_hora_inicio AS DATE) = :fecha " +
                    "GROUP BY a.id, a.nombre, a.fecha_hora_inicio, " +
                    "a.sala, a.aforo_max, m.nombre, m.apellidos " +
                    "HAVING COUNT(r.id) < a.aforo_max",
            nativeQuery = true)
    List<ActividadDTO> buscarActividadesPorFecha(
            @Param("fecha") LocalDate fecha
    );
}