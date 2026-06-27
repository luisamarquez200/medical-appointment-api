package com.medisalud.application.shared.dto.appointment;

import java.util.UUID;

/**
 * Command para cancelar una cita existente.
 */
public record CancelAppointmentCommand(UUID appointmentId) {}
