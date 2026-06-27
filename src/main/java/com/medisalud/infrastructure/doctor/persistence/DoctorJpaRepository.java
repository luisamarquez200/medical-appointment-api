package com.medisalud.infrastructure.doctor.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA Repository para DoctorJpaEntity.
 *
 * <p>Esta interfaz vive en infraestructura — es un detalle de implementación.
 * El dominio no la conoce. La implementación del puerto IDoctorRepository
 * ({@link DoctorRepositoryImpl}) la usa internamente.
 */
public interface DoctorJpaRepository extends JpaRepository<DoctorJpaEntity, UUID> {
}
