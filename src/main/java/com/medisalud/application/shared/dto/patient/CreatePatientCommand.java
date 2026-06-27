package com.medisalud.application.shared.dto.patient;

import java.time.LocalDate;

/**
 * Command para registrar un nuevo paciente.
 */
public record CreatePatientCommand(
        String fullName,
        String documentId,
        String phone,
        String email,
        LocalDate birthDate
) {}
