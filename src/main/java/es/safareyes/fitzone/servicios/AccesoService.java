package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.AforoDTO;
import es.safareyes.fitzone.dto.EntradaDTO;
import es.safareyes.fitzone.dto.ResultadoEntradaDTO;
import es.safareyes.fitzone.modelos.Acceso;
import es.safareyes.fitzone.modelos.Cuota;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.repositorios.IAccesoRepository;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final IAccesoRepository accesoRepository;
    private final ISocioRepository socioRepository;
    private final ICuotaRepository cuotaRepository;

    // GET 6: Consultar personas dentro y aforo máximo
    public AforoDTO consultarAforo() {

        long personasDentro = accesoRepository.contarPersonasDentro();

        int aforoMaximo = 150;

        return new AforoDTO(personasDentro, aforoMaximo);
    }

    // POST 4: Registrar entrada al gimnasio
    @Transactional
    public ResultadoEntradaDTO registrarEntrada(EntradaDTO datos) {

        // 1. Buscar socio por DNI
        Socio socio = socioRepository.findByDni(datos.getDni())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ningún socio con ese DNI"
                ));

        // 2. Comprobar si está activo
        if (socio.getActivo() != Socio.EstadoSocio.ACTIVO) {
            return guardarEntradaDenegada(
                    socio,
                    Acceso.MotivoDenegacion.INACTIVO
            );
        }

        // 3. Comprobar si ha pagado la cuota del mes actual
        LocalDate hoy = LocalDate.now();

        boolean cuotaPagada = cuotaRepository.existsBySocio_IdAndMesAndAnioAndEstado(
                socio.getId(),
                hoy.getMonthValue(),
                hoy.getYear(),
                Cuota.EstadoCuota.PAGADO
        );

        if (!cuotaPagada) {
            return guardarEntradaDenegada(
                    socio,
                    Acceso.MotivoDenegacion.CUOTA_PENDIENTE
            );
        }

        // 4. Comprobar si ya está dentro
        boolean yaDentro = accesoRepository
                .existsBySocio_IdAndHoraSalidaIsNullAndTipoResultado(
                        socio.getId(),
                        Acceso.TipoResultado.PERMITIDO
                );

        if (yaDentro) {
            return guardarEntradaDenegada(
                    socio,
                    Acceso.MotivoDenegacion.YA_DENTRO
            );
        }

        // 5. Comprobar aforo máximo
        long personasDentro = accesoRepository.contarPersonasDentro();

        if (personasDentro >= 150) {
            return guardarEntradaDenegada(
                    socio,
                    Acceso.MotivoDenegacion.AFORO_COMPLETO
            );
        }

        // 6. Registrar entrada permitida
        Acceso acceso = new Acceso();

        acceso.setFecha(hoy);
        acceso.setHoraEntrada(LocalDateTime.now());
        acceso.setHoraSalida(null);
        acceso.setTipoResultado(Acceso.TipoResultado.PERMITIDO);
        acceso.setMotivoDenegacion(null);
        acceso.setSocio(socio);

        accesoRepository.save(acceso);

        return new ResultadoEntradaDTO(
                true,
                "Entrada permitida",
                null
        );
    }
    // POST 5: Registrar salida del gimnasio
    @Transactional
    public ResultadoEntradaDTO registrarSalida(EntradaDTO datos) {

        // 1. Buscar socio por DNI
        Socio socio = socioRepository.findByDni(datos.getDni())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ningún socio con ese DNI"
                ));

        // 2. Buscar su entrada permitida sin hora de salida
        Acceso acceso = accesoRepository
                .findFirstBySocio_IdAndHoraSalidaIsNullAndTipoResultado(
                        socio.getId(),
                        Acceso.TipoResultado.PERMITIDO
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "El socio no tiene ninguna entrada pendiente de salida"
                ));

        // 3. Registrar la hora de salida
        acceso.setHoraSalida(LocalDateTime.now());

        accesoRepository.save(acceso);

        return new ResultadoEntradaDTO(
                true,
                "Salida registrada correctamente",
                null
        );
    }
    // Guardar los intentos de entrada denegados
    private ResultadoEntradaDTO guardarEntradaDenegada(
            Socio socio,
            Acceso.MotivoDenegacion motivo) {

        Acceso acceso = new Acceso();

        acceso.setFecha(LocalDate.now());
        acceso.setHoraEntrada(LocalDateTime.now());
        acceso.setHoraSalida(null);
        acceso.setTipoResultado(Acceso.TipoResultado.DENEGADO);
        acceso.setMotivoDenegacion(motivo);
        acceso.setSocio(socio);

        accesoRepository.save(acceso);

        return new ResultadoEntradaDTO(
                false,
                "Entrada denegada",
                motivo.name()
        );
    }
}