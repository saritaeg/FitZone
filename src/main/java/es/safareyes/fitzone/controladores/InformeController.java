package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.IngresoDTO;
import es.safareyes.fitzone.servicios.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;

    // GET 8: Consultar ingresos por año
    @GetMapping("/ingresos")
    public List<IngresoDTO> buscarIngresosPorAnio(
            @RequestParam Integer anio) {

        return informeService.buscarIngresosPorAnio(anio);
    }
}