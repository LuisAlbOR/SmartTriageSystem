package DAO;

import MODEL.Sintomas;

import java.sql.*;

public class SintomasDAO {

    /**
     * Inserta los datos de síntomas y triage asociados a un Turno.
     * @param sintomas Objeto Sintomas a insertar.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public void save(Sintomas sintomas) throws SQLException {
        // CORRECCIÓN: Agregamos '::jsonb' al tercer parámetro (?)
        String SQL = "INSERT INTO sintomas (turno_id, motivo_consulta, signos_vitales, prioridad_calculada) " +
                "VALUES (?, ?, ?::jsonb, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sintomas.getTurnoId());
            stmt.setString(2, sintomas.getMotivoConsulta());
            // Ahora PostgreSQL convertirá este String a JSONB válido gracias al cast ::jsonb
            stmt.setString(3, sintomas.getSignosVitales());
            stmt.setInt(4, sintomas.getPrioridadCalculada());

            stmt.executeUpdate();
        }
    }
}