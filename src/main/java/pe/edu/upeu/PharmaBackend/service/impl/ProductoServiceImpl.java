package pe.edu.upeu.PharmaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.CategoriaResumenDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.repository.CategoriaRepository;
import pe.edu.upeu.PharmaBackend.repository.ProductoRepository;
import pe.edu.upeu.PharmaBackend.service.service.ProductoService;
import java.util.Set;
import java.math.BigDecimal;
import org.springframework.context.ApplicationEventPublisher;
import pe.edu.upeu.PharmaBackend.dto.EventoAuditoria;

@Service
public class ProductoServiceImpl implements ProductoService {
    private static final Set<String> CAMPOS_ORDENABLES = Set.of("id", "nombre", "precio", "stock");
    private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ApplicationEventPublisher eventos;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, ApplicationEventPublisher eventos){
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"productos", "productoPorId"}, allEntries = true)
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
    @CacheEvict(cacheNames = {"productos", "productoPorId"}, allEntries = true)
    public ProductoResponseDTO update(Long aLong, ProductoRequestDTO t) {
        Producto producto = productoRepository.findById(aLong).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + aLong));

        BigDecimal precioAnterior = producto.getPrecio();
        producto.setNombre(t.getNombre());
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setCategoria(buscarCategoriaPorId(t.getCategoriaId()));

        Producto prodActualizada = productoRepository.save(producto);
        if (precioAnterior.compareTo(prodActualizada.getPrecio()) != 0) {
            eventos.publishEvent(EventoAuditoria.de("PRECIO_MODIFICADO", "Producto", prodActualizada.getId(),
                    precioAnterior + " -> " + prodActualizada.getPrecio()));
        }
        LOG.info("Producto id={} actualizado", prodActualizada.getId());
        return convertirResponse(prodActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productoPorId", key = "#aLong")
    public ProductoResponseDTO read(Long aLong) {
        Producto producto = productoRepository.findById(aLong)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + aLong));
        return convertirResponse(producto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"productos", "productoPorId"}, allEntries = true)
    public void delete(Long aLong) {
        Producto producto = productoRepository.findById(aLong).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + aLong));
        productoRepository.delete(producto);
        eventos.publishEvent(EventoAuditoria.de("PRODUCTO_ELIMINADO", "Producto", producto.getId(), producto.getNombre()));
        LOG.info("Producto id={} eliminado", aLong);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("productos")
    public Iterable<ProductoResponseDTO> readAll() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<ProductoResponseDTO> listar(Long categoriaId, String buscar, int pagina, int tamanio, String ordenarPor, String direccion) {
        if (pagina < 0) throw new ReglaNegocioException("La página no puede ser negativa");
        if (tamanio < 1 || tamanio > 100) throw new ReglaNegocioException("El tamaño de página debe estar entre 1 y 100");
        String campo = ordenarPor == null || ordenarPor.isBlank() ? "nombre" : ordenarPor.trim();
        if (!CAMPOS_ORDENABLES.contains(campo)) throw new ReglaNegocioException("El campo de ordenamiento no está permitido");
        String sentido = direccion == null || direccion.isBlank() ? "asc" : direccion.trim();
        if (!sentido.equalsIgnoreCase("asc") && !sentido.equalsIgnoreCase("desc")) {
            throw new ReglaNegocioException("La dirección de ordenamiento debe ser asc o desc");
        }
        Sort sort = sentido.equalsIgnoreCase("asc") ? Sort.by(campo).ascending() : Sort.by(campo).descending();
        Pageable pageable = PageRequest.of(pagina, tamanio, sort);
        String texto = buscar == null || buscar.isBlank() ? null : buscar.trim();
        Page<ProductoResponseDTO> resultado = productoRepository.buscarPagina(categoriaId, texto, pageable)
                .map(this::convertirResponse);
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isLast());
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
                producto.getFechaModificacion(),
                producto.getCreadoPor(),
                producto.getModificadoPor()
        );
    }
}
