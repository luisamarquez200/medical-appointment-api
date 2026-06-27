package com.medisalud.domain.penalty.service;

import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.penalty.exception.PatientBlockedException;

import java.time.LocalDateTime;

/**
 * Domain Service para la lógica de penalizaciones (RN-05).
 *
 * <p>Por qué es un Domain Service:
 * La lógica de penalización involucra dos aggregates (Appointment y Penalty)
 * y una regla de negocio transversal. Ningún aggregate es el "dueño natural"
 * de esta regla → Domain Service.
 *
 * <p>RN-05:
 * <ul>
 *   <li>Si una cita se cancela con menos de 2 horas de antelación → penalización.</li>
 *   <li>Si un paciente tiene 3 o más penalizaciones en los últimos 30 días → bloqueado.</li>
 * </ul>
 *
 * <p>Este servicio evalúa las condiciones. El Use Case decide qué hacer con el resultado
 * (persiste la penalización, lanza excepción de bloqueo, etc.).
 */
public class PenaltyDomainService {

    private static final int PENALTY_BLOCK_THRESHOLD = 3;

    /**
     * Determina si una cancelación debe generar una penalización (RN-05).
     *
     * <p>Delega en el comportamiento rico de Appointment para mantener
     * la lógica de "2 horas antes" encapsulada en la entidad que conoce
     * su propio TimeSlot. Tell Don't Ask.
     *
     * @param appointment      la cita que se está cancelando
     * @param cancellationTime el instante en que se solicita la cancelación
     * @return true si se debe registrar una penalización
     */
    public boolean shouldApplyPenalty(Appointment appointment, LocalDateTime cancellationTime) {
        return appointment.isLateCancellation(cancellationTime);
    }

    /**
     * Verifica si un paciente está bloqueado para agendar citas (RN-05).
     *
     * @param recentPenaltyCount número de penalizaciones del paciente en los últimos 30 días
     * @throws PatientBlockedException si el paciente tiene 3 o más penalizaciones recientes
     */
    public void assertPatientNotBlocked(long recentPenaltyCount) {
        if (recentPenaltyCount >= PENALTY_BLOCK_THRESHOLD) {
            throw new PatientBlockedException(recentPenaltyCount, PENALTY_BLOCK_THRESHOLD);
        }
    }
}
