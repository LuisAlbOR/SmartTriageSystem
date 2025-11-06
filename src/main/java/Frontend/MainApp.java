package Frontend;


import Frontend.UI.controller.LoginController;
import Frontend.UI.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Crear la Vista (UI pasiva)
        LoginView loginView = new LoginView();

        // 2. Crear el Controlador (Lógica activa) y enlazarlo con la vista y el Stage
        new LoginController(loginView, primaryStage);

        // 3. Mostrar la escena inicial
        Scene scene = new Scene(loginView.getView(), 400, 500);
        primaryStage.setTitle("Triage Comunitario - Acceso");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}