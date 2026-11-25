
package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IAccesoRepository extends JpaRepository<Acceso, Integer> {

    // GET 6: Contar personas dentro
    @Query(value = "SELECT COUNT(*) FROM acceso " +
            "WHERE tipo_resultado = 'PERMITIDO' " +
            "AND hora_salida IS NULL",
            nativeQuery = true)
    long contarPersonasDentro();

    // Comprobar si el socio ya está dentro
    boolean existsBySocio_IdAndHoraSalidaIsNullAndTipoResultado(
            Integer idSocio,
            Acceso.TipoResultado tipoResultado
    );

    // Buscar una entrada abierta del socio
    Optional<Acceso> findFirstBySocio_IdAndHoraSalidaIsNullAndTipoResultado(
            Integer idSocio,
            Acceso.TipoResultado tipoResultado
    );
}
