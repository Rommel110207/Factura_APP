package ni.edu.uam.factura_app.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ni.edu.uam.factura_app.DAO.CargoDAO;
import ni.edu.uam.factura_app.DAO.EmpleadoDAO;
import ni.edu.uam.factura_app.model.Cargo;
import ni.edu.uam.factura_app.model.Empleado;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmpleadoController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private ComboBox<Cargo> cmbCargo;
    @FXML private DatePicker dtpFechaContratacion;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Empleado> tblEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombres;
    @FXML private TableColumn<Empleado, String> colApellidos;
    @FXML private TableColumn<Empleado, String> colCargo;
    @FXML private TableColumn<Empleado, LocalDate> colFecha;
    @FXML private TableColumn<Empleado, Boolean> colActivo;

    private final ObservableList<Empleado> empleadosList = FXCollections.observableArrayList();
    private final ObservableList<Cargo> cargosList = FXCollections.observableArrayList();
    
    private Empleado empleadoSeleccionado = null;
    
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final CargoDAO cargoDAO = new CargoDAO();

    @FXML
    public void initialize() {
        cargarCargos();
        
        cmbCargo.setItems(cargosList);
        cmbCargo.setConverter(new StringConverter<Cargo>() {
            @Override
            public String toString(Cargo cargo) {
                return cargo != null ? cargo.getNombre() : "";
            }
            @Override
            public Cargo fromString(String string) {
                return null;
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCargo.setCellValueFactory(cellData -> {
            Cargo c = cellData.getValue().getCargo();
            return new SimpleStringProperty(c != null ? c.getNombre() : "");
        });
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaContratacion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        cargarEmpleados();

        dtpFechaContratacion.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
        dtpFechaContratacion.setValue(LocalDate.now());
        chkActivo.setSelected(true);

        tblEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                empleadoSeleccionado = newSelection;
                txtNombres.setText(newSelection.getNombres());
                txtApellidos.setText(newSelection.getApellidos());
                
                if (newSelection.getCargo() != null) {
                    for (Cargo c : cargosList) {
                        if (c.getId().equals(newSelection.getCargo().getId())) {
                            cmbCargo.setValue(c);
                            break;
                        }
                    }
                }
                
                dtpFechaContratacion.setValue(newSelection.getFechaContratacion());
                chkActivo.setSelected(newSelection.isActivo());
            }
        });
    }

    private void cargarCargos() {
        List<Cargo> lista = cargoDAO.obtenerTodos();
        cargosList.setAll(lista);
    }

    private void cargarEmpleados() {
        List<Empleado> lista = empleadoDAO.obtenerTodos();
        empleadosList.setAll(lista);
        tblEmpleados.setItems(empleadosList);
    }

    @FXML
    public void buscar() {
        String filtro = txtBuscar.getText() != null ? txtBuscar.getText().trim() : "";
        List<Empleado> lista = empleadoDAO.buscar(filtro);
        empleadosList.setAll(lista);
    }

    @FXML
    public void guardar() {
        if (txtNombres.getText() == null || txtNombres.getText().trim().isEmpty() ||
            txtApellidos.getText() == null || txtApellidos.getText().trim().isEmpty() ||
            cmbCargo.getValue() == null ||
            dtpFechaContratacion.getValue() == null) {
            
            new Alert(Alert.AlertType.WARNING, "Todos los campos son obligatorios.", ButtonType.OK).showAndWait();
            return;
        }

        if (dtpFechaContratacion.getValue().isAfter(LocalDate.now())) {
            new Alert(Alert.AlertType.WARNING, "La fecha no puede ser futura.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            if (empleadoSeleccionado != null) {
                empleadoSeleccionado.setNombres(txtNombres.getText().trim());
                empleadoSeleccionado.setApellidos(txtApellidos.getText().trim());
                empleadoSeleccionado.setCargo(cmbCargo.getValue());
                empleadoSeleccionado.setFechaContratacion(dtpFechaContratacion.getValue());
                empleadoSeleccionado.setActivo(chkActivo.isSelected());
                empleadoDAO.actualizar(empleadoSeleccionado);
                new Alert(Alert.AlertType.INFORMATION, "Empleado actualizado.", ButtonType.OK).showAndWait();
            } else {
                Empleado nuevo = new Empleado(
                    null, txtNombres.getText().trim(), txtApellidos.getText().trim(),
                    cmbCargo.getValue(), dtpFechaContratacion.getValue(), chkActivo.isSelected()
                );
                empleadoDAO.insertar(nuevo);
                new Alert(Alert.AlertType.INFORMATION, "Empleado agregado.", ButtonType.OK).showAndWait();
            }
            cargarEmpleados();
            limpiar();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar empleado.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    public void eliminar() {
        if (empleadoSeleccionado != null) {
            try {
                empleadoDAO.eliminar(empleadoSeleccionado.getId());
                cargarEmpleados();
                limpiar();
                new Alert(Alert.AlertType.INFORMATION, "Empleado eliminado correctamente.", ButtonType.OK).showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al eliminar (Puede que este en uso).", ButtonType.OK).showAndWait();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Seleccione un empleado para eliminar.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    public void limpiar() {
        empleadoSeleccionado = null;
        txtNombres.clear();
        txtApellidos.clear();
        cmbCargo.getSelectionModel().clearSelection();
        dtpFechaContratacion.setValue(LocalDate.now());
        chkActivo.setSelected(true);
        if(txtBuscar != null) txtBuscar.clear();
        cargarEmpleados();
        tblEmpleados.getSelectionModel().clearSelection();
    }

    @FXML
    public void cerrar() {
        if (txtNombres.getScene() != null) {
            Stage stage = (Stage) txtNombres.getScene().getWindow();
            stage.close();
        }
    }
}
