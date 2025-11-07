package Frontend.UI.controller;

import Backend.protocol.Response;
import Frontend.DTO.TurnoPublicoDTO;
import Frontend.TriageApiClient;
import Frontend.UI.view.PantallaView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.lang.reflect.Type;
import java.util.List;

public class PantallaController {

    private final PantallaView view;
    private final TriageApiClient apiClient;
    private final Stage stage;
    private final Gson gson = new Gson();
    private Timeline pollingTimeline;
    private Timeline clockTimeline;

    public PantallaController(PantallaView view, TriageApiClient apiClient, Stage stage) {
        this.view = view;
        this.apiClient = apiClient;
        this.stage = stage;
        initController();
    }

    private void initController() {
        // 1. Iniciar el Reloj
        setupClock();

        // 2. Iniciar el Polling (Sondeo) al servidor
        setupPolling();

        // 3. Manejar cierre de ventana
        stage.setOnCloseRequest(e -> stopServices());
    }

    private void setupClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> view.updateClock()));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void setupPolling() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> fetchQueueData()));
        pollingTimeline.setCycleCount(Animation.INDEFINITE);
        
        // Primera carga inmediata
        fetchQueueData();
        // Iniciar ciclo de polling
        pollingTimeline.play();
    }

    private void fetchQueueData() {
        view.setStatusMessage("Actualizando cola...", false);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() throws Exception {
                // El apiClient ya debe estar conectado y autenticado por el LoginController
                return apiClient.sendRequest("QUEUE_STATUS", null);
            }
        };

        task.setOnSucceeded(e -> {
            Response resp = task.getValue();
            if ("OK".equals(resp.getStatus())) {
                try {
                    // Convertir el 'data' (que es un List<JsonElement>) a List<TurnoPublicoDTO>
                    Type listType = new TypeToken<List<TurnoPublicoDTO>>() {}.getType();
                    List<TurnoPublicoDTO> turnos = gson.fromJson(gson.toJsonTree(resp.getData()), listType);
                    
                    view.setQueueData(turnos);
                    view.setStatusMessage("Cola actualizada.", false);
                } catch (Exception ex) {
                    view.setStatusMessage("Error al procesar datos de la cola.", true);
                    ex.printStackTrace();
                }
            } else {
                view.setStatusMessage("Error del servidor: " + resp.getMessage(), true);
            }
        });

        task.setOnFailed(e -> {
            view.setStatusMessage("Error de conexión. Reintentando en 10s...", true);
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    // Detener hilos al cerrar sesión o ventana
    public void stopServices() {
        if (pollingTimeline != null) pollingTimeline.stop();
        if (clockTimeline != null) clockTimeline.stop();
        try { apiClient.close(); } catch (Exception ignored) {}
    }
}