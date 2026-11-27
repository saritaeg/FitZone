
package es.safareyes.fitzone.security;

import es.safareyes.fitzone.modelos.Usuario;
import es.safareyes.fitzone.repositorios.IUsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioDetailsService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nomContraseña) {

        Usuario usuario = usuarioRepository
                .findByNomContraseña(nomContraseña)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe ningún usuario con ese nombre"
                ));

        return new UsuarioDetails(usuario);
    }
}
