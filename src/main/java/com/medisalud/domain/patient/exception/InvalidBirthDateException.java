package com.medisalud.domain.patient.exception;

import com.medisalud.domain.shared.exception.BusinessException;

import java.time.LocalDate;

public class InvalidBirthDateException extends BusinessException {

    public InvalidBirthDateException(LocalDate birthDate) {
        super("La fecha de nacimiento no puede ser futura: " + birthDate +
              ". Un paciente no puede nacer en el futuro.");
    }
}
