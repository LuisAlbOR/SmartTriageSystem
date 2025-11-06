package Frontend;

import Backend.protocol.Request;
import Backend.protocol.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TriageApiClient implements AutoCloseable {

    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson;

    public TriageApiClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new Gson();
    }

    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Conectado al servidor Triage en " + host + ":" + port);
    }

    public Response login(String username, String password) throws IOException {
        JsonObject loginPayload = new JsonObject();
        loginPayload.addProperty("login", username);
        loginPayload.addProperty("password", password);
        return sendRequest("AUTH", loginPayload);
    }

    public Response sendRequest(String type, Object payload) throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Cliente no conectado.");
        }

        // 1. Crear y serializar petición
        Request request = new Request();
        request.setType(type);
        if (payload != null) {
             // Convertir payload a JsonElement si no lo es ya
            request.setPayload(gson.toJsonTree(payload));
        }
        String jsonRequest = gson.toJson(request);
        out.println(jsonRequest);

        // 2. Leer y deserializar respuesta
        String jsonResponse = in.readLine();
        if (jsonResponse == null) {
            throw new IOException("Servidor cerró la conexión.");
        }
        try {
            return gson.fromJson(jsonResponse, Response.class);
        } catch (JsonSyntaxException e) {
            throw new IOException("Respuesta inválida del servidor: " + jsonResponse);
        }
    }

    @Override
    public void close() throws Exception {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}