package ni.edu.uam.factura_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.factura_app.DAO.CategoriaDAO;
import ni.edu.uam.factura_app.model.Categoria;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private final ObservableList<Categoria> categoriasList = FXCollections.observableArrayList();
    private Categoria categoriaSeleccionada = null;
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
        
        cargarDesdeBD();
        
        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoriaSeleccionada = newSelection;
                txtNombre.setText(newSelection.getNombre());
                chkActiva.setSelected(newSelection.isActiva());
            }
        });
    }

    private void cargarDesdeBD() {
        List<Categoria> lista = categoriaDAO.obtenerTodas();
        categoriasList.setAll(lista);
        tblCategorias.setItems(categoriasList);
    }

    @FXML
    public void guardar() {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        if (nombre.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El nombre de la categoria es obligatorio.", ButtonType.OK).showAndWait();
            return;
        }

        boolean activa = chkActiva.isSelected();

        try {
            if (categoriaSeleccionada != null) {
                categoriaSeleccionada.setNombre(nombre);
                categoriaSeleccionada.setActiva(activa);
                categoriaDAO.actualizar(categoriaSeleccionada);
                new Alert(Alert.AlertType.INFORMATION, "Categoria actualizada correctamente.", ButtonType.OK).showAndWait();
            } else {
                Categoria nueva = new Categoria(null, nombre, activa);
                categoriaDAO.insertar(nueva);
                new Alert(Alert.AlertType.INFORMATION, "Categoria agregada correctamente.", ButtonType.OK).showAndWait();
            }
            cargarDesdeBD();
            limpiar();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                new Alert(Alert.AlertType.ERROR, "Ya existe un registro con ese nombre. No se pueden guardar nombres duplicados.", ButtonType.OK).showAndWait();
            } else {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al guardar en la base de datos.", ButtonType.OK).showAndWait();
            }
        }
    }

    @FXML
    public void eliminar() {
        if (categoriaSeleccionada != null) {
            try {
                categoriaDAO.eliminar(categoriaSeleccionada.getId());
                new Alert(Alert.AlertType.INFORMATION, "Categoria eliminada correctamente.", ButtonType.OK).showAndWait();
                cargarDesdeBD();
                limpiar();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al eliminar la categoria (Puede estar en uso).", ButtonType.OK).showAndWait();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Seleccione una categoria para eliminar.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    public void buscar() {
        String filtro = txtBuscar.getText() != null ? txtBuscar.getText().trim() : "";
        List<Categoria> resultados = categoriaDAO.buscar(filtro);
        categoriasList.setAll(resultados);
    }

    @FXML
    public void limpiar() {
        categoriaSeleccionada = null;
        txtNombre.clear();
        chkActiva.setSelected(false);
        txtBuscar.clear();
        cargarDesdeBD(); 
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
