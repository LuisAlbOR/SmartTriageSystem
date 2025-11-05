package DAO;

import MODEL.Usuario;

import java.sql.*;

public class UsuarioDAO {

    /**
     * Busca un usuario por login para fines de autenticación.
     * NO debe retornar el hash; se usa internamente para comparación.
     * @param login El nombre de usuario.
     * @return Un objeto Usuario si se encuentra, o null.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public Usuario findByLogin(String login) throws SQLException {
        Usuario usuario = null;
        String SQL = "SELECT id, login, hash, rol, activo FROM usuario WHERE login = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setLogin(rs.getString("login"));
                    usuario.setHash(rs.getString("hash"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setActivo(rs.getBoolean("activo"));
                }
            }
        }
        return usuario;
    }
}