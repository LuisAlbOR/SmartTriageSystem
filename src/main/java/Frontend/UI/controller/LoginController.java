package Frontend.UI.controller;

import Backend.protocol.Response;
import Frontend.TriageApiClient;
import Frontend.UI.view.LoginView;
import Frontend.UI.view.MedicoView;
import Frontend.UI.view.RecepcionView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
// Importar aquí las vistas/controladores a los que se redirigirá después del login
// import com.triage.cliente.ui.view.RecepcionView;
// import com.triage.cliente.ui.controller.RecepcionController;

public class LoginController {

    private final LoginView view;
    private final TriageApiClient apiClient;
    private final Stage primaryStage;
    private final Gson gson = new Gson();

    public LoginController(LoginView view, Stage primaryStage) {
        this.view = view;
        this.primaryStage = primaryStage;
        // Inicializamos el cliente API apuntando al servidor local
        this.apiClient = new TriageApiClient("localhost", 5050);
        initController();
    }



    private void initController() {
        // Asignar la acción al botón de la vista
        view.getLoginBtn().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String user = view.getUsername();
        String pass = view.getPassword();

        if (user.isEmpty() || pass.isEmpty()) {
            view.showMessage("Por favor ingrese usuario y contraseña.", true);
            return;
        }

        view.showProcessing(true);

        // Tarea en segundo plano para no congelar la UI
        Task<Response> loginTask = new Task<>() {
            @Override
            protected Response call() throws Exception {
                apiClient.connect(); // Conecta socket
                return apiClient.login(user, pass); // Envía AUTH
            }
        };

        loginTask.setOnSucceeded(e -> {
            view.showProcessing(false);
            Response resp = loginTask.getValue();

            if ("OK".equals(resp.getStatus())) {
                view.showMessage("¡Bienvenido! Redirigiendo...", false);
                // Procesar el rol del usuario para saber a dónde redirigir
                processSuccessfulLogin(resp);
            } else {
                view.showMessage("Error: " + resp.getMessage(), true);
                closeClientQuietly(); // Cierra socket para reintentar limpiamente
            }
        });

        loginTask.setOnFailed(e -> {
            view.showProcessing(false);
            view.showMessage("Fallo de conexión: " + loginTask.getException().getMessage(), true);
            loginTask.getException().printStackTrace();
            closeClientQuietly();
        });

        new Thread(loginTask).start();
    }

    private void processSuccessfulLogin(Response resp) {
        try {
            // Extraer el rol del objeto 'data' en la respuesta JSON
            JsonObject userData = gson.toJsonTree(resp.getData()).getAsJsonObject();
            String rol = userData.get("rol").getAsString();

            System.out.println("Usuario logueado con rol: " + rol);

            // Redirección basada en roles (Implementar las vistas correspondientes)
            Platform.runLater(() -> {
                switch (rol) {
                    // En el switch(rol) de LoginController:
                    case "recepcion":
                        String username = userData.get("login").getAsString(); // Obtener nombre de usuario
                        RecepcionView rv = new RecepcionView();
                        // Pasar el apiClient YA CONECTADO y autenticado
                        new RecepcionController(rv, apiClient, primaryStage, username);

                        primaryStage.setScene(new Scene(rv.getView(), 900, 650)); // Pantalla más grande para recepción
                        primaryStage.centerOnScreen();
                        primaryStage.setTitle("Sistema de Triage - Recepción: " + username);
                        break;
                    case "medico":
                         // Redirigir a MedicoView...
                        String medicoUsername = userData.get("login").getAsString();
                        MedicoView medicoView = new MedicoView();
                        // Pasar el cliente YA CONECTADO y autenticado al controlador del médico
                        new MedicoController(medicoView, apiClient, primaryStage, medicoUsername);

                        // Configurar la escena para la vista del médico
                        primaryStage.setScene(new Scene(medicoView.getView(), 800, 600));
                        primaryStage.centerOnScreen();
                        primaryStage.setTitle("Sistema de Triage - Consultorio Médico: " + medicoUsername);
                        break;
                    case "pantalla":
                         // Redirigir a PantallaView...
                         break;
                    default:
                        view.showMessage("Rol desconocido: " + rol, true);
                        closeClientQuietly();
                }
            });

        } catch (Exception ex) {
            view.showMessage("Error al procesar datos de usuario.", true);
            ex.printStackTrace();
            closeClientQuietly();
        }
    }

    private void closeClientQuietly() {
        try { apiClient.close(); } catch (Exception ignored) {}
    }
}