package com.medisalud.application.shared.dto.doctor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resultado de las operaciones sobre médicos.
 * Salida de los Use Cases — no contiene tipos de dominio internos.
 */
public record DoctorResult(
        UUID id,
        String fullName,
        String specialty,
        String phone,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
