package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.AforoDTO;
import es.safareyes.fitzone.repositorios.IAccesoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final IAccesoRepository accesoRepository;

    // GET 6: Consultar personas dentro y aforo máximo
    public AforoDTO consultarAforo() {

        long personasDentro = accesoRepository.contarPersonasDentro();

        int aforoMaximo = 150;

        return new AforoDTO(personasDentro, aforoMaximo);
    }
}