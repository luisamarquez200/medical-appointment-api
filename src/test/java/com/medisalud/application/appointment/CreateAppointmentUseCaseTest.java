package com.medisalud.application.appointment;

import com.medisalud.application.shared.dto.appointment.CreateAppointmentCommand;
import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.AppointmentStatus;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.exception.AppointmentConflictException;
import com.medisalud.domain.appointment.exception.InvalidTimeSlotException;
import com.medisalud.domain.appointment.exception.PatientAppointmentConflictException;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.appointment.service.TimeSlotValidatorService;
import com.medisalud.domain.doctor.Doctor;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.doctor.exception.DoctorNotFoundException;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import com.medisalud.domain.patient.Patient;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.patient.exception.PatientNotFoundException;
import com.medisalud.domain.patient.repository.IPatientRepository;
import com.medisalud.domain.penalty.exception.PatientBlockedException;
import com.medisalud.domain.penalty.repository.IPenaltyRepository;
import com.medisalud.domain.penalty.service.PenaltyDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CreateAppointmentUseCase.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAppointmentUseCase")
class CreateAppointmentUseCaseTest {

    @Mock private IAppointmentRepository appointmentRepository;
    @Mock private IDoctorRepository doctorRepository;
    @Mock private IPatientRepository patientRepository;
    @Mock private IPenaltyRepository penaltyRepository;

    private TimeSlotValidatorService timeSlotValidator;
    private PenaltyDomainService penaltyDomainService;
    private Clock fixedClock;
    private CreateAppointmentUseCase useCase;

    // Datos de prueba
    private final UUID patientUuid = UUID.randomUUID();
    private final UUID doctorUuid = UUID.randomUUID();
    private final PatientId patientId = PatientId.of(patientUuid);
    private final DoctorId doctorId = DoctorId.of(doctorUuid);

    // Lunes 2026-06-08 a las 10:00 — franja válida
    private final LocalDateTime VALID_SLOT = LocalDateTime.of(2026, 6, 8, 10, 0);
    private final LocalDateTime NOW = LocalDateTime.of(2026, 6, 7, 9, 0);

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(NOW.atZone(ZoneId.of("America/Bogota")).toInstant(), ZoneId.of("America/Bogota"));
        timeSlotValidator = new TimeSlotValidatorService();
        penaltyDomainService = new PenaltyDomainService();

