package com.medisalud.application.shared.dto.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Command para reprogramar una cita a un nuevo horario.
 */
public record RescheduleAppointmentCommand(
        UUID appointmentId,
        LocalDateTime newDateTime
) {}
