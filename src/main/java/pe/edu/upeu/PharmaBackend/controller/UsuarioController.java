package pe.edu.upeu.PharmaBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.service.service.UsuarioService;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController @RequestMapping("/api/v1")
public class UsuarioController {
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }
    @GetMapping("/usuarios") public ResponseEntity<List<UsuarioResponseDTO>> listar() { return ResponseEntity.ok(usuarioService.listar()); }
    @PostMapping("/usuarios") public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request)); }
    @GetMapping("/empleados") public ResponseEntity<List<EmpleadoResponseDTO>> empleados() { return ResponseEntity.ok(usuarioService.listarEmpleados()); }
    @GetMapping("/roles") public ResponseEntity<List<RolResponseDTO>> roles() { return ResponseEntity.ok(usuarioService.listarRoles()); }
    @PutMapping("/usuarios/{id}/roles")
    public ResponseEntity<UsuarioResponseDTO> asignarRoles(@PathVariable Long id,
            @Valid @RequestBody AsignarRolesRequestDTO request, Authentication auth) {
        return ResponseEntity.ok(usuarioService.asignarRoles(id, request.getRolIds(), auth.getName()));
    }
    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> actualizarEstado(@PathVariable Long id,
            @Valid @RequestBody EstadoUsuarioRequestDTO request, Authentication auth) {
        return ResponseEntity.ok(usuarioService.actualizarEstado(id, request.getEstado(), auth.getName()));
    }
}
