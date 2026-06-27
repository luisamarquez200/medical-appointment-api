package com.medisalud.domain.appointment.repository;

import com.medisalud.domain.appointment.Appointment;
import com.medisalud.domain.appointment.AppointmentId;
import com.medisalud.domain.appointment.AppointmentStatus;
import com.medisalud.domain.appointment.TimeSlot;
import com.medisalud.domain.doctor.DoctorId;
import com.medisalud.domain.patient.PatientId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del dominio para persistencia de citas.
 *
 * <p>Contiene las queries necesarias para validar RN-02, RN-04, RF-04 y RF-06.
 * Cada método tiene una responsabilidad precisa y nombre expresivo.
 */
public interface IAppointmentRepository {

    /**
     * Persiste una cita (inserción o actualización).
     */
    Appointment save(Appointment appointment);

    /**
     * Busca una cita por su identificador.
     */
    Optional<Appointment> findById(AppointmentId id);

    /**
     * Verifica si un médico ya tiene una cita PROGRAMADA en la franja dada (RN-02).
     *
     * <p>Solo considera citas en estado PROGRAMADA — una cita cancelada libera la franja.
     *
     * @param doctorId el médico
     * @param timeSlot la franja a verificar
     * @return true si existe conflicto
     */
    boolean existsScheduledByDoctorAndTimeSlot(DoctorId doctorId, TimeSlot timeSlot);

    /**
     * Verifica si un paciente ya tiene una cita PROGRAMADA con un médico en la franja dada (RN-04).
     *
     * @param patientId el paciente
     * @param doctorId  el médico
     * @param timeSlot  la franja a verificar
     * @return true si existe conflicto
     */
    boolean existsScheduledByPatientAndDoctorAndTimeSlot(
            PatientId patientId, DoctorId doctorId, TimeSlot timeSlot);

    /**
     * Obtiene todas las citas PROGRAMADAS de un médico en un rango de fechas (RF-04).
     * Usado para calcular las franjas ocupadas y derivar las disponibles.
     *
     * @param doctorId  el médico
     * @param rangeStart inicio del rango (inclusive)
     * @param rangeEnd   fin del rango (exclusive)
     * @return lista de citas programadas en el rango
     */
    List<Appointment> findScheduledByDoctorAndDateRange(
            DoctorId doctorId, LocalDateTime rangeStart, LocalDateTime rangeEnd);

    /**
     * Lista citas con filtros opcionales (RF-06).
     *
     * <p>Todos los parámetros son opcionales (null = sin filtro).
     * La implementación construye la query dinámicamente.
     *
     * @param doctorId   filtrar por médico (opcional)
     * @param patientId  filtrar por paciente (opcional)
     * @param status     filtrar por estado (opcional)
     * @param rangeStart filtrar desde esta fecha (opcional)
     * @param rangeEnd   filtrar hasta esta fecha (opcional)
     * @return lista de citas que cumplen los filtros
     */
    List<Appointment> findByFilters(
            DoctorId doctorId,
            PatientId patientId,
            AppointmentStatus status,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd);
}
