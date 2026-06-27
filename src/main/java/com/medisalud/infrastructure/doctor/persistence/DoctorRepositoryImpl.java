package com.medisalud.infrastructure.doctor.persistence;

import com.medisalud.domain.doctor.Doctor;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.doctor.repository.IDoctorRepository;
import com.medisalud.infrastructure.doctor.mapper.DoctorPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementación de IDoctorRepository usando JPA.
 *
 * <p>Implementa el puerto definido en el dominio.
 * La anotación @Repository pertenece a Spring — está correctamente ubicada
 * en infraestructura, no en el dominio.
 *
 * <p>Convierte entre Doctor (dominio) y DoctorJpaEntity (JPA) usando el mapper.
 */
@Repository
public class DoctorRepositoryImpl implements IDoctorRepository {

    private final DoctorJpaRepository jpaRepository;
    private final DoctorPersistenceMapper mapper;

    public DoctorRepositoryImpl(DoctorJpaRepository jpaRepository, DoctorPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Doctor save(Doctor doctor) {
        DoctorJpaEntity entity = mapper.toJpaEntity(doctor);
        DoctorJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Doctor> findById(DoctorId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(DoctorId id) {
        return jpaRepository.existsById(id.value());
    }
}
