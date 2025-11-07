-- #############################################
-- SCRIPT DE TRIGGERS Y FUNCIONES (PL/pgSQL)
-- #############################################

-- Función: Registrar la creación de un nuevo turno en la bitacora
CREATE OR REPLACE FUNCTION log_new_turno()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO bitacora (usuario_id, turno_id, evento, detalle)
    VALUES (
        NEW.recepcionista_id,
        NEW.id,
        'NEW_PATIENT',
        jsonb_build_object(
            'prioridad', NEW.prioridad,
            'estado_inicial', NEW.estado
        )
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger: Ejecutar después de INSERT en la tabla turno
CREATE TRIGGER trg_log_new_turno
AFTER INSERT ON turno
FOR EACH ROW
EXECUTE FUNCTION log_new_turno();


-- #############################################
-- FIN TRIGGERS Y FUNCIONES
-- #############################################