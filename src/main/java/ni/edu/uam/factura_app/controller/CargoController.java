package ni.edu.uam.factura_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.factura_app.model.Cargo;

public class CargoController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtSalario;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Cargo> tablaCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, Double> colSalario;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    private final ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    private int idCounter = 1;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tablaCargos.setItems(cargos);

        tablaCargos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarCargo(newSelection);
            }
        });
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank() || txtSalario.getText().isBlank()) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios (Nombre y Salario).");
            return;
        }

        try {
            double salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario < 0) {
                mensaje(Alert.AlertType.WARNING, "El salario no puede ser negativo.");
                return;
            }

            if (txtId.getText().isBlank()) {
                // Nuevo
                Cargo nuevoCargo = new Cargo(idCounter++, txtNombre.getText().trim(), txtDescripcion.getText().trim(), salario);
                cargos.add(nuevoCargo);
                mensaje(Alert.AlertType.INFORMATION, "Cargo agregado correctamente.");
            } else {
                // Actualizar
                Cargo seleccionado = tablaCargos.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    seleccionado.setNombre(txtNombre.getText().trim());
                    seleccionado.setSalarioBase(salario);
                    seleccionado.setDescripcion(txtDescripcion.getText().trim());
                    tablaCargos.refresh();
                    mensaje(Alert.AlertType.INFORMATION, "Cargo actualizado correctamente.");
                }
            }
            limpiar();
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Salario no válido.");
        }
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        txtSalario.clear();
        txtDescripcion.clear();
        tablaCargos.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminar() {
        Cargo seleccionado = tablaCargos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            cargos.remove(seleccionado);
            limpiar();
            mensaje(Alert.AlertType.INFORMATION, "Cargo eliminado correctamente.");
        } else {
            mensaje(Alert.AlertType.WARNING, "Seleccione un cargo para eliminar.");
        }
    }
    
    @FXML
    private void cerrar() {
        if (txtId.getScene() != null) {
            ((javafx.stage.Stage) txtId.getScene().getWindow()).close();
        }
    }

    private void mostrarCargo(Cargo cargo) {
        txtId.setText(String.valueOf(cargo.getId()));
        txtNombre.setText(cargo.getNombre());
        txtSalario.setText(String.valueOf(cargo.getSalarioBase()));
        txtDescripcion.setText(cargo.getDescripcion());
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
