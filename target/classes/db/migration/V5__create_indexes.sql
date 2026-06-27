-- V5: Índices de rendimiento

-- RF-04: Consultar franjas disponibles por médico en rango de fechas
CREATE INDEX idx_appointments_doctor_datetime
    ON appointments (doctor_id, appointment_date_time);

-- RF-06: Filtrar citas por paciente y estado
CREATE INDEX idx_appointments_patient_status
    ON appointments (patient_id, status);

-- RF-06: Filtrar citas por médico y estado
CREATE INDEX idx_appointments_doctor_status
    ON appointments (doctor_id, status);

-- RN-04: Verificar conflicto paciente+médico+franja
CREATE INDEX idx_appointments_patient_doctor_datetime
    ON appointments (patient_id, doctor_id, appointment_date_time);

-- RN-05: Contar penalizaciones recientes de un paciente (últimos 30 días)
CREATE INDEX idx_penalties_patient_datetime
    ON penalties (patient_id, penalty_date_time);
