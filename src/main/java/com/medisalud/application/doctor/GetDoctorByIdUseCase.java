package com.medisalud.application.doctor;

import com.medisalud.application.shared.dto.doctor.DoctorResult;
import com.medisalud.domain.doctor.Doctor;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.doctor.exception.DoctorNotFoundException;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso: Obtener un médico por su ID.
 */
public class GetDoctorByIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetDoctorByIdUseCase.class);

    private final IDoctorRepository doctorRepository;

    public GetDoctorByIdUseCase(IDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResult execute(String id) {
        log.debug("Fetching doctor with ID: {}", id);
        DoctorId doctorId = DoctorId.of(id);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        return RegisterDoctorUseCase.toResult(doctor);
    }
}
