package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.CuotaDTO;
import es.safareyes.fitzone.dto.CuotaPendienteDTO;
import es.safareyes.fitzone.dto.GeneracionCuotasDTO;
import es.safareyes.fitzone.modelos.Cuota;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.modelos.SocioPlan;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import es.safareyes.fitzone.repositorios.ISocioPlanRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final ICuotaRepository cuotaRepository;
    private final ISocioRepository socioRepository;
    private final ISocioPlanRepository socioPlanRepository;
    // GET 4: Buscar cuotas de un socio
    public Page<CuotaDTO> buscarCuotasPorSocio(Integer idSocio, Pageable pageable) {

        // Obtener el usuario que ha iniciado sesión
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        // Comprobar si tiene el rol SOCIO
        boolean esSocio = autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_SOCIO"));

        if (esSocio) {

            // Buscar el socio asociado a su cuenta
            Socio socioActual = socioRepository.buscarPorNombreUsuario(autenticacion.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "No tienes permiso para consultar estas cuotas"
                    ));

            // Comprobar que está consultando sus propias cuotas
            if (!socioActual.getId().equals(idSocio)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No puedes consultar las cuotas de otro socio"
                );
            }
        }

        return cuotaRepository.buscarCuotasPorSocio(idSocio, pageable)
                .map(this::convertirADTO);
    }

    // GET 5: Buscar cuotas pendientes de un mes y año
    public List<CuotaPendienteDTO> buscarCuotasPendientes(Integer mes, Integer anio) {

        return cuotaRepository.buscarCuotasPendientes(mes, anio);
    }

    // POST 2: Generar cuotas mensuales
    @Transactional
    public GeneracionCuotasDTO generarCuotas(Integer mes, Integer anio) {

        // Comprobar que el mes sea válido
        if (mes < 1 || mes > 12 || anio < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El mes o el año no son válidos"
            );
        }

        // 1. Inactivar socios con dos o más cuotas pendientes
        int sociosInactivos = socioRepository.inactivarSociosConDosCuotasPendientes();

        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        List<SocioPlan> planesVigentes =
                socioPlanRepository.buscarPlanesVigentesEnFecha(inicioMes, finMes);

        int cuotasCreadas = 0;

        // 3. Recorrer los socios y generar sus cuotas
        for (SocioPlan socioPlan : planesVigentes) {

            Socio socio = socioPlan.getSocio();

            // Solo generar cuotas para socios activos
            if (socio.getActivo() != Socio.EstadoSocio.ACTIVO) {
                continue;
            }

            // Comprobar que no tenga ya una cuota de ese mes
            boolean existeCuota = cuotaRepository.existsBySocio_IdAndMesAndAnio(
                    socio.getId(),
                    mes,
                    anio
            );

            if (existeCuota) {
                continue;
            }

            // Crear la cuota
            Cuota cuota = new Cuota();

            cuota.setMes(mes);
            cuota.setAnio(anio);
            cuota.setImporte(socioPlan.getPlan().getPrecioMensual());
            cuota.setEstado(Cuota.EstadoCuota.PENDIENTE);
            cuota.setFechaPago(null);
            cuota.setSocio(socio);
            cuota.setPlan(socioPlan.getPlan());

            cuotaRepository.save(cuota);

            cuotasCreadas++;
        }

        // 4. Devolver el resultado
        return new GeneracionCuotasDTO(cuotasCreadas, sociosInactivos);
    }

    // Convertir Cuota a CuotaDTO
    private CuotaDTO convertirADTO(Cuota cuota) {

        return new CuotaDTO(
                cuota.getId(),
                cuota.getMes(),
                cuota.getAnio(),
                cuota.getImporte(),
                cuota.getEstado().name(),
                cuota.getFechaPago(),
                cuota.getPlan().getNombre()
        );
    }
    // POST 3: Registrar el pago de una cuota
    @Transactional
    public CuotaDTO pagarCuota(Integer idCuota) {

        // Buscar la cuota
        Cuota cuota = cuotaRepository.findById(idCuota)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe esa cuota"
                ));

        // Comprobar si ya está pagada
        if (cuota.getEstado() == Cuota.EstadoCuota.PAGADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta cuota ya está pagada"
            );
        }

        // Cambiar el estado y guardar la fecha del pago
        cuota.setEstado(Cuota.EstadoCuota.PAGADO);
        cuota.setFechaPago(java.time.LocalDateTime.now());

        cuotaRepository.save(cuota);

        return convertirADTO(cuota);
    }
}