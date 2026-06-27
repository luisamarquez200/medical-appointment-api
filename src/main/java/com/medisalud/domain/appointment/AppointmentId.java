package com.medisalud.domain.appointment;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una cita.
 *
 * @see com.medisalud.domain.doctor.DoctorId 
 */
public record AppointmentId(UUID value) {

    public AppointmentId {
        Objects.requireNonNull(value, "AppointmentId value cannot be null");
    }

    public static AppointmentId generate() {
        return new AppointmentId(UUID.randomUUID());
    }

    public static AppointmentId of(UUID value) {
        return new AppointmentId(value);
    }

    public static AppointmentId of(String value) {
        return new AppointmentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
