package ni.edu.uam.practicahilospae;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("monitor-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Monitor de Procesamiento - NicaData Solutions S.A.");
        stage.setScene(scene);
        stage.show();

        MonitorController controller = loader.getController();

        stage.setOnCloseRequest(event ->
                controller.detenerAlCerrar()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}