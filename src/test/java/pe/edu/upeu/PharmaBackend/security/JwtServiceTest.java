package pe.edu.upeu.PharmaBackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import pe.edu.upeu.PharmaBackend.entity.Rol;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    private static final String SECRET = "w9Z6dP/dzykNH/uiI3Z/KGjBlP0H4Ji+Tuq4fm2cTNuLD4jWafu1gBL74V2SJEvy";
    private static final String OTHER_SECRET = "q8Bx3mN0vRk7TzYp1LcW5aHs9JdFg2Ue4OiPlKjMnBvCxZaSdFgHjKlQwErTyUiO";
    private final JwtService jwtService = new JwtService(SECRET, 60);
    private UserDetails vendedor() {
        return User.withUsername("vendedor").password("n/a")
            .authorities(PermisosPorRol.authorities(List.of(new Rol(2L, "VENDEDOR", null)))).build();
    }
    @Test @DisplayName("El JWT lleva sub, authorities, iat y exp")
    void generarYLeerToken() {
        String token = jwtService.generarToken(vendedor());
        var claims = jwtService.leerClaims(token);
        assertThat(token.split("\\.")).hasSize(3);
        assertThat(claims.getSubject()).isEqualTo("vendedor");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
        List<String> authorities = claims.get("authorities", List.class);
        assertThat(authorities).contains("ROLE_VENDEDOR", "VENTAS_REGISTRAR").doesNotContain("REPORTES_VER");
    }
    @Test void tokenAlterado() {
        String token = jwtService.generarToken(vendedor());
        String altered = token.substring(0, token.length() - 2) + (token.endsWith("AA") ? "BB" : "AA");
        assertThatThrownBy(() -> jwtService.extraerUsername(altered)).isInstanceOf(JwtException.class);
    }
    @Test void tokenFirmadoConOtraClave() {
        String token = new JwtService(OTHER_SECRET, 60).generarToken(vendedor());
        assertThatThrownBy(() -> jwtService.extraerUsername(token)).isInstanceOf(JwtException.class);
    }
    @Test void tokenVencido() {
        String token = new JwtService(SECRET, -1).generarToken(vendedor());
        assertThatThrownBy(() -> jwtService.extraerUsername(token)).isInstanceOf(ExpiredJwtException.class);
    }
    @Test void expiracionEnSegundos() { assertThat(jwtService.getExpiracionSegundos()).isEqualTo(3600); }
}
