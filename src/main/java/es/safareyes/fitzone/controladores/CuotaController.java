package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.CuotaDTO;
import es.safareyes.fitzone.dto.CuotaPendienteDTO;
import es.safareyes.fitzone.servicios.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;

    // GET 4: Buscar cuotas de un socio
    @GetMapping("/socio/{idSocio}")
    public Page<CuotaDTO> buscarCuotasPorSocio(
            @PathVariable Integer idSocio,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        return cuotaService.buscarCuotasPorSocio(
                idSocio,
                PageRequest.of(pagina, tamanio)
        );
    }

    // GET 5: Buscar cuotas pendientes de un mes y año
    @GetMapping("/pendientes")
    public List<CuotaPendienteDTO> buscarCuotasPendientes(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {

        return cuotaService.buscarCuotasPendientes(mes, anio);
    }
}