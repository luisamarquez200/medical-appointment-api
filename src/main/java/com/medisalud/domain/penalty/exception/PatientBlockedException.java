package com.medisalud.domain.penalty.exception;

import com.medisalud.domain.shared.exception.ConflictException;

public class PatientBlockedException extends ConflictException {

    public PatientBlockedException(long currentPenalties, int threshold) {
        super("El paciente no puede agendar nuevas citas. Tiene " + currentPenalties +
              " penalizaciones en los últimos 30 días (máximo permitido: " + (threshold - 1) +
              "). Podrá agendar nuevamente una vez transcurran 30 días desde la primera penalización.");
    }
}
