
-- 1. ELIMINAR LAS TABLAS 
DROP TABLE IF EXISTS reserva CASCADE;
DROP TABLE IF EXISTS acceso CASCADE;
DROP TABLE IF EXISTS cuota CASCADE;
DROP TABLE IF EXISTS socio_plan CASCADE;
DROP TABLE IF EXISTS actividad CASCADE;
DROP TABLE IF EXISTS monitor CASCADE;
DROP TABLE IF EXISTS socio CASCADE;
DROP TABLE IF EXISTS plan CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    rol VARCHAR(20) NOT NULL
        CHECK (rol IN ('ADMIN', 'RECEPCION', 'SOCIO')),
    nom_contraseña VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(100) NOT NULL
);

CREATE TABLE plan (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    precio_mensual NUMERIC(10, 2) NOT NULL CHECK (precio_mensual > 0),
    tipo VARCHAR(20) NOT NULL UNIQUE
        CHECK (tipo IN ('BASICO', 'PREMIUM'))
);

-- El usuario es opcional: puede existir un socio sin cuenta de acceso.
-- La columna "activo" se mantiene para coincidir con el modelo Java actual.
CREATE TABLE socio (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_alta TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    matricula VARCHAR(7),
    activo VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
        CHECK (activo IN ('ACTIVO', 'INACTIVO')),
    email VARCHAR(150) NOT NULL UNIQUE,
    dni VARCHAR(9) NOT NULL UNIQUE,
    id_usuario INTEGER UNIQUE,
    CONSTRAINT fk_socio_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_socio_dni_formato
        CHECK (dni ~ '^[0-9]{8}[A-Z]$'),
    CONSTRAINT chk_socio_matricula
        CHECK (
            matricula IS NULL
            OR matricula ~ '^[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}$'
        )
);

