package com.medisalud.domain.appointment.service;

import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.exception.InvalidTimeSlotException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Servicio de dominio que valida franjas horarias.
 *
 * <p>RN-01: Los médicos atienden:
 * <ul>
 *   <li>Lunes a Viernes: 08:00 - 18:00 (último inicio de franja: 17:30)</li>
 *   <li>Sábados: 08:00 - 13:00 (último inicio de franja: 12:30)</li>
 *   <li>Domingos: sin atención</li>
 *   <li>Festivos: no validados en MVP (documentado en README)</li>
 * </ul>
 *
 * <p>Validación de franja: el inicio debe ser en minuto 0 o 30, y segundos = 0.
 */
public class TimeSlotValidatorService {

    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime WEEKDAY_CLOSING_TIME = LocalTime.of(18, 0);
    private static final LocalTime SATURDAY_CLOSING_TIME = LocalTime.of(13, 0);

    // Último minuto válido de inicio (30 min antes del cierre)
    private static final LocalTime WEEKDAY_LAST_SLOT_START = LocalTime.of(17, 30);
    private static final LocalTime SATURDAY_LAST_SLOT_START = LocalTime.of(12, 30);

    private static final Set<DayOfWeek> WORKING_DAYS = Set.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
    );

    /**
     * Valida que un instante dado sea un inicio de franja válido según RN-01.
     *
     * @param dateTime el instante propuesto como inicio de la cita
     * @return un TimeSlot válido si pasa todas las validaciones
     * @throws InvalidTimeSlotException si el horario no cumple RN-01
     */
    public TimeSlot validate(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();

        validateWorkingDay(dayOfWeek);
        validateSlotBoundary(time);
        validateWorkingHours(dayOfWeek, time);

        return TimeSlot.of(dateTime);
    }

    /**
     * Verifica que el día sea laborable (no domingo).
     */
    private void validateWorkingDay(DayOfWeek dayOfWeek) {
        if (!WORKING_DAYS.contains(dayOfWeek)) {
            throw new InvalidTimeSlotException(
                    "No se puede agendar citas en " + dayOfWeek.name() +
                    ". Los médicos atienden de lunes a sábado."
            );
        }
    }

    /**
     * Verifica que la hora sea exactamente en minuto 0 o 30 (franja de 30 min).
     * Los segundos y nanosegundos deben ser cero.
     */
    private void validateSlotBoundary(LocalTime time) {
        int minute = time.getMinute();
        if ((minute != 0 && minute != 30) || time.getSecond() != 0 || time.getNano() != 0) {
            throw new InvalidTimeSlotException(
                    "Las citas solo pueden agendarse en franjas de 30 minutos (ej: 08:00, 08:30, 09:00)."
            );
        }
    }

    /**
     * Verifica que la hora esté dentro del rango laboral del día.
     */
    private void validateWorkingHours(DayOfWeek dayOfWeek, LocalTime time) {
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            validateTimeInRange(time, OPENING_TIME, SATURDAY_LAST_SLOT_START, "sábado (08:00 - 13:00)");
        } else {
            validateTimeInRange(time, OPENING_TIME, WEEKDAY_LAST_SLOT_START, "lunes a viernes (08:00 - 18:00)");
        }
    }

    private void validateTimeInRange(LocalTime time, LocalTime open, LocalTime lastSlot, String dayDescription) {
        if (time.isBefore(open) || time.isAfter(lastSlot)) {
            throw new InvalidTimeSlotException(
                    "El horario " + time + " está fuera del rango de atención para " + dayDescription + "."
            );
        }
    }
}
