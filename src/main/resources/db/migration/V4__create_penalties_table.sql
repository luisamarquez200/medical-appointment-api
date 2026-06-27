-- V4: Tabla de penalizaciones (RN-05)
CREATE TABLE penalties (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id        UUID         NOT NULL,
    appointment_id    UUID         NOT NULL,
    penalty_date_time TIMESTAMPTZ  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_penalties_patient     FOREIGN KEY (patient_id)    REFERENCES patients(id),
    CONSTRAINT fk_penalties_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);
