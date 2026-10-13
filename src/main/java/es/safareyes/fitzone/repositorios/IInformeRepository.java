package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.dto.IngresoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.safareyes.fitzone.modelos.Cuota;
import java.util.List;

public interface IInformeRepository extends JpaRepository<Cuota, Integer> {
    @Query(value =
            "SELECT c.mes AS mes, p.nombre AS plan, " +
                    "SUM(CAST(REPLACE(c.importe, ',', '.') AS NUMERIC)) AS ingresos " +
                    "FROM cuota c " +
                    "JOIN socio s ON c.id_socio = s.id " +
                    "JOIN socio_plan sp ON sp.id_socio = s.id " +
                    "JOIN plan p ON sp.id_plan = p.id " +
                    "WHERE c.estado = 'PAGADO' " +
                    "AND c.año = :anio " +
                    "GROUP BY c.mes, p.nombre " +
                    "ORDER BY c.mes, p.nombre",
            nativeQuery = true)
    List<IngresoDTO> buscarIngresosPorAnio(
            @Param("anio") Integer anio
    );

}
