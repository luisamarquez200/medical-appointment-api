package com.medisalud.presentation.appointment.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleAppointmentRequest(

        @NotNull(message = "La nueva fecha y hora son obligatorias")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime newDateTime
) {}
