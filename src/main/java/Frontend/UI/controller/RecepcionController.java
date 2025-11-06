package Frontend.UI.controller;

import Backend.protocol.Response;
import Frontend.TriageApiClient;
import Frontend.UI.view.LoginView;
import Frontend.UI.view.RecepcionView;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class RecepcionController {

    private final RecepcionView view;
    private final TriageApiClient apiClient;
    private final Stage stage;
    private final String currentUsername;

    public RecepcionController(RecepcionView view, TriageApiClient apiClient, Stage stage, String username) {
        this.view = view;
        this.apiClient = apiClient;
        this.stage = stage;
        this.currentUsername = username;
        initController();
    }

    private void initController() {
        // Configuración inicial de la vista
        view.setUserInfo(currentUsername);

        // Asignación de manejadores de eventos
        view.getRegistrarBtn().setOnAction(e -> handleRegistrar());
        view.getLimpiarBtn().setOnAction(e -> view.clearForm());
        view.getLogoutBtn().setOnAction(e -> handleLogout());
    }

    private void handleRegistrar() {
        // 1. Extracción de datos
        String nombre = view.getNombreField().getText().trim();
        String edadStr = view.getEdadField().getText().trim();
        String motivo = view.getMotivoArea().getText().trim();
        String curp = view.getCurpField().getText().trim();

        // 2. Validaciones Locales
        if (nombre.isEmpty()) {
            view.showError("El nombre del paciente es obligatorio.");
            view.getNombreField().requestFocus();
            return;
        }
        if (edadStr.isEmpty()) {
            view.showError("La edad es obligatoria.");
            view.getEdadField().requestFocus();
            return;
        }
        if (motivo.isEmpty()) {
            view.showError("Debe especificar el motivo de consulta.");
            view.getMotivoArea().requestFocus();
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
            if (edad < 0 || edad > 120) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            view.showError("La edad debe ser un número válido entre 0 y 120.");
            view.getEdadField().selectAll();
            view.getEdadField().requestFocus();
            return;
        }

        // 3. Construcción del Payload JSON
        JsonObject pacienteJson = new JsonObject();
        pacienteJson.addProperty("nombre", nombre);
        pacienteJson.addProperty("edad", edad);
        pacienteJson.addProperty("motivo", motivo);
        if (!curp.isEmpty()) {
            pacienteJson.addProperty("curp", curp);
        }

        // Signos vitales (opcionales)
        JsonObject signosJson = new JsonObject();
        addIfNotEmpty(signosJson, "temperatura", view.getTempField().getText());
        addIfNotEmpty(signosJson, "presion", view.getPresionField().getText());
        addIfNotEmpty(signosJson, "fc", view.getFcField().getText());
        addIfNotEmpty(signosJson, "saturacion", view.getSatField().getText());

        pacienteJson.add("signos", signosJson); // Añadir objeto anidado

        // 4. Envío al Servidor (Segundo Plano)
        view.showProcessing(true);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() throws Exception {
                return apiClient.sendRequest("NEW_PATIENT", pacienteJson);
            }
        };

        task.setOnSucceeded(e -> {
            view.showProcessing(false);
            Response resp = task.getValue();

            if ("OK".equals(resp.getStatus())) {
                // --- ÉXITO: Mostrar Alerta de Información ---
                view.showSuccess("Paciente registrado correctamente.");

                // Alerta Modal
                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Registro Exitoso",
                        "¡Paciente registrado!",
                        resp.getMessage() + "\n\nEl paciente ha sido añadido a la cola de espera.");

                view.clearForm(); // Limpiar formulario después de que el usuario cierre la alerta

            } else {
                // --- ERROR DEL SERVIDOR: Mostrar Alerta de Error ---
                // Ej: "Error: CURP duplicada"
                view.showError(resp.getMessage());

                mostrarAlerta(Alert.AlertType.ERROR,
                        "Error de Registro",
                        "No se pudo registrar al paciente.",
                        "Detalle del error: " + resp.getMessage());
            }
        });

        task.setOnFailed(e -> {
            view.showProcessing(false);
            Throwable ex = task.getException();
            String errorMsg = "Fallo de conexión con el servidor.";

            view.showError(errorMsg);
            ex.printStackTrace(); // Para depuración en consola

            // --- ERROR DE RED: Mostrar Alerta de Error Crítico ---
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error de Conexión",
                    "No se pudo contactar al servidor.",
                    "Verifique su conexión a la red.\nDetalle técnico: " + ex.getMessage());
        });

        new Thread(task).start();
    }

    private void handleLogout() {
        try {
            apiClient.close(); // Cierra el socket actual
        } catch (Exception e) {
            // Ignorar errores al cerrar
        }
        // Regresar a LoginView
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage);
        stage.setScene(new Scene(loginView.getView(), 400, 500));
        stage.centerOnScreen();
    }

    // Helper para añadir campos al JSON solo si tienen texto
    private void addIfNotEmpty(JsonObject json, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            json.addProperty(key, value.trim());
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String encabezado, String contenido) {
        // Ejecutar en el hilo de JavaFX por seguridad, ya que este método
        // podría ser llamado desde el hilo de la Task (background).
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(encabezado);
            alert.setContentText(contenido);
            // Opcional: Hacer que la alerta sea modal respecto a la ventana principal
            alert.initOwner(stage);
            alert.showAndWait();
        });
    }
}