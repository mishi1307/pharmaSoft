package pe.edu.upeu.PharmaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.mapper.UsuarioMapper;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;
import pe.edu.upeu.PharmaBackend.security.JwtService;
import pe.edu.upeu.PharmaBackend.security.PermisosPorRol;
import pe.edu.upeu.PharmaBackend.service.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    public AuthServiceImpl(AuthenticationManager am, JwtService jwt, UsuarioRepository usuarios, UsuarioMapper mapper) {
        authenticationManager = am; jwtService = jwt; usuarioRepository = usuarios; usuarioMapper = mapper;
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
    private Usuario buscar(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));
    }
}
