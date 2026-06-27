package com.medisalud.domain.appointment.service;

import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.exception.InvalidTimeSlotException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para TimeSlotValidatorService (RN-01).
 * Cubre todos los casos borde del horario laboral.
 */
@DisplayName("TimeSlotValidatorService — RN-01")
class TimeSlotValidatorServiceTest {

    private TimeSlotValidatorService validator;

    @BeforeEach
    void setUp() {
        validator = new TimeSlotValidatorService();
    }

    @Nested
    @DisplayName("Franjas válidas")
    class ValidSlots {

        @ParameterizedTest(name = "Lunes-Viernes {0} debe ser válido")
        @CsvSource({
                "2026-06-08T08:00:00",  // Lunes 08:00
                "2026-06-08T08:30:00",  // Lunes 08:30
                "2026-06-08T12:00:00",  // Lunes 12:00
                "2026-06-08T17:30:00",  // Lunes 17:30 (último inicio)
                "2026-06-12T08:00:00",  // Viernes 08:00
                "2026-06-12T17:30:00",  // Viernes 17:30
        })
        void shouldAcceptWeekdaySlots(String dateTimeStr) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            TimeSlot slot = validator.validate(dateTime);
            assertThat(slot).isNotNull();
            assertThat(slot.getStart()).isEqualTo(dateTime);
        }

        @ParameterizedTest(name = "Sábado {0} debe ser válido")
        @CsvSource({
                "2026-06-13T08:00:00",  // Sábado 08:00
                "2026-06-13T10:30:00",  // Sábado 10:30
                "2026-06-13T12:30:00",  // Sábado 12:30 (último inicio)
        })
        void shouldAcceptSaturdaySlots(String dateTimeStr) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            TimeSlot slot = validator.validate(dateTime);
            assertThat(slot).isNotNull();
        }
    }

    @Nested
    @DisplayName("Domingo — sin atención")
    class Sunday {

        @ParameterizedTest(name = "Domingo {0} debe rechazarse")
        @ValueSource(strings = {
                "2026-06-14T08:00:00",
                "2026-06-14T12:00:00",
                "2026-06-14T17:30:00"
        })
        void shouldRejectSundaySlots(String dateTimeStr) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            assertThatThrownBy(() -> validator.validate(dateTime))
                    .isInstanceOf(InvalidTimeSlotException.class)
                    .hasMessageContaining("SUNDAY");
        }
    }

    @Nested
    @DisplayName("Fuera de horario laboral")
    class OutsideWorkingHours {

        @ParameterizedTest(name = "Lunes {0} fuera de horario debe rechazarse")
        @CsvSource({
                "2026-06-08T07:30:00",  // Antes de apertura
                "2026-06-08T18:00:00",  // Igual a la hora de cierre (no hay franja que empiece aquí)
                "2026-06-08T18:30:00",  // Después de cierre
                "2026-06-08T07:59:00",  // 1 minuto antes
        })
        void shouldRejectWeekdayOutsideHours(String dateTimeStr) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            assertThatThrownBy(() -> validator.validate(dateTime))
                    .isInstanceOf(InvalidTimeSlotException.class);
        }

        @ParameterizedTest(name = "Sábado {0} fuera de horario debe rechazarse")
        @CsvSource({
                "2026-06-13T13:00:00",  // Igual a cierre del sábado
                "2026-06-13T13:30:00",  // Después del cierre
        })
        void shouldRejectSaturdayOutsideHours(String dateTimeStr) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            assertThatThrownBy(() -> validator.validate(dateTime))
                    .isInstanceOf(InvalidTimeSlotException.class);
        }
    }

    @Nested
    @DisplayName("Franja no válida (no múltiplo de 30 min)")
    class InvalidSlotBoundary {

        @ParameterizedTest(name = "{0} no es múltiplo de 30 min")
        @ValueSource(strings = {
                "2026-06-08T08:15:00",
                "2026-06-08T09:01:00",
                "2026-06-08T10:45:00",
                "2026-06-08T08:00:01",  // segundos != 0
        })
        void shouldRejectNonSlotBoundary(String dateTimeStr) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            assertThatThrownBy(() -> validator.validate(dateTime))
                    .isInstanceOf(InvalidTimeSlotException.class)
                    .hasMessageContaining("30 minutos");
        }
    }
}
