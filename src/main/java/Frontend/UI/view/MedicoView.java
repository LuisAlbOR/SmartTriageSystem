package Frontend.UI.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

public class MedicoView {

    private BorderPane mainLayout;
    private Label userInfoLabel;
    private Button logoutBtn;
    
    // --- Controles Principales ---
    private Button llamarSiguienteBtn;
    private Button finalizarConsultaBtn;
    private Label estadoColaLabel; // Ej: "Hay 5 pacientes en espera"

    // --- Panel de Paciente Actual ---
    private VBox pacientePanel;
    private Label nombrePacienteLbl;
    private Label edadPacienteLbl;
    private Label motivoLbl;
    private Label prioridadLbl;
    private Label turnoIdLbl;
    // Signos Vitales (mostrados como texto si existen)
    private TextArea signosArea;

    private Label statusLabel; // Para mensajes de error/éxito generales

    public MedicoView() {
        createUI();
    }

    private void createUI() {
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));

        // 1. HEADER
        setupHeader();

        // 2. CENTRO (Panel de Paciente)
        setupCenterPanel();

        // 3. FOOTER (Botones de Acción Principal)
        setupFooter();
        
        // Estado inicial: Sin paciente
        showNoPatientState();
    }

    private void setupHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));
        header.setStyle("-fx-border-color: transparent transparent #ccc transparent;");

        Label titleLbl = new Label("Consultorio Médico");
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 22));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        userInfoLabel = new Label("Dr. ---");
        userInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        logoutBtn = new Button("Cerrar Sesión");
        logoutBtn.setStyle("-fx-base: #d32f2f; -fx-text-fill: white;");

        header.getChildren().addAll(titleLbl, spacer, userInfoLabel, logoutBtn);
        mainLayout.setTop(header);
    }

    private void setupCenterPanel() {
        pacientePanel = new VBox(15);
        pacientePanel.setPadding(new Insets(20));
        pacientePanel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #eee; -fx-border-radius: 5; -fx-background-radius: 5;");
        pacientePanel.setAlignment(Pos.TOP_LEFT);

        Label panelTitle = new Label("Paciente Actual");
        panelTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        panelTitle.setTextFill(Color.web("#1565c0"));

        GridPane datosGrid = new GridPane();
        datosGrid.setHgap(15); datosGrid.setVgap(10);

        // Inicialización de etiquetas de datos
        nombrePacienteLbl = new Label("---"); nombrePacienteLbl.setFont(Font.font(16));
        edadPacienteLbl = new Label("---");
        turnoIdLbl = new Label("---");
        prioridadLbl = new Label("---");
        prioridadLbl.setStyle("-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 3;");

        datosGrid.addRow(0, new Label("Turno:"), turnoIdLbl, new Label("Prioridad:"), prioridadLbl);
        datosGrid.addRow(1, new Label("Nombre:"), nombrePacienteLbl, new Label("Edad:"), edadPacienteLbl);

        VBox motivoBox = new VBox(5);
        motivoBox.getChildren().add(new Label("Motivo de Consulta:"));
        motivoLbl = new Label("---");
        motivoLbl.setWrapText(true);
        motivoLbl.setStyle("-fx-font-size: 15px; -fx-font-style: italic; -fx-background-color: #fff; -fx-padding: 10; -fx-border-color: #ddd;");
        motivoBox.getChildren().add(motivoLbl);

        VBox signosBox = new VBox(5);
        signosBox.getChildren().add(new Label("Signos Vitales Registrados:"));
        signosArea = new TextArea();
        signosArea.setEditable(false);
        signosArea.setWrapText(true);
        signosArea.setPrefRowCount(4);
        signosBox.getChildren().add(signosArea);

        pacientePanel.getChildren().addAll(panelTitle, datosGrid, new Separator(), motivoBox, signosBox);
        
        // Por defecto, el panel está oculto hasta que se llama a un paciente
        pacientePanel.setVisible(false);
        mainLayout.setCenter(pacientePanel);
    }

    private void setupFooter() {
        VBox footer = new VBox(15);
        footer.setPadding(new Insets(20, 0, 0, 0));
        footer.setAlignment(Pos.CENTER);

        HBox actionBtns = new HBox(20);
        actionBtns.setAlignment(Pos.CENTER);

        llamarSiguienteBtn = new Button("🔔 Llamar Siguiente Paciente");
        llamarSiguienteBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        llamarSiguienteBtn.setStyle("-fx-base: #2e7d32; -fx-text-fill: white; -fx-padding: 12 30;");

        finalizarConsultaBtn = new Button("✅ Finalizar Consulta");
        finalizarConsultaBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        finalizarConsultaBtn.setStyle("-fx-base: #1976d2; -fx-text-fill: white; -fx-padding: 12 30;");
        finalizarConsultaBtn.setDisable(true); // Deshabilitado al inicio

        actionBtns.getChildren().addAll(llamarSiguienteBtn, finalizarConsultaBtn);

        estadoColaLabel = new Label("Esperando acción...");
        estadoColaLabel.setFont(Font.font(14));

        statusLabel = new Label("");

        footer.getChildren().addAll(estadoColaLabel, actionBtns, statusLabel);
        mainLayout.setBottom(footer);
    }

    // --- MÉTODOS DE ESTADO DE UI ---

    public void showPatientData(String turnoId, String prioridad, String nombre, int edad, String motivo, String signos) {
        turnoIdLbl.setText("#" + turnoId);
        prioridadLbl.setText(prioridad);
        updatePrioridadColor(prioridad); // Método helper para color
        nombrePacienteLbl.setText(nombre);
        edadPacienteLbl.setText(edad + " años");
        motivoLbl.setText(motivo);
        signosArea.setText(signos.isEmpty() || signos.equals("{}") ? "Sin signos registrados." : signos);

        pacientePanel.setVisible(true);
        llamarSiguienteBtn.setDisable(true);
        finalizarConsultaBtn.setDisable(false);
        estadoColaLabel.setText("Atendiendo paciente...");
    }

    public void showNoPatientState() {
        pacientePanel.setVisible(false);
        llamarSiguienteBtn.setDisable(false);
        finalizarConsultaBtn.setDisable(true);
        estadoColaLabel.setText("Listo para llamar al siguiente paciente.");
        limpiarDatosPaciente();
    }

    private void limpiarDatosPaciente() {
        nombrePacienteLbl.setText("---");
        motivoLbl.setText("---");
        signosArea.clear();
    }

    private void updatePrioridadColor(String prioridadStr) {
        String color = "#9e9e9e"; // Gris por defecto
        try {
            int p = Integer.parseInt(prioridadStr);
            switch (p) {
                case 1: color = "#d32f2f"; break; // Rojo (Emergencia)
                case 2: color = "#f57c00"; break; // Naranja
                case 3: color = "#fbc02d"; break; // Amarillo
                case 4: color = "#388e3c"; break; // Verde
                case 5: color = "#1976d2"; break; // Azul
            }
        } catch (NumberFormatException ignored) {}
        prioridadLbl.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 3;");
    }

    public void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "red;" : "green;"));
    }
    
    // Getters necesarios para el controlador
    public Parent getView() { return mainLayout; }
    public Button getLlamarSiguienteBtn() { return llamarSiguienteBtn; }
    public Button getFinalizarConsultaBtn() { return finalizarConsultaBtn; }
    public Button getLogoutBtn() { return logoutBtn; }
    public Label getUserInfoLabel() { return userInfoLabel; }
    public Label getTurnoIdLbl() { return turnoIdLbl; }
}