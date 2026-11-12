package pe.edu.upeu.PharmaBackend.service.impl;

import pe.edu.upeu.PharmaBackend.dto.AuditoriaEventoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.EventoAuditoria;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.AuditoriaEvento;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.repository.AuditoriaEventoRepository;
import pe.edu.upeu.PharmaBackend.service.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {
    private final AuditoriaEventoRepository repository;

    public AuditoriaServiceImpl(AuditoriaEventoRepository repository) { this.repository = repository; }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(EventoAuditoria evento) {
        AuditoriaEvento entidad = new AuditoriaEvento();
        entidad.setUsuario(evento.usuario() == null ? "sistema" : evento.usuario());
        entidad.setAccion(evento.accion());
        entidad.setEntidad(evento.entidad());
        entidad.setEntidadId(evento.entidadId());
        entidad.setDetalle(evento.detalle());
        entidad.setRequestId(evento.requestId());
        repository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<AuditoriaEventoResponseDTO> buscar(String usuario, String accion, LocalDate desde, LocalDate hasta, int pagina, int tamanio) {
        if (pagina < 0 || tamanio < 1 || tamanio > 100) throw new ReglaNegocioException("La página o el tamaño solicitado no son válidos");
        if (desde != null && hasta != null && desde.isAfter(hasta)) throw new ReglaNegocioException("El rango de fechas es inválido");
        LocalDateTime inicio = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? null : hasta.atTime(LocalTime.MAX);
        Page<AuditoriaEvento> resultado = repository.buscar(vacioANulo(usuario), vacioANulo(accion), inicio, fin,
                PageRequest.of(pagina, tamanio, Sort.by("fecha").descending()));
        return new PaginaResponseDTO<>(resultado.getContent().stream().map(e -> new AuditoriaEventoResponseDTO(
                e.getFecha(), e.getUsuario(), e.getAccion(), e.getEntidad(), e.getEntidadId(), e.getDetalle(), e.getRequestId())).toList(),
                resultado.getNumber(), resultado.getSize(), resultado.getTotalElements(), resultado.getTotalPages(), resultado.isLast());
    }

    private static String vacioANulo(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
}
