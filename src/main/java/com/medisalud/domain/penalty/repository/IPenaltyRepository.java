package com.medisalud.domain.penalty.repository;

import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.penalty.Penalty;

import java.time.LocalDateTime;

/**
 * Puerto de salida del dominio para persistencia de penalizaciones.
 */
public interface IPenaltyRepository {

    /**
     * Persiste una penalización.
     */
    Penalty save(Penalty penalty);

    /**
     * Cuenta las penalizaciones de un paciente posteriores a una fecha (RN-05).
     *
     * <p>El Use Case invoca este método con {@code since = now - 30 días}
     * para verificar si el paciente está bloqueado.
     *
     * @param patientId el paciente a consultar
     * @param since     fecha desde la que contar (últimos 30 días)
     * @return número de penalizaciones en el período
     */
    long countByPatientIdAndPenaltyDateTimeAfter(PatientId patientId, LocalDateTime since);
}
