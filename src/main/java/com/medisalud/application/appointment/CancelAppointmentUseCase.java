package com.medisalud.application.appointment;

import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.application.shared.dto.appointment.CancelAppointmentCommand;
import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.exception.AppointmentNotFoundException;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.penalty.Penalty;
import com.medisalud.domain.penalty.PenaltyId;
import com.medisalud.domain.penalty.repository.IPenaltyRepository;
import com.medisalud.domain.penalty.service.PenaltyDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Caso de uso: Cancelar una cita médica (RF-05).
 *
 * <p>Proceso:
 * <ol>
 *   <li>Buscar la cita (lanza excepción si no existe)</li>
 *   <li>Delegar la cancelación a la entidad Appointment (que valida el estado)</li>
 *   <li>Evaluar si aplica penalización (RN-05) — delegado a PenaltyDomainService</li>
 *   <li>Persistir la penalización si aplica</li>
 *   <li>Persistir la cita actualizada</li>
 * </ol>
 *
 * <p>El Use Case coordina los aggregates pero no contiene la lógica de negocio:
 * - La validación de estado está en Appointment.cancel()
 * - La lógica de 2 horas está en Appointment.isLateCancellation()
 * - La decisión de penalizar está en PenaltyDomainService.shouldApplyPenalty()
 */
public class CancelAppointmentUseCase {

    private static final Logger log = LoggerFactory.getLogger(CancelAppointmentUseCase.class);

    private final IAppointmentRepository appointmentRepository;
    private final IPenaltyRepository penaltyRepository;
    private final PenaltyDomainService penaltyDomainService;
    private final Clock clock;

    public CancelAppointmentUseCase(
            IAppointmentRepository appointmentRepository,
            IPenaltyRepository penaltyRepository,
            PenaltyDomainService penaltyDomainService,
            Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.penaltyRepository = penaltyRepository;
        this.penaltyDomainService = penaltyDomainService;
        this.clock = clock;
    }

    public AppointmentResult execute(CancelAppointmentCommand command) {
        LocalDateTime now = LocalDateTime.now(clock);
        AppointmentId appointmentId = AppointmentId.of(command.appointmentId());

        // 1. Buscar la cita
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        // 2. Evaluar penalización ANTES de cancelar (necesita el timeSlot original)
        boolean applyPenalty = penaltyDomainService.shouldApplyPenalty(appointment, now);

        // 3. Cancelar la cita (entidad valida el estado — lanza si no es PROGRAMADA)
        appointment.cancel(now);

        // 4. Registrar penalización si aplica (RN-05)
        if (applyPenalty) {
            Penalty penalty = Penalty.create(
                    PenaltyId.generate(),
                    appointment.getPatientId(),
                    appointment.getId(),
                    now
            );
            penaltyRepository.save(penalty);
            log.warn("Late cancellation penalty applied to patient {} for appointment {}",
                    appointment.getPatientId(), appointmentId);
        }

        // 5. Persistir la cita cancelada
        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment cancelled: ID={}", appointmentId);

        return CreateAppointmentUseCase.toResult(saved);
    }
}
