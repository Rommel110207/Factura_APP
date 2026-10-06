package ni.edu.uam.factura_app.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {


    public static void abrirVentana(String rutaFxml, String titulo) throws IOException {

        // Busca el archivo dentro de los recursos del proyecto compilado
        URL fxmlLocation = SceneManager.class.getResource(rutaFxml);

        if (fxmlLocation == null) {
            throw new IOException("No se encontró el archivo en resources: " + rutaFxml);
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.show();
    }
}