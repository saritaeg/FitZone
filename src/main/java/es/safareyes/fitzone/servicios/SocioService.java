
package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.AltaSocioDTO;
import es.safareyes.fitzone.dto.SocioDTO;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.dto.EstadoSocioDTO;
import es.safareyes.fitzone.modelos.SocioPlan;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import es.safareyes.fitzone.repositorios.ISocioPlanRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import es.safareyes.fitzone.repositorios.IPlanRepository;
import es.safareyes.fitzone.dto.ActualizarSocioDTO;
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

        // Buscar el socio solicitado
        Optional<Socio> socioBuscado = socioRepository.buscarPorId(id);

        // Si no existe, devolver vacío
        if (socioBuscado.isEmpty()) {
            return Optional.empty();
        }

        // Obtener el usuario que ha iniciado sesión
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        // Comprobar si el usuario tiene el rol SOCIO
        boolean esSocio = autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_SOCIO"));

        if (esSocio) {

            // Buscar el socio asociado a su cuenta
            Socio socioActual = socioRepository.buscarPorNombreUsuario(autenticacion.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "No tienes permiso para consultar esta ficha"
                    ));

            // Comprobar que está consultando su propia ficha
            if (!socioActual.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No puedes consultar la ficha de otro socio"
                );
            }
        }

        return socioBuscado.map(this::convertirADTO);
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

    // PUT: Actualizar los datos de un socio
    @Transactional
    public SocioDTO actualizarSocio(Integer id, ActualizarSocioDTO datos) {

        // 1. Buscar socio
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ese socio"
                ));

        // 2. Comprobar que el email no pertenece a otro socio
        if (socioRepository.existsByEmailAndIdNot(datos.getEmail(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ese email ya está siendo utilizado"
            );
        }

        // 3. Comprobar el formato de la matrícula
        String matricula = datos.getMatricula();

        if (matricula != null && !matricula.trim().isEmpty()) {
            matricula = matricula.toUpperCase();

            if (!matricula.matches("[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La matrícula no tiene un formato válido"
                );
            }
        } else {
            matricula = null;
        }

        // 4. Actualizar datos de contacto y matrícula
        socio.setTelefono(datos.getTelefono());
        socio.setEmail(datos.getEmail());
        socio.setMatricula(matricula);

        socioRepository.save(socio);

        // 5. Buscar el nuevo plan
        Plan nuevoPlan = planRepository.findById(datos.getIdPlan())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ese plan"
                ));

        // 6. Buscar el plan actual
        Optional<SocioPlan> planActual = socioPlanRepository
                .findFirstBySocio_IdAndFechaFinIsNullOrderByFechaInicioDesc(id);

        if (planActual.isEmpty()) {
            // Si no tiene plan actual, crear la asignación
            SocioPlan nuevaAsignacion = new SocioPlan();
            nuevaAsignacion.setSocio(socio);
            nuevaAsignacion.setPlan(nuevoPlan);
            nuevaAsignacion.setFechaInicio(LocalDate.now());
            nuevaAsignacion.setFechaFin(null);

            socioPlanRepository.save(nuevaAsignacion);

        } else if (!planActual.get().getPlan().getId().equals(nuevoPlan.getId())) {

            SocioPlan anterior = planActual.get();

            // Si el plan empezó hoy, modificamos esa asignación
            if (anterior.getFechaInicio().equals(LocalDate.now())) {
                anterior.setPlan(nuevoPlan);
                socioPlanRepository.save(anterior);

            } else {
                // Finalizar el plan anterior
                anterior.setFechaFin(LocalDate.now().minusDays(1));
                socioPlanRepository.save(anterior);

                // Crear la nueva asignación
                SocioPlan nuevaAsignacion = new SocioPlan();
                nuevaAsignacion.setSocio(socio);
                nuevaAsignacion.setPlan(nuevoPlan);
                nuevaAsignacion.setFechaInicio(LocalDate.now());
                nuevaAsignacion.setFechaFin(null);

                socioPlanRepository.save(nuevaAsignacion);
            }
        }

        return convertirADTO(socio);
    }
    // PATCH: Cambiar estado de un socio
    public SocioDTO cambiarEstado(Integer id, EstadoSocioDTO datos) {

        // 1. Buscar socio
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ese socio"
                ));

        // 2. Comprobar el estado recibido
        if (!datos.getActivo().equalsIgnoreCase("ACTIVO")
                && !datos.getActivo().equalsIgnoreCase("INACTIVO")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado debe ser ACTIVO o INACTIVO"
            );
        }

        // 3. Cambiar estado
        socio.setActivo(Socio.EstadoSocio.valueOf(
                datos.getActivo().toUpperCase()
        ));

        socioRepository.save(socio);

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
