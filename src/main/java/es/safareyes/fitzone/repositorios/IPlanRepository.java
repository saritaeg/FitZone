package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IPlanRepository extends JpaRepository<Plan, Integer> {
    @Query(value = "SELECT * FROM plan WHERE (:tipo IS NULL OR tipo = :tipo)",
            nativeQuery = true)
    List<Plan> buscarPlanes(@Param("tipo") String tipo);

}
