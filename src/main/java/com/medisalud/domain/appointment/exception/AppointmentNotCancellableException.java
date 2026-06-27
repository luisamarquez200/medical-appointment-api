package com.medisalud.domain.appointment.exception;

import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.AppointmentStatus;
import com.medisalud.domain.shared.exception.ConflictException;

public class AppointmentNotCancellableException extends ConflictException {

    public AppointmentNotCancellableException(AppointmentId id, AppointmentStatus currentStatus) {
        super("La cita con ID " + id + " no puede cancelarse porque su estado actual es: " +
              currentStatus.name() + ". Solo se pueden cancelar citas en estado PROGRAMADA.");
    }
}
