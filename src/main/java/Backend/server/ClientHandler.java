package Backend.server;

import Backend.DAO.TurnoDAO;
import Backend.DTO.AsignacionTurnoDTO;
import Backend.DTO.TurnoPublicoDTO;
import Backend.MODEL.Turno;
import Backend.MODEL.Usuario;
import Backend.Service.AuthService;
import Backend.Service.TriageService;
import Backend.Service.TurnoService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import Backend.protocol.Request;
import Backend.protocol.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Maneja la comunicación con un solo cliente en su propio hilo.
 * Lee peticiones JSON, las procesa y envía respuestas JSON.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Gson gson;
    // Servicios necesarios para procesar las peticiones
    private final AuthService authService;
    private final TriageService triageService;
    private final TurnoService turnoService;
    private final TurnoDAO turnoDAO; // Para consultas simples como QUEUE_STATUS

    // Sesión del usuario actual (null hasta que se autentique)
    private Usuario currentUser;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gson = new Gson();
        // Inicializar servicios (en una app real, usarías Inyección de Dependencias)
        this.authService = new AuthService();
        this.triageService = new TriageService();
        this.turnoService = new TurnoService();
        this.turnoDAO = new TurnoDAO();
    }

    @Override
    public void run() {
        try (
            // Streams para lectura y escritura de texto (JSON)
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            // Bucle principal: lee mensajes mientras la conexión esté activa
            while ((inputLine = in.readLine()) != null) {
                try {
                    // 1. Deserializar JSON a objeto Request
                    Request request = gson.fromJson(inputLine, Request.class);
                    System.out.println("[REQ] " + clientSocket.getInetAddress() + ": " + request.getType());

                    // 2. Procesar la petición y obtener respuesta
                    Response response = handleRequest(request);

                    // 3. Serializar objeto Response a JSON y enviarlo
                    String jsonResponse = gson.toJson(response);
                    out.println(jsonResponse);

                } catch (JsonSyntaxException e) {
                    out.println(gson.toJson(Response.error("JSON mal formado")));
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println(gson.toJson(Response.error("Error interno del servidor: " + e.getMessage())));
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado abruptamente: " + clientSocket.getInetAddress());
        } finally {
            closeConnection();
        }
    }

    /**
     * Método central que enruta la petición al servicio adecuado según su 'type'.
     */
    private Response handleRequest(Request request) {
        String type = request.getType();

        // 1. Peticiones que NO requieren autenticación previa
        if ("AUTH".equals(type)) {
            return handleAuth(request);
        }
        if ("PING".equals(type)) {
            return new Response("PONG", "Server alive");
        }

        // 2. Verificar autenticación para el resto de peticiones
        if (currentUser == null) {
            return new Response("AUTH_ERR", "Debe autenticarse primero.");
        }

        // 3. Enrutamiento de peticiones autenticadas
        try {
            switch (type) {
                case "NEW_PATIENT":
                    return handleNewPatient(request);
                case "NEXT":
                    return handleNextTurn();
                case "QUEUE_STATUS":
                    return handleQueueStatus();
                case "FINISH_TURN":
                    return handleFinishTurn(request);
                default:
                    return Response.error("Tipo de petición desconocido: " + type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error de base de datos: " + e.getMessage());
        }
    }

    // --- MÉTODOS MANEJADORES ESPECÍFICOS ---

    private Response handleAuth(Request request) {
        try {
            // Extraer login y password del payload
            JsonObject payload = gson.toJsonTree(request.getPayload()).getAsJsonObject();
            String login = payload.get("login").getAsString();
            String password = payload.get("password").getAsString();

            Optional<Usuario> userOpt = authService.authenticate(login, password);

            if (userOpt.isPresent()) {
                this.currentUser = userOpt.get();
                System.out.println("Usuario autenticado: " + currentUser.getLogin() + " (" + currentUser.getRol() + ")");
                // Retornamos datos básicos del usuario (SIN EL HASH)
                currentUser.setHash(null); 
                return Response.ok("Autenticación exitosa", currentUser);
            } else {
                return new Response("AUTH_ERR", "Credenciales inválidas");
            }
        } catch (Exception e) {
            return Response.error("Payload de autenticación inválido");
        }
    }

    private Response handleNewPatient(Request request) throws SQLException {
        if (!"recepcion".equals(currentUser.getRol())) {
            return Response.error("Rol no autorizado para esta acción.");
        }
        // Deserializar payload a una estructura conocida o extraer campos manualmente
        JsonObject p = gson.toJsonTree(request.getPayload()).getAsJsonObject();
        
        String nombre = p.get("nombre").getAsString();
        String curp = p.has("curp") ? p.get("curp").getAsString() : null;
        int edad = p.get("edad").getAsInt();
        String motivo = p.get("motivo").getAsString();
        // Signos vitales pueden venir como objeto JSON anidado, lo convertimos a String para guardarlo
        String signos = p.has("signos") ? p.get("signos").toString() : "{}";

        int turnoId = triageService.processNewPatient(nombre, curp, edad, motivo, signos, currentUser.getId());
        
        return Response.ok("Paciente registrado con éxito.", turnoId);
    }

    private Response handleNextTurn() throws SQLException {
        if (!"medico".equals(currentUser.getRol())) {
            return Response.error("Rol no autorizado para esta acción.");
        }

        AsignacionTurnoDTO asignacion = turnoService.assignNextTurn(currentUser.getId());

        if (asignacion == null) {
            return new Response("NO_QUEUE", "No hay pacientes en espera.");
        } else {
            return Response.ok("Nuevo paciente asignado.", asignacion);
        }
    }

    private Response handleQueueStatus() throws SQLException {
        // Cualquiera autenticado (incluyendo 'pantalla') puede ver la cola

        // --- CÓDIGO ACTUALIZADO ---
        // Se llama al método del DAO que ahora retorna la lista de DTOs públicos
        List<TurnoPublicoDTO> colaPublica = turnoDAO.getQueueStatus();

        // Se retorna la lista pública al cliente
        return Response.ok("Estado de la cola actualizado.", colaPublica);
    }

    private Response handleFinishTurn(Request request) throws SQLException {
        if (!"medico".equals(currentUser.getRol())) {
            return Response.error("Rol no autorizado.");
        }
        // El médico debe enviar el ID del turno que quiere finalizar
        JsonObject p = gson.toJsonTree(request.getPayload()).getAsJsonObject();
        int turnoId = p.get("turnoId").getAsInt();
        
        // Opcional: Verificar que este turno pertenezca al médico actual antes de finalizarlo.
        
        boolean exito = turnoDAO.finishTurn(turnoId);
        if (exito) {
            return Response.ok("Turno finalizado correctamente.", null);
        } else {
            return Response.error("No se pudo finalizar el turno (¿ID incorrecto?).");
        }
    }

    private void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            // Ignorar error al cerrar
            System.out.println(e.getMessage());
        }
    }
}