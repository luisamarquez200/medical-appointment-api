package com.medisalud.application.appointment;

import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.application.shared.dto.appointment.ListAppointmentsQuery;
import com.medisalud.domain.appointment.AppointmentStatus;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso: Listar citas con filtros opcionales (RF-06).
 *
 * <p>Todos los filtros son opcionales. Si un parámetro es null, no se aplica ese filtro.
 * La construcción dinámica de la query está en la implementación del repositorio.
 */
public class ListAppointmentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListAppointmentsUseCase.class);

    private final IAppointmentRepository appointmentRepository;

    public ListAppointmentsUseCase(IAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<AppointmentResult> execute(ListAppointmentsQuery query) {
        log.debug("Listing appointments with filters: {}", query);

        DoctorId doctorId = query.doctorId() != null ? DoctorId.of(query.doctorId()) : null;
        PatientId patientId = query.patientId() != null ? PatientId.of(query.patientId()) : null;
        AppointmentStatus status = query.status() != null
                ? AppointmentStatus.valueOf(query.status().toUpperCase())
                : null;

        return appointmentRepository
                .findByFilters(doctorId, patientId, status, query.rangeStart(), query.rangeEnd())
                .stream()
                .map(CreateAppointmentUseCase::toResult)
                .collect(Collectors.toList());
    }
}
