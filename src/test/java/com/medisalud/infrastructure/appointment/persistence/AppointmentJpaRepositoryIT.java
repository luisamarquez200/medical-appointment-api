package com.medisalud.infrastructure.appointment.persistence;

import com.medisalud.domain.appointment.AppointmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AppointmentJpaRepository Integration Tests (Testcontainers)")
class AppointmentJpaRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("medisalud_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private AppointmentJpaRepository repository;

    @Test
    @DisplayName("Debería guardar una cita y detectar duplicados por franja horaria usando restricciones de BD")
    void testSaveAndDuplicate() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 15, 10, 0);

        AppointmentJpaEntity appointment = AppointmentJpaEntity.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .appointmentDateTime(dateTime)
                .status(AppointmentStatus.PROGRAMADA)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Primera vez se guarda exitosamente
        AppointmentJpaEntity saved = repository.saveAndFlush(appointment);
        assertNotNull(saved);
        
        // Verifica que la restricción unique constraint lanzará error si guardamos otro con mismo doctor y fecha (RN-02)
        AppointmentJpaEntity duplicate = AppointmentJpaEntity.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(UUID.randomUUID()) // otro paciente
                .appointmentDateTime(dateTime) // misma fecha
                .status(AppointmentStatus.PROGRAMADA)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(duplicate);
        });
    }
}