-- Guarda el historial de planes de cada socio.
-- fecha_fin NULL significa que es el plan actual.
CREATE TABLE socio_plan (
    id SERIAL PRIMARY KEY,
    id_socio INTEGER NOT NULL,
    id_plan INTEGER NOT NULL,
    fecha_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_fin DATE,
    CONSTRAINT fk_socio_plan_socio
        FOREIGN KEY (id_socio) REFERENCES socio(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_socio_plan_plan
        FOREIGN KEY (id_plan) REFERENCES plan(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_socio_plan_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

-- Un socio solo puede tener un plan actual.
CREATE UNIQUE INDEX uq_socio_plan_actual
    ON socio_plan(id_socio)
    WHERE fecha_fin IS NULL;

CREATE INDEX idx_socio_plan_plan
    ON socio_plan(id_plan);

-- Se guarda id_plan para conservar el plan y el precio que se cobraron
-- aunque el socio cambie de plan más adelante.
CREATE TABLE cuota (
    id SERIAL PRIMARY KEY,
    mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    anio INTEGER NOT NULL CHECK (anio BETWEEN 2000 AND 2100),
    importe NUMERIC(10, 2) NOT NULL CHECK (importe >= 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE', 'PAGADO')),
    fecha_pago TIMESTAMP WITHOUT TIME ZONE,
    id_socio INTEGER NOT NULL,
    id_plan INTEGER NOT NULL,
    CONSTRAINT fk_cuota_socio
        FOREIGN KEY (id_socio) REFERENCES socio(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_cuota_plan
        FOREIGN KEY (id_plan) REFERENCES plan(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_cuota_socio_mes_anio
        UNIQUE (id_socio, mes, anio),
    CONSTRAINT chk_cuota_fecha_pago
        CHECK (
            (estado = 'PAGADO' AND fecha_pago IS NOT NULL)
            OR
            (estado = 'PENDIENTE' AND fecha_pago IS NULL)
        )
);

CREATE INDEX idx_cuota_estado
    ON cuota(estado);

CREATE INDEX idx_cuota_mes_anio
    ON cuota(anio, mes);


-- Cada fila representa un intento de entrada.
-- hora_entrada y hora_salida son TIMESTAMP para poder calcular
-- duración y saber quién continúa dentro.
CREATE TABLE acceso (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    hora_entrada TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    hora_salida TIMESTAMP WITHOUT TIME ZONE,
    tipo_resultado VARCHAR(20) NOT NULL
        CHECK (tipo_resultado IN ('PERMITIDO', 'DENEGADO')),
    motivo_denegacion VARCHAR(50)
        CHECK (
            motivo_denegacion IN (
                'INACTIVO',
                'CUOTA_PENDIENTE',
                'YA_DENTRO',
                'AFORO_COMPLETO'
            )
        ),
    ticket_parking VARCHAR(50),
    id_socio INTEGER NOT NULL,
    CONSTRAINT fk_acceso_socio
        FOREIGN KEY (id_socio) REFERENCES socio(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_acceso_horas
        CHECK (hora_salida IS NULL OR hora_salida >= hora_entrada),
    CONSTRAINT chk_acceso_fecha
        CHECK (fecha = hora_entrada::DATE),
    CONSTRAINT chk_acceso_resultado_motivo
        CHECK (
            (tipo_resultado = 'PERMITIDO' AND motivo_denegacion IS NULL)
            OR
            (tipo_resultado = 'DENEGADO' AND motivo_denegacion IS NOT NULL)
        )
);

CREATE INDEX idx_acceso_socio
    ON acceso(id_socio);

CREATE INDEX idx_acceso_fecha
    ON acceso(fecha);

CREATE INDEX idx_acceso_abiertos
    ON acceso(hora_salida)
    WHERE tipo_resultado = 'PERMITIDO' AND hora_salida IS NULL;

CREATE TABLE monitor (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL
);

-- Cada registro representa una sesión concreta de una clase.
-- Por eso se guarda fecha y hora reales, no solo el día de la semana.
CREATE TABLE actividad (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_hora_inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    duracion_minutos INTEGER NOT NULL CHECK (duracion_minutos > 0),
    sala VARCHAR(50) NOT NULL,
    aforo_max INTEGER NOT NULL CHECK (aforo_max > 0),
    id_monitor INTEGER NOT NULL,
    CONSTRAINT fk_actividad_monitor
        FOREIGN KEY (id_monitor) REFERENCES monitor(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_actividad_fecha
    ON actividad(fecha_hora_inicio);


CREATE TABLE reserva (
    id SERIAL PRIMARY KEY,
    fecha_reserva TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA'
        CHECK (estado IN ('ACTIVA', 'CANCELADA', 'ASISTIDA')),
    id_actividad INTEGER NOT NULL,
    id_socio INTEGER NOT NULL,
    CONSTRAINT fk_reserva_socio
        FOREIGN KEY (id_socio) REFERENCES socio(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_actividad
        FOREIGN KEY (id_actividad) REFERENCES actividad(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- No se permiten dos reservas no canceladas del mismo socio
-- para la misma sesión. Una reserva cancelada sí permite volver a reservar.
CREATE UNIQUE INDEX uq_reserva_socio_actividad_activa
    ON reserva(id_socio, id_actividad)
    WHERE estado <> 'CANCELADA';

CREATE INDEX idx_reserva_actividad
    ON reserva(id_actividad);

CREATE INDEX idx_reserva_socio_estado
    ON reserva(id_socio, estado);


-- Hash BCrypt de prueba, contraseña: password
INSERT INTO usuario (rol, nom_contraseña, contraseña) VALUES
('ADMIN',     'admin_carlos',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('ADMIN',     'admin_laura',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('RECEPCION', 'recep_marta',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('RECEPCION', 'recep_javier',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_alejandro','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_maria',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_carlos',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_lucia',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_jorge',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_ana',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_david',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_elena',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_pablo',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('SOCIO',     'socio_sara',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');


-- 12. PLANES
INSERT INTO plan (nombre, precio_mensual, tipo) VALUES
('Plan Básico', 29.99, 'BASICO'),
('Plan Premium', 49.99, 'PREMIUM');


INSERT INTO monitor (nombre, apellidos) VALUES
('Roberto', 'García Gómez'),
('Carmen', 'Fernández López'),
('Adrián', 'Martínez Ruiz'),
('Silvia', 'Navarro Torres'),
('Marcos', 'Sánchez Díaz'),
('Patricia', 'Romero Gil'),
('Daniel', 'Serrano Blanco'),
('Irene', 'Molina Castro'),
('Gonzalo', 'Ortega Rubio'),
('Nerea', 'Delgado Morales');


-- DNI: letra de control calculada para cada número.
-- Socio 4 está INACTIVO por tener dos cuotas pendientes.
-- Todos tienen usuario asociado, pero la relación permite NULL.
INSERT INTO socio
(nombre, apellidos, telefono, fecha_alta, matricula, activo, email, dni, id_usuario)
VALUES
('Alejandro', 'Pérez Moreno',       '612345678', '2025-01-10 10:00:00', '1234BCD', 'ACTIVO',   'ale.perez@email.com',       '12345678Z', 5),
('María',     'Gómez Fernández',    '623456789', '2025-01-15 11:30:00', NULL,      'ACTIVO',   'maria.gomez@email.com',     '23456789D', 6),
('Carlos',    'López Hernández',    '634567890', '2025-02-01 09:15:00', '5678FGH', 'ACTIVO',   'carlos.lopez@email.com',    '34567890V', 7),
('Lucía',     'Martín García',      '645678901', '2025-02-10 16:45:00', NULL,      'INACTIVO', 'lucia.martin@email.com',    '45678901G', 8),
('Jorge',     'Jiménez Ruiz',       '656789012', '2025-03-01 12:00:00', '9012HJK', 'ACTIVO',   'jorge.jimenez@email.com',   '56789012B', 9),
('Ana',       'Sánchez Díaz',       '667890123', '2025-03-05 18:20:00', NULL,      'ACTIVO',   'ana.sanchez@email.com',     '67890123B', 10),
('David',     'Álvarez Santos',     '678901234', '2025-03-20 10:10:00', '3456KLM', 'ACTIVO',   'david.alvarez@email.com',   '78901234X', 11),
('Elena',     'Romero Torres',      '689012345', '2025-04-02 11:00:00', NULL,      'ACTIVO',   'elena.romero@email.com',    '89012345E', 12),
('Pablo',     'Alonso Gil',         '690123456', '2025-04-12 17:30:00', '7890NPV', 'ACTIVO',   'pablo.alonso@email.com',    '90123456A', 13),
('Sara',      'Gutiérrez Calvo',    '601234567', '2025-05-01 08:45:00', NULL,      'ACTIVO',   'sara.gutierrez@email.com',  '01234567L', 14);


-- Socios 1, 3, 5, 6, 8 y 10: PREMIUM.
-- Socios 2, 4, 7 y 9: BASICO.
INSERT INTO socio_plan (id_socio, id_plan, fecha_inicio, fecha_fin) VALUES
(1, 2, '2025-01-10', NULL),
(2, 1, '2025-01-15', NULL),
(3, 2, '2025-02-01', NULL),
(4, 1, '2025-02-10', NULL),
(5, 2, '2025-03-01', NULL),
(6, 2, '2025-03-05', NULL),
(7, 1, '2025-03-20', NULL),
(8, 2, '2025-04-02', NULL),
(9, 1, '2025-04-12', NULL),
(10, 2, '2025-05-01', NULL);


-- Datos de octubre y noviembre de 2026.
-- Socio 4: dos cuotas pendientes (septiembre y octubre), inactivo.
-- Socio 7: una cuota pendiente en noviembre, sigue activo.
-- El resto tiene pagada la cuota de noviembre.
INSERT INTO cuota (mes, anio, importe, estado, fecha_pago, id_socio, id_plan) VALUES
-- Septiembre 2026: dos impagos del socio 4
(9, 2026, 29.99, 'PENDIENTE', NULL, 4, 1),

-- Octubre 2026
(10, 2026, 49.99, 'PAGADO',    '2026-10-02 09:15:00', 1, 2),
(10, 2026, 29.99, 'PAGADO',    '2026-10-02 10:30:00', 2, 1),
(10, 2026, 49.99, 'PAGADO',    '2026-10-03 11:00:00', 3, 2),
(10, 2026, 29.99, 'PENDIENTE', NULL,                   4, 1),
(10, 2026, 49.99, 'PAGADO',    '2026-10-03 12:00:00', 5, 2),
(10, 2026, 49.99, 'PAGADO',    '2026-10-03 13:00:00', 6, 2),
(10, 2026, 29.99, 'PAGADO',    '2026-10-04 09:00:00', 7, 1),
(10, 2026, 49.99, 'PAGADO',    '2026-10-04 10:00:00', 8, 2),
(10, 2026, 29.99, 'PAGADO',    '2026-10-04 11:00:00', 9, 1),
(10, 2026, 49.99, 'PAGADO',    '2026-10-04 12:00:00', 10, 2),

-- Noviembre 2026
(11, 2026, 49.99, 'PAGADO',    '2026-11-02 09:15:00', 1, 2),
(11, 2026, 29.99, 'PAGADO',    '2026-11-02 10:30:00', 2, 1),
(11, 2026, 49.99, 'PAGADO',    '2026-11-03 11:00:00', 3, 2),
(11, 2026, 49.99, 'PAGADO',    '2026-11-03 12:00:00', 5, 2),
(11, 2026, 49.99, 'PAGADO',    '2026-11-03 13:00:00', 6, 2),
(11, 2026, 29.99, 'PENDIENTE', NULL,                   7, 1),
(11, 2026, 49.99, 'PAGADO',    '2026-11-04 09:00:00', 8, 2),
(11, 2026, 29.99, 'PAGADO',    '2026-11-04 10:00:00', 9, 1),
(11, 2026, 49.99, 'PAGADO',    '2026-11-04 11:00:00', 10, 2);


-- Son sesiones concretas futuras, para poder consultar por fecha
-- y comprobar reservas y plazas disponibles.
INSERT INTO actividad
(nombre, fecha_hora_inicio, duracion_minutos, sala, aforo_max, id_monitor)
VALUES
('Spinning Intensivo', '2026-11-06 09:00:00', 45, 'Sala A', 2, 1),
('Yoga Flow',          '2026-11-06 10:00:00', 60, 'Sala C', 2, 2),
('Body Pump',          '2026-11-07 18:00:00', 50, 'Sala B', 20, 3),
('Pilates Avanzado',   '2026-11-08 19:00:00', 50, 'Sala C', 15, 4),
('Cross Training',     '2026-11-09 08:00:00', 60, 'Zona Funcional', 12, 5),
('Zumba Dance',        '2026-11-10 19:30:00', 50, 'Sala A', 30, 6),
('Spinning Suave',     '2026-11-11 18:30:00', 45, 'Sala A', 20, 7),
('GAP',                '2026-11-12 11:00:00', 45, 'Sala B', 20, 8),
('Boxeo Fit',          '2026-11-13 18:00:00', 60, 'Zona Ring', 10, 9),
('Aquagym',            '2026-11-14 10:30:00', 45, 'Piscina', 15, 10);


-- Se incluyen entradas permitidas, intentos rechazados y entradas abiertas.
-- Las entradas permitidas abiertas representan socios que siguen dentro.
INSERT INTO acceso
(fecha, hora_entrada, hora_salida, tipo_resultado, motivo_denegacion, ticket_parking, id_socio)
VALUES
('2026-11-05', '2026-11-05 08:00:00', NULL,                  'PERMITIDO', NULL,              'PARK-1001', 1),
('2026-11-05', '2026-11-05 08:15:00', '2026-11-05 09:30:00', 'PERMITIDO', NULL,              NULL,        3),
('2026-11-05', '2026-11-05 09:00:00', NULL,                  'PERMITIDO', NULL,              'PARK-1002', 5),
('2026-11-05', '2026-11-05 09:10:00', NULL,                  'DENEGADO',  'INACTIVO',        NULL,        4),
('2026-11-05', '2026-11-05 09:20:00', NULL,                  'DENEGADO',  'CUOTA_PENDIENTE', NULL,        7),
('2026-11-05', '2026-11-05 09:25:00', NULL,                  'DENEGADO',  'YA_DENTRO',       NULL,        1),
('2026-11-05', '2026-11-05 09:30:00', NULL,                  'DENEGADO',  'AFORO_COMPLETO',  NULL,        8);


-- Solo socios PREMIUM y con la cuota de noviembre pagada.
-- La actividad 1 queda completa (aforo máximo: 2).
-- El socio 1 tiene tres reservas futuras activas (su máximo permitido).
INSERT INTO reserva (fecha_reserva, estado, id_actividad, id_socio) VALUES
('2026-11-01 10:00:00', 'ACTIVA',    1, 1),
('2026-11-01 10:05:00', 'ACTIVA',    1, 3),
('2026-11-01 10:10:00', 'ACTIVA',    2, 5),
('2026-11-01 10:15:00', 'ACTIVA',    2, 6),
('2026-11-02 09:00:00', 'ACTIVA',    3, 1),
('2026-11-02 09:05:00', 'ACTIVA',    4, 1),
('2026-11-02 09:10:00', 'ACTIVA',    5, 3),
('2026-11-02 09:15:00', 'CANCELADA', 6, 5),
('2026-11-02 09:20:00', 'ACTIVA',    7, 6),
('2026-11-02 09:25:00', 'ACTIVA',    8, 8);


-- ============================================================
-- FIN DEL SCRIPT
--
-- Comprobaciones rápidas:
--   SELECT * FROM usuario;
--   SELECT * FROM plan;
--   SELECT * FROM socio;
--   SELECT * FROM cuota ORDER BY anio, mes, id_socio;
--   SELECT * FROM actividad ORDER BY fecha_hora_inicio;
--   SELECT * FROM acceso ORDER BY hora_entrada;
--   SELECT * FROM reserva;
-- ============================================================
