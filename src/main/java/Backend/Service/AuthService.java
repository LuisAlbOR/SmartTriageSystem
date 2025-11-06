package Backend.Service;


import Backend.DAO.UsuarioDAO;
import Backend.MODEL.Usuario;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Servicio encargado de verificar las credenciales de los usuarios
 * al momento de iniciar la conexión con el servidor.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Intenta autenticar a un usuario y retorna el objeto Usuario si es exitoso.
     * @param login Nombre de usuario (ej. 'medico1', 'recepcion_a').
     * @param password La contraseña enviada por el cliente.
     * @return Un Optional que contiene el Usuario si la autenticación es correcta.
     * @throws SQLException Si hay error al consultar la base de datos.
     */
    public Optional<Usuario> authenticate(String login, String password) throws SQLException {
        // 1. Buscar el usuario en la DB por su login
        Usuario usuario = usuarioDAO.findByLogin(login);

        // 2. Validaciones básicas: usuario debe existir y estar activo
        if (usuario == null || !usuario.isActivo()) {
            return Optional.empty();
        }

        // 3. Verificación de contraseña
        // --------------------------------------------------------------------------------
        // ⚠️ ADVERTENCIA DE SEGURIDAD: Esto es una simulación para desarrollo.
        // En producción, NUNCA almacenes ni compares contraseñas en texto plano.
        // Usa BCrypt.checkpw(password, usuario.getHash()) con librerías como jBCrypt.
        // --------------------------------------------------------------------------------
        
        // Simulación 1: Comparación directa (solo si guardaste contraseñas planas en la DB)
        // boolean match = password.equals(usuario.getHash());
        
        // Simulación 2 (Recomendada para pruebas rápidas): 
        // Acepta si la contraseña enviada es igual al hash almacenado O es una "maestra" de prueba.
        // Por ejemplo, si en tu DB el hash de 'medico1' es 'pass_medico1'.
        boolean match = password.equals(usuario.getHash());

        if (match) {
            return Optional.of(usuario);
        } else {
            return Optional.empty();
        }
    }
}