package pe.edu.upeu.PharmaBackend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import pe.edu.upeu.PharmaBackend.entity.Rol;
import pe.edu.upeu.PharmaBackend.enums.Permiso;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class PermisosPorRolTest {
    private static final Rol ADMIN = new Rol(1L, "ADMIN", null);
    private static final Rol VENDEDOR = new Rol(2L, "VENDEDOR", null);
    private static final Rol ALMACENERO = new Rol(3L, "ALMACENERO", null);
    private List<String> authorities(Rol... roles) {
        return PermisosPorRol.authorities(List.of(roles)).stream().map(GrantedAuthority::getAuthority).toList();
    }
    @Test @DisplayName("ADMIN tiene todos los permisos") void admin() {
        assertThat(PermisosPorRol.de("ADMIN")).containsExactlyInAnyOrder(Permiso.values());
        assertThat(authorities(ADMIN)).contains("ROLE_ADMIN", "REPORTES_VER", "USUARIOS_ADMINISTRAR");
    }
    @Test @DisplayName("VENDEDOR no puede modificar catálogo ni ver reportes") void vendedor() {
        assertThat(authorities(VENDEDOR)).contains("ROLE_VENDEDOR", "VENTAS_REGISTRAR", "CATALOGO_LEER")
            .doesNotContain("REPORTES_VER", "CATALOGO_ESCRIBIR", "USUARIOS_ADMINISTRAR");
    }
    @Test @DisplayName("Los roles combinados suman permisos sin duplicar") void variosRoles() {
        assertThat(authorities(VENDEDOR, ALMACENERO)).contains("ROLE_ALMACENERO", "ROLE_VENDEDOR", "CATALOGO_ESCRIBIR", "VENTAS_REGISTRAR")
            .doesNotContain("REPORTES_VER").doesNotHaveDuplicates();
    }
    @Test @DisplayName("Un rol no reconocido solo aporta su autoridad de rol") void rolDesconocido() {
        assertThat(authorities(new Rol(9L, "AUDITOR", null))).containsExactly("ROLE_AUDITOR");
    }
}
