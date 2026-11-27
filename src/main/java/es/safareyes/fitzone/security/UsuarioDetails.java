
package es.safareyes.fitzone.security;

import es.safareyes.fitzone.modelos.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UsuarioDetails(Usuario usuario) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }

    @Override
    public String getPassword() {
        return usuario.getContraseña();
    }

    @Override
    public String getUsername() {
        return usuario.getNomContraseña();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
