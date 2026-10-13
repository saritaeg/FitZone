package test;
import es.safareyes.fitzone.modelos.Socio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class FitZoneGetTest {
    ocios(
                null, null, null, PageRequest.of(0, 10)
            )
                    );

    // GET 2: Buscar socio por ID
    assertNotNull(socioRepository.buscarPorId(1));

    // GET 3: Listar planes
    assertNotNull(planRepository.buscarPlanes(null));

    // GET 4: Consultar cuotas de un socio
    assertNotNull(
            cuotaRepository.buscarCuotasPorSocio(
                    1, PageRequest.of(0, 10)
            )
                    );

    // GET 5: Consultar cuotas pendientes por mes y año
    assertNotNull(
            cuotaRepository.buscarCuotasPendientes(10, 2026)
        );

    // GET 6: Contar personas dentro del gimnasio
    assertNotNull(accesoRepository.contarPersonasDentro());

    // GET 7: Consultar actividades y plazas libres
    assertNotNull(
            actividadRepository.buscarActividadesPorFecha(
                    LocalDate.now()
            )
                    );

    // GET 8: Consultar ingresos por año y plan
    assertNotNull(informeRepository.buscarIngresosPorAnio(2026));
}
}
