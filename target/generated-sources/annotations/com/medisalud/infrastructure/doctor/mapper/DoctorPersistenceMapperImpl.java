package com.medisalud.infrastructure.doctor.mapper;

import com.medisalud.domain.doctor.Doctor;
import com.medisalud.infrastructure.doctor.persistence.DoctorJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T21:46:43-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Homebrew)"
)
@Component
public class DoctorPersistenceMapperImpl implements DoctorPersistenceMapper {

    @Override
    public DoctorJpaEntity toJpaEntity(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorJpaEntity.DoctorJpaEntityBuilder doctorJpaEntity = DoctorJpaEntity.builder();

        doctorJpaEntity.fullName( doctor.getFullName() );
        doctorJpaEntity.specialty( doctor.getSpecialty() );
        doctorJpaEntity.phone( doctor.getPhone() );
        doctorJpaEntity.email( doctor.getEmail() );
        doctorJpaEntity.createdAt( doctor.getCreatedAt() );
        doctorJpaEntity.updatedAt( doctor.getUpdatedAt() );

        doctorJpaEntity.id( doctor.getId().value() );

        return doctorJpaEntity.build();
    }
}
