package pe.edu.upeu.PharmaBackend.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerRequestIdTest {
    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void includesRequestIdInErrorBody() {
        MDC.put("requestId", "error-456");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/productos/999");

        var response = new GlobalExceptionHandler()
                .handleNotFound(new RecursoNoEncontradoException("Producto no encontrado"), request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRequestId()).isEqualTo("error-456");
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}
