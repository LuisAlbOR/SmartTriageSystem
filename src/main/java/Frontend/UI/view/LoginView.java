package Frontend.UI.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {

    private VBox mainLayout;
    private TextField userField;
    private PasswordField passField;
    private Button loginBtn;
    private Label statusLabel;

    public LoginView() {
        createUI();
    }

    private void createUI() {
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f0f2f5;");

        Label titleLbl = new Label("Sistema de Triage");
        titleLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label subtitleLbl = new Label("Iniciar Sesión");
        subtitleLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        userField = new TextField();
        userField.setPromptText("Usuario");
        userField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        userField.setPrefWidth(250);
        userField.setMaxWidth(300);

        passField = new PasswordField();
        passField.setPromptText("Contraseña");
        passField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        passField.setMaxWidth(300);

        loginBtn = new Button("Acceder");
        loginBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-font-weight: bold; " +
                            "-fx-padding: 10px 20px; -fx-cursor: hand;");
        loginBtn.setMaxWidth(300);

        statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(300);
        statusLabel.setStyle("-fx-font-size: 12px;");

        mainLayout.getChildren().addAll(titleLbl, subtitleLbl, userField, passField, loginBtn, statusLabel);
    }

    public Parent getView() {
        return mainLayout;
    }

    // Getters para que el controlador acceda a los componentes
    public String getUsername() { return userField.getText().trim(); }
    public String getPassword() { return passField.getText(); }
    public Button getLoginBtn() { return loginBtn; }

    // Métodos de utilidad para actualizar la UI desde el controlador
    public void showProcessing(boolean processing) {
        loginBtn.setDisable(processing);
        loginBtn.setText(processing ? "Verificando..." : "Acceder");
        if (processing) {
            statusLabel.setText("");
        }
    }

    public void showMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "#d32f2f;" : "#388e3c;") + " -fx-font-weight: bold;");
    }
}