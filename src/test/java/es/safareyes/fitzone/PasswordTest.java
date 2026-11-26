
package es.safareyes.fitzone;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {

    @Test
    void generarPassword() {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("password");

        System.out.println("HASH GENERADO: " + hash);

        System.out.println("COMPROBACION: " +
                encoder.matches("password", hash));
    }
}
