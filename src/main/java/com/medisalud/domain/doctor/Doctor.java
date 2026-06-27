package com.medisalud.domain.doctor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root del módulo de médicos.
 *
 * <p>Responsabilidad: Representar a un médico del sistema con sus datos de contacto
 * y especialidad. Es el punto de entrada para cualquier operación sobre médicos.
 *
 * <p>Modelo NO anémico: el comportamiento de actualización vive aquí, no en servicios.
 *
 * <p>Inmutabilidad parcial: el ID nunca cambia; los datos de contacto pueden actualizarse
 * mediante métodos de comportamiento explícitos (no setters públicos).
 *
 * <p>Sin anotaciones de Spring ni JPA — dominio puro, testeable sin contexto.
 */
public final class Doctor {

    private final DoctorId id;
    private String fullName;
    private String specialty;
    private String phone;
    private String email;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Doctor(
            DoctorId id,
            String fullName,
            String specialty,
            String phone,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Crea un nuevo médico.
     *
     * <p>Factory method que encapsula la creación y establece el momento de creación.
     * El ID se genera externamente para permitir la asignación desde infraestructura
     * sin acoplar el dominio a UUID.randomUUID() en tests.
     *
     * @param id        identificador único pre-generado
     * @param fullName  nombre completo (3-100 caracteres, validado en presentación)
     * @param specialty especialidad médica
     * @param phone     teléfono (puede ser null)
     * @param email     email (puede ser null)
     * @param now       instante actual (inyectado desde Clock para testabilidad)
     */
    public static Doctor create(
            DoctorId id,
            String fullName,
            String specialty,
            String phone,
            String email,
            LocalDateTime now) {
        Objects.requireNonNull(id, "Doctor id is required");
        Objects.requireNonNull(fullName, "Doctor fullName is required");
        Objects.requireNonNull(specialty, "Doctor specialty is required");
        Objects.requireNonNull(now, "Creation timestamp is required");

        return new Doctor(id, fullName, specialty, phone, email, now, now);
    }

    /**
     * Reconstruye un Doctor desde persistencia.
     * No es una operación de "creación de negocio" — es una reconstitución.
     */
    public static Doctor reconstitute(
            DoctorId id,
            String fullName,
            String specialty,
            String phone,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Doctor(id, fullName, specialty, phone, email, createdAt, updatedAt);
    }

    // ==================== GETTERS ====================

    public DoctorId getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSpecialty() { return specialty; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", fullName='" + fullName + "', specialty='" + specialty + "'}";
    }
}
