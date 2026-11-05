package pe.edu.upeu.PharmaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pe.edu.upeu.PharmaBackend.dto.CategoriaResumenDTO;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.repository.CategoriaRepository;
import pe.edu.upeu.PharmaBackend.repository.ProductoRepository;
import pe.edu.upeu.PharmaBackend.service.service.ProductoService;

import java.util.Set;

@Service
public class ProductoServiceImpl implements ProductoService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);
    private static final Set<String> CAMPOS_ORDENABLES =
            Set.of("id", "nombre", "precio", "stock", "fechaCreacion");
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository){
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "productos", allEntries = true)
    public ProductoResponseDTO create(ProductoRequestDTO t) {
        String nombre = t.getNombre().trim();
        if (productoRepository.existsByNombreIgnoreCase(nombre)){
            throw new ReglaNegocioException("Ya existe un producto con el nombre " + nombre);
        }
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setCategoria(buscarCategoriaPorId(t.getCategoriaId()));
        Producto prodCreada = productoRepository.save(producto);
        LOG.info("Producto creado con id={}", prodCreada.getId());
        return convertirResponse(prodCreada);
    }

    private Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "productos", allEntries = true)
    public ProductoResponseDTO update(Long aLong, ProductoRequestDTO t) {
        Producto producto = productoRepository.findById(aLong).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + aLong));

        producto.setNombre(t.getNombre());
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setCategoria(buscarCategoriaPorId(t.getCategoriaId()));

        Producto prodActualizada = productoRepository.save(producto);
        LOG.info("Producto id={} actualizado", prodActualizada.getId());
        return convertirResponse(prodActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productos", key = "'id-' + #p0")
    public ProductoResponseDTO read(Long aLong) {
        Producto producto = productoRepository.findById(aLong)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + aLong));
        return convertirResponse(producto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "productos", allEntries = true)
    public void delete(Long aLong) {
        Producto producto = productoRepository.findById(aLong).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + aLong));
        productoRepository.delete(producto);
        LOG.info("Producto id={} eliminado", aLong);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productos", key = "'todas'")
    public Iterable<ProductoResponseDTO> readAll() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productos",
            key = "#p0 + '-' + #p1 + '-' + #p2 + '-' + #p3")
    public PaginaResponseDTO<ProductoResponseDTO> listarPaginado(
            int pagina,
            int tamanio,
            String ordenarPor,
            String direccion) {
        int paginaSegura = Math.max(0, pagina);
        int tamanioSeguro = Math.min(Math.max(1, tamanio), 100);
        String campo = CAMPOS_ORDENABLES.contains(ordenarPor) ? ordenarPor : "nombre";
        Sort sort = "desc".equalsIgnoreCase(direccion)
                ? Sort.by(campo).descending()
                : Sort.by(campo).ascending();
        Pageable pageable = PageRequest.of(paginaSegura, tamanioSeguro, sort);

        Page<Producto> paginaProductos = productoRepository.findAll(pageable);
        var contenido = paginaProductos.getContent().stream()
                .map(this::convertirResponse)
                .toList();

        return new PaginaResponseDTO<>(
                contenido,
                paginaProductos.getNumber(),
                paginaProductos.getSize(),
                paginaProductos.getTotalElements(),
                paginaProductos.getTotalPages(),
                paginaProductos.isLast());
    }

    private ProductoResponseDTO convertirResponse(Producto producto){
        CategoriaResumenDTO categoriaResumen = producto.getCategoria() != null
                ? new CategoriaResumenDTO(producto.getCategoria().getId(), producto.getCategoria().getNombre())
                : null;

        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getEstado(),
                categoriaResumen,
                producto.getFechaCreacion(),
                producto.getFechaModificacion()
        );
    }
}
