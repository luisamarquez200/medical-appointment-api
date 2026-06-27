package com.medisalud.application.shared.dto.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resultado de las operaciones sobre citas médicas.
 */
public record AppointmentResult(
        UUID id,
        UUID patientId,
        UUID doctorId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String status,
        LocalDateTime cancelledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
