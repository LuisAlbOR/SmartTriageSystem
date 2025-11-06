package Backend.DAO;

import Backend.MODEL.Paciente;
import java.sql.*;

public class PacienteDAO {

    /**
     * Inserta un nuevo paciente y retorna su ID generado.
     * @param paciente Objeto Paciente a insertar.
     * @return El ID de la clave primaria generada.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public int save(Paciente paciente) throws SQLException {
        int pacienteId = -1;
        // La consulta usa RETURNING id para obtener la clave primaria generada.
        String SQL = "INSERT INTO paciente (nombre, curp, edad) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getCurp());
            stmt.setInt(3, paciente.getEdad());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pacienteId = rs.getInt(1);
                    }
                }
            } else {
                throw new SQLException("Fallo al crear el paciente, no se obtuvo ID.");
            }
        }
        return pacienteId;
    }

    // ----------------------------------------------------------------------
    // Nuevo Método Requerido: findById
    // ----------------------------------------------------------------------

    /**
     * Busca un paciente por su ID.
     * @param id ID del paciente.
     * @return Objeto Paciente si se encuentra, o null.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public Paciente findById(int id) throws SQLException {
        Paciente paciente = null;
        String SQL = "SELECT id, nombre, curp, edad, ts_creado FROM paciente WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente();
                    // Mapeo de ResultSet a POJO (Objeto Paciente)
                    paciente.setId(rs.getInt("id"));
                    paciente.setNombre(rs.getString("nombre"));
                    paciente.setCurp(rs.getString("curp"));
                    paciente.setEdad(rs.getInt("edad"));
                    paciente.setTsCreado(rs.getTimestamp("ts_creado"));
                }
            }
        }
        return paciente;
    }
}