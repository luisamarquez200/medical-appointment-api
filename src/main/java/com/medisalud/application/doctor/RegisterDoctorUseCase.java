package com.medisalud.application.doctor;

import com.medisalud.application.shared.dto.doctor.CreateDoctorCommand;
import com.medisalud.application.shared.dto.doctor.DoctorResult;
import com.medisalud.domain.doctor.Doctor;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Caso de uso: Registrar un médico nuevo en el sistema (RF-01).
 *
 * <p>Responsabilidad única: orquestar la creación de un médico.
 * Sin lógica de negocio compleja — los datos vienen validados desde presentación.
 *
 * <p>No tiene anotaciones de Spring — se registra como bean en ApplicationConfig.
 * Esto mantiene la capa de aplicación agnóstica del framework.
 */
public class RegisterDoctorUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterDoctorUseCase.class);

    private final IDoctorRepository doctorRepository;
    private final Clock clock;

    public RegisterDoctorUseCase(IDoctorRepository doctorRepository, Clock clock) {
        this.doctorRepository = doctorRepository;
        this.clock = clock;
    }

    public DoctorResult execute(CreateDoctorCommand command) {
        log.info("Registering new doctor: {} - {}", command.fullName(), command.specialty());

        LocalDateTime now = LocalDateTime.now(clock);
        DoctorId id = DoctorId.generate();

        Doctor doctor = Doctor.create(
                id,
                command.fullName(),
                command.specialty(),
                command.phone(),
                command.email(),
                now
        );

        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor registered successfully with ID: {}", saved.getId());

        return toResult(saved);
    }

    static DoctorResult toResult(Doctor doctor) {
        return new DoctorResult(
                doctor.getId().value(),
                doctor.getFullName(),
                doctor.getSpecialty(),
                doctor.getPhone(),
                doctor.getEmail(),
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }
}
