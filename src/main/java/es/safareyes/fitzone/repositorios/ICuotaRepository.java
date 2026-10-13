package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.dto.CuotaPendienteDTO;
import es.safareyes.fitzone.modelos.Cuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICuotaRepository extends JpaRepository<Cuota, Integer> {

    @Query(
            value = "SELECT * FROM cuota WHERE id_socio = :idSocio ORDER BY año DESC, mes DESC",
            countQuery = "SELECT COUNT(*) FROM cuota WHERE id_socio = :idSocio",
            nativeQuery = true
    )
    Page<Cuota> buscarCuotasPorSocio(
            @Param("idSocio") Integer idSocio,
            Pageable pageable
    );

    @Query(
            value = "SELECT c.id AS id, c.mes AS mes, c.año AS año, " +
                    "c.importe AS importe, s.nombre AS nombre, " +
                    "s.apellidos AS apellidos, s.dni AS dni " +
                    "FROM cuota c " +
                    "JOIN socio s ON c.id_socio = s.id " +
                    "WHERE c.estado = 'PENDIENTE' " +
                    "AND c.mes = :mes AND c.año = :anio",
            nativeQuery = true
    )
    List<CuotaPendienteDTO> buscarCuotasPendientes(
            @Param("mes") Integer mes,
            @Param("anio") Integer anio
    );

}
