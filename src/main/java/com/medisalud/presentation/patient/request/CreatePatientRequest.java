package com.medisalud.presentation.patient.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String fullName,

        @NotBlank(message = "El documento de identidad es obligatorio")
        @Size(min = 7, max = 50, message = "El documento debe tener entre 7 y 50 caracteres")
        String documentId,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\d{7,}$", message = "El teléfono debe tener mínimo 7 dígitos numéricos")
        String phone,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate
) {}
