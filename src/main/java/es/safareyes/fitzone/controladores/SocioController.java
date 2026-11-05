package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.SocioDTO;
import es.safareyes.fitzone.servicios.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    // GET 1: Buscar socios con filtros
    @GetMapping
    public Page<SocioDTO> buscarSocios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String activo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        return socioService.buscarSocios(
                nombre,
                apellidos,
                activo,
                PageRequest.of(pagina, tamanio)
        );
    }

    // GET 2: Buscar socio por ID
    @GetMapping("/{id}")
    public ResponseEntity<SocioDTO> buscarPorId(@PathVariable Integer id) {

        return socioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}