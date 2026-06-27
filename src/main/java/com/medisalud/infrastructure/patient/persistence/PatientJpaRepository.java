package com.medisalud.infrastructure.patient.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientJpaRepository extends JpaRepository<PatientJpaEntity, UUID> {

    boolean existsByDocumentId(String documentId);
}
