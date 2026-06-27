-- V2: Tabla de pacientes
CREATE TABLE patients (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name    VARCHAR(100) NOT NULL,
    document_id  VARCHAR(50)  NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    email        VARCHAR(255) NOT NULL,
    birth_date   DATE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_patients_document_id UNIQUE (document_id)
);
