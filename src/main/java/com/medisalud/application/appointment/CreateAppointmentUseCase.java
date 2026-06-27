package com.medisalud.application.appointment;

import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.application.shared.dto.appointment.CreateAppointmentCommand;
import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.exception.AppointmentConflictException;
import com.medisalud.domain.appointment.exception.PatientAppointmentConflictException;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.appointment.service.TimeSlotValidatorService;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.doctor.exception.DoctorNotFoundException;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import com.medisalud.domain.patient.Patient;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.patient.exception.PatientNotFoundException;
import com.medisalud.domain.patient.repository.IPatientRepository;
import com.medisalud.domain.penalty.repository.IPenaltyRepository;
import com.medisalud.domain.penalty.service.PenaltyDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Caso de uso: Crear una nueva cita médica (RF-03).
 *
 * <p>Orquesta la validación de todas las reglas de negocio en orden:
 * <ol>
 *   <li>Paciente existe (base)</li>
 *   <li>Médico existe (base)</li>
 *   <li>Fecha de nacimiento válida (RN-03)</li>
 *   <li>Franja horaria laboral válida (RN-01)</li>
 *   <li>Médico libre en esa franja (RN-02)</li>
 *   <li>Paciente sin cita con ese médico en esa franja (RN-04)</li>
 *   <li>Paciente no bloqueado por penalizaciones (RN-05)</li>
 * </ol>
 *
 * <p>Este Use Case es el más complejo del sistema. Su longitud está justificada
 * por la cantidad de reglas de negocio que deben coordinarse. Cada validación
 * es discreta y delegada al dominio cuando aplica.
 */
public class CreateAppointmentUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateAppointmentUseCase.class);

    private final IAppointmentRepository appointmentRepository;
    private final IDoctorRepository doctorRepository;
    private final IPatientRepository patientRepository;
    private final IPenaltyRepository penaltyRepository;
    private final TimeSlotValidatorService timeSlotValidator;
    private final PenaltyDomainService penaltyDomainService;
    private final Clock clock;

    public CreateAppointmentUseCase(
            IAppointmentRepository appointmentRepository,
            IDoctorRepository doctorRepository,
            IPatientRepository patientRepository,
            IPenaltyRepository penaltyRepository,
            TimeSlotValidatorService timeSlotValidator,
            PenaltyDomainService penaltyDomainService,
            Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.penaltyRepository = penaltyRepository;
        this.timeSlotValidator = timeSlotValidator;
        this.penaltyDomainService = penaltyDomainService;
        this.clock = clock;
    }

    public AppointmentResult execute(CreateAppointmentCommand command) {
        LocalDateTime now = LocalDateTime.now(clock);
        PatientId patientId = PatientId.of(command.patientId());
        DoctorId doctorId = DoctorId.of(command.doctorId());

        // 1. Verificar existencia de paciente
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        // 2. Verificar existencia de médico
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }

        // 3. Validar fecha de nacimiento (RN-03)
        patient.validateBirthDate(LocalDate.now(clock));

        // 4. Validar franja horaria laboral (RN-01) — obtiene TimeSlot validado o lanza excepción
        TimeSlot timeSlot = timeSlotValidator.validate(command.dateTime());

        // 5. Verificar disponibilidad del médico (RN-02)
        if (appointmentRepository.existsScheduledByDoctorAndTimeSlot(doctorId, timeSlot)) {
            throw new AppointmentConflictException(doctorId, timeSlot);
        }

        // 6. Verificar conflicto paciente-médico (RN-04)
        if (appointmentRepository.existsScheduledByPatientAndDoctorAndTimeSlot(patientId, doctorId, timeSlot)) {
            throw new PatientAppointmentConflictException(patientId, doctorId, timeSlot);
        }

        // 7. Verificar que el paciente no esté bloqueado (RN-05)
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        long recentPenalties = penaltyRepository.countByPatientIdAndPenaltyDateTimeAfter(patientId, thirtyDaysAgo);
        penaltyDomainService.assertPatientNotBlocked(recentPenalties);

        // 8. Crear y persistir la cita
        Appointment appointment = Appointment.create(
                AppointmentId.generate(),
                patientId,
                doctorId,
                timeSlot,
                now
        );

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created: ID={} patient={} doctor={} slot={}",
                saved.getId(), patientId, doctorId, timeSlot.getStart());

        return toResult(saved);
    }

    static AppointmentResult toResult(Appointment appointment) {
        return new AppointmentResult(
                appointment.getId().value(),
                appointment.getPatientId().value(),
                appointment.getDoctorId().value(),
                appointment.getTimeSlot().getStart(),
                appointment.getTimeSlot().getEnd(),
                appointment.getStatus().name(),
                appointment.getCancelledAt(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
