package com.medisalud.application.patient;

import com.medisalud.application.shared.dto.patient.PatientResult;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.patient.exception.PatientNotFoundException;
import com.medisalud.domain.patient.repository.IPatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso: Obtener un paciente por su ID.
 */
public class GetPatientByIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetPatientByIdUseCase.class);

    private final IPatientRepository patientRepository;

    public GetPatientByIdUseCase(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResult execute(String id) {
        log.debug("Fetching patient with ID: {}", id);
        PatientId patientId = PatientId.of(id);
        return patientRepository.findById(patientId)
                .map(RegisterPatientUseCase::toResult)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }
}
