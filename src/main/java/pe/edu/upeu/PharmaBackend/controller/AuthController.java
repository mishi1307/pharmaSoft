package pe.edu.upeu.PharmaBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/login") public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) { return ResponseEntity.ok(authService.login(request)); }
    @GetMapping("/me") public ResponseEntity<UsuarioResponseDTO> me(Authentication auth) { return ResponseEntity.ok(authService.usuarioActual(auth.getName())); }
    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(Authentication auth, @Valid @RequestBody CambioPasswordRequestDTO request) {
        authService.cambiarPassword(auth.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
