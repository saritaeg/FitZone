package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.AforoDTO;
import es.safareyes.fitzone.servicios.AccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    // GET 6: Consultar personas dentro y aforo máximo
    @GetMapping("/dentro")
    public AforoDTO consultarAforo() {
        return accesoService.consultarAforo();
    }
}