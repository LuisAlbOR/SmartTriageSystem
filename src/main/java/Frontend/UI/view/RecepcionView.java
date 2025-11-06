package Frontend.UI.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RecepcionView {

    private BorderPane mainLayout;
    private TextField nombreField;
    private TextField edadField;
    private TextField curpField;
    private TextArea motivoArea;
    // Signos Vitales (Opcionales)
    private TextField tempField;
    private TextField presionField;
    private TextField fcField; // Frecuencia Cardiaca
    private TextField satField; // Saturación O2

    private Button registrarBtn;
    private Button limpiarBtn;
    private Button logoutBtn;
    private Label statusLabel;
    private Label userInfoLabel; // Para mostrar "Logueado como: recepcion1"

    public RecepcionView() {
        createUI();
    }

    private void createUI() {
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));

        // --- HEADER ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPadding(new Insets(0, 0, 20, 0));
        header.setStyle("-fx-border-color: transparent transparent #e0e0e0 transparent;");

        Label titleLbl = new Label("Módulo de Recepción");
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 22));

        userInfoLabel = new Label("Usuario: ---");
        userInfoLabel.setStyle("-fx-text-fill: #666;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        logoutBtn = new Button("Cerrar Sesión");
        logoutBtn.setStyle("-fx-base: #f44336; -fx-text-fill: white;");

        header.getChildren().addAll(titleLbl, spacer, userInfoLabel, logoutBtn);
        mainLayout.setTop(header);

        // --- CENTRO (Formulario) ---
        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(20, 0, 0, 0));

        // Sección 1: Datos del Paciente
        TitledPane datosPane = new TitledPane("Datos del Paciente", createDatosGrid());
        datosPane.setCollapsible(false);
        datosPane.setExpanded(true);

        // Sección 2: Signos Vitales
        TitledPane signosPane = new TitledPane("Signos Vitales (Iniciales)", createSignosGrid());
        signosPane.setCollapsible(true);
        signosPane.setExpanded(true); // Expandido por defecto para facilitar ingreso rápido

        centerBox.getChildren().addAll(datosPane, signosPane);

        // Envolver en ScrollPane por si la pantalla es pequeña
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        mainLayout.setCenter(scrollPane);

        // --- FOOTER (Botones de Acción) ---
        VBox footerBox = new VBox(10);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(20, 0, 0, 0));

        HBox actionBtns = new HBox(15);
        actionBtns.setAlignment(Pos.CENTER);

        registrarBtn = new Button("Registrar Paciente");
        registrarBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        registrarBtn.setStyle("-fx-base: #4CAF50; -fx-text-fill: white; -fx-padding: 10 30;");
        registrarBtn.setPrefWidth(200);

        limpiarBtn = new Button("Limpiar Campos");
        limpiarBtn.setStyle("-fx-base: #ff9800; -fx-text-fill: white;");

        actionBtns.getChildren().addAll(limpiarBtn, registrarBtn);

        statusLabel = new Label("Listo para registrar.");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        footerBox.getChildren().addAll(actionBtns, statusLabel);
        mainLayout.setBottom(footerBox);
    }

    private GridPane createDatosGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setPadding(new Insets(15));

        nombreField = new TextField();
        nombreField.setPromptText("Apellido Paterno, Materno, Nombres");
        edadField = new TextField();
        edadField.setPrefWidth(80);
        curpField = new TextField();
        motivoArea = new TextArea();
        motivoArea.setPrefRowCount(3);
        motivoArea.setWrapText(true);
        motivoArea.setPromptText("Describa brevemente el motivo principal de la consulta...");

        grid.add(new Label("Nombre Completo: *"), 0, 0);
        grid.add(nombreField, 1, 0, 3, 1); // Ocupa 3 columnas

        grid.add(new Label("Edad: *"), 0, 1);
        grid.add(edadField, 1, 1);

        grid.add(new Label("CURP:"), 2, 1);
        grid.add(curpField, 3, 1);

        grid.add(new Label("Motivo Consulta: *"), 0, 2);
        grid.add(motivoArea, 1, 2, 3, 1);

        return grid;
    }

    private GridPane createSignosGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setPadding(new Insets(15));

        tempField = new TextField(); tempField.setPromptText("°C");
        presionField = new TextField(); presionField.setPromptText("ej. 120/80");
        fcField = new TextField(); fcField.setPromptText("lpm");
        satField = new TextField(); satField.setPromptText("%");

        grid.add(new Label("Temperatura:"), 0, 0); grid.add(tempField, 1, 0);
        grid.add(new Label("Presión Arterial:"), 2, 0); grid.add(presionField, 3, 0);
        grid.add(new Label("Frec. Cardiaca:"), 0, 1); grid.add(fcField, 1, 1);
        grid.add(new Label("Saturación O2:"), 2, 1); grid.add(satField, 3, 1);

        return grid;
    }

    public Parent getView() { return mainLayout; }

    // --- Getters de Componentes ---
    public TextField getNombreField() { return nombreField; }
    public TextField getEdadField() { return edadField; }
    public TextField getCurpField() { return curpField; }
    public TextArea getMotivoArea() { return motivoArea; }
    public TextField getTempField() { return tempField; }
    public TextField getPresionField() { return presionField; }
    public TextField getFcField() { return fcField; }
    public TextField getSatField() { return satField; }
    public Button getRegistrarBtn() { return registrarBtn; }
    public Button getLimpiarBtn() { return limpiarBtn; }
    public Button getLogoutBtn() { return logoutBtn; }

    // --- Métodos de Utilidad para la UI ---
    public void setUserInfo(String username) {
        userInfoLabel.setText("Usuario: " + username);
    }

    public void showProcessing(boolean processing) {
        registrarBtn.setDisable(processing);
        limpiarBtn.setDisable(processing);
        logoutBtn.setDisable(processing);
        registrarBtn.setText(processing ? "Enviando..." : "Registrar Paciente");
        if (processing) {
            statusLabel.setText("Procesando solicitud...");
            statusLabel.setStyle("-fx-text-fill: black;");
        }
    }

    public void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
    }

    public void showError(String message) {
        statusLabel.setText("❌ Error: " + message);
        statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
    }

    public void clearForm() {
        nombreField.clear();
        edadField.clear();
        curpField.clear();
        motivoArea.clear();
        tempField.clear();
        presionField.clear();
        fcField.clear();
        satField.clear();
        nombreField.requestFocus();
        statusLabel.setText("Formulario limpiado. Listo para nuevo registro.");
        statusLabel.setStyle("-fx-text-fill: black;");
    }
}