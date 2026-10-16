package pe.edu.upeu.PharmaBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import pe.edu.upeu.PharmaBackend.enums.Permiso;
import pe.edu.upeu.PharmaBackend.security.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] PUBLIC = {"/api/v1/auth/login", "/api/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error"};
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService,
            UserDetailsService userDetailsService,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsSource,
            RestAuthenticationEntryPoint entryPoint, RestAccessDeniedHandler deniedHandler) throws Exception {
        var jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        http.csrf(AbstractHttpConfigurer::disable).logout(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsSource))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**", "/api/v1/productos/**").hasAuthority(Permiso.CATALOGO_LEER.name())
                .requestMatchers("/api/v1/categorias/**", "/api/v1/productos/**").hasAuthority(Permiso.CATALOGO_ESCRIBIR.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/clientes/**").hasAuthority(Permiso.CLIENTES_LEER.name())
                .requestMatchers("/api/v1/clientes/**").hasAuthority(Permiso.CLIENTES_ESCRIBIR.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/ventas").hasAuthority(Permiso.VENTAS_REGISTRAR.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/ventas/**").hasAuthority(Permiso.VENTAS_LEER.name())
                .requestMatchers("/api/v1/ventas/**").hasAuthority(Permiso.VENTAS_REGISTRAR.name())
                .requestMatchers("/api/v1/usuarios/**", "/api/v1/empleados/**", "/api/v1/roles/**").hasAuthority(Permiso.USUARIOS_ADMINISTRAR.name())
                .anyRequest().authenticated())
            .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(deniedHandler))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { return config.getAuthenticationManager(); }
}
