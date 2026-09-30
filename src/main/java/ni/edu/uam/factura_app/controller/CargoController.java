package ni.edu.uam.factura_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.factura_app.DAO.CargoDAO;
import ni.edu.uam.factura_app.model.Cargo;
import java.sql.SQLException;
import java.util.List;

public class CargoController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtSalario;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cargo> tablaCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, Double> colSalario;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    private final ObservableList<Cargo> cargosList = FXCollections.observableArrayList();
    private final CargoDAO cargoDAO = new CargoDAO();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        
        cargarDesdeBD();

        tablaCargos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarCargo(newSelection);
            }
        });
    }

    private void cargarDesdeBD() {
        try {
            List<Cargo> lista = cargoDAO.obtenerTodos();
            cargosList.setAll(lista);
            tablaCargos.setItems(cargosList);
        } catch (Exception e) {
            mensaje(Alert.AlertType.ERROR, "Error conectando a la base de datos. Verifique la conexión.");
            e.printStackTrace();
        }
    }

    @FXML
    private void buscar() {
        String filtro = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().trim() : "";
        List<Cargo> lista = cargoDAO.buscar(filtro);
        cargosList.setAll(lista);
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

            try {
                if (txtId.getText().isBlank()) {
                    Cargo nuevo = new Cargo(null, txtNombre.getText().trim(), txtDescripcion.getText().trim(), salario);
                    cargoDAO.insertar(nuevo);
                    mensaje(Alert.AlertType.INFORMATION, "Cargo agregado correctamente.");
                } else {
                    Cargo actualizar = new Cargo(Integer.parseInt(txtId.getText().trim()), txtNombre.getText().trim(), txtDescripcion.getText().trim(), salario);
                    cargoDAO.actualizar(actualizar);
                    mensaje(Alert.AlertType.INFORMATION, "Cargo actualizado correctamente.");
                }
                cargarDesdeBD();
                limpiar();
            } catch (SQLException ex) {
                if ("23505".equals(ex.getSQLState())) {
                    mensaje(Alert.AlertType.ERROR, "Ya existe un cargo con ese nombre. No se pueden guardar duplicados.");
                } else {
                    ex.printStackTrace();
                    mensaje(Alert.AlertType.ERROR, "Error al guardar en la base de datos.");
                }
            }

        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Salario no valido.");
        }
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        txtSalario.clear();
        txtDescripcion.clear();
        if(txtBuscar != null) txtBuscar.clear();
        cargarDesdeBD();
        tablaCargos.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminar() {
        Cargo seleccionado = tablaCargos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                cargoDAO.eliminar(seleccionado.getId());
                cargarDesdeBD();
                limpiar();
                mensaje(Alert.AlertType.INFORMATION, "Cargo eliminado correctamente.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                mensaje(Alert.AlertType.ERROR, "Error al eliminar (Puede que este en uso).");
            }
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
