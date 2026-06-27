package com.medisalud.domain.doctor;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de un médico.
 *
 * <p>Por qué un Value Object y no un UUID directo:
 * - Tipado fuerte: DoctorId no se puede confundir con PatientId en tiempo de compilación.
 * - Encapsula la generación y validación del identificador.
 * - Inmutable por diseño (record).
 *
 * <p>Se usa Java Record (Java 16+) para máxima inmutabilidad y limpieza.
 */
public record DoctorId(UUID value) {

    /**
     * Constructor compacto — valida que el value no sea null.
     */
    public DoctorId {
        Objects.requireNonNull(value, "DoctorId value cannot be null");
    }

    /**
     * Genera un nuevo DoctorId con UUID aleatorio.
     */
    public static DoctorId generate() {
        return new DoctorId(UUID.randomUUID());
    }

    /**
     * Crea un DoctorId a partir de un UUID existente (ej: desde persistencia).
     */
    public static DoctorId of(UUID value) {
        return new DoctorId(value);
    }

    /**
     * Crea un DoctorId a partir de un String UUID (ej: desde HTTP request).
     *
     * @throws IllegalArgumentException si el String no es un UUID válido
     */
    public static DoctorId of(String value) {
        return new DoctorId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
