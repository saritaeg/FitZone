package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.CuotaDTO;
import es.safareyes.fitzone.dto.CuotaPendienteDTO;
import es.safareyes.fitzone.dto.GeneracionCuotasDTO;
import es.safareyes.fitzone.servicios.CuotaService;
import es.safareyes.fitzone.dto.PagoCuotaDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // POST 2: Generar cuotas mensuales
    @PostMapping("/generar")
    public ResponseEntity<GeneracionCuotasDTO> generarCuotas(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cuotaService.generarCuotas(mes, anio));
    }
    // POST 3: Registrar el pago de una cuota
    @PostMapping("/pagar")
    public CuotaDTO pagarCuota(@RequestBody PagoCuotaDTO datos) {

        return cuotaService.pagarCuota(datos.getIdCuota());
    }
}