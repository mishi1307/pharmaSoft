package pe.edu.upeu.PharmaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.mapper.UsuarioMapper;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;
import pe.edu.upeu.PharmaBackend.security.JwtService;
import pe.edu.upeu.PharmaBackend.security.PermisosPorRol;
import pe.edu.upeu.PharmaBackend.service.service.AuthService;
import java.nio.charset.StandardCharsets;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    public AuthServiceImpl(AuthenticationManager am, JwtService jwt, UsuarioRepository usuarios,
            UsuarioMapper mapper, PasswordEncoder passwordEncoder) {
        authenticationManager = am; jwtService = jwt; usuarioRepository = usuarios;
        usuarioMapper = mapper; this.passwordEncoder = passwordEncoder;
    }
    @Override @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername().trim();
        log.info("Intento de inicio de sesión: usuario={}", username);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        UserDetails principal = (UserDetails) auth.getPrincipal();
        Usuario user = buscar(principal.getUsername());
        String token = jwtService.generarToken(principal);
        log.info("Inicio de sesión correcto: usuario={} roles={}", user.getUsername(), usuarioMapper.nombresRoles(user.getRoles()));
        return new LoginResponseDTO(token, "Bearer", jwtService.getExpiracionSegundos(), user.getUsername(),
            user.getEmpleado().getNombreCompleto(), usuarioMapper.nombresRoles(user.getRoles()), PermisosPorRol.permisos(user.getRoles()));
    }
    @Override @Transactional(readOnly = true)
    public UsuarioResponseDTO usuarioActual(String username) { return usuarioMapper.toUsuarioResponse(buscar(username)); }
    @Override @Transactional
    public void cambiarPassword(String username, CambioPasswordRequestDTO request) {
        Usuario usuario = buscar(username);
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta");
        }
        if (request.getPasswordActual().equals(request.getPasswordNueva())) {
            throw new ReglaNegocioException("La nueva contraseña debe ser distinta de la actual");
        }
        if (request.getPasswordNueva().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La nueva contraseña no puede superar 72 bytes");
        }
        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
        log.info("Contraseña actualizada para usuario={}", usuario.getUsername());
    }
    private Usuario buscar(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));
    }
}
