package ni.edu.uam.factura_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.factura_app.model.Categoria;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private final ObservableList<Categoria> categoriasOriginales = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categoriasFiltradas = FXCollections.observableArrayList();
    
    private int idCounter = 1;
    private Categoria categoriaSeleccionada = null;

    @FXML
    public void initialize() {
        // Binding de columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
        
        // Datos de muestra
        categoriasOriginales.addAll(
            new Categoria(idCounter++, "Lácteos", true),
            new Categoria(idCounter++, "Bebidas", true),
            new Categoria(idCounter++, "Limpieza", true)
        );
        categoriasFiltradas.addAll(categoriasOriginales);
        tblCategorias.setItems(categoriasFiltradas);
        
        // Listener de selección
        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoriaSeleccionada = newSelection;
                txtNombre.setText(newSelection.getNombre());
                chkActiva.setSelected(newSelection.isActiva());
            }
        });
    }

    @FXML
    public void guardar() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El nombre de la categoría es obligatorio.", ButtonType.OK).showAndWait();
            return;
        }

        boolean activa = chkActiva.isSelected();

        if (categoriaSeleccionada != null) {
            // Actualizar
            categoriaSeleccionada.setNombre(txtNombre.getText().trim());
            categoriaSeleccionada.setActiva(activa);
            tblCategorias.refresh();
            new Alert(Alert.AlertType.INFORMATION, "Categoría actualizada correctamente.", ButtonType.OK).showAndWait();
        } else {
            // Nuevo
            Categoria nueva = new Categoria(idCounter++, txtNombre.getText().trim(), activa);
            categoriasOriginales.add(nueva);
            buscar(); // Actualizar lista filtrada
            new Alert(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.", ButtonType.OK).showAndWait();
        }
        limpiar();
    }

    @FXML
    public void buscar() {
        String filtro = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        if (filtro.isEmpty()) {
            categoriasFiltradas.setAll(categoriasOriginales);
        } else {
            categoriasFiltradas.setAll(
                categoriasOriginales.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(filtro))
                    .collect(Collectors.toList())
            );
        }
    }

    @FXML
    public void limpiar() {
        categoriaSeleccionada = null;
        txtNombre.clear();
        chkActiva.setSelected(false);
        txtBuscar.clear();
        buscar(); // Reset filtro
        tblCategorias.getSelectionModel().clearSelection();
    }
    
    @FXML
    public void cerrar() {
        if (txtNombre.getScene() != null) {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();
        }
    }
}
