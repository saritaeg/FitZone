package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.AforoDTO;
import es.safareyes.fitzone.dto.EntradaDTO;
import es.safareyes.fitzone.dto.ResultadoEntradaDTO;
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

    // POST 4: Registrar entrada
    @PostMapping("/entrada")
    public ResultadoEntradaDTO registrarEntrada(
            @RequestBody EntradaDTO datos) {

        return accesoService.registrarEntrada(datos);
    }
    // POST 5: Registrar salida
    @PostMapping("/salida")
    public ResultadoEntradaDTO registrarSalida(
            @RequestBody EntradaDTO datos) {

        return accesoService.registrarSalida(datos);
    }
}