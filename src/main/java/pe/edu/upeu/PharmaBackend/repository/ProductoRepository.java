package pe.edu.upeu.PharmaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.PharmaBackend.entity.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, long id);

    List<Producto> findByCategoriaId(Long categoriaId);
    Page<Producto> findAllByCategoriaId(Long categoriaId, Pageable pageable);
    @Query("""
            select p from Producto p left join p.categoria c
            where (:categoriaId is null or c.id = :categoriaId)
              and (:buscar is null or lower(p.nombre) like lower(concat('%', :buscar, '%'))
                   or lower(c.nombre) like lower(concat('%', :buscar, '%')))
            """)
    Page<Producto> buscarPagina(@Param("categoriaId") Long categoriaId, @Param("buscar") String buscar, Pageable pageable);
    boolean existsByCategoriaId(Long categoriaId);

}
