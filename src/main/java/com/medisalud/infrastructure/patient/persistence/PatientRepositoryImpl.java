package com.medisalud.infrastructure.patient.persistence;

import com.medisalud.domain.patient.Patient;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.patient.repository.IPatientRepository;
import com.medisalud.infrastructure.patient.mapper.PatientPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PatientRepositoryImpl implements IPatientRepository {

    private final PatientJpaRepository jpaRepository;
    private final PatientPersistenceMapper mapper;

    public PatientRepositoryImpl(PatientJpaRepository jpaRepository, PatientPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Patient save(Patient patient) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(patient)));
    }

    @Override
    public Optional<Patient> findById(PatientId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDocumentId(String documentId) {
        return jpaRepository.existsByDocumentId(documentId);
    }
}
