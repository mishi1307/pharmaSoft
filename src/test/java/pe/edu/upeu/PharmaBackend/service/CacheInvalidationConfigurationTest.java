package pe.edu.upeu.PharmaBackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.CacheEvict;
import pe.edu.upeu.PharmaBackend.dto.CategoriaRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.VentaRequestDTO;
import pe.edu.upeu.PharmaBackend.service.impl.CategoriaServiceImpl;
import pe.edu.upeu.PharmaBackend.service.impl.ProductoServiceImpl;
import pe.edu.upeu.PharmaBackend.service.impl.VentaServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

class CacheInvalidationConfigurationTest {
    @Test
    void categoryChangesEvictCategoryAndProductViews() throws Exception {
        assertEvicts(CategoriaServiceImpl.class.getMethod("create", CategoriaRequestDTO.class),
                "categorias", "productos", "productoPorId");
        assertEvicts(CategoriaServiceImpl.class.getMethod("update", Long.class, CategoriaRequestDTO.class),
                "categorias", "productos", "productoPorId");
        assertEvicts(CategoriaServiceImpl.class.getMethod("delete", Long.class),
                "categorias", "productos", "productoPorId");
    }

    @Test
    void productAndSaleChangesEvictProductViews() throws Exception {
        assertEvicts(ProductoServiceImpl.class.getMethod("create", ProductoRequestDTO.class), "productos", "productoPorId");
        assertEvicts(ProductoServiceImpl.class.getMethod("update", Long.class, ProductoRequestDTO.class), "productos", "productoPorId");
        assertEvicts(ProductoServiceImpl.class.getMethod("delete", Long.class), "productos", "productoPorId");
        assertEvicts(VentaServiceImpl.class.getMethod("registrar", VentaRequestDTO.class), "productos", "productoPorId");
        assertEvicts(VentaServiceImpl.class.getMethod("anular", Long.class), "productos", "productoPorId");
    }

    private void assertEvicts(java.lang.reflect.Method method, String... names) {
        CacheEvict annotation = method.getAnnotation(CacheEvict.class);
        assertThat(annotation).isNotNull();
        assertThat(annotation.allEntries()).isTrue();
        assertThat(annotation.cacheNames()).containsExactlyInAnyOrder(names);
    }
}
