package pe.edu.upeu.PharmaBackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override public void commence(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException ex) throws IOException {
        Object detail = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        RespuestaErrorSeguridad.escribir(request, response, HttpStatus.UNAUTHORIZED,
            detail == null ? "Debe iniciar sesión para acceder a este recurso" : detail.toString());
    }
}
