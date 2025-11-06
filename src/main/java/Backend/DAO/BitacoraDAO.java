package Backend.DAO;

import Backend.MODEL.Bitacora;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BitacoraDAO {

    /**
     * Registra un evento en la bitácora. Los campos usuarioId y turnoId pueden ser NULL.
     * @param bitacora Objeto Bitacora con el evento, detalle y posibles IDs.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public void log(Bitacora bitacora) throws SQLException {
        String SQL = "INSERT INTO bitacora (usuario_id, turno_id, evento, detalle) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            // Manejo de campos NULL: si el valor es null, se establece como SQL NULL
            if (bitacora.getUsuarioId() == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, bitacora.getUsuarioId());
            }

            if (bitacora.getTurnoId() == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, bitacora.getTurnoId());
            }

            stmt.setString(3, bitacora.getEvento());
            stmt.setString(4, bitacora.getDetalle()); // Asumiendo detalle es un String JSON

            stmt.executeUpdate();
        }
    }
}