package com.medisalud.infrastructure.appointment.persistence;

import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.AppointmentStatus;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.appointment.repository.IAppointmentRepository;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.infrastructure.appointment.mapper.AppointmentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AppointmentRepositoryImpl implements IAppointmentRepository {

    private final AppointmentJpaRepository jpaRepository;
    private final AppointmentPersistenceMapper mapper;

    public AppointmentRepositoryImpl(AppointmentJpaRepository jpaRepository, AppointmentPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(appointment)));
    }

    @Override
    public Optional<Appointment> findById(AppointmentId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsScheduledByDoctorAndTimeSlot(DoctorId doctorId, TimeSlot timeSlot) {
        return jpaRepository.existsByDoctorIdAndAppointmentDateTimeAndStatus(
                doctorId.value(),
                timeSlot.getStart(),
                AppointmentStatus.PROGRAMADA
        );
    }

    @Override
    public boolean existsScheduledByPatientAndDoctorAndTimeSlot(
            PatientId patientId, DoctorId doctorId, TimeSlot timeSlot) {
        return jpaRepository.existsByPatientIdAndDoctorIdAndAppointmentDateTimeAndStatus(
                patientId.value(),
                doctorId.value(),
                timeSlot.getStart(),
                AppointmentStatus.PROGRAMADA
        );
    }

    @Override
    public List<Appointment> findScheduledByDoctorAndDateRange(
            DoctorId doctorId, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return jpaRepository.findScheduledByDoctorAndDateRange(doctorId.value(), rangeStart, rangeEnd)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByFilters(
            DoctorId doctorId,
            PatientId patientId,
            AppointmentStatus status,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd) {
        UUID doctorUuid = doctorId != null ? doctorId.value() : null;
        UUID patientUuid = patientId != null ? patientId.value() : null;

        return jpaRepository.findByFilters(doctorUuid, patientUuid, status, rangeStart, rangeEnd)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
