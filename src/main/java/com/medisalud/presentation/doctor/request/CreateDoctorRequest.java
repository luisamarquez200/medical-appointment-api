package com.medisalud.presentation.doctor.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO para registrar un médico.
 */
public record CreateDoctorRequest(

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String fullName,

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
        String specialty,

        @Pattern(regexp = "^\\d{7,}$", message = "El teléfono debe tener mínimo 7 dígitos numéricos")
        String phone,

        @Email(message = "El email debe tener un formato válido")
        String email
) {}
