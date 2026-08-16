package pe.edu.pe.PharmaBackend.controller;

import org.springframework.web.bind.annotation.*;
import pe.edu.pe.PharmaBackend.entity.Categoria;
import pe.edu.pe.PharmaBackend.service.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public Iterable<Categoria> getCategorias() {
        return categoriaService.readAll();
    }
    @GetMapping("/{id}")
    public Categoria getIdCategorias(@PathVariable Long id) {
        return categoriaService.read(id).get();
    }
    @PostMapping
    public Categoria createCategoria(@RequestBody Categoria categoria) {
        return categoriaService.create(categoria);
    }
}