package com.medisalud.application.appointment;

import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.application.shared.dto.appointment.CancelAppointmentCommand;
import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.AppointmentStatus;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.exception.AppointmentNotFoundException;
import com.medisalud.domain.appointment.exception.AppointmentNotCancellableException;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelAppointmentUseCase")
class CancelAppointmentUseCaseTest {

    @Mock private IAppointmentRepository appointmentRepository;
    @Mock private IPenaltyRepository penaltyRepository;

    private PenaltyDomainService penaltyDomainService;
    private Clock fixedClock;
    private CancelAppointmentUseCase useCase;

    // Slot a las 14:00 — Clock fijado a las 13:30 (30 min antes → cancelación TARDÍA)
    private final LocalDateTime SLOT_START = LocalDateTime.of(2026, 6, 8, 14, 0);
    private final LocalDateTime CANCELLATION_TIME_LATE = LocalDateTime.of(2026, 6, 8, 13, 30); // < 2h antes
    private final LocalDateTime CANCELLATION_TIME_EARLY = LocalDateTime.of(2026, 6, 8, 11, 0); // > 2h antes

    @BeforeEach
    void setUp() {
        penaltyDomainService = new PenaltyDomainService();
    }

    private void setClockTo(LocalDateTime time) {
        fixedClock = Clock.fixed(time.atZone(ZoneId.of("America/Bogota")).toInstant(), ZoneId.of("America/Bogota"));
        useCase = new CancelAppointmentUseCase(appointmentRepository, penaltyRepository, penaltyDomainService, fixedClock);
    }

    private Appointment buildScheduledAppointment(UUID id) {
        return Appointment.create(
                AppointmentId.of(id),
                PatientId.of(UUID.randomUUID()),
                DoctorId.of(UUID.randomUUID()),
                TimeSlot.of(SLOT_START),
                LocalDateTime.of(2026, 6, 7, 9, 0)
        );
    }

    @Nested
    @DisplayName("Caso feliz")
    class HappyPath {

        @Test
        @DisplayName("Debe cancelar cita exitosamente (sin penalización — cancelación temprana)")
        void shouldCancelWithoutPenaltyWhenEarlyCancellation() {
            setClockTo(CANCELLATION_TIME_EARLY);
            UUID id = UUID.randomUUID();
            Appointment appointment = buildScheduledAppointment(id);

            when(appointmentRepository.findById(AppointmentId.of(id))).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AppointmentResult result = useCase.execute(new CancelAppointmentCommand(id));

            assertThat(result.status()).isEqualTo(AppointmentStatus.CANCELADA.name());
            verify(penaltyRepository, never()).save(any()); // Sin penalización
        }

        @Test
        @DisplayName("Debe registrar penalización en cancelación tardía (RN-05)")
        void shouldApplyPenaltyWhenLateCancellation() {
            setClockTo(CANCELLATION_TIME_LATE);
            UUID id = UUID.randomUUID();
            Appointment appointment = buildScheduledAppointment(id);

            when(appointmentRepository.findById(AppointmentId.of(id))).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(penaltyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AppointmentResult result = useCase.execute(new CancelAppointmentCommand(id));

            assertThat(result.status()).isEqualTo(AppointmentStatus.CANCELADA.name());
            verify(penaltyRepository).save(any()); // SÍ se registra penalización
        }
    }

    @Nested
    @DisplayName("Errores")
    class ErrorCases {

        @Test
        @DisplayName("Debe lanzar excepción si la cita no existe")
        void shouldThrowWhenAppointmentNotFound() {
            setClockTo(CANCELLATION_TIME_EARLY);
            UUID id = UUID.randomUUID();
            when(appointmentRepository.findById(AppointmentId.of(id))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(new CancelAppointmentCommand(id)))
                    .isInstanceOf(AppointmentNotFoundException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción si la cita ya está cancelada")
        void shouldThrowWhenAlreadyCancelled() {
            setClockTo(CANCELLATION_TIME_EARLY);
            UUID id = UUID.randomUUID();
            Appointment appointment = buildScheduledAppointment(id);
            appointment.cancel(CANCELLATION_TIME_EARLY); // Pre-cancelar

            when(appointmentRepository.findById(AppointmentId.of(id))).thenReturn(Optional.of(appointment));

            assertThatThrownBy(() -> useCase.execute(new CancelAppointmentCommand(id)))
                    .isInstanceOf(AppointmentNotCancellableException.class)
                    .hasMessageContaining("CANCELADA");
        }
    }
}
