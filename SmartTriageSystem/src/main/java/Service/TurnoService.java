package Service;


import DAO.DatabaseUtil;
import DAO.PacienteDAO;
import DAO.TurnoDAO;
import DTO.AsignacionTurnoDTO;
import MODEL.Paciente;
import MODEL.Turno;

import java.sql.Connection;
import java.sql.SQLException;

public class TurnoService {

    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    // private final BitacoraDAO bitacoraDAO = new BitacoraDAO(); // Opcional para logging

    /**
     * Ejecuta una transacción para asignar el siguiente turno disponible al médico.
     * Usa SELECT FOR UPDATE para garantizar la concurrencia.
     * @param medicoId ID del usuario con rol 'medico'.
     * @return AsignacionTurnoDTO con el turno y paciente asignados, o null si no hay turnos.
     * @throws SQLException si ocurre un error transaccional.
     */
    public AsignacionTurnoDTO assignNextTurn(int medicoId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 1. INICIO DE LA TRANSACCIÓN
            
            // 2. Obtener y Bloquear el ID del turno de mayor prioridad (SELECT FOR UPDATE)
            int turnoId = turnoDAO.getNextTurnIdForUpdate(conn);

            if (turnoId == -1) {
                // No hay turnos en cola
                conn.rollback();
                return null;
            }

            // 3. Actualizar el estado del turno (dentro de la transacción)
            boolean actualizado = turnoDAO.assignTurnToMedico(conn, turnoId, medicoId);

            if (actualizado) {
                // 4. Carga de datos y Confirmación
                
                // Cargar el turno (ahora actualizado) y el paciente para el DTO
                Turno turnoAsignado = turnoDAO.findTurnById(turnoId);
                Paciente pacienteAsignado = pacienteDAO.findById(turnoAsignado.getPacienteId());
                
                conn.commit(); // ÉXITO: Confirma la asignación, libera el bloqueo.
                
                // Opcional: Log después del commit
                // bitacoraDAO.log(new Bitacora(medicoId, turnoId, "TURN_ASSIGNED", "Turno " + turnoId + " asignado a médico " + medicoId));
                
                return new AsignacionTurnoDTO(turnoAsignado, pacienteAsignado);
            } else {
                conn.rollback();
                throw new SQLException("Fallo al actualizar el turno " + turnoId + ". Rollback ejecutado.");
            }

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // 5. ERROR: Deshace cualquier cambio.
            }
            throw e; // Relanza la excepción para que el servidor la maneje.
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Restaurar a autoCommit
                conn.close(); // Devolver la conexión al pool (HikariCP)
            }
        }
    }
}