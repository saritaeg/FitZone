package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.ActividadDTO;
import es.safareyes.fitzone.repositorios.IActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final IActividadRepository actividadRepository;

    // GET 7: Consultar actividades por fecha
    public List<ActividadDTO> buscarActividadesPorFecha(LocalDate fecha) {
        return actividadRepository.buscarActividadesPorFecha(fecha);
    }
}