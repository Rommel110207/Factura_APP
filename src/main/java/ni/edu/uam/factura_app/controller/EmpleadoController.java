package ni.edu.uam.factura_app.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ni.edu.uam.factura_app.model.Cargo;
import ni.edu.uam.factura_app.model.Empleado;

import java.time.LocalDate;

public class EmpleadoController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private ComboBox<Cargo> cmbCargo;
    @FXML private DatePicker dpFechaContratacion;
    @FXML private CheckBox chkActivo;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Empleado> tablaEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombres;
    @FXML private TableColumn<Empleado, String> colApellidos;
    @FXML private TableColumn<Empleado, String> colCargo;
    @FXML private TableColumn<Empleado, LocalDate> colFecha;
    @FXML private TableColumn<Empleado, Boolean> colActivo;

    private final ObservableList<Empleado> empleados = FXCollections.observableArrayList();
    private final ObservableList<Cargo> cargosList = FXCollections.observableArrayList();
    private int idCounter = 1;

    @FXML
    private void initialize() {
        // Inicializar datos quemados para cargos
        cargosList.addAll(
                new Cargo(1, "Gerente General", "Administración", 30000.0),
                new Cargo(2, "Vendedor", "Ventas", 10000.0),
                new Cargo(3, "Contador", "Finanzas", 15000.0)
        );
        cmbCargo.setItems(cargosList);
        cmbCargo.setConverter(new StringConverter<Cargo>() {
            @Override
            public String toString(Cargo cargo) {
                return cargo != null ? cargo.getNombre() : "";
            }
            @Override
            public Cargo fromString(String s) {
                return null;
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        // Para mostrar el nombre del cargo
        colCargo.setCellValueFactory(cellData -> {
            Cargo c = cellData.getValue().getCargo();
            return new SimpleStringProperty(c != null ? c.getNombre() : "");
        });
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaContratacion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        tablaEmpleados.setItems(empleados);

        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarEmpleado(newSelection);
            }
        });
        
        // Configurar DatePicker para que no permita fechas futuras en el calendario
        dpFechaContratacion.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate hoy = LocalDate.now();
                setDisable(empty || date.compareTo(hoy) > 0);
            }
        });

        dpFechaContratacion.setValue(LocalDate.now());
        chkActivo.setSelected(true);
    }

    @FXML
    private void guardar() {
        if (txtNombres.getText().isBlank() || txtApellidos.getText().isBlank() || cmbCargo.getValue() == null || dpFechaContratacion.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }

        // Validación de fecha no futura
        if (dpFechaContratacion.getValue().isAfter(LocalDate.now())) {
            mensaje(Alert.AlertType.WARNING, "La fecha de contratación no puede ser mayor al día de hoy.");
            return;
        }

        if (txtId.getText().isBlank()) {
            // Nuevo
            Empleado nuevoEmpleado = new Empleado(
                    idCounter++, 
                    txtNombres.getText().trim(), 
                    txtApellidos.getText().trim(), 
                    cmbCargo.getValue(), 
                    dpFechaContratacion.getValue(), 
                    chkActivo.isSelected()
            );
            empleados.add(nuevoEmpleado);
            mensaje(Alert.AlertType.INFORMATION, "Empleado agregado correctamente.");
        } else {
            // Actualizar
            Empleado seleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                seleccionado.setNombres(txtNombres.getText().trim());
                seleccionado.setApellidos(txtApellidos.getText().trim());
                seleccionado.setCargo(cmbCargo.getValue());
                seleccionado.setFechaContratacion(dpFechaContratacion.getValue());
                seleccionado.setActivo(chkActivo.isSelected());
                tablaEmpleados.refresh();
                mensaje(Alert.AlertType.INFORMATION, "Empleado actualizado correctamente.");
            }
        }
        limpiar();
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombres.clear();
        txtApellidos.clear();
        cmbCargo.getSelectionModel().clearSelection();
        dpFechaContratacion.setValue(LocalDate.now());
        chkActivo.setSelected(true);
        tablaEmpleados.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminar() {
        Empleado seleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            empleados.remove(seleccionado);
            limpiar();
            mensaje(Alert.AlertType.INFORMATION, "Empleado eliminado correctamente.");
        } else {
            mensaje(Alert.AlertType.WARNING, "Seleccione un empleado para eliminar.");
        }
    }
    
    @FXML
    private void cerrar() {
        if (txtId.getScene() != null) {
            ((javafx.stage.Stage) txtId.getScene().getWindow()).close();
        }
    }

    private void mostrarEmpleado(Empleado empleado) {
        txtId.setText(String.valueOf(empleado.getId()));
        txtNombres.setText(empleado.getNombres());
        txtApellidos.setText(empleado.getApellidos());
        cmbCargo.setValue(empleado.getCargo());
        dpFechaContratacion.setValue(empleado.getFechaContratacion());
        chkActivo.setSelected(empleado.isActivo());
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
