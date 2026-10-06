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
            txtNombre.requestFocus();
            return;
        }

        boolean activa = chkActiva.isSelected();

        try {
            if (categoriaSeleccionada != null) {
                if (categoriaDAO.existeNombre(nombre, categoriaSeleccionada.getId())) {
                    new Alert(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.", ButtonType.OK).showAndWait();
                    return;
                }
                categoriaSeleccionada.setNombre(nombre);
                categoriaSeleccionada.setActiva(activa);
                categoriaDAO.actualizar(categoriaSeleccionada);
                new Alert(Alert.AlertType.INFORMATION, "Categoria actualizada correctamente.", ButtonType.OK).showAndWait();
            } else {
                if (categoriaDAO.existeNombre(nombre, null)) {
                    new Alert(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.", ButtonType.OK).showAndWait();
                    return;
                }
                Categoria nueva = new Categoria(null, nombre, activa);
                categoriaDAO.insertar(nueva);
                new Alert(Alert.AlertType.INFORMATION, "Categoria agregada correctamente.", ButtonType.OK).showAndWait();
            }
            cargarDesdeBD();
            limpiar();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No fue posible completar la operación.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    public void eliminar() {
        if (categoriaSeleccionada != null) {
            try {
                if (categoriaDAO.tieneProductos(categoriaSeleccionada.getId())) {
                    new Alert(Alert.AlertType.WARNING, "No puede eliminar la categoría porque tiene productos asociados.", ButtonType.OK).showAndWait();
                    return;
                }
                categoriaDAO.eliminar(categoriaSeleccionada.getId());
                new Alert(Alert.AlertType.INFORMATION, "Categoria eliminada correctamente.", ButtonType.OK).showAndWait();
                cargarDesdeBD();
                limpiar();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "No fue posible completar la operación.", ButtonType.OK).showAndWait();
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
