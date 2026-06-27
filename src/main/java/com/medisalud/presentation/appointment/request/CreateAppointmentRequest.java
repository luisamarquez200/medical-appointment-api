package com.medisalud.presentation.appointment.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(

        @NotNull(message = "El ID del paciente es obligatorio")
        UUID patientId,

        @NotNull(message = "El ID del médico es obligatorio")
        UUID doctorId,

        @NotNull(message = "La fecha y hora son obligatorias")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateTime
) {}
