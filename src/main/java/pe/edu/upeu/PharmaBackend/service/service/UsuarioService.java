package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.auth.*;
import java.util.List;
import java.util.Set;

public interface UsuarioService {
    UsuarioResponseDTO crear(UsuarioRequestDTO request);
    List<UsuarioResponseDTO> listar();
    List<EmpleadoResponseDTO> listarEmpleados();
    List<RolResponseDTO> listarRoles();
    UsuarioResponseDTO asignarRoles(Long id, Set<Long> rolIds, String usernameAdministrador);
    UsuarioResponseDTO actualizarEstado(Long id, Boolean estado, String usernameAdministrador);
}
