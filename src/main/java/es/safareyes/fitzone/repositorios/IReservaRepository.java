package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface IReservaRepository extends JpaRepository<Reserva, Integer> {

    // Comprobar si ya tiene reservada esa actividad
    boolean existsBySocio_IdAndActividad_IdAndEstado(
            Integer idSocio,
            Integer idActividad,
            Reserva.EstadoReserva estado
    );

    // Contar las reservas futuras del socio
    @Query("SELECT COUNT(r) FROM Reserva r " +
            "WHERE r.socio.id = :idSocio " +
            "AND r.estado = :estado " +
            "AND r.actividad.fechaHoraInicio > :ahora")
    long contarReservasFuturas(
            @Param("idSocio") Integer idSocio,
            @Param("estado") Reserva.EstadoReserva estado,
            @Param("ahora") LocalDateTime ahora
    );

    // Contar las reservas activas de una actividad
    long countByActividad_IdAndEstado(
            Integer idActividad,
            Reserva.EstadoReserva estado
    );
}