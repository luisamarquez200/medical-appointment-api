-- V1: Tabla de médicos
CREATE TABLE doctors (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name   VARCHAR(100) NOT NULL,
    specialty   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(255),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
