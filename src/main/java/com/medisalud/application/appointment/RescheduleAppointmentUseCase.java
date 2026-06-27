package com.medisalud.application.appointment;

import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.application.shared.dto.appointment.CancelAppointmentCommand;
import com.medisalud.application.shared.dto.appointment.CreateAppointmentCommand;
import com.medisalud.application.shared.dto.appointment.RescheduleAppointmentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso: Reprogramar una cita médica (RN-06).
 *
 * <p>La reprogramación implica exactamente:
 * <ol>
 *   <li>Cancelar la cita anterior (con evaluación de penalización según RN-05)</li>
 *   <li>Crear una nueva cita en el nuevo horario (con todas las validaciones RN-01 a RN-05)</li>
 * </ol>
 *
 * <p>Decisión de diseño: este Use Case orquesta los Use Cases existentes
 * {@link CancelAppointmentUseCase} y {@link CreateAppointmentUseCase}.
 * Esto evita duplicar la lógica de cancelación y creación, y respeta DRY.
 *
 * <p>Trade-off: el Use Case necesita saber el patientId y doctorId de la cita original.
 * Se obtienen del resultado de la cancelación (AppointmentResult contiene ambos IDs).
 */
public class RescheduleAppointmentUseCase {

    private static final Logger log = LoggerFactory.getLogger(RescheduleAppointmentUseCase.class);

    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final CreateAppointmentUseCase createAppointmentUseCase;

    public RescheduleAppointmentUseCase(
            CancelAppointmentUseCase cancelAppointmentUseCase,
            CreateAppointmentUseCase createAppointmentUseCase) {
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
        this.createAppointmentUseCase = createAppointmentUseCase;
    }

    public AppointmentResult execute(RescheduleAppointmentCommand command) {
        log.info("Rescheduling appointment {} to {}", command.appointmentId(), command.newDateTime());

        // Paso 1: cancelar la cita original (aplica RN-05 si corresponde)
        AppointmentResult cancelled = cancelAppointmentUseCase.execute(
                new CancelAppointmentCommand(command.appointmentId())
        );

        // Paso 2: crear nueva cita con el nuevo horario (valida RN-01, RN-02, RN-04, RN-05)
        AppointmentResult newAppointment = createAppointmentUseCase.execute(
                new CreateAppointmentCommand(
                        cancelled.patientId(),
                        cancelled.doctorId(),
                        command.newDateTime()
                )
        );

        log.info("Appointment rescheduled: old={} new={}", command.appointmentId(), newAppointment.id());
        return newAppointment;
    }
}
