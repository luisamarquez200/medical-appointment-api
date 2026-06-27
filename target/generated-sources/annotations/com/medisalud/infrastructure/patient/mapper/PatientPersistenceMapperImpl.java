package com.medisalud.infrastructure.patient.mapper;

import com.medisalud.domain.patient.Patient;
import com.medisalud.infrastructure.patient.persistence.PatientJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T21:14:42-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PatientPersistenceMapperImpl implements PatientPersistenceMapper {

    @Override
    public PatientJpaEntity toJpaEntity(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientJpaEntity.PatientJpaEntityBuilder patientJpaEntity = PatientJpaEntity.builder();

        if ( patient.hasBirthDate() ) {
            patientJpaEntity.birthDate( patient.getBirthDate() );
        }
        patientJpaEntity.createdAt( patient.getCreatedAt() );
        patientJpaEntity.documentId( patient.getDocumentId() );
        patientJpaEntity.email( patient.getEmail() );
        patientJpaEntity.fullName( patient.getFullName() );
        patientJpaEntity.phone( patient.getPhone() );
        patientJpaEntity.updatedAt( patient.getUpdatedAt() );

        patientJpaEntity.id( patient.getId().value() );

        return patientJpaEntity.build();
    }
}
