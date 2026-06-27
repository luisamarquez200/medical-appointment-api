package com.medisalud.domain.doctor.exception;

import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.shared.exception.NotFoundException;

public class DoctorNotFoundException extends NotFoundException {

    public DoctorNotFoundException(DoctorId id) {
        super("Médico no encontrado con ID: " + id);
    }
}
