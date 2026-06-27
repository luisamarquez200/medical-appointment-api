package com.medisalud.application.shared.dto.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Query para listar citas con filtros opcionales (RF-06).
 * Todos los campos son opcionales (null = sin filtro).
 */
public record ListAppointmentsQuery(
        UUID doctorId,
        UUID patientId,
        String status,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd
) {}
