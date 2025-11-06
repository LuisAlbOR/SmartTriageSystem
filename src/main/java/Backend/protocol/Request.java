package Backend.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Representa un mensaje de petición JSON enviado por el cliente al servidor.
 * Se usa Gson para deserializar el JSON entrante a este objeto.
 */
public class Request {
    // Tipo de acción solicitada: AUTH, NEW_PATIENT, NEXT, QUEUE_STATUS, FINISH_TURN, PING
    private String type;

    // Contenido de la petición. Usamos JsonElement de Gson para manejar cualquier estructura JSON
    // de forma flexible hasta que sepamos qué 'type' es y podamos deserializarlo al objeto correcto.
    private JsonElement payload;

    // Constructor vacío (necesario para Gson)
    public Request() {}

    // Constructor útil para pruebas
    public Request(String type, JsonElement payload) {
        this.type = type;
        this.payload = payload;
    }

    // Getters
    public String getType() {
        return type;
    }

    public JsonElement getPayload() {
        return payload;
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(JsonElement payload) {
        this.payload = payload;
    }

    // Método utilitario para obtener el payload como un objeto específico
    public <T> T getPayloadAs(Class<T> classOfT) {
        return new Gson().fromJson(payload, classOfT);
    }

    @Override
    public String toString() {
        return "Request{" + "type='" + type + '\'' + ", payload=" + payload + '}';
    }
}