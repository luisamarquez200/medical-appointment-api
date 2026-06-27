package com.medisalud.domain.appointment;

/**
 * Estados posibles de una cita médica.
 *
 * <p>La transición de estados válidas son:
 * <pre>
 *   PROGRAMADA → CANCELADA  (por RF-05 o RN-06)
 *   PROGRAMADA → ATENDIDA   (cuando el médico registra la atención — extensión futura)
 * </pre>
 *
 * <p>Una cita CANCELADA o ATENDIDA no puede volver a PROGRAMADA.
 * Esta regla la aplica {@link Appointment#cancel(java.time.LocalDateTime)}.
 */
public enum AppointmentStatus {

    /** Estado inicial al crear la cita. */
    PROGRAMADA,

    /** La cita fue cancelada por el paciente o el sistema. */
    CANCELADA,

    /** El paciente asistió y fue atendido por el médico. */
    ATENDIDA
}
