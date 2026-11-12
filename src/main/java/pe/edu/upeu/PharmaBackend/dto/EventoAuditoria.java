package pe.edu.upeu.PharmaBackend.dto;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public record EventoAuditoria(String usuario, String accion, String entidad, Long entidadId, String detalle, String requestId) {
    public static EventoAuditoria de(String accion, String entidad, Long entidadId, String detalle) {
        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        String usuario = actual != null && actual.isAuthenticated() ? actual.getName() : "sistema";
        return new EventoAuditoria(usuario, accion, entidad, entidadId, detalle, MDC.get("requestId"));
    }
    public static EventoAuditoria login(String accion, String usuario) {
        return new EventoAuditoria(usuario, accion, "Usuario", null, null, MDC.get("requestId"));
    }
}
