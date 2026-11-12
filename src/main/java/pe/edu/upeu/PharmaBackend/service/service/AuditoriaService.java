package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.AuditoriaEventoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.EventoAuditoria;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import java.time.LocalDate;

public interface AuditoriaService {
    void registrar(EventoAuditoria evento);
    PaginaResponseDTO<AuditoriaEventoResponseDTO> buscar(String usuario, String accion, LocalDate desde, LocalDate hasta, int pagina, int tamanio);
}
