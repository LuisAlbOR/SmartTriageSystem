import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5050;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            // --- PRUEBA 1: RECEPCIONISTA REGISTRA PACIENTE ---
            System.out.println("\n--- INICIANDO PRUEBA DE RECEPCIÓN ---");
            testRecepcionFlow();

            // Espera un momento para asegurar que el servidor procesó
            Thread.sleep(1000);

            // --- PRUEBA 2: MÉDICO ATIENDE PACIENTE ---
            System.out.println("\n--- INICIANDO PRUEBA DE MÉDICO ---");
            testMedicoFlow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testRecepcionFlow() throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Autenticación
            JsonObject authPayload = new JsonObject();
            authPayload.addProperty("login", "recepcion1");
            authPayload.addProperty("password", "pass_recepcion");
            sendRequest(out, "AUTH", authPayload);
            readResponse(in); // Espera AUTH_OK

            // 2. Crear Paciente (Prioridad Alta: Dolor torácico)
            JsonObject patientPayload = new JsonObject();
            patientPayload.addProperty("nombre", "Juan Perez Test");
            patientPayload.addProperty("edad", 45);
            patientPayload.addProperty("motivo", "Dolor torácico intenso");
            sendRequest(out, "NEW_PATIENT", patientPayload);
            readResponse(in); // Espera OK con ID de turno
        }
    }

    private static void testMedicoFlow() throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Autenticación Médico
            JsonObject authPayload = new JsonObject();
            authPayload.addProperty("login", "medico1");
            authPayload.addProperty("password", "pass_medico");
            sendRequest(out, "AUTH", authPayload);
            readResponse(in);

            // 2. Solicitar Siguiente Turno
            sendRequest(out, "NEXT", null);
            String nextTurnResponse = readResponse(in);

            // Extraer ID del turno para finalizarlo (parseo simple para la prueba)
            // En una app real, deserializarías a un objeto Response completo.
            if (nextTurnResponse.contains("OK")) {
                // Simulación de atención...
                System.out.println("Médico atendiendo...");
                try { Thread.sleep(2000); } catch (InterruptedException e) {}

                // 3. Finalizar Turno (asumimos que extrajimos el ID, aquí hardcodeamos para prueba rápida
                // o necesitaríamos parsear el JSON de respuesta anterior.
                // Para esta prueba simple, solo verificamos que NEXT funcione).
                 System.out.println("(Prueba: Turno asignado correctamente. Implementar parseo para FINISH_TURN)");
            }
        }
    }

    private static void sendRequest(PrintWriter out, String type, Object payload) {
        JsonObject request = new JsonObject();
        request.addProperty("type", type);
        if (payload != null) {
            if (payload instanceof JsonObject) {
                request.add("payload", (JsonObject) payload);
            } else {
                 // Si fuera otro objeto, usar gson.toJsonTree(payload)
            }
        }
        String json = gson.toJson(request);
        System.out.println("[CLIENTE] Enviando: " + json);
        out.println(json);
    }

    private static String readResponse(BufferedReader in) throws IOException {
        String response = in.readLine();
        System.out.println("[CLIENTE] Recibido: " + response);
        return response;
    }
}