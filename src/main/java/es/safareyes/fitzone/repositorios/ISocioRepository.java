package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ISocioRepository extends JpaRepository<Socio, Integer> {

    @Query(
            value = "SELECT * FROM socio " +
                    "WHERE (:nombre IS NULL OR LOWER(nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
                    "AND (:apellidos IS NULL OR LOWER(apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) " +
                    "AND (:activo IS NULL OR activo = :activo)",
            countQuery = "SELECT COUNT(*) FROM socio " +
                    "WHERE (:nombre IS NULL OR LOWER(nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
                    "AND (:apellidos IS NULL OR LOWER(apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) " +
                    "AND (:activo IS NULL OR activo = :activo)",
            nativeQuery = true
    )
    Page<Socio> buscarSocios(
            @Param("nombre") String nombre,
            @Param("apellidos") String apellidos,
            @Param("activo") String activo,
            Pageable pageable
    );
    @Query(
            value = "SELECT * FROM socio WHERE id = :id",
            nativeQuery = true 
    )
    Optional<Socio> buscarPorId(@Param("id") Integer id);

}
