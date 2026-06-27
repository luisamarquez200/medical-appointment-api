package com.medisalud.domain.appointment;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object que representa una franja horaria de 30 minutos.
 *
 * <p>Responsabilidad única: encapsular el concepto de "franja horaria" del dominio.
 * Una franja comienza en un minuto exacto (0 o 30) y dura exactamente 30 minutos.
 *
 * <p>Por qué no es un Record:
 * Aunque es inmutable, necesita un constructor privado con lógica de construcción
 * controlada (no se puede crear con cualquier dateTime, solo con valores válidos
 * desde {@link #of(LocalDateTime)}). Un record expone su constructor compacto
 * directamente, lo que rompería el encapsulamiento.
 *
 * <p>Inmutabilidad: todos los campos son final. LocalDateTime es inmutable.
 *
 * <p>Identidad por valor: dos TimeSlots son iguales si tienen el mismo startDateTime.
 */
public final class TimeSlot {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private TimeSlot(LocalDateTime start) {
        this.start = start;
        this.end = start.plusMinutes(30);
    }

    /**
     * Crea un TimeSlot a partir de un instante de inicio.
     *
     * <p>La validación de horario laboral (RN-01) es responsabilidad del
     * {@link com.medisalud.domain.appointment.service.TimeSlotValidatorService},
     * no de este Value Object. Este solo representa la franja, no la valida.
     *
     * @param start el momento exacto de inicio de la franja
     * @return un nuevo TimeSlot inmutable
     */
    public static TimeSlot of(LocalDateTime start) {
        Objects.requireNonNull(start, "TimeSlot start cannot be null");
        return new TimeSlot(start);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Verifica si este TimeSlot se superpone con otro.
     * Dos franjas se superponen si una comienza antes de que la otra termine.
     */
    public boolean overlapsWith(TimeSlot other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    /**
     * Verifica si un instante dado cae dentro de esta franja (inclusive inicio, exclusivo fin).
     */
    public boolean contains(LocalDateTime dateTime) {
        return !dateTime.isBefore(start) && dateTime.isBefore(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot other)) return false;
        return Objects.equals(start, other.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start);
    }

    @Override
    public String toString() {
        return "TimeSlot{" + start + " - " + end + "}";
    }
}
