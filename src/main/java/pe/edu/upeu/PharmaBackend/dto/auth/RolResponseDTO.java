package pe.edu.upeu.PharmaBackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RolResponseDTO { private Long id; private String nombre; private String descripcion; private List<String> permisos; }
