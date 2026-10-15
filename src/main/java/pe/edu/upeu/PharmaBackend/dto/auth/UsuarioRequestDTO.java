package pe.edu.upeu.PharmaBackend.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter @Setter @NoArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "El usuario es obligatorio") @Size(min = 4, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El usuario solo admite letras, números, punto, guion y guion bajo")
    private String username;
    @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, max = 72)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe contener al menos una letra y un número")
    private String password;
    @NotNull(message = "El empleado es obligatorio") private Long empleadoId;
    @NotEmpty(message = "Debe asignar al menos un rol") private Set<@NotNull Long> rolIds;
}
