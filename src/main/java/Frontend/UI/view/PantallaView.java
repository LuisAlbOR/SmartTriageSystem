package Frontend.UI.view;

import Frontend.DTO.TurnoPublicoDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PantallaView {

    private BorderPane mainLayout;
    private TableView<TurnoPublicoDTO> tablaTurnos;
    private Label statusLabel;
    private Label clockLabel;
    private Label headerLabel;
    private ObservableList<TurnoPublicoDTO> turnosData = FXCollections.observableArrayList();

    public PantallaView() {
        createUI();
    }

    private void createUI() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #2c3e50;"); // Fondo oscuro

        // 1. Header (Título)
        headerLabel = new Label("Sala de Espera - Triage Comunitario");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        headerLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        headerLabel.setPadding(new Insets(20));
        headerLabel.setAlignment(Pos.CENTER);
        mainLayout.setTop(headerLabel);

        // 2. Centro (Tabla de Turnos)
        tablaTurnos = new TableView<>(turnosData);
        tablaTurnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaTurnos.setStyle("-fx-font-size: 24px;");

        // --- Definición de Columnas ---
        TableColumn<TurnoPublicoDTO, Integer> colTurno = new TableColumn<>("TURNO");
        colTurno.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTurno.setMinWidth(120); colTurno.setMaxWidth(120);

        TableColumn<TurnoPublicoDTO, String> colNombre = new TableColumn<>("PACIENTE");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<TurnoPublicoDTO, String> colEstado = new TableColumn<>("ESTADO");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setMinWidth(250); colEstado.setMaxWidth(250);
        
        tablaTurnos.getColumns().addAll(colTurno, colNombre, colEstado);
        
        // --- Estilo de Fila (Colorear "ATENDIENDO") ---
        tablaTurnos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(TurnoPublicoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if ("ATENDIENDO".equals(item.getEstado())) {
                    // Fila verde y en negritas para "ATENDIENDO"
                    setStyle("-fx-background-color: #c8e6c9; -fx-font-weight: bold;");
                } else {
                    // Fila normal para "EN_COLA"
                    setStyle("-fx-background-color: white;");
                }
            }
        });

        mainLayout.setCenter(tablaTurnos);

        // 3. Footer (Reloj y Estado de Conexión)
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Conectando...");
        statusLabel.setFont(Font.font(16));
        statusLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        clockLabel = new Label();
        clockLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        clockLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        
        footer.getChildren().addAll(statusLabel, spacer, clockLabel);
        mainLayout.setBottom(footer);
    }

    public Parent getView() {
        return mainLayout;
    }

    // --- Métodos de UI para el Controlador ---

    public void setQueueData(java.util.List<TurnoPublicoDTO> turnos) {
        turnosData.setAll(turnos);
        // Opcional: Ocultar prioridad para el público, pero usarla para ordenar
        // turnosData.sort(Comparator.comparing(TurnoPublicoDTO::getPrioridad));
    }
    
    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? javafx.scene.paint.Color.INDIANRED : javafx.scene.paint.Color.LIGHTGRAY);
    }

    public void updateClock() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        clockLabel.setText(LocalDateTime.now().format(dtf));
    }
}