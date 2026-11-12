package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.IngresoDTO;
import es.safareyes.fitzone.repositorios.IInformeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InformeService {

    private final IInformeRepository informeRepository;

    // GET 8: Consultar ingresos por año
    public List<IngresoDTO> buscarIngresosPorAnio(Integer anio) {
        return informeRepository.buscarIngresosPorAnio(anio);
    }
}