package pe.edu.upeu.PharmaBackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pe.edu.upeu.PharmaBackend.dto.ErrorResponseDTO;
import java.io.IOException;
import java.time.LocalDateTime;

final class RespuestaErrorSeguridad {
    private static final ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();
    private RespuestaErrorSeguridad() { }
    static void escribir(HttpServletRequest request, HttpServletResponse response,
                         HttpStatus status, String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        JSON.writeValue(response.getWriter(), new ErrorResponseDTO(LocalDateTime.now(), status.value(),
            status.getReasonPhrase(), mensaje, request.getRequestURI(), null));
    }
}
