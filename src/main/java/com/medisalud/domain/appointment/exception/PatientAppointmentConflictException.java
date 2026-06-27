package com.medisalud.domain.appointment.exception;

import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.shared.exception.ConflictException;

public class PatientAppointmentConflictException extends ConflictException {

    public PatientAppointmentConflictException(PatientId patientId, DoctorId doctorId, TimeSlot timeSlot) {
        super("El paciente con ID " + patientId + " ya tiene una cita programada con el médico " +
              doctorId + " en la franja " + timeSlot.getStart() + ".");
    }
}
