package com.medisalud.infrastructure.config;

import com.medisalud.application.appointment.*;
import com.medisalud.application.doctor.*;
import com.medisalud.application.patient.*;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.appointment.service.TimeSlotValidatorService;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import com.medisalud.domain.patient.repository.IPatientRepository;
import com.medisalud.domain.penalty.repository.IPenaltyRepository;
import com.medisalud.domain.penalty.service.PenaltyDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Registra todos los Use Cases y Domain Services como beans de Spring.
 *
 * <p>Por qué aquí y no con @Component en cada Use Case:
 * Los Use Cases pertenecen a la capa de aplicación, que no debe depender de Spring.
 * Al registrar los beans aquí (infraestructura), la capa de aplicación permanece
 * agnóstica del framework. Es la implementación del principio de inversión de dependencias
 * a nivel de framework.
 *
 * <p>Si en el futuro se quisiera usar los Use Cases con otro framework (Quarkus, Micronaut),
 * solo cambia este archivo de configuración.
 */
@Configuration
public class ApplicationConfig {

    // ==================== DOMAIN SERVICES ====================

    @Bean
    public TimeSlotValidatorService timeSlotValidatorService() {
        return new TimeSlotValidatorService();
    }

    @Bean
    public PenaltyDomainService penaltyDomainService() {
        return new PenaltyDomainService();
    }

    // ==================== DOCTOR USE CASES ====================

    @Bean
    public RegisterDoctorUseCase registerDoctorUseCase(IDoctorRepository doctorRepository, Clock clock) {
        return new RegisterDoctorUseCase(doctorRepository, clock);
    }

    @Bean
    public GetDoctorByIdUseCase getDoctorByIdUseCase(IDoctorRepository doctorRepository) {
        return new GetDoctorByIdUseCase(doctorRepository);
    }

    @Bean
    public GetAvailableSlotsUseCase getAvailableSlotsUseCase(
            IDoctorRepository doctorRepository,
            IAppointmentRepository appointmentRepository) {
        return new GetAvailableSlotsUseCase(doctorRepository, appointmentRepository);
    }

    // ==================== PATIENT USE CASES ====================

    @Bean
    public RegisterPatientUseCase registerPatientUseCase(IPatientRepository patientRepository, Clock clock) {
        return new RegisterPatientUseCase(patientRepository, clock);
    }

    @Bean
    public GetPatientByIdUseCase getPatientByIdUseCase(IPatientRepository patientRepository) {
        return new GetPatientByIdUseCase(patientRepository);
    }

    // ==================== APPOINTMENT USE CASES ====================

    @Bean
    public CreateAppointmentUseCase createAppointmentUseCase(
            IAppointmentRepository appointmentRepository,
            IDoctorRepository doctorRepository,
            IPatientRepository patientRepository,
            IPenaltyRepository penaltyRepository,
            TimeSlotValidatorService timeSlotValidatorService,
            PenaltyDomainService penaltyDomainService,
            Clock clock) {
        return new CreateAppointmentUseCase(
                appointmentRepository, doctorRepository, patientRepository,
                penaltyRepository, timeSlotValidatorService, penaltyDomainService, clock);
    }

    @Bean
    public CancelAppointmentUseCase cancelAppointmentUseCase(
            IAppointmentRepository appointmentRepository,
            IPenaltyRepository penaltyRepository,
            PenaltyDomainService penaltyDomainService,
            Clock clock) {
        return new CancelAppointmentUseCase(appointmentRepository, penaltyRepository, penaltyDomainService, clock);
    }

    @Bean
    public RescheduleAppointmentUseCase rescheduleAppointmentUseCase(
            CancelAppointmentUseCase cancelAppointmentUseCase,
            CreateAppointmentUseCase createAppointmentUseCase) {
        return new RescheduleAppointmentUseCase(cancelAppointmentUseCase, createAppointmentUseCase);
    }

    @Bean
    public ListAppointmentsUseCase listAppointmentsUseCase(IAppointmentRepository appointmentRepository) {
        return new ListAppointmentsUseCase(appointmentRepository);
    }
}
