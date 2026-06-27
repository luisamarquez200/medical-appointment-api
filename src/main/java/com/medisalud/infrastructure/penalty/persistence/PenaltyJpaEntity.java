package com.medisalud.infrastructure.penalty.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "penalties")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;

    @Column(name = "penalty_date_time", nullable = false)
    private LocalDateTime penaltyDateTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
