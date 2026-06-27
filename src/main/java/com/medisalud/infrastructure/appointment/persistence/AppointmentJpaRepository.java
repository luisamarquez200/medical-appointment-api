package com.medisalud.infrastructure.appointment.persistence;

import com.medisalud.domain.appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository para AppointmentJpaEntity.
 *
 * <p>Las queries JPQL están tipadas y seguras contra inyección SQL.
 * Se evita el uso de native queries salvo que sean imprescindibles.
 */
public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, UUID> {

    /**
     * RN-02: Verifica si el médico tiene una cita PROGRAMADA en esa franja exacta.
     */
    boolean existsByDoctorIdAndAppointmentDateTimeAndStatus(
            UUID doctorId,
            LocalDateTime appointmentDateTime,
            AppointmentStatus status);

    /**
     * RN-04: Verifica si el paciente tiene una cita PROGRAMADA con el médico en esa franja.
     */
    boolean existsByPatientIdAndDoctorIdAndAppointmentDateTimeAndStatus(
            UUID patientId,
            UUID doctorId,
            LocalDateTime appointmentDateTime,
            AppointmentStatus status);

    /**
     * RF-04: Obtiene citas PROGRAMADAS de un médico en un rango de fechas.
     */
    @Query("SELECT a FROM AppointmentJpaEntity a " +
           "WHERE a.doctorId = :doctorId " +
           "AND a.status = 'PROGRAMADA' " +
           "AND a.appointmentDateTime >= :rangeStart " +
           "AND a.appointmentDateTime < :rangeEnd")
    List<AppointmentJpaEntity> findScheduledByDoctorAndDateRange(
            @Param("doctorId") UUID doctorId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd);

    /**
     * RF-06: Listado dinámico con filtros opcionales.
     * COALESCE con parámetros opcionales — null significa "sin filtro".
     */
    @Query("SELECT a FROM AppointmentJpaEntity a " +
           "WHERE (:doctorId IS NULL OR a.doctorId = :doctorId) " +
           "AND (:patientId IS NULL OR a.patientId = :patientId) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:rangeStart IS NULL OR a.appointmentDateTime >= :rangeStart) " +
           "AND (:rangeEnd IS NULL OR a.appointmentDateTime < :rangeEnd) " +
           "ORDER BY a.appointmentDateTime ASC")
    List<AppointmentJpaEntity> findByFilters(
            @Param("doctorId") UUID doctorId,
            @Param("patientId") UUID patientId,
            @Param("status") AppointmentStatus status,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd);
}
