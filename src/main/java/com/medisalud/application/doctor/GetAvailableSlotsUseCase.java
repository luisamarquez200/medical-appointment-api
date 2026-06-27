package com.medisalud.application.doctor;

import com.medisalud.application.shared.dto.doctor.TimeSlotResult;
import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.doctor.exception.DoctorNotFoundException;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de uso: Consultar franjas horarias disponibles de un médico en un rango de fechas (RF-04).
 *
 * <p>Algoritmo:
 * 1. Verificar que el médico exista.
 * 2. Obtener citas PROGRAMADAS del médico en el rango (franjas ocupadas).
 * 3. Generar todas las franjas posibles en el rango (RN-01: horarios laborales).
 * 4. Retornar las franjas que no están ocupadas.
 *
 * <p>La lógica de generación de franjas está aquí (no en el dominio) porque es
 * orquestación — consulta repositorio y aplica reglas de horario laboral. No pertenece
 * a ninguna entidad de dominio específica.
 */
public class GetAvailableSlotsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetAvailableSlotsUseCase.class);

    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime WEEKDAY_CLOSING_TIME = LocalTime.of(18, 0);
    private static final LocalTime SATURDAY_CLOSING_TIME = LocalTime.of(13, 0);

    private static final Set<DayOfWeek> WORKING_DAYS = Set.of(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    );

    private final IDoctorRepository doctorRepository;
    private final IAppointmentRepository appointmentRepository;

    public GetAvailableSlotsUseCase(
            IDoctorRepository doctorRepository,
            IAppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<TimeSlotResult> execute(String doctorIdStr, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        DoctorId doctorId = DoctorId.of(doctorIdStr);

        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }

        log.debug("Fetching available slots for doctor {} from {} to {}", doctorId, rangeStart, rangeEnd);

        // Obtener franjas ya ocupadas
        Set<LocalDateTime> occupiedStarts = appointmentRepository
                .findScheduledByDoctorAndDateRange(doctorId, rangeStart, rangeEnd)
                .stream()
                .map(Appointment::getTimeSlot)
                .map(TimeSlot::getStart)
                .collect(Collectors.toSet());

        // Generar todas las franjas posibles en el rango y filtrar las ocupadas
        return generateAllSlots(rangeStart, rangeEnd).stream()
                .filter(slot -> !occupiedStarts.contains(slot.getStart()))
                .map(slot -> new TimeSlotResult(slot.getStart(), slot.getEnd()))
                .collect(Collectors.toList());
    }

    /**
     * Genera todas las franjas horarias posibles entre dos fechas, respetando RN-01.
     */
    private List<TimeSlot> generateAllSlots(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<TimeSlot> slots = new ArrayList<>();

        LocalDate currentDate = rangeStart.toLocalDate();
        LocalDate endDate = rangeEnd.toLocalDate();

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            if (WORKING_DAYS.contains(dayOfWeek)) {
                LocalTime closingTime = (dayOfWeek == DayOfWeek.SATURDAY)
                        ? SATURDAY_CLOSING_TIME
                        : WEEKDAY_CLOSING_TIME;

                LocalTime slotTime = OPENING_TIME;
                while (slotTime.isBefore(closingTime)) {
                    LocalDateTime slotStart = LocalDateTime.of(currentDate, slotTime);

                    // Solo incluir si está dentro del rango solicitado
                    if (!slotStart.isBefore(rangeStart) && slotStart.isBefore(rangeEnd)) {
                        slots.add(TimeSlot.of(slotStart));
                    }

                    slotTime = slotTime.plusMinutes(30);
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        return slots;
    }
}
