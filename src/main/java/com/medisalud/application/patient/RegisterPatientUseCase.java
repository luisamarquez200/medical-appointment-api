package com.medisalud.application.patient;

import com.medisalud.application.shared.dto.patient.CreatePatientCommand;
import com.medisalud.application.shared.dto.patient.PatientResult;
import com.medisalud.domain.patient.Patient;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.patient.exception.DuplicatePatientDocumentException;
import com.medisalud.domain.patient.repository.IPatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Caso de uso: Registrar un nuevo paciente (RF-02).
 *
 * <p>Valida unicidad del documento de identidad antes de persistir.
 * La validación de birthDate (RN-03) ocurre en CreateAppointmentUseCase,
 * no aquí — el RF-02 no requiere birthDate para el registro.
 */
public class RegisterPatientUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterPatientUseCase.class);

    private final IPatientRepository patientRepository;
    private final Clock clock;

    public RegisterPatientUseCase(IPatientRepository patientRepository, Clock clock) {
        this.patientRepository = patientRepository;
        this.clock = clock;
    }

    public PatientResult execute(CreatePatientCommand command) {
        log.info("Registering new patient with documentId: {}", command.documentId());

        if (patientRepository.existsByDocumentId(command.documentId())) {
            throw new DuplicatePatientDocumentException(command.documentId());
        }

        LocalDateTime now = LocalDateTime.now(clock);
        Patient patient = Patient.create(
                PatientId.generate(),
                command.fullName(),
                command.documentId(),
                command.phone(),
                command.email(),
                command.birthDate(),
                now
        );

        Patient saved = patientRepository.save(patient);
        log.info("Patient registered successfully with ID: {}", saved.getId());

        return toResult(saved);
    }

    static PatientResult toResult(Patient patient) {
        return new PatientResult(
                patient.getId().value(),
                patient.getFullName(),
                patient.getDocumentId(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getBirthDate(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}
