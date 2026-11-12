package pe.edu.upeu.PharmaBackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RequestIdFilterTest {
    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void preservesSafeIdInHeaderAndMdcOnlyDuringRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(RequestIdFilter.HEADER, "sale-123");
        AtomicReference<String> observedId = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) ->
                observedId.set(MDC.get("requestId")));

        assertThat(observedId).hasValue("sale-123");
        assertThat(response.getHeader(RequestIdFilter.HEADER)).isEqualTo("sale-123");
        assertThat(MDC.get("requestId")).isNull();
    }

    @Test
    void replacesUnsafeIdWithGeneratedUuid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(RequestIdFilter.HEADER, "bad\nid");

        filter.doFilter(request, response, (req, res) -> { });

        assertThat(response.getHeader(RequestIdFilter.HEADER)).matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}
