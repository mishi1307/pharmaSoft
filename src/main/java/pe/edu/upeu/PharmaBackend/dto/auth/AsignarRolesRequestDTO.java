package pe.edu.upeu.PharmaBackend.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AsignarRolesRequestDTO {
    @NotEmpty(message = "Debe asignar al menos un rol")
    private Set<@NotNull(message = "El id del rol es obligatorio") Long> rolIds;
}
