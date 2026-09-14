package ni.edu.uam.factura_app.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FacturacionApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(FacturacionApplication.class.getResource("/ni/edu/uam/factura_app/fxml/menu-principal.fxml"));
        stage.setTitle("Sistema de facturación");
        stage.setScene(new Scene(fxmlLoader.load(), 900, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
