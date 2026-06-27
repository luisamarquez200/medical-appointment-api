package com.medisalud.infrastructure.doctor.mapper;

import com.medisalud.domain.doctor.Doctor;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.infrastructure.doctor.persistence.DoctorJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para convertir entre Doctor (dominio) y DoctorJpaEntity (infraestructura).
 *
 * <p>componentModel = "spring" → MapStruct genera un @Component que Spring gestiona.
 * La configuración global está en el pom.xml: -Amapstruct.defaultComponentModel=spring
 */
@Mapper(componentModel = "spring")
public interface DoctorPersistenceMapper {

    @Mapping(target = "id", expression = "java(doctor.getId().value())")
    DoctorJpaEntity toJpaEntity(Doctor doctor);

    default Doctor toDomain(DoctorJpaEntity entity) {
        return Doctor.reconstitute(
                DoctorId.of(entity.getId()),
                entity.getFullName(),
                entity.getSpecialty(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
