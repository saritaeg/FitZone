package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.ActividadDTO;
import es.safareyes.fitzone.dto.ReservaDTO;
import es.safareyes.fitzone.dto.ResultadoReservaDTO;
import es.safareyes.fitzone.servicios.ActividadService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    // GET 7: Actividades del día con plazas libres
    @GetMapping
    public List<ActividadDTO> buscarActividadesPorFecha(
            @RequestParam LocalDate fecha) {

        return actividadService.buscarActividadesPorFecha(fecha);
    }

    // POST 6: Reservar actividad
    @PostMapping("/{id}/reservas")
    public ResultadoReservaDTO reservarActividad(
            @PathVariable Integer id,
            @RequestBody ReservaDTO datos) {

        return actividadService.reservarActividad(id, datos);
    }
}