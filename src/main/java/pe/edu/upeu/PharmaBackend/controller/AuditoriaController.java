package pe.edu.upeu.PharmaBackend.controller;

import pe.edu.upeu.PharmaBackend.dto.AuditoriaEventoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.upeu.PharmaBackend.service.service.AuditoriaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {
    private final AuditoriaService auditoriaService;
    public AuditoriaController(AuditoriaService auditoriaService) { this.auditoriaService = auditoriaService; }

    @GetMapping
    public ResponseEntity<PaginaResponseDTO<AuditoriaEventoResponseDTO>> buscar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {
        return ResponseEntity.ok(auditoriaService.buscar(usuario, accion, desde, hasta, pagina, tamanio));
    }
}
