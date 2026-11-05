package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.SocioDTO;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final ISocioRepository socioRepository;

    // GET 1: Buscar socios con filtros
    public Page<SocioDTO> buscarSocios(
            String nombre,
            String apellidos,
            String activo,
            Pageable pageable) {

        return socioRepository.buscarSocios(nombre, apellidos, activo, pageable)
                .map(this::convertirADTO);
    }

    // GET 2: Buscar socio por ID
    public Optional<SocioDTO> buscarPorId(Integer id) {

        return socioRepository.buscarPorId(id)
                .map(this::convertirADTO);
    }

    // Convertir Socio a SocioDTO
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