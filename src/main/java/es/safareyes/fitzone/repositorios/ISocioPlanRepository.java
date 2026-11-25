package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.SocioPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface ISocioPlanRepository extends JpaRepository<SocioPlan, Integer> {

    @Query("SELECT sp FROM SocioPlan sp " +
            "JOIN FETCH sp.socio " +
            "JOIN FETCH sp.plan " +
            "WHERE sp.fechaInicio <= :finMes " +
            "AND (sp.fechaFin IS NULL OR sp.fechaFin >= :inicioMes)")
    List<SocioPlan> buscarPlanesVigentesEnFecha(
            @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes
    );
    @Query(value =
            "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM socio_plan sp " +
                    "JOIN plan p ON sp.id_plan = p.id " +
                    "WHERE sp.id_socio = :idSocio " +
                    "AND p.tipo = 'PREMIUM' " +
                    "AND sp.fecha_inicio <= :fecha " +
                    "AND (sp.fecha_fin IS NULL OR sp.fecha_fin >= :fecha)",
            nativeQuery = true)
    boolean tienePlanPremiumVigente(
            @Param("idSocio") Integer idSocio,
            @Param("fecha") LocalDate fecha
    );
    // Buscar el plan actual del socio
    Optional<SocioPlan> findFirstBySocio_IdAndFechaFinIsNullOrderByFechaInicioDesc(
            Integer idSocio
    );
}