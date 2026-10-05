-- 1. TABLA USUARIO
CREATE TABLE "usuario" (
    "id" SERIAL NOT NULL,
    "rol" VARCHAR(50) CHECK ("rol" IN ('ADMIN', 'RECEPCION')) NOT NULL,
    "nom_contraseña" VARCHAR(100) NOT NULL,
    "contraseña" VARCHAR(100) NOT NULL,
    CONSTRAINT "pk_usuario" PRIMARY KEY ("id"),
    CONSTRAINT "usuario_nom_contraseña_unique" UNIQUE ("nom_contraseña")
);

-- 2. TABLA PLAN
CREATE TABLE "plan" (
    "id" SERIAL NOT NULL,
    "nombre" VARCHAR(50) NOT NULL,
    "precio_mensual" VARCHAR(20) NOT NULL,
    "tipo" VARCHAR(50) CHECK ("tipo" IN ('BASICO', 'PREMIUM')) NOT NULL,
    CONSTRAINT "pk_plan" PRIMARY KEY ("id")
);

-- 3. TABLA SOCIO
CREATE TABLE "socio" (
    "id" SERIAL NOT NULL,
    "nombre" VARCHAR(100) NOT NULL,
    "apellidos" VARCHAR(150) NOT NULL,
    "telefono" VARCHAR(20) NOT NULL,
    "fecha_alta" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "matricula" VARCHAR(20) NULL,
    "activo" VARCHAR(20) CHECK ("activo" IN ('ACTIVO', 'INACTIVO')) NOT NULL DEFAULT 'ACTIVO',
    "email" VARCHAR(150) NOT NULL,
    "dni" VARCHAR(20) NOT NULL,
    "id_usuario" INTEGER NOT NULL,
    CONSTRAINT "pk_socio" PRIMARY KEY ("id"),
    CONSTRAINT "socio_email_unique" UNIQUE ("email"),
    CONSTRAINT "socio_dni_unique" UNIQUE ("dni"),
    CONSTRAINT "socio_id_usuario_foreign" FOREIGN KEY ("id_usuario") REFERENCES "usuario" ("id")
);

-- 4. TABLA SOCIO_PLAN (Tabla intermedia N:M)
CREATE TABLE "socio_plan" (
    "id" SERIAL NOT NULL,
    "id_plan" INTEGER NOT NULL,
    "id_socio" INTEGER NOT NULL,
    CONSTRAINT "pk_socio_plan" PRIMARY KEY ("id"),
    CONSTRAINT "socio_plan_id_socio_foreign" FOREIGN KEY ("id_socio") REFERENCES "socio" ("id"),
    CONSTRAINT "socio_plan_id_plan_foreign" FOREIGN KEY ("id_plan") REFERENCES "plan" ("id")
);

-- 5. TABLA CUOTA
CREATE TABLE "cuota" (
    "id" SERIAL NOT NULL,
    "mes" INTEGER NOT NULL,
    "año" INTEGER NOT NULL,
    "importe" VARCHAR(20) NOT NULL,
    "estado" VARCHAR(20) CHECK ("estado" IN ('PENDIENTE', 'PAGADO')) NOT NULL DEFAULT 'PENDIENTE',
    "fecha_pago" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "id_socio" INTEGER NOT NULL,
    CONSTRAINT "pk_cuota" PRIMARY KEY ("id"),
    CONSTRAINT "cuota_id_socio_foreign" FOREIGN KEY ("id_socio") REFERENCES "socio" ("id")
);

-- 6. TABLA ACCESO
CREATE TABLE "acceso" (
    "id" SERIAL NOT NULL,
    "fecha" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "hora_entrada" VARCHAR(20) NOT NULL,
    "hora_salida" VARCHAR(20) NULL,
    "tipo_resultado" VARCHAR(20) CHECK ("tipo_resultado" IN ('PERMITIDO', 'DENEGADO')) NOT NULL,
    "motivo_denegacion" VARCHAR(50) CHECK ("motivo_denegacion" IN ('INACTIVO', 'CUOTA_PENDIENTE', 'YA_DENTRO', 'AFORO_COMPLETO')) NULL,
    "ticket_parking" VARCHAR(50) NULL,
    "id_socio" INTEGER NOT NULL,
    CONSTRAINT "pk_acceso" PRIMARY KEY ("id"),
    CONSTRAINT "acceso_id_socio_foreign" FOREIGN KEY ("id_socio") REFERENCES "socio" ("id")
);

