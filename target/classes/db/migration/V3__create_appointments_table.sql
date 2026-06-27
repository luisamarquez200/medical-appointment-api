-- V3: Tabla de citas médicas
CREATE TABLE appointments (
    id                    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id            UUID         NOT NULL,
    doctor_id             UUID         NOT NULL,
    appointment_date_time TIMESTAMPTZ  NOT NULL,
    status                VARCHAR(20)  NOT NULL DEFAULT 'PROGRAMADA',
    cancelled_at          TIMESTAMPTZ,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_appointments_patient  FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_appointments_doctor   FOREIGN KEY (doctor_id)  REFERENCES doctors(id),
    CONSTRAINT chk_appointments_status  CHECK (status IN ('PROGRAMADA', 'CANCELADA', 'ATENDIDA')),

    CONSTRAINT uq_appointments_doctor_slot UNIQUE (doctor_id, appointment_date_time)
);
