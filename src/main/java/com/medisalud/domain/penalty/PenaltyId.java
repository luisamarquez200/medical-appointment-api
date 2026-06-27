package com.medisalud.domain.penalty;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una penalización.
 *
 * @see com.medisalud.domain.doctor.DoctorId 
 */
public record PenaltyId(UUID value) {

    public PenaltyId {
        Objects.requireNonNull(value, "PenaltyId value cannot be null");
    }

    public static PenaltyId generate() {
        return new PenaltyId(UUID.randomUUID());
    }

    public static PenaltyId of(UUID value) {
        return new PenaltyId(value);
    }

    public static PenaltyId of(String value) {
        return new PenaltyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
