package com.medisalud.infrastructure.appointment.mapper;

import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.infrastructure.appointment.persistence.AppointmentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentPersistenceMapper {

    @Mapping(target = "id", expression = "java(appointment.getId().value())")
    @Mapping(target = "patientId", expression = "java(appointment.getPatientId().value())")
    @Mapping(target = "doctorId", expression = "java(appointment.getDoctorId().value())")
    @Mapping(target = "appointmentDateTime", expression = "java(appointment.getTimeSlot().getStart())")
    AppointmentJpaEntity toJpaEntity(Appointment appointment);

    default Appointment toDomain(AppointmentJpaEntity entity) {
        return Appointment.reconstitute(
                AppointmentId.of(entity.getId()),
                PatientId.of(entity.getPatientId()),
                DoctorId.of(entity.getDoctorId()),
                TimeSlot.of(entity.getAppointmentDateTime()),
                entity.getStatus(),
                entity.getCancelledAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
