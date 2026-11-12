package pe.edu.upeu.PharmaBackend.dto;

import java.time.LocalDateTime;

public record AuditoriaEventoResponseDTO(LocalDateTime fecha, String usuario, String accion, String entidad,
                                        Long entidadId, String detalle, String requestId) { }
