-- #############################################
-- SCRIPT DE CREACIÓN DE TABLAS (DDL)
-- #############################################

-- 1. Tabla PACIENTE
CREATE TABLE paciente (
    id SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    curp TEXT UNIQUE,
    edad INT NOT NULL CHECK (edad >= 0),
    ts_creado TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla USUARIO
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    login TEXT NOT NULL UNIQUE,
    hash TEXT NOT NULL, -- Almacenar hash seguro (e.g., BCrypt)
    rol TEXT NOT NULL CHECK (rol IN ('recepcion', 'medico', 'pantalla', 'admin')),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Tabla TURNO
CREATE TABLE turno (
    id SERIAL PRIMARY KEY,
    paciente_id INT NOT NULL REFERENCES paciente(id),
    recepcionista_id INT NOT NULL REFERENCES usuario(id),
    medico_id INT REFERENCES usuario(id), -- Nullable, se asigna al atender
    prioridad INT NOT NULL CHECK (prioridad >= 1 AND prioridad <= 5),
    estado TEXT NOT NULL CHECK (estado IN ('EN_COLA', 'ATENDIENDO', 'FINALIZADO', 'CANCELADO')),
    ts_creado TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ts_inicio TIMESTAMP WITHOUT TIME ZONE,
    ts_fin TIMESTAMP WITHOUT TIME ZONE
);

-- Índices para optimizar la selección del siguiente turno
CREATE INDEX idx_turno_cola ON turno (prioridad DESC, ts_creado ASC) WHERE estado = 'EN_COLA';


-- 4. Tabla SINTOMAS (Información de Triage)
CREATE TABLE sintomas (
    id SERIAL PRIMARY KEY,
    turno_id INT NOT NULL UNIQUE REFERENCES turno(id) ON DELETE CASCADE, -- ON DELETE CASCADE: Si se borra el turno, se borran los síntomas
    motivo_consulta TEXT NOT NULL,
    signos_vitales JSONB, -- Almacena TA, FC, Temp, etc. en formato JSON
    prioridad_calculada INT NOT NULL CHECK (prioridad_calculada >= 1 AND prioridad_calculada <= 5),
    ts_registro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Tabla BITACORA (Auditoría de Eventos)
CREATE TABLE bitacora (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuario(id), -- Nullable: Puede ser un evento automático del sistema
    turno_id INT REFERENCES turno(id),
    evento TEXT NOT NULL, -- Ej: 'NEW_PATIENT', 'TURN_ASSIGNED', 'AUTH_FAIL'
    detalle JSONB,
    ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- #############################################
-- FIN SCRIPT DE CREACIÓN DE TABLAS
-- #############################################