
package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.LoginDTO;
import es.safareyes.fitzone.dto.TokenDTO;
import es.safareyes.fitzone.servicios.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(
            @RequestBody LoginDTO loginDTO) {

        return ResponseEntity.ok(
                loginService.iniciarSesion(loginDTO)
        );
    }
}
