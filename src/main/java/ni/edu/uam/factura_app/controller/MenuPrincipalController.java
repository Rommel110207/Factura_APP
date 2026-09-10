package ni.edu.uam.factura_app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.factura_app.util.SceneManager;

import java.io.IOException;

public class MenuPrincipalController {
    @FXML
    private void abrirProductos() {
        try {
            SceneManager.abrirVentana("/ni/edu/uam/fxml/producto-view.fxml", "Gestión de productos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Productos: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}
