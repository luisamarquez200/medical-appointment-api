package com.medisalud.domain.appointment;

import com.medisalud.domain.appointment.exception.AppointmentNotCancellableException;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root del módulo de citas médicas. Es la entidad central del sistema.
 *
 * <p>Responsabilidad: Representar una cita reservada entre un paciente y un médico
 * en una franja horaria determinada. Gestiona la transición de estados.
 *
 * <p>Comportamiento rico (modelo NO anémico):
 * - {@link #cancel(LocalDateTime)} encapsula la transición PROGRAMADA → CANCELADA
 *   y protege contra cancelaciones inválidas.
 * - {@link #isProgramada()} y {@link #isCancellable()} son comportamientos del dominio,
 *   no lógica filtrada hacia afuera.
 *
 * <p>Decisión: el Aggregate Root referencia IDs de otros agregados (DoctorId, PatientId)
 * en lugar de los objetos completos. Esto respeta los límites del agregado y evita
 * cargas innecesarias (lazy loading, N+1). Es un patrón estándar en DDD.
 *
 * <p>La validación de franjas horarias y penalizaciones ocurre en los Use Cases,
 * no aquí. El Appointment solo es responsable de su propio estado.
 */
public final class Appointment {

    private final AppointmentId id;
    private final PatientId patientId;
    private final DoctorId doctorId;
    private final TimeSlot timeSlot;
    private AppointmentStatus status;
    private LocalDateTime cancelledAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Appointment(
            AppointmentId id,
            PatientId patientId,
            DoctorId doctorId,
            TimeSlot timeSlot,
            AppointmentStatus status,
            LocalDateTime cancelledAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.timeSlot = timeSlot;
        this.status = status;
        this.cancelledAt = cancelledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Crea una nueva cita. El estado inicial siempre es PROGRAMADA (RF-03).
     *
     * @param id        identificador único pre-generado
     * @param patientId referencia al paciente (no el objeto completo — límite de agregado)
     * @param doctorId  referencia al médico
     * @param timeSlot  franja de 30 minutos ya validada por TimeSlotValidatorService
     * @param now       instante actual del Clock (America/Bogota)
     */
    public static Appointment create(
            AppointmentId id,
            PatientId patientId,
            DoctorId doctorId,
            TimeSlot timeSlot,
            LocalDateTime now) {
        Objects.requireNonNull(id, "AppointmentId is required");
        Objects.requireNonNull(patientId, "PatientId is required");
        Objects.requireNonNull(doctorId, "DoctorId is required");
        Objects.requireNonNull(timeSlot, "TimeSlot is required");
        Objects.requireNonNull(now, "Creation timestamp is required");

        return new Appointment(
                id,
                patientId,
                doctorId,
                timeSlot,
                AppointmentStatus.PROGRAMADA,
                null,
                now,
                now);
    }

    /**
     * Reconstruye una Appointment desde persistencia.
     */
    public static Appointment reconstitute(
            AppointmentId id,
            PatientId patientId,
            DoctorId doctorId,
            TimeSlot timeSlot,
            AppointmentStatus status,
            LocalDateTime cancelledAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Appointment(
                id, patientId, doctorId, timeSlot,
                status, cancelledAt, createdAt, updatedAt);
    }

    /**
     * Cancela esta cita (RF-05).
     *
     * <p>Transición de estado: PROGRAMADA → CANCELADA.
     * Si la cita no está en estado PROGRAMADA, lanza {@link AppointmentNotCancellableException}.
     *
     * <p>La decisión de si se aplica penalización (RN-05) es responsabilidad del
     * {@link com.medisalud.application.appointment.CancelAppointmentUseCase}, no de este método.
     * Este método solo cambia el estado de la cita.
     *
     * @param now instante actual del Clock
     * @throws AppointmentNotCancellableException si la cita ya fue cancelada o atendida
     */
    public void cancel(LocalDateTime now) {
        if (!isCancellable()) {
            throw new AppointmentNotCancellableException(this.id, this.status);
        }
        this.status = AppointmentStatus.CANCELADA;
        this.cancelledAt = now;
        this.updatedAt = now;
    }

    /**
     * Verifica si la cita está en estado PROGRAMADA.
     */
    public boolean isProgramada() {
        return AppointmentStatus.PROGRAMADA.equals(this.status);
    }

    /**
     * Una cita es cancelable solo si está en estado PROGRAMADA.
     */
    public boolean isCancellable() {
        return isProgramada();
    }

    /**
     * Calcula si la cancelación ocurre con menos de 2 horas de antelación (RN-05).
     *
     * <p>Este comportamiento pertenece al Appointment porque conoce su timeSlot.
     * Tell Don't Ask: el Use Case le pregunta al Appointment si aplica penalización,
     * en lugar de extraer el timeSlot y calcularlo fuera.
     *
     * @param cancellationTime el instante en que se solicita la cancelación
     * @return true si la cancelación es tardía (menos de 2 horas antes del inicio)
     */
    public boolean isLateCancellation(LocalDateTime cancellationTime) {
        LocalDateTime twoHoursBefore = timeSlot.getStart().minusHours(2);
        return !cancellationTime.isBefore(twoHoursBefore);
    }

    // ==================== GETTERS ====================

    public AppointmentId getId() { return id; }
    public PatientId getPatientId() { return patientId; }
    public DoctorId getDoctorId() { return doctorId; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Appointment{id=" + id +
                ", patient=" + patientId +
                ", doctor=" + doctorId +
                ", slot=" + timeSlot +
                ", status=" + status + "}";
    }
}
