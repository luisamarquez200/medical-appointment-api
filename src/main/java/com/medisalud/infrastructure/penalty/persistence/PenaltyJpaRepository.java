package com.medisalud.infrastructure.penalty.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PenaltyJpaRepository extends JpaRepository<PenaltyJpaEntity, UUID> {

    /**
     * RN-05: Cuenta penalizaciones del paciente posteriores a la fecha dada (últimos 30 días).
     */
    @Query("SELECT COUNT(p) FROM PenaltyJpaEntity p " +
           "WHERE p.patientId = :patientId " +
           "AND p.penaltyDateTime >= :since")
    long countByPatientIdAndPenaltyDateTimeAfter(
            @Param("patientId") UUID patientId,
            @Param("since") LocalDateTime since);
}
