package Frontend.UI.controller;

import Backend.protocol.Response;
import Frontend.TriageApiClient;
import Frontend.UI.view.LoginView;
import Frontend.UI.view.MedicoView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MedicoController {

    private final MedicoView view;
    private final TriageApiClient apiClient;
    private final Stage stage;
    private final Gson gson = new Gson();
    
    // Estado local: ID del turno que se está atendiendo actualmente
    private Integer currentTurnoId = null;

    public MedicoController(MedicoView view, TriageApiClient apiClient, Stage stage, String username) {
        this.view = view;
        this.apiClient = apiClient;
        this.stage = stage;
        this.view.getUserInfoLabel().setText("Médico: " + username);
        initController();
    }

    private void initController() {
        view.getLlamarSiguienteBtn().setOnAction(e -> llamarSiguiente());
        view.getFinalizarConsultaBtn().setOnAction(e -> finalizarConsulta());
        view.getLogoutBtn().setOnAction(e -> logout());
    }

    private void llamarSiguiente() {
        view.setStatus("Buscando siguiente paciente...", false);
        view.getLlamarSiguienteBtn().setDisable(true); // Evitar doble click

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() throws Exception {
                return apiClient.sendRequest("NEXT", null);
            }
        };

        task.setOnSucceeded(e -> {
            Response resp = task.getValue();
            if ("OK".equals(resp.getStatus())) {
                procesarPacienteAsignado(resp);
            } else if ("NO_QUEUE".equals(resp.getStatus())) {
                view.setStatus("No hay pacientes en espera en este momento.", false);
                view.getLlamarSiguienteBtn().setDisable(false); // Reactivar botón
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cola Vacía", "Sin pacientes", "Por el momento no hay nadie en la sala de espera.");
            } else {
                view.setStatus("Error: " + resp.getMessage(), true);
                view.getLlamarSiguienteBtn().setDisable(false);
            }
        });

        task.setOnFailed(e -> {
            view.getLlamarSiguienteBtn().setDisable(false);
            view.setStatus("Error de conexión al llamar paciente.", true);
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void procesarPacienteAsignado(Response resp) {
        try {
            // El JSON ahora tiene esta estructura:
            // { ..., data: { turno: {...}, paciente: {...}, sintomas: {...} } }

            JsonObject data = gson.toJsonTree(resp.getData()).getAsJsonObject();
            JsonObject turno = data.getAsJsonObject("turno");
            JsonObject paciente = data.getAsJsonObject("paciente");
            // Extraemos el nuevo objeto de síntomas
            JsonObject sintomas = data.getAsJsonObject("sintomas");

            this.currentTurnoId = turno.get("id").getAsInt();

            String prioridad = turno.get("prioridad").getAsString();
            String nombre = paciente.get("nombre").getAsString();
            int edad = paciente.get("edad").getAsInt();

            // Extraemos motivo y signos vitales del objeto sintomas
            String motivo = sintomas.get("motivoConsulta").getAsString();
            String signos = sintomas.get("signosVitales").getAsString();

            // Ahora sí pasamos los datos REALES a la vista
            view.showPatientData(
                    String.valueOf(currentTurnoId),
                    prioridad,
                    nombre,
                    edad,
                    motivo,
                    signos
            );

            view.setStatus("Atendiendo a: " + nombre, false);

        } catch (Exception ex) {
            view.setStatus("Error al procesar datos del paciente.", true);
            ex.printStackTrace();
        }
    }
    private void finalizarConsulta() {
        if (currentTurnoId == null) return;

        view.setStatus("Finalizando consulta...", false);
        view.getFinalizarConsultaBtn().setDisable(true);

        JsonObject payload = new JsonObject();
        payload.addProperty("turnoId", currentTurnoId);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() throws Exception {
                return apiClient.sendRequest("FINISH_TURN", payload);
            }
        };

        task.setOnSucceeded(e -> {
            Response resp = task.getValue();
            if ("OK".equals(resp.getStatus())) {
                view.setStatus("Consulta finalizada exitosamente.", false);
                currentTurnoId = null; // Limpiar ID actual
                view.showNoPatientState(); // Regresar a estado inactivo
                mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta Terminada", "Turno Finalizado", "Ya puede llamar al siguiente paciente.");
            } else {
                view.setStatus("Error al finalizar: " + resp.getMessage(), true);
                view.getFinalizarConsultaBtn().setDisable(false); // Permitir reintentar
            }
        });

        task.setOnFailed(e -> {
            view.getFinalizarConsultaBtn().setDisable(false);
            view.setStatus("Error de red al finalizar consulta.", true);
        });

        new Thread(task).start();
    }

    private void logout() {
        try { apiClient.close(); } catch (Exception ignored) {}
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage);
        stage.setScene(new Scene(loginView.getView(), 400, 500));
        stage.centerOnScreen();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String contenido) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(header);
            alert.setContentText(contenido);
            alert.initOwner(stage);
            alert.showAndWait();
        });
    }
}