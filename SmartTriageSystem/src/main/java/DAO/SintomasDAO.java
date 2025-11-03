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
        // La prioridad_calculada se guarda para tener una traza del resultado del motor.
        String SQL = "INSERT INTO sintomas (turno_id, motivo_consulta, signos_vitales, prioridad_calculada) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sintomas.getTurnoId());
            stmt.setString(2, sintomas.getMotivoConsulta());
            // Se usa setString, asumiendo que signosVitales es un String JSON válido.
            stmt.setString(3, sintomas.getSignosVitales()); 
            stmt.setInt(4, sintomas.getPrioridadCalculada());

            stmt.executeUpdate();
        }
    }
}