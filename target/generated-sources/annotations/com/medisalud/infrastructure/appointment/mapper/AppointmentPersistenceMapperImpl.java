package com.medisalud.infrastructure.appointment.mapper;

import com.medisalud.domain.appointment.Appointment;
import com.medisalud.infrastructure.appointment.persistence.AppointmentJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T21:14:42-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class AppointmentPersistenceMapperImpl implements AppointmentPersistenceMapper {

    @Override
    public AppointmentJpaEntity toJpaEntity(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        AppointmentJpaEntity.AppointmentJpaEntityBuilder appointmentJpaEntity = AppointmentJpaEntity.builder();

        appointmentJpaEntity.cancelledAt( appointment.getCancelledAt() );
        appointmentJpaEntity.createdAt( appointment.getCreatedAt() );
        appointmentJpaEntity.status( appointment.getStatus() );
        appointmentJpaEntity.updatedAt( appointment.getUpdatedAt() );

        appointmentJpaEntity.id( appointment.getId().value() );
        appointmentJpaEntity.patientId( appointment.getPatientId().value() );
        appointmentJpaEntity.doctorId( appointment.getDoctorId().value() );
        appointmentJpaEntity.appointmentDateTime( appointment.getTimeSlot().getStart() );

        return appointmentJpaEntity.build();
    }
}
