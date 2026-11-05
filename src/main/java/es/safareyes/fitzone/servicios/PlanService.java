package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.repositorios.IPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final IPlanRepository planRepository;

    // GET 3: Buscar planes
    public List<Plan> buscarPlanes(String tipo) {
        return planRepository.buscarPlanes(tipo);
    }
}