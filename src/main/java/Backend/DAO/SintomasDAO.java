package Backend.DAO;

import Backend.MODEL.Sintomas;

import java.sql.*;

public class SintomasDAO {

    public void save(Sintomas sintomas) throws SQLException {
        String SQL = "INSERT INTO sintomas (turno_id, motivo_consulta, signos_vitales, prioridad_calculada) " +
                "VALUES (?, ?, ?::jsonb, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sintomas.getTurnoId());
            stmt.setString(2, sintomas.getMotivoConsulta());
            stmt.setString(3, sintomas.getSignosVitales());
            stmt.setInt(4, sintomas.getPrioridadCalculada());

            stmt.executeUpdate();
        }
    }

    // ----------------------------------------------------------------------
    // Nuevo Método Requerido: findByTurnoId
    // ----------------------------------------------------------------------

    /**
     * Busca los síntomas asociados a un turno específico.
     * @param turnoId El ID del turno.
     * @return Objeto Sintomas si existe, o null.
     * @throws SQLException si ocurre un error en la DB.
     */
    public Sintomas findByTurnoId(int turnoId) throws SQLException {
        Sintomas sintomas = null;
        String SQL = "SELECT * FROM sintomas WHERE turno_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, turnoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sintomas = new Sintomas();
                    sintomas.setId(rs.getInt("id"));
                    sintomas.setTurnoId(rs.getInt("turno_id"));
                    sintomas.setMotivoConsulta(rs.getString("motivo_consulta"));
                    // Convertimos el JSONB de vuelta a String para el POJO
                    sintomas.setSignosVitales(rs.getString("signos_vitales"));
                    sintomas.setPrioridadCalculada(rs.getInt("prioridad_calculada"));
                    sintomas.setTsRegistro(rs.getTimestamp("ts_registro"));
                }
            }
        }
        return sintomas;
    }
}