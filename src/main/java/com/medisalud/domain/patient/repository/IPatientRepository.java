package com.medisalud.domain.patient.repository;

import com.medisalud.domain.patient.Patient;
import com.medisalud.domain.patient.PatientId;

import java.util.Optional;

/**
 * Puerto de salida del dominio para persistencia de pacientes.
 *
 * @see com.medisalud.domain.doctor.repository.IDoctorRepository 
 */
public interface IPatientRepository {

    /**
     * Persiste un paciente.
     */
    Patient save(Patient patient);

    /**
     * Busca un paciente por su ID de dominio.
     */
    Optional<Patient> findById(PatientId id);

    /**
     * Verifica si existe un paciente con ese documento de identidad (RF-02: unicidad).
     *
     * @param documentId el documento a verificar
     * @return true si ya está registrado en el sistema
     */
    boolean existsByDocumentId(String documentId);
}
