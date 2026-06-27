package com.medisalud.infrastructure.doctor.mapper;

import com.medisalud.domain.doctor.Doctor;
import com.medisalud.infrastructure.doctor.persistence.DoctorJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T21:14:42-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class DoctorPersistenceMapperImpl implements DoctorPersistenceMapper {

    @Override
    public DoctorJpaEntity toJpaEntity(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorJpaEntity.DoctorJpaEntityBuilder doctorJpaEntity = DoctorJpaEntity.builder();

        doctorJpaEntity.createdAt( doctor.getCreatedAt() );
        doctorJpaEntity.email( doctor.getEmail() );
        doctorJpaEntity.fullName( doctor.getFullName() );
        doctorJpaEntity.phone( doctor.getPhone() );
        doctorJpaEntity.specialty( doctor.getSpecialty() );
        doctorJpaEntity.updatedAt( doctor.getUpdatedAt() );

        doctorJpaEntity.id( doctor.getId().value() );

        return doctorJpaEntity.build();
    }
}
