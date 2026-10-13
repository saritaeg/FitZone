package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IAccesoRepository extends JpaRepository<Acceso, Integer> {

    // GET 6
    @Query(value = "SELECT COUNT(*) FROM acceso " +
            "WHERE tipo_resultado = 'PERMITIDO' " +
            "AND hora_salida IS NULL",
            nativeQuery = true)
    long contarPersonasDentro();

}
