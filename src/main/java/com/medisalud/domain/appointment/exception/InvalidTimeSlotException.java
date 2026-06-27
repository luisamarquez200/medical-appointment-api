package com.medisalud.domain.appointment.exception;

import com.medisalud.domain.shared.exception.BusinessException;

public class InvalidTimeSlotException extends BusinessException {

    public InvalidTimeSlotException(String message) {
        super(message);
    }
}
