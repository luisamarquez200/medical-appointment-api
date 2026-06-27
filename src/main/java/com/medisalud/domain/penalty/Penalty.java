package com.medisalud.domain.penalty;

import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.patient.PatientId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root del módulo de penalizaciones.
 *
 * <p>Responsabilidad: Registrar una penalización por cancelación tardía de cita (RN-05).
 *
 * <p>Por qué Aggregate Root independiente y no una colección dentro de Patient:
 * - Permite consultas eficientes de penalizaciones recientes sin cargar el agregado Patient.
 * - Cumple SRP: Patient gestiona sus datos; Penalty gestiona el historial de sanciones.
 * - La relación es referencial (PatientId), no de composición.
 *
 * <p>El conteo de penalizaciones activas (últimos 30 días) se realiza vía repositorio
 * en {@link com.medisalud.domain.penalty.service.PenaltyDomainService}.
 */
public final class Penalty {

    private final PenaltyId id;
    private final PatientId patientId;
    private final AppointmentId appointmentId;
    private final LocalDateTime penaltyDateTime;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Penalty(
            PenaltyId id,
            PatientId patientId,
            AppointmentId appointmentId,
            LocalDateTime penaltyDateTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.penaltyDateTime = penaltyDateTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Registra una nueva penalización para un paciente.
     *
     * @param id            identificador único de la penalización
     * @param patientId     paciente penalizado
     * @param appointmentId cita que originó la penalización
     * @param now           instante de la cancelación (Clock)
     */
    public static Penalty create(
            PenaltyId id,
            PatientId patientId,
            AppointmentId appointmentId,
            LocalDateTime now) {
        Objects.requireNonNull(id, "PenaltyId is required");
        Objects.requireNonNull(patientId, "PatientId is required");
        Objects.requireNonNull(appointmentId, "AppointmentId is required");
        Objects.requireNonNull(now, "Penalty timestamp is required");

        return new Penalty(id, patientId, appointmentId, now, now, now);
    }

    /**
     * Reconstruye una Penalty desde persistencia.
     */
    public static Penalty reconstitute(
            PenaltyId id,
            PatientId patientId,
            AppointmentId appointmentId,
            LocalDateTime penaltyDateTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Penalty(id, patientId, appointmentId, penaltyDateTime, createdAt, updatedAt);
    }

    // ==================== GETTERS ====================

    public PenaltyId getId() { return id; }
    public PatientId getPatientId() { return patientId; }
    public AppointmentId getAppointmentId() { return appointmentId; }
    public LocalDateTime getPenaltyDateTime() { return penaltyDateTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Penalty other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Penalty{id=" + id + ", patient=" + patientId + ", at=" + penaltyDateTime + "}";
    }
}
