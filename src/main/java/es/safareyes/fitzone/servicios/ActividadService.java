package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.ActividadDTO;
import es.safareyes.fitzone.dto.ReservaDTO;
import es.safareyes.fitzone.dto.ResultadoReservaDTO;
import es.safareyes.fitzone.modelos.*;
import es.safareyes.fitzone.repositorios.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final IActividadRepository actividadRepository;
    private final IReservaRepository reservaRepository;
    private final ISocioRepository socioRepository;
    private final ICuotaRepository cuotaRepository;
    private final ISocioPlanRepository socioPlanRepository;

    // GET 7: Consultar actividades por fecha
    public List<ActividadDTO> buscarActividadesPorFecha(LocalDate fecha) {
        return actividadRepository.buscarActividadesPorFecha(fecha);
    }

    // POST 6: Reservar una actividad
    @Transactional
    public ResultadoReservaDTO reservarActividad(Integer idActividad, ReservaDTO datos) {

        // 1. Buscar socio por DNI
        Socio socio = socioRepository.findByDni(datos.getDni())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ningún socio con ese DNI"
                ));

        // 2. Comprobar que está activo
        if (socio.getActivo() != Socio.EstadoSocio.ACTIVO) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El socio está inactivo"
            );
        }

        // 3. Comprobar que tiene pagada la cuota del mes actual
        LocalDate hoy = LocalDate.now();

        boolean cuotaPagada = cuotaRepository.existsBySocio_IdAndMesAndAnioAndEstado(
                socio.getId(),
                hoy.getMonthValue(),
                hoy.getYear(),
                Cuota.EstadoCuota.PAGADO
        );

        if (!cuotaPagada) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El socio tiene la cuota pendiente"
            );
        }

        // 4. Comprobar que tiene un plan PREMIUM
        boolean tienePremium = socioPlanRepository.tienePlanPremiumVigente(
                socio.getId(),
                hoy
        );

        if (!tienePremium) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El socio necesita un plan PREMIUM"
            );
        }

        // 5. Buscar actividad
        Actividad actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe esa actividad"
                ));

        LocalDateTime ahora = LocalDateTime.now();

        // 6. Comprobar que la actividad todavía no ha comenzado
        if (!actividad.getFechaHoraInicio().isAfter(ahora)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La actividad ya ha comenzado o ha terminado"
            );
        }

        // 7. Comprobar si ya tiene reservada esa actividad
        boolean yaReservada = reservaRepository
                .existsBySocio_IdAndActividad_IdAndEstado(
                        socio.getId(),
                        idActividad,
                        Reserva.EstadoReserva.ACTIVA
                );

        if (yaReservada) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El socio ya tiene reservada esta actividad"
            );
        }

        // 8. Comprobar que no supera las 3 reservas futuras
        long reservasFuturas = reservaRepository.contarReservasFuturas(
                socio.getId(),
                Reserva.EstadoReserva.ACTIVA,
                ahora
        );

        if (reservasFuturas >= 3) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El socio ya tiene 3 reservas futuras"
            );
        }

        // 9. Comprobar que quedan plazas disponibles
        long plazasOcupadas = reservaRepository.countByActividad_IdAndEstado(
                idActividad,
                Reserva.EstadoReserva.ACTIVA
        );

        if (plazasOcupadas >= actividad.getAforoMax()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "No quedan plazas disponibles"
            );
        }

        // 10. Crear reserva
        Reserva reserva = new Reserva();

        reserva.setSocio(socio);
        reserva.setActividad(actividad);
        reserva.setEstado(Reserva.EstadoReserva.ACTIVA);

        reservaRepository.save(reserva);

        return new ResultadoReservaDTO(
                true,
                "Reserva realizada correctamente"
        );
    }
}