        useCase = new CreateAppointmentUseCase(
                appointmentRepository, doctorRepository, patientRepository,
                penaltyRepository, timeSlotValidator, penaltyDomainService, fixedClock);
    }

    private Patient buildPatient() {
        return Patient.create(patientId, "Juan Pérez", "12345678", "3001234567",
                "juan@test.com", LocalDate.of(1990, 1, 1), NOW);
    }

    private Appointment buildSavedAppointment() {
        return Appointment.create(
                AppointmentId.generate(), patientId, doctorId,
                TimeSlot.of(VALID_SLOT), NOW);
    }

    @Nested
    @DisplayName("Caso feliz")
    class HappyPath {

        @Test
        @DisplayName("Debe crear cita exitosamente cuando todos los datos son válidos")
        void shouldCreateAppointmentSuccessfully() {
            // Arrange
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);
            when(appointmentRepository.existsScheduledByDoctorAndTimeSlot(eq(doctorId), any())).thenReturn(false);
            when(appointmentRepository.existsScheduledByPatientAndDoctorAndTimeSlot(eq(patientId), eq(doctorId), any())).thenReturn(false);
            when(penaltyRepository.countByPatientIdAndPenaltyDateTimeAfter(eq(patientId), any())).thenReturn(0L);
            when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            AppointmentResult result = useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT));

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(AppointmentStatus.PROGRAMADA.name());
            assertThat(result.startDateTime()).isEqualTo(VALID_SLOT);
            assertThat(result.endDateTime()).isEqualTo(VALID_SLOT.plusMinutes(30));
            verify(appointmentRepository).save(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("Validaciones de existencia")
    class ExistenceValidation {

        @Test
        @DisplayName("Debe lanzar excepción si el paciente no existe")
        void shouldThrowWhenPatientNotFound() {
            when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT)))
                    .isInstanceOf(PatientNotFoundException.class);

            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el médico no existe")
        void shouldThrowWhenDoctorNotFound() {
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(false);

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT)))
                    .isInstanceOf(DoctorNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("RN-01: Horario laboral")
    class WorkingHoursValidation {

        @Test
        @DisplayName("Debe rechazar cita en domingo")
        void shouldRejectSundaySlot() {
            LocalDateTime sunday = LocalDateTime.of(2026, 6, 14, 10, 0);
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, sunday)))
                    .isInstanceOf(InvalidTimeSlotException.class);
        }

        @Test
        @DisplayName("Debe rechazar franja no en múltiplo de 30 min")
        void shouldRejectNonBoundarySlot() {
            LocalDateTime invalidSlot = LocalDateTime.of(2026, 6, 8, 10, 15);
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, invalidSlot)))
                    .isInstanceOf(InvalidTimeSlotException.class);
        }
    }

    @Nested
    @DisplayName("RN-02: No duplicidad por médico")
    class DoctorConflict {

        @Test
        @DisplayName("Debe rechazar si el médico ya tiene cita en esa franja")
        void shouldThrowWhenDoctorSlotOccupied() {
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);
            when(appointmentRepository.existsScheduledByDoctorAndTimeSlot(eq(doctorId), any())).thenReturn(true);

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT)))
                    .isInstanceOf(AppointmentConflictException.class)
                    .hasMessageContaining("ya tiene una cita");
        }
    }

    @Nested
    @DisplayName("RN-04: No duplicidad paciente+médico")
    class PatientDoctorConflict {

        @Test
        @DisplayName("Debe rechazar si el paciente ya tiene cita con ese médico en esa franja")
        void shouldThrowWhenPatientDoctorConflict() {
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);
            when(appointmentRepository.existsScheduledByDoctorAndTimeSlot(eq(doctorId), any())).thenReturn(false);
            when(appointmentRepository.existsScheduledByPatientAndDoctorAndTimeSlot(eq(patientId), eq(doctorId), any())).thenReturn(true);

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT)))
                    .isInstanceOf(PatientAppointmentConflictException.class);
        }
    }

    @Nested
    @DisplayName("RN-05: Paciente bloqueado por penalizaciones")
    class PatientBlocking {

        @Test
        @DisplayName("Debe rechazar si el paciente tiene 3 o más penalizaciones en 30 días")
        void shouldThrowWhenPatientBlocked() {
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);
            when(appointmentRepository.existsScheduledByDoctorAndTimeSlot(eq(doctorId), any())).thenReturn(false);
            when(appointmentRepository.existsScheduledByPatientAndDoctorAndTimeSlot(any(), any(), any())).thenReturn(false);
            when(penaltyRepository.countByPatientIdAndPenaltyDateTimeAfter(eq(patientId), any())).thenReturn(3L);

            assertThatThrownBy(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT)))
                    .isInstanceOf(PatientBlockedException.class)
                    .hasMessageContaining("penalizaciones");
        }

        @Test
        @DisplayName("Debe permitir con exactamente 2 penalizaciones")
        void shouldAllowWith2Penalties() {
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
            when(doctorRepository.existsById(doctorId)).thenReturn(true);
            when(appointmentRepository.existsScheduledByDoctorAndTimeSlot(any(), any())).thenReturn(false);
            when(appointmentRepository.existsScheduledByPatientAndDoctorAndTimeSlot(any(), any(), any())).thenReturn(false);
            when(penaltyRepository.countByPatientIdAndPenaltyDateTimeAfter(any(), any())).thenReturn(2L);
            when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertThatCode(() ->
                    useCase.execute(new CreateAppointmentCommand(patientUuid, doctorUuid, VALID_SLOT)))
                    .doesNotThrowAnyException();
        }
    }
}
