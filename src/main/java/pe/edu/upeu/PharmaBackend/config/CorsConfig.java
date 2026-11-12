package pe.edu.upeu.PharmaBackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class CorsConfig {
    private final List<String> origins;
    public CorsConfig(@Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:4200,http://localhost:4201,http://localhost:3000,http://localhost:8081,http://127.0.0.1:8081}") String[] origins) {
        this.origins = List.of(origins);
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Accept", "Authorization", "Origin", "X-Request-Id"));
        config.setExposedHeaders(List.of("Location", "X-Request-Id"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
