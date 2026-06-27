package com.medisalud.domain.appointment.exception;

import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.shared.exception.NotFoundException;

public class AppointmentNotFoundException extends NotFoundException {

    public AppointmentNotFoundException(AppointmentId id) {
        super("Cita no encontrada con ID: " + id);
    }
}
