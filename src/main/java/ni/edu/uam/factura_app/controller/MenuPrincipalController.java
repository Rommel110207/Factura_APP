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
            SceneManager.abrirVentana("/ni/edu/uam/factura_app/fxml/producto-view.fxml", "Gestión de Productos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Productos: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirCategorias() {
        try {
            SceneManager.abrirVentana("/ni/edu/uam/factura_app/fxml/categoria-view.fxml", "Gestión de Categorías");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Categorías: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirCargos() {
        try {
            SceneManager.abrirVentana("/ni/edu/uam/factura_app/fxml/cargo-view.fxml", "Gestión de Cargos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Cargos: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirEmpleados() {
        try {
            SceneManager.abrirVentana("/ni/edu/uam/factura_app/fxml/empleado-view.fxml", "Gestión de Empleados");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Empleados: " + e.getMessage()).showAndWait();
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
