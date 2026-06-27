-- Datos iniciales
-- Esto se ejecuta automáticamente porque ddl-auto está en create-drop.
INSERT INTO doctors (id, full_name, specialty, phone, email, created_at, updated_at) VALUES
    ('a1b2c3d4-0001-0001-0001-000000000001', 'Dra. María González', 'Cardiología',  '5551001', 'maria.gonzalez@medisalud.com', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    ('a1b2c3d4-0002-0002-0002-000000000002', 'Dr. Carlos Ruiz',     'Pediatría',    '5551002', 'carlos.ruiz@medisalud.com',    CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    ('a1b2c3d4-0003-0003-0003-000000000003', 'Dra. Ana López',      'Dermatología', '5551003', 'ana.lopez@medisalud.com',      CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
