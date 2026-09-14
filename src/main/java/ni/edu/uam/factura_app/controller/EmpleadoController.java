package ni.edu.uam.factura_app.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ni.edu.uam.factura_app.model.Cargo;
import ni.edu.uam.factura_app.model.Empleado;

import java.time.LocalDate;
import java.util.stream.Collectors;

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

    private final ObservableList<Empleado> empleadosOriginales = FXCollections.observableArrayList();
    private final ObservableList<Empleado> empleadosFiltrados = FXCollections.observableArrayList();
    private final ObservableList<Cargo> cargosList = FXCollections.observableArrayList();
    
    private int idCounter = 1;
    private Empleado empleadoSeleccionado = null;

    @FXML
    public void initialize() {
        // Inicializar datos de prueba para cargos
        cargosList.addAll(
            new Cargo(1, "Gerente General", "Administración", 30000.0),
            new Cargo(2, "Vendedor", "Ventas", 10000.0),
            new Cargo(3, "Cajero", "Finanzas", 12000.0)
        );
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

        // Binding de columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCargo.setCellValueFactory(cellData -> {
            Cargo c = cellData.getValue().getCargo();
            return new SimpleStringProperty(c != null ? c.getNombre() : "");
        });
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaContratacion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Datos de muestra
        empleadosOriginales.addAll(
            new Empleado(idCounter++, "Juan", "Pérez", cargosList.get(0), LocalDate.now().minusYears(2), true),
            new Empleado(idCounter++, "María", "López", cargosList.get(1), LocalDate.now().minusMonths(5), true)
        );
        empleadosFiltrados.addAll(empleadosOriginales);
        tblEmpleados.setItems(empleadosFiltrados);

        // DatePicker restricción: No fechas futuras
        dtpFechaContratacion.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
        dtpFechaContratacion.setValue(LocalDate.now());
        chkActivo.setSelected(true);

        // Listener de selección
        tblEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                empleadoSeleccionado = newSelection;
                txtNombres.setText(newSelection.getNombres());
                txtApellidos.setText(newSelection.getApellidos());
                cmbCargo.setValue(newSelection.getCargo());
                dtpFechaContratacion.setValue(newSelection.getFechaContratacion());
                chkActivo.setSelected(newSelection.isActivo());
            }
        });
    }

    @FXML
    public void guardar() {
        if (txtNombres.getText() == null || txtNombres.getText().trim().isEmpty() ||
            txtApellidos.getText() == null || txtApellidos.getText().trim().isEmpty() ||
            cmbCargo.getValue() == null ||
            dtpFechaContratacion.getValue() == null) {
            
            new Alert(Alert.AlertType.WARNING, "Todos los campos (Nombres, Apellidos, Cargo, Fecha) son obligatorios.", ButtonType.OK).showAndWait();
            return;
        }

        if (dtpFechaContratacion.getValue().isAfter(LocalDate.now())) {
            new Alert(Alert.AlertType.WARNING, "La fecha de contratación no puede ser mayor al día de hoy.", ButtonType.OK).showAndWait();
            return;
        }

        if (empleadoSeleccionado != null) {
            // Actualizar
            empleadoSeleccionado.setNombres(txtNombres.getText().trim());
            empleadoSeleccionado.setApellidos(txtApellidos.getText().trim());
            empleadoSeleccionado.setCargo(cmbCargo.getValue());
            empleadoSeleccionado.setFechaContratacion(dtpFechaContratacion.getValue());
            empleadoSeleccionado.setActivo(chkActivo.isSelected());
            tblEmpleados.refresh();
            new Alert(Alert.AlertType.INFORMATION, "Empleado actualizado correctamente.", ButtonType.OK).showAndWait();
        } else {
            // Nuevo
            Empleado nuevo = new Empleado(
                idCounter++,
                txtNombres.getText().trim(),
                txtApellidos.getText().trim(),
                cmbCargo.getValue(),
                dtpFechaContratacion.getValue(),
                chkActivo.isSelected()
            );
            empleadosOriginales.add(nuevo);
            buscar(); // Actualizar tabla
            new Alert(Alert.AlertType.INFORMATION, "Empleado agregado correctamente.", ButtonType.OK).showAndWait();
        }
        limpiar();
    }

    @FXML
    public void buscar() {
        String filtro = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        if (filtro.isEmpty()) {
            empleadosFiltrados.setAll(empleadosOriginales);
        } else {
            empleadosFiltrados.setAll(
                empleadosOriginales.stream()
                    .filter(e -> e.getNombres().toLowerCase().contains(filtro) || e.getApellidos().toLowerCase().contains(filtro))
                    .collect(Collectors.toList())
            );
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
        txtBuscar.clear();
        buscar();
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
