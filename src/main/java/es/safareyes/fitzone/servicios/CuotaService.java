package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.CuotaDTO;
import es.safareyes.fitzone.dto.CuotaPendienteDTO;
import es.safareyes.fitzone.modelos.Cuota;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final ICuotaRepository cuotaRepository;

    // GET 4: Buscar cuotas de un socio
    public Page<CuotaDTO> buscarCuotasPorSocio(Integer idSocio, Pageable pageable) {

        return cuotaRepository.buscarCuotasPorSocio(idSocio, pageable)
                .map(this::convertirADTO);
    }

    // GET 5: Buscar cuotas pendientes de un mes y año
    public List<CuotaPendienteDTO> buscarCuotasPendientes(Integer mes, Integer anio) {

        return cuotaRepository.buscarCuotasPendientes(mes, anio);
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
}