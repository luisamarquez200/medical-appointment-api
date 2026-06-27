package com.medisalud.application.shared.dto.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Command para crear una nueva cita médica.
 */
public record CreateAppointmentCommand(
        UUID patientId,
        UUID doctorId,
        LocalDateTime dateTime
) {}
