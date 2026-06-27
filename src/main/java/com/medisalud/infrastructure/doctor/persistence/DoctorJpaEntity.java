package com.medisalud.infrastructure.doctor.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la tabla doctors.
 *
 * <p>Esta clase existe en infraestructura, NO en el dominio.
 * El dominio tiene su propia clase Doctor sin anotaciones de JPA.
 * El mapper convierte entre ambas representaciones.
 *
 * <p>Esto protege al dominio de los detalles de persistencia:
 * si mañana cambiamos de JPA a JDBC, el dominio no se toca.
 */
@Entity
@Table(name = "doctors")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
