package pe.edu.upeu.PharmaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.entity.*;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.mapper.UsuarioMapper;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.service.service.UsuarioService;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final UsuarioRepository usuarios; private final EmpleadoRepository empleados;
    private final RolRepository roles; private final PasswordEncoder encoder; private final UsuarioMapper mapper;
    public UsuarioServiceImpl(UsuarioRepository usuarios, EmpleadoRepository empleados, RolRepository roles, PasswordEncoder encoder, UsuarioMapper mapper) {
        this.usuarios = usuarios; this.empleados = empleados; this.roles = roles; this.encoder = encoder; this.mapper = mapper;
    }
    @Override @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        Empleado empleado = empleados.findById(request.getEmpleadoId()).orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con id: " + request.getEmpleadoId()));
        List<Rol> rolesEncontrados = roles.findAllById(request.getRolIds());
        if (rolesEncontrados.size() != request.getRolIds().size()) {
            Set<Long> faltantes = new HashSet<>(request.getRolIds());
            faltantes.removeAll(rolesEncontrados.stream().map(Rol::getId).collect(Collectors.toSet()));
            throw new RecursoNoEncontradoException("Roles no encontrados: " + faltantes);
        }
        if (!Boolean.TRUE.equals(empleado.getEstado())) throw new ReglaNegocioException("El empleado " + empleado.getNombreCompleto() + " está inactivo");
        if (usuarios.existsByEmpleadoId(empleado.getId())) throw new ReglaNegocioException("El empleado " + empleado.getNombreCompleto() + " ya tiene un usuario");
        if (usuarios.existsByUsernameIgnoreCase(username)) throw new ReglaNegocioException("Ya existe un usuario con el nombre: " + username);
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) throw new ReglaNegocioException("La contraseña no puede superar 72 bytes");
        Usuario user = new Usuario(); user.setUsername(username); user.setPassword(encoder.encode(request.getPassword()));
        user.setEstado(true); user.setEmpleado(empleado); user.setRoles(new HashSet<>(rolesEncontrados));
        Usuario saved = usuarios.save(user);
        log.info("Usuario registrado id={} usuario={} roles={}", saved.getId(), username, mapper.nombresRoles(rolesEncontrados));
        return mapper.toUsuarioResponse(saved);
    }
    @Override @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() { return usuarios.findAll(Sort.by("username")).stream().map(mapper::toUsuarioResponse).toList(); }
    @Override @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarEmpleados() {
        Set<Long> conUsuario = usuarios.findEmpleadoIdsConUsuario();
        return empleados.findAll(Sort.by("apellidos", "nombres")).stream().map(e -> mapper.toEmpleadoResponse(e, conUsuario.contains(e.getId()))).toList();
    }
    @Override @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() { return roles.findAll(Sort.by("nombre")).stream().map(mapper::toRolResponse).toList(); }
}
