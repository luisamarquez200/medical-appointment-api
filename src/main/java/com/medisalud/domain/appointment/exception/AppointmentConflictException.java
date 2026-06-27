package com.medisalud.domain.appointment.exception;

import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.shared.exception.ConflictException;

public class AppointmentConflictException extends ConflictException {

    public AppointmentConflictException(DoctorId doctorId, TimeSlot timeSlot) {
        super("El médico con ID " + doctorId + " ya tiene una cita programada en la franja " +
              timeSlot.getStart() + ". Por favor, elija otro horario.");
    }
}
