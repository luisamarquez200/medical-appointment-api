package com.medisalud.domain.patient;

import com.medisalud.domain.patient.exception.InvalidBirthDateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root del módulo de pacientes.
 *
 * <p>Responsabilidad: Representar a un paciente registrado en el sistema.
 * Contiene datos personales y la lógica de validación de fecha de nacimiento (RN-03).
 *
 * <p>Decisión D01: birthDate es opcional (null). Si es null, se asume edad 0 al agendar.
 * Si birthDate es una fecha futura, se lanza {@link InvalidBirthDateException}.
 *
 * <p>La validación de birthDate ocurre en {@link #validateBirthDate(LocalDate)},
 * que recibe la fecha actual como parámetro para ser testeable con Clock fijo.
 */
public final class Patient {

    private final PatientId id;
    private String fullName;
    private final String documentId;
    private String phone;
    private String email;
    private final LocalDate birthDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Patient(
            PatientId id,
            String fullName,
            String documentId,
            String phone,
            String email,
            LocalDate birthDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.documentId = documentId;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Crea un nuevo paciente en el sistema.
     *
     * <p>La validación de la fecha de nacimiento se delega al caso de uso de agendamiento.
     */
    public static Patient create(
            PatientId id,
            String fullName,
            String documentId,
            String phone,
            String email,
            LocalDate birthDate,
            LocalDateTime now) {
        Objects.requireNonNull(id, "Patient id is required");
        Objects.requireNonNull(fullName, "Patient fullName is required");
        Objects.requireNonNull(documentId, "Patient documentId is required");
        Objects.requireNonNull(phone, "Patient phone is required");
        Objects.requireNonNull(email, "Patient email is required");
        Objects.requireNonNull(now, "Creation timestamp is required");

        return new Patient(id, fullName, documentId, phone, email, birthDate, now, now);
    }

    /**
     * Reconstruye un Patient desde persistencia.
     */
    public static Patient reconstitute(
            PatientId id,
            String fullName,
            String documentId,
            String phone,
            String email,
            LocalDate birthDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Patient(id, fullName, documentId, phone, email, birthDate, createdAt, updatedAt);
    }

    /**
     * Valida que la fecha de nacimiento no sea futura (RN-03).
     *
     * <p>Se invoca desde {@link com.medisalud.application.appointment.CreateAppointmentUseCase}
     * antes de crear la cita. Recibe {@code today} como parámetro para testabilidad.
     *
     * @param today la fecha actual, obtenida desde Clock en el Use Case
     * @throws InvalidBirthDateException si birthDate es posterior a today
     */
    public void validateBirthDate(LocalDate today) {
        if (birthDate != null && birthDate.isAfter(today)) {
            throw new InvalidBirthDateException(birthDate);
        }
    }

    /**
     * Indica si el paciente tiene fecha de nacimiento registrada.
     * Si no la tiene, se asume edad 0 al agendar (RN-03).
     */
    public boolean hasBirthDate() {
        return birthDate != null;
    }

    // ==================== GETTERS ====================

    public PatientId getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDocumentId() { return documentId; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", documentId='" + documentId + "', fullName='" + fullName + "'}";
    }
}
