package Backend.Service;

import Backend.DAO.DatabaseUtil;
import Backend.DAO.PacienteDAO;
import Backend.DAO.SintomasDAO;
import Backend.DAO.TurnoDAO;
import Backend.DTO.AsignacionTurnoDTO;
import Backend.MODEL.Paciente;
import Backend.MODEL.Sintomas;
import Backend.MODEL.Turno;

import java.sql.Connection;
import java.sql.SQLException;

public class TurnoService {

    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    // Nuevo DAO necesario
    private final SintomasDAO sintomasDAO = new SintomasDAO();

    /**
     * Ejecuta una transacción para asignar el siguiente turno disponible al médico.
     * Usa SELECT FOR UPDATE para garantizar la concurrencia.
     * @param medicoId ID del usuario con rol 'medico'.
     * @return AsignacionTurnoDTO con el turno, paciente y síntomas asignados, o null si no hay turnos.
     * @throws SQLException si ocurre un error transaccional.
     */
    public AsignacionTurnoDTO assignNextTurn(int medicoId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 1. INICIO DE LA TRANSACCIÓN

            // 2. Obtener y Bloquear el ID del turno de mayor prioridad
            int turnoId = turnoDAO.getNextTurnIdForUpdate(conn);

            if (turnoId == -1) {
                conn.rollback();
                return null; // No hay pacientes en espera
            }

            // 3. Actualizar el estado del turno
            boolean actualizado = turnoDAO.assignTurnToMedico(conn, turnoId, medicoId);

            if (actualizado) {
                conn.commit(); // 4. Confirmar transacción (libera bloqueo)

                // 5. Cargar todos los datos necesarios para el Médico fuera de la transacción
                Turno turnoAsignado = turnoDAO.findTurnById(turnoId);
                Paciente pacienteAsignado = pacienteDAO.findById(turnoAsignado.getPacienteId());
                // Cargar los síntomas asociados al turno
                Sintomas sintomasAsignados = sintomasDAO.findByTurnoId(turnoId);

                // Retornar el DTO completo
                return new AsignacionTurnoDTO(turnoAsignado, pacienteAsignado, sintomasAsignados);
            } else {
                conn.rollback();
                throw new SQLException("Fallo al actualizar el turno " + turnoId);
            }

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}