-- Cambiar las columnas de hora de VARCHAR a TIME
ALTER TABLE "acceso" 
    ALTER COLUMN "hora_entrada" TYPE TIME(0) USING "hora_entrada"::TIME(0),
    ALTER COLUMN "hora_salida" TYPE TIME(0) USING "hora_salida"::TIME(0);

-- 7. TABLA MONITOR
CREATE TABLE "monitor" (
    "id" SERIAL NOT NULL,
    "nombre" VARCHAR(100) NOT NULL,
    "apellidos" VARCHAR(150) NOT NULL,
    CONSTRAINT "pk_monitor" PRIMARY KEY ("id")
);

-- 8. TABLA ACTIVIDAD
CREATE TABLE "actividad" (
    "id" SERIAL NOT NULL,
    "nombre" VARCHAR(100) NOT NULL,
    "dia" VARCHAR(20) NOT NULL,
    "hora" VARCHAR(20) NOT NULL,
    "duracion" VARCHAR(20) NOT NULL,
    "sala" VARCHAR(50) NOT NULL,
    "aforo_max" INTEGER NOT NULL,
    "id_monitor" INTEGER NOT NULL,
    CONSTRAINT "pk_actividad" PRIMARY KEY ("id"),
    CONSTRAINT "actividad_id_monitor_foreign" FOREIGN KEY ("id_monitor") REFERENCES "monitor" ("id")
);

-- 9. TABLA RESERVA (Tabla intermedia N:M)
CREATE TABLE "reserva" (
    "id" SERIAL NOT NULL,
    "fecha_reserva" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "estado" VARCHAR(20) CHECK ("estado" IN ('ACTIVA', 'CANCELADA', 'ASISTIDA')) NOT NULL DEFAULT 'ACTIVA',
    "id_actividad" INTEGER NOT NULL,
    "id_socio" INTEGER NOT NULL,
    CONSTRAINT "pk_reserva" PRIMARY KEY ("id"),
    CONSTRAINT "reserva_id_socio_foreign" FOREIGN KEY ("id_socio") REFERENCES "socio" ("id"),
    CONSTRAINT "reserva_id_actividad_foreign" FOREIGN KEY ("id_actividad") REFERENCES "actividad" ("id")
);




-- 1. INSERTAR 10 USUARIOS (Recepcionistas y Administradores)
INSERT INTO "usuario" ("rol", "nom_contraseña", "contraseña") VALUES
('ADMIN', 'admin_carlos', '$2b$12$eImiTXuWVxfM37uY4JANjO8eB91f5e8a1d7c9f3b'),
('RECEPCION', 'recep_marta', '$2b$12$8x3k102931283012380128301283012830128301'),
('RECEPCION', 'recep_javier', '$2b$12$9y4l213042394123491239123912391239123912'),
('RECEPCION', 'recep_lucia', '$2b$12$0z5m324153405234502340234023402340234023'),
('ADMIN', 'admin_laura', '$2b$12$1a6n435264516345613451345134513451345134'),
('RECEPCION', 'recep_david', '$2b$12$2b7o546375627456724562456245624562456245'),
('RECEPCION', 'recep_elena', '$2b$12$3c8p657486738567835673567356735673567356'),
('ADMIN', 'admin_pablo', '$2b$12$4d9q768597849678946784678467846784678467'),
('RECEPCION', 'recep_sofia', '$2b$12$5e0r879608950789057895789578957895789578'),
('RECEPCION', 'recep_hugo', '$2b$12$6f1s980719061890168906890689068906890689');

