
package es.safareyes.fitzone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenDTO {

    private Integer id;
    private String nomContraseña;
    private String rol;
    private String token;

}
