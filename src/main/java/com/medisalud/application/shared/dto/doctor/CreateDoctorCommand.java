package com.medisalud.application.shared.dto.doctor;

/**
 * Command para registrar un nuevo médico.
 * Entrada del Use Case — independiente del transporte HTTP.
 */
public record CreateDoctorCommand(
        String fullName,
        String specialty,
        String phone,
        String email
) {}
