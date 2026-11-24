package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.SocioPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}