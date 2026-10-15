package pe.edu.upeu.PharmaBackend.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.entity.*;
import pe.edu.upeu.PharmaBackend.security.PermisosPorRol;
import java.util.Collection;
import java.util.List;

@Component
public class UsuarioMapper {
    public UsuarioResponseDTO toUsuarioResponse(Usuario u) {
        Empleado e = u.getEmpleado();
        return new UsuarioResponseDTO(u.getId(), u.getUsername(), e.getId(), e.getNombreCompleto(), e.getCargo(), nombresRoles(u.getRoles()), PermisosPorRol.permisos(u.getRoles()), u.getEstado(), u.getFechaCreacion());
    }
    public EmpleadoResponseDTO toEmpleadoResponse(Empleado e, boolean tieneUsuario) {
        return new EmpleadoResponseDTO(e.getId(), e.getDni(), e.getNombres(), e.getApellidos(), e.getCargo(), e.getEmail(), e.getTelefono(), e.getEstado(), tieneUsuario);
    }
    public RolResponseDTO toRolResponse(Rol r) { return new RolResponseDTO(r.getId(), r.getNombre(), r.getDescripcion(), PermisosPorRol.permisos(List.of(r))); }
    public List<String> nombresRoles(Collection<Rol> roles) { return roles.stream().map(Rol::getNombre).sorted().toList(); }
}
