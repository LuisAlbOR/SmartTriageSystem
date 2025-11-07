package Backend.DAO;

import Backend.DTO.TurnoPublicoDTO;
import Backend.MODEL.Turno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    // Método auxiliar para mapear un ResultSet a un objeto Turno
    private Turno extractTurnoFromResultSet(ResultSet rs) throws SQLException {
        Turno turno = new Turno();
        turno.setId(rs.getInt("id"));
        turno.setPacienteId(rs.getInt("paciente_id"));
        turno.setRecepcionistaId(rs.getInt("recepcionista_id"));

        turno.setMedicoId(rs.getObject("medico_id") != null ? rs.getInt("medico_id") : null);
        turno.setPrioridad(rs.getInt("prioridad"));
        turno.setEstado(rs.getString("estado"));
        turno.setTsCreado(rs.getTimestamp("ts_creado"));
        turno.setTsInicio(rs.getTimestamp("ts_inicio"));
        turno.setTsFin(rs.getTimestamp("ts_fin"));
        return turno;
    }

    /**
     * Inserta un nuevo turno y retorna su ID generado (CRÍTICO para el TriageService).
     * @param turno Objeto Turno a insertar.
     * @return El ID de la clave primaria generada.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public int save(Turno turno) throws SQLException {
        int turnoId = -1;
        String SQL = "INSERT INTO turno (paciente_id, recepcionista_id, prioridad, estado) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, turno.getPacienteId());
            stmt.setInt(2, turno.getRecepcionistaId());
            stmt.setInt(3, turno.getPrioridad());
            stmt.setString(4, turno.getEstado());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        turnoId = rs.getInt(1);
                    }
                }
            } else {
                throw new SQLException("Fallo al crear el turno, no se obtuvo ID.");
            }
        }
        return turnoId;
    }

    /**
     * Lógica Crítica de Concurrencia: Selecciona, BLOQUEA y retorna el ID del 
     * turno de mayor prioridad y antigüedad que está 'EN_COLA'.
     * @param conn La conexión activa para la transacción. Debe tener autoCommit=false.
     * @return El ID del turno bloqueado, o -1 si no hay turnos en cola.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public int getNextTurnIdForUpdate(Connection conn) throws SQLException {
        int turnoId = -1;
        String SQL = "SELECT id FROM turno WHERE estado = 'EN_COLA' " +
                "ORDER BY prioridad ASC, ts_creado ASC LIMIT 1 FOR UPDATE";

        try (PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                turnoId = rs.getInt("id");
            }
        }
        return turnoId;
    }

    /**
     * Actualiza el estado del turno a 'ATENDIENDO', asigna el médico y registra ts_inicio.
     * DEBE usarse dentro de la misma transacción que getNextTurnIdForUpdate().
     * @param conn La conexión activa para la transacción.
     * @param turnoId ID del turno a actualizar.
     * @param medicoId ID del médico que toma el turno.
     * @return true si se actualizó el turno.
     */
    public boolean assignTurnToMedico(Connection conn, int turnoId, int medicoId) throws SQLException {
        String SQL = "UPDATE turno SET estado = 'ATENDIENDO', medico_id = ?, ts_inicio = CURRENT_TIMESTAMP WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, medicoId);
            stmt.setInt(2, turnoId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Busca y retorna un turno por su ID.
     */
    public Turno findTurnById(int id) throws SQLException {
        Turno turno = null;
        String SQL = "SELECT * FROM turno WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    turno = extractTurnoFromResultSet(rs);
                }
            }
        }
        return turno;
    }

    /**
     * Obtiene el estado actual de la cola (turnos 'EN_COLA' y 'ATENDIENDO').
     * Esta consulta es vital para la pantalla pública.
     * @return Lista de objetos Turno, ordenados por la prioridad de atención.
     */
    public List<TurnoPublicoDTO> getQueueStatus() throws SQLException {
        List<TurnoPublicoDTO> colaPublica = new ArrayList<>();

        // SQL actualizado con JOIN para obtener el nombre del paciente
        String SQL = "SELECT t.id, p.nombre, t.estado, t.prioridad " +
                "FROM turno t JOIN paciente p ON t.paciente_id = p.id " +
                "WHERE t.estado IN ('EN_COLA', 'ATENDIENDO') " +
                "ORDER BY t.estado DESC, t.prioridad ASC, t.ts_creado ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                // Mapear el resultado al DTO público
                TurnoPublicoDTO dto = new TurnoPublicoDTO();
                dto.setId(rs.getInt("id"));
                dto.setNombre(rs.getString("nombre"));
                dto.setEstado(rs.getString("estado"));
                dto.setPrioridad(rs.getInt("prioridad"));

                colaPublica.add(dto);
            }
        }
        return colaPublica;
    }
    
    /**
     * Actualiza el estado a 'FINALIZADO' y registra ts_fin.
     */
    public boolean finishTurn(int turnoId) throws SQLException {
        String SQL = "UPDATE turno SET estado = 'FINALIZADO', ts_fin = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, turnoId);
            return stmt.executeUpdate() > 0;
        }
    }
}