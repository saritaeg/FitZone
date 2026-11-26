
package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.LoginDTO;
import es.safareyes.fitzone.dto.TokenDTO;
import es.safareyes.fitzone.modelos.Usuario;
import es.safareyes.fitzone.repositorios.IUsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(IUsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenDTO iniciarSesion(LoginDTO loginDTO) {

        Usuario usuario = usuarioRepository
                .findByNomContraseña(loginDTO.getNomContraseña())
                .orElseThrow(() ->
                        new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(
                loginDTO.getContraseña(),
                usuario.getContraseña())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        String token = jwtService.generarToken(
                usuario.getNomContraseña(),
                usuario.getRol().name()
        );

        return new TokenDTO(
                usuario.getId(),
                usuario.getNomContraseña(),
                usuario.getRol().name(),
                token
        );
    }
}
