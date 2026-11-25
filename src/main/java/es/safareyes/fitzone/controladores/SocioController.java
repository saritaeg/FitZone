
package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.AltaSocioDTO;
import es.safareyes.fitzone.dto.SocioDTO;
import es.safareyes.fitzone.servicios.SocioService;
import es.safareyes.fitzone.dto.ActualizarSocioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import es.safareyes.fitzone.dto.EstadoSocioDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    // GET 1: Buscar socios
    @GetMapping
    public Page<SocioDTO> buscarSocios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String activo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        return socioService.buscarSocios(nombre, apellidos, activo,
                PageRequest.of(pagina, tamanio));
    }

    // GET 2: Buscar socio por ID
    @GetMapping("/{id}")
    public ResponseEntity<SocioDTO> buscarPorId(@PathVariable Integer id) {

        Optional<SocioDTO> socio = socioService.buscarPorId(id);

        return socio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST 1: Dar de alta un socio
    @PostMapping
    public ResponseEntity<SocioDTO> crearSocio(@RequestBody AltaSocioDTO datos) {

        SocioDTO socio = socioService.crearSocio(datos);

        return ResponseEntity.status(HttpStatus.CREATED).body(socio);
    }
    // PUT: Actualizar socio
    @PutMapping("/{id}")
    public SocioDTO actualizarSocio(
            @PathVariable Integer id,
            @RequestBody ActualizarSocioDTO datos) {

        return socioService.actualizarSocio(id, datos);
    }
    // PATCH: Cambiar estado del socio
    @PatchMapping("/{id}/estado")
    public SocioDTO cambiarEstado(
            @PathVariable Integer id,
            @RequestBody EstadoSocioDTO datos) {

        return socioService.cambiarEstado(id, datos);
    }
}
