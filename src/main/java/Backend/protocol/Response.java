package Backend.protocol;

/**
 * Representa un mensaje de respuesta JSON enviado por el servidor al cliente.
 * Se usa Gson para serializar este objeto a JSON saliente.
 */
public class Response {
    // Estado de la operación: OK, ERR, AUTH_ERR, NO_QUEUE, etc.
    private String status;

    // Mensaje descriptivo para el humano o para logs del cliente.
    private String message;

    // Datos de respuesta. Puede ser cualquier objeto Java (Turno, Paciente, Lista<Turno>, etc.)
    // Gson se encargará de serializarlo correctamente a JSON.
    private Object data;

    // Constructor vacío
    public Response() {}

    // Constructor para respuestas simples (ej: OK/ERR sin datos)
    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Constructor para respuestas exitosas con datos
    public Response(String status, String message, Object data) {
        this(status, message);
        this.data = data;
    }

    // Métodos estáticos de fábrica para facilitar la creación de respuestas comunes
    public static Response ok(String message, Object data) {
        return new Response("OK", message, data);
    }

    public static Response error(String message) {
        return new Response("ERR", message);
    }

    // Getters y Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @Override
    public String toString() {
        return "Response{" + "status='" + status + '\'' + ", message='" + message + '\'' + ", data=" + data + '}';
    }
}