-- 2. INSERTAR PLANES (Básico y Premium)
INSERT INTO "plan" ("nombre", "precio_mensual", "tipo") VALUES
('Plan Básico Mensual', '29.99', 'BASICO'),
('Plan Premium Mensual', '49.99', 'PREMIUM'),
('Plan Básico Anualizado', '24.99', 'BASICO'),
('Plan Premium Anualizado', '39.99', 'PREMIUM'),
('Plan Básico Estudiante', '19.99', 'BASICO'),
('Plan Premium Estudiante', '34.99', 'PREMIUM'),
('Plan Básico Senior', '15.99', 'BASICO'),
('Plan Premium Senior', '29.99', 'PREMIUM'),
('Plan Básico Pareja', '25.00', 'BASICO'),
('Plan Premium Pareja', '42.00', 'PREMIUM');

-- 3. INSERTAR 10 MONITORES
INSERT INTO "monitor" ("nombre", "apellidos") VALUES
('Roberto', 'García Gómez'),
('Carmen', 'Fernández López'),
('Adrian', 'Martínez Ruiz'),
('Silvia', 'Navarro Torres'),
('Marcos', 'Sánchez Díaz'),
('Patricia', 'Romero Gil'),
('Daniel', 'Serrano Blanco'),
('Irene', 'Molina Castro'),
('Gonzalo', 'Ortega Rubio'),
('Nerea', 'Delgado Morales');

-- 4. INSERTAR 10 SOCIOS (Relacionados con id_usuario de creación)
INSERT INTO "socio" ("nombre", "apellidos", "telefono", "fecha_alta", "matricula", "activo", "email", "dni", "id_usuario") VALUES
('Alejandro', 'Pérez Moreno', '612345678', '2025-01-10 10:00:00', '1234BCD', 'ACTIVO', 'ale.perez@email.com', '12345678A', 2),
('María', 'Gómez Fernández', '623456789', '2025-01-15 11:30:00', NULL, 'ACTIVO', 'maria.gomez@email.com', '23456789B', 2),
('Carlos', 'López Hernández', '634567890', '2025-02-01 09:15:00', '5678EFG', 'ACTIVO', 'carlos.lopez@email.com', '34567890C', 3),
('Lucía', 'Martín García', '645678901', '2025-02-10 16:45:00', NULL, 'INACTIVO', 'lucia.martin@email.com', '45678901D', 3),
('Jorge', 'Jiménez Ruiz', '656789012', '2025-03-01 12:00:00', '9012HIJ', 'ACTIVO', 'jorge.jimenez@email.com', '56789012E', 4),
('Ana', 'Sánchez Díaz', '667890123', '2025-03-05 18:20:00', NULL, 'ACTIVO', 'ana.sanchez@email.com', '67890123F', 4),
('David', 'Á Alvarez Santos', '678901234', '2025-03-20 10:10:00', '3456KLM', 'INACTIVO', 'david.alvarez@email.com', '78901234G', 6),
('Elena', 'Romero Torres', '689012345', '2025-04-02 11:00:00', NULL, 'ACTIVO', 'elena.romero@email.com', '89012345H', 6),
('Pablo', 'Alonso Gil', '690123456', '2025-04-12 17:30:00', '7890NOP', 'ACTIVO', 'pablo.alonso@email.com', '90123456I', 7),
('Sara', 'Gutiérrez Calvo', '601234567', '2025-05-01 08:45:00', NULL, 'ACTIVO', 'sara.gutierrez@email.com', '01234567J', 7);

-- 5. INSERTAR RELACIÓN SOCIO_PLAN (Planes contratados por cada socio)
INSERT INTO "socio_plan" ("id_plan", "id_socio") VALUES
(2, 1), -- Alejandro -> Premium
(1, 2), -- María -> Básico
(2, 3), -- Carlos -> Premium
(1, 4), -- Lucía -> Básico
(2, 5), -- Jorge -> Premium
(2, 6), -- Ana -> Premium
(1, 7), -- David -> Básico
(2, 8), -- Elena -> Premium
(1, 9), -- Pablo -> Básico
(2, 10);-- Sara -> Premium

-- 6. INSERTAR 10 CUOTAS
INSERT INTO "cuota" ("mes", "año", "importe", "estado", "fecha_pago", "id_socio") VALUES
(9, 2026, '49.99', 'PAGADO', '2026-09-01 09:15:00', 1),
(9, 2026, '29.99', 'PAGADO', '2026-09-02 10:30:00', 2),
(9, 2026, '49.99', 'PAGADO', '2026-09-01 18:00:00', 3),
(8, 2026, '29.99', 'PENDIENTE', NULL, 4), -- Impagada
(9, 2026, '29.99', 'PENDIENTE', NULL, 4), -- Impagada 
(9, 2026, '49.99', 'PAGADO', '2026-09-03 11:20:00', 5),
(9, 2026, '49.99', 'PAGADO', '2026-09-01 08:10:00', 6),
(9, 2026, '29.99', 'PENDIENTE', NULL, 7),
(9, 2026, '49.99', 'PAGADO', '2026-09-04 17:45:00', 8),
(9, 2026, '29.99', 'PAGADO', '2026-09-02 19:00:00', 9);

-- 7. INSERTAR 10 ACTIVIDADES (Clases Dirigidas)
INSERT INTO "actividad" ("nombre", "dia", "hora", "duracion", "sala", "aforo_max", "id_monitor") VALUES
('Spinning Intensivo', 'Lunes', '09:00', '45 min', 'Sala A', 20, 1),
('Yoga Flow', 'Lunes', '10:00', '60 min', 'Sala C', 15, 2),
('Body Pump', 'Martes', '18:00', '50 min', 'Sala B', 25, 3),
('Pilates Avanzado', 'Martes', '19:00', '50 min', 'Sala C', 15, 4),
('Cross Training', 'Miércoles', '08:00', '60 min', 'Zona Funcional', 12, 5),
('Zumba Dance', 'Miércoles', '19:30', '50 min', 'Sala A', 30, 6),
('Spinning Suave', 'Jueves', '18:30', '45 min', 'Sala A', 20, 7),
('GAP', 'Viernes', '11:00', '45 min', 'Sala B', 20, 8),
('Boxeo Fit', 'Viernes', '18:00', '60 min', 'Zona Ring', 10, 9),
('Aquagym', 'Sábado', '10:30', '45 min', 'Piscina', 15, 10);

-- 8. INSERTAR 10 ACCESOS (Intentos permitidos y denegados)
INSERT INTO "acceso" ("fecha", "hora_entrada", "hora_salida", "tipo_resultado", "motivo_denegacion", "ticket_parking", "id_socio") VALUES
('2026-09-29 08:00:00', '08:00', '09:30', 'PERMITIDO', NULL, 'PARK-1001', 1),
('2026-09-29 09:15:00', '09:15', '10:45', 'PERMITIDO', NULL, NULL, 2),
('2026-09-29 10:00:00', '10:00', NULL, 'PERMITIDO', NULL, 'PARK-1002', 3), -- Sigue dentro
('2026-09-29 10:30:00', '10:30', NULL, 'DENEGADO', 'INACTIVO', NULL, 4), -- Rechazado
('2026-09-29 11:00:00', '11:00', '12:15', 'PERMITIDO', NULL, 'PARK-1003', 5);
-- 9. INSERTAR 10 RESERVAS (Realizadas solo por socios con Plan Premium)
INSERT INTO "reserva" ("fecha_reserva", "estado", "id_actividad", "id_socio") VALUES
('2026-09-28 10:00:00', 'ASISTIDA', 1, 1),
('2026-09-28 11:00:00', 'ASISTIDA', 2, 3),
('2026-09-29 09:00:00', 'ACTIVA', 3, 1),
('2026-09-29 09:30:00', 'ACTIVA', 3, 5),
('2026-09-29 10:00:00', 'ACTIVA', 4, 6),
('2026-09-29 12:00:00', 'ACTIVA', 5, 8),
('2026-09-29 14:00:00', 'ACTIVA', 6, 10),
('2026-09-29 15:00:00', 'ACTIVA', 7, 1),
('2026-09-29 16:00:00', 'CANCELADA', 8, 3),
('2026-09-29 17:00:00', 'ACTIVA', 9, 5);