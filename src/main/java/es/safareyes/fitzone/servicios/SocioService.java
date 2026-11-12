
package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.AltaSocioDTO;
import es.safareyes.fitzone.dto.SocioDTO;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.modelos.SocioPlan;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import es.safareyes.fitzone.repositorios.ISocioPlanRepository;
import es.safareyes.fitzone.repositorios.IPlanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final ISocioRepository socioRepository;
    private final ISocioPlanRepository socioPlanRepository;
    private final IPlanRepository planRepository;

    // GET 1: Buscar socios
    public Page<SocioDTO> buscarSocios(String nombre, String apellidos, String activo, Pageable pageable) {
        return socioRepository.buscarSocios(nombre, apellidos, activo, pageable)
                .map(this::convertirADTO);
    }

    // GET 2: Buscar socio por ID
    public Optional<SocioDTO> buscarPorId(Integer id) {
        return socioRepository.buscarPorId(id).map(this::convertirADTO);
    }

    // POST 1: Dar de alta un socio
    @Transactional
    public SocioDTO crearSocio(AltaSocioDTO datos) {

        // Comprobar que el DNI tiene un formato válido
        String dni = datos.getDni().toUpperCase();

        if (!dni.matches("[0-9]{8}[A-Z]")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI no tiene un formato válido");
        }

        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(dni.substring(0, 8));

        if (dni.charAt(8) != letras.charAt(numero % 23)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La letra del DNI no es correcta");
        }

        // Comprobar que el DNI y el email no existen
        if (socioRepository.existsByDni(dni)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un socio con ese DNI");
        }

        if (socioRepository.existsByEmail(datos.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un socio con ese email");
        }

        // Comprobar la matrícula si se ha introducido
        String matricula = datos.getMatricula();

        if (matricula != null && !matricula.trim().isEmpty()) {
            matricula = matricula.toUpperCase();

            // Comprobar el formato de matrícula según PostgreSQL
            if (!matricula.matches("[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La matrícula no tiene un formato válido");
            }
        } else {
            matricula = null;
        }

        // Buscar el plan elegido
        Plan plan = planRepository.findById(datos.getIdPlan())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe ese plan"));

        // Crear el socio
        Socio socio = new Socio();
        socio.setNombre(datos.getNombre());
        socio.setApellidos(datos.getApellidos());
        socio.setMatricula(matricula);
        socio.setTelefono(datos.getTelefono());
        socio.setEmail(datos.getEmail());
        socio.setDni(dni);
        socio.setFechaAlta(LocalDateTime.now());
        socio.setActivo(Socio.EstadoSocio.ACTIVO);

        socio = socioRepository.save(socio);

        // Asociar el socio con el plan elegido
        SocioPlan socioPlan = new SocioPlan();
        socioPlan.setSocio(socio);
        socioPlan.setPlan(plan);
        socioPlan.setFechaInicio(LocalDate.now());
        socioPlan.setFechaFin(null);

        socioPlanRepository.save(socioPlan);

        return convertirADTO(socio);
    }

    // Convertir socio a DTO
    private SocioDTO convertirADTO(Socio socio) {
        return new SocioDTO(
                socio.getId(),
                socio.getNombre(),
                socio.getApellidos(),
                socio.getMatricula(),
                socio.getTelefono(),
                socio.getEmail(),
                socio.getDni(),
                socio.getFechaAlta(),
                socio.getActivo().name()
        );
    }
}
