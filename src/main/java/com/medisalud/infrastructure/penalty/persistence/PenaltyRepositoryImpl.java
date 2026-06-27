package com.medisalud.infrastructure.penalty.persistence;

import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.patient.PatientId;
import com.medisalud.domain.penalty.Penalty;
import com.medisalud.domain.penalty.PenaltyId;
import com.medisalud.domain.penalty.repository.IPenaltyRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class PenaltyRepositoryImpl implements IPenaltyRepository {

    private final PenaltyJpaRepository jpaRepository;

    public PenaltyRepositoryImpl(PenaltyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Penalty save(Penalty penalty) {
        PenaltyJpaEntity entity = PenaltyJpaEntity.builder()
                .id(penalty.getId().value())
                .patientId(penalty.getPatientId().value())
                .appointmentId(penalty.getAppointmentId().value())
                .penaltyDateTime(penalty.getPenaltyDateTime())
                .createdAt(penalty.getCreatedAt())
                .updatedAt(penalty.getUpdatedAt())
                .build();
        PenaltyJpaEntity saved = jpaRepository.save(entity);
        return Penalty.reconstitute(
                PenaltyId.of(saved.getId()),
                PatientId.of(saved.getPatientId()),
                AppointmentId.of(saved.getAppointmentId()),
                saved.getPenaltyDateTime(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    public long countByPatientIdAndPenaltyDateTimeAfter(PatientId patientId, LocalDateTime since) {
        return jpaRepository.countByPatientIdAndPenaltyDateTimeAfter(patientId.value(), since);
    }
}
