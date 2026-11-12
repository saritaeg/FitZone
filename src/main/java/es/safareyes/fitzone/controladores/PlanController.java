package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.servicios.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // GET 3: Buscar planes
    @GetMapping
    public List<Plan> buscarPlanes(
            @RequestParam(required = false) String tipo) {

        return planService.buscarPlanes(tipo);
    }
}