package com.medisalud.infrastructure.patient.mapper;

import com.medisalud.domain.patient.Patient;
import com.medisalud.infrastructure.patient.persistence.PatientJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T21:46:43-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Homebrew)"
)
@Component
public class PatientPersistenceMapperImpl implements PatientPersistenceMapper {

    @Override
    public PatientJpaEntity toJpaEntity(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientJpaEntity.PatientJpaEntityBuilder patientJpaEntity = PatientJpaEntity.builder();

        patientJpaEntity.fullName( patient.getFullName() );
        patientJpaEntity.documentId( patient.getDocumentId() );
        patientJpaEntity.phone( patient.getPhone() );
        patientJpaEntity.email( patient.getEmail() );
        if ( patient.hasBirthDate() ) {
            patientJpaEntity.birthDate( patient.getBirthDate() );
        }
        patientJpaEntity.createdAt( patient.getCreatedAt() );
        patientJpaEntity.updatedAt( patient.getUpdatedAt() );

        patientJpaEntity.id( patient.getId().value() );

        return patientJpaEntity.build();
    }
}
