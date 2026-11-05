package es.safareyes.fitzone;

import es.safareyes.fitzone.repositorios.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FitZoneGetTest {

    @Autowired
    private ISocioRepository socioRepository;

    @Autowired
    private IPlanRepository planRepository;

    @Autowired
    private ICuotaRepository cuotaRepository;

    @Autowired
    private IAccesoRepository accesoRepository;

    @Autowired
    private IActividadRepository actividadRepository;

    @Autowired
    private IInformeRepository informeRepository;

    @Test
    @DisplayName("GET 1: Buscar socios paginados con filtros vacíos")
    public void testBuscarSociosPaginados() {
        assertNotNull(socioRepository.buscarSocios(null, null, null, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("GET 2: Buscar socio por ID")
    public void testBuscarSocioPorId() {
        assertNotNull(socioRepository.buscarPorId(1));
    }

    @Test
    @DisplayName("GET 3: Listar planes de gimnasio")
    public void testBuscarPlanes() {
        assertNotNull(planRepository.buscarPlanes(null));
    }

    @Test
    @DisplayName("GET 4: Consultar cuotas de un socio")
    public void testBuscarCuotasPorSocio() {
        assertNotNull(cuotaRepository.buscarCuotasPorSocio(1, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("GET 5: Consultar cuotas pendientes por mes y año")
    public void testBuscarCuotasPendientes() {
        assertNotNull(cuotaRepository.buscarCuotasPendientes(10, 2026));
    }

    @Test
    @DisplayName("GET 6: Contar personas actualmente dentro del gimnasio")
    public void testContarPersonasDentro() {
        assertNotNull(accesoRepository.contarPersonasDentro());
    }

    @Test
    @DisplayName("GET 7: Consultar actividades y plazas libres por fecha")
    public void testBuscarActividadesPorFecha() {
        assertNotNull(actividadRepository.buscarActividadesPorFecha(LocalDate.of(2026, 11, 6)));
    }

    @Test
    @DisplayName("GET 8: Consultar ingresos por año y plan")
    public void testBuscarIngresosPorAnio() {
        assertNotNull(informeRepository.buscarIngresosPorAnio(2026));
    }
}