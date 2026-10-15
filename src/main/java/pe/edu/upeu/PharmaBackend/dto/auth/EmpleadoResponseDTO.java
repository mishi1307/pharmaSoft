package pe.edu.upeu.PharmaBackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmpleadoResponseDTO {
    private Long id; private String dni; private String nombres; private String apellidos;
    private String cargo; private String email; private String telefono; private Boolean estado;
    private boolean tieneUsuario;
}
