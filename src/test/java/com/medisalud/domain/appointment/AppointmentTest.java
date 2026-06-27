package com.medisalud.domain.appointment;

import com.medisalud.domain.appointment.exception.AppointmentNotCancellableException;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Aggregate Root Appointment.
 */
@DisplayName("Appointment Aggregate Root")
class AppointmentTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 6, 10, 10, 0);
    private static final LocalDateTime SLOT_START = LocalDateTime.of(2026, 6, 10, 14, 0);

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = Appointment.create(
                AppointmentId.generate(),
                PatientId.of(UUID.randomUUID()),
                DoctorId.of(UUID.randomUUID()),
                TimeSlot.of(SLOT_START),
                NOW
        );
    }

    @Nested
    @DisplayName("Creación")
    class Creation {

        @Test
        @DisplayName("Estado inicial debe ser PROGRAMADA")
        void shouldBeScheduledWhenCreated() {
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PROGRAMADA);
            assertThat(appointment.isProgramada()).isTrue();
            assertThat(appointment.getCancelledAt()).isNull();
        }

        @Test
        @DisplayName("TimeSlot debe tener 30 minutos de duración")
        void timeSlotShouldBe30Minutes() {
            assertThat(appointment.getTimeSlot().getEnd())
                    .isEqualTo(SLOT_START.plusMinutes(30));
        }
    }

    @Nested
    @DisplayName("Cancelación")
    class Cancellation {

        @Test
        @DisplayName("Debe cancelar cita PROGRAMADA exitosamente")
        void shouldCancelScheduledAppointment() {
            appointment.cancel(NOW);

            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELADA);
            assertThat(appointment.getCancelledAt()).isEqualTo(NOW);
            assertThat(appointment.isCancellable()).isFalse();
        }

        @Test
        @DisplayName("No debe permitir cancelar una cita ya cancelada")
        void shouldThrowWhenCancellingAlreadyCancelledAppointment() {
            appointment.cancel(NOW);

            assertThatThrownBy(() -> appointment.cancel(NOW))
                    .isInstanceOf(AppointmentNotCancellableException.class)
                    .hasMessageContaining("CANCELADA");
        }
    }

    @Nested
    @DisplayName("Penalización tardía (RN-05)")
    class LateCancellation {

        @Test
        @DisplayName("Debe ser tardía si se cancela menos de 2 horas antes")
        void shouldBeLateCancellationWhenLessThan2Hours() {
            // Cita a las 14:00 — cancelación a las 12:30 (1h30m antes) → TARDÍA
            LocalDateTime cancellationTime = SLOT_START.minusMinutes(90);
            assertThat(appointment.isLateCancellation(cancellationTime)).isTrue();
        }

        @Test
        @DisplayName("Debe ser tardía si se cancela exactamente 2 horas antes")
        void shouldBeLateCancellationAtExactly2Hours() {
            // Exactamente 2 horas antes → TARDÍA (no es "menos de 2h")
            LocalDateTime cancellationTime = SLOT_START.minusHours(2);
            assertThat(appointment.isLateCancellation(cancellationTime)).isTrue();
        }

        @Test
        @DisplayName("NO debe ser tardía si se cancela más de 2 horas antes")
        void shouldNotBeLateCancellationWhenMoreThan2Hours() {
            // 2h01m antes → NO tardía
            LocalDateTime cancellationTime = SLOT_START.minusHours(2).minusMinutes(1);
            assertThat(appointment.isLateCancellation(cancellationTime)).isFalse();
        }

        @Test
        @DisplayName("Debe ser tardía si se cancela después del inicio")
        void shouldBeLateCancellationWhenAfterSlotStart() {
            LocalDateTime cancellationTime = SLOT_START.plusMinutes(5);
            assertThat(appointment.isLateCancellation(cancellationTime)).isTrue();
        }
    }
}
