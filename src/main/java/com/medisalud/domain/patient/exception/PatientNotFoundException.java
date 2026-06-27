package com.medisalud.domain.patient.exception;

import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.shared.exception.NotFoundException;

public class PatientNotFoundException extends NotFoundException {

    public PatientNotFoundException(PatientId id) {
        super("Paciente no encontrado con ID: " + id);
    }
}
