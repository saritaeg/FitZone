package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.dto.IngresoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.safareyes.fitzone.modelos.Cuota;

import java.util.List;

@Repository
public interface IInformeRepository extends JpaRepository<Cuota, Integer> {

    // GET 8: Consultar ingresos por año y plan
    @Query(value =
            "SELECT c.mes AS mes, " +
                    "p.nombre AS plan, " +
                    "SUM(c.importe) AS ingresos " +
                    "FROM cuota c " +
                    "JOIN socio s ON c.id_socio = s.id " +
                    "JOIN socio_plan sp ON sp.id_socio = s.id " +
                    "JOIN plan p ON sp.id_plan = p.id " +
                    "WHERE c.estado = 'PAGADO' " +
                    "AND c.anio = :anio " +
                    "GROUP BY c.mes, p.nombre " +
                    "ORDER BY c.mes, p.nombre",
            nativeQuery = true)
    List<IngresoDTO> buscarIngresosPorAnio(
            @Param("anio") Integer anio
    );

}