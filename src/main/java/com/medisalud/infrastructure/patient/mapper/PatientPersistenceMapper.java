package com.medisalud.infrastructure.patient.mapper;

import com.medisalud.domain.patient.Patient;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.infrastructure.patient.persistence.PatientJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientPersistenceMapper {

    @Mapping(target = "id", expression = "java(patient.getId().value())")
    PatientJpaEntity toJpaEntity(Patient patient);

    default Patient toDomain(PatientJpaEntity entity) {
        return Patient.reconstitute(
                PatientId.of(entity.getId()),
                entity.getFullName(),
                entity.getDocumentId(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
