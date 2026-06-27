package com.medisalud.application.shared.dto.doctor;

import java.time.LocalDateTime;

/**
 * Resultado de una franja horaria disponible para un médico.
 */
public record TimeSlotResult(
        LocalDateTime start,
        LocalDateTime end
) {}
