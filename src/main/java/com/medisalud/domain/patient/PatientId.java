package com.medisalud.domain.patient;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de un paciente.
 *
 * @see com.medisalud.domain.doctor.DoctorId 
 */
public record PatientId(UUID value) {

    public PatientId {
        Objects.requireNonNull(value, "PatientId value cannot be null");
    }

    public static PatientId generate() {
        return new PatientId(UUID.randomUUID());
    }

    public static PatientId of(UUID value) {
        return new PatientId(value);
    }

    public static PatientId of(String value) {
        return new PatientId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
