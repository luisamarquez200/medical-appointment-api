package com.medisalud.application.shared.dto.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resultado de las operaciones sobre pacientes.
 */
public record PatientResult(
        UUID id,
        String fullName,
        String documentId,
        String phone,
        String email,
        LocalDate birthDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
