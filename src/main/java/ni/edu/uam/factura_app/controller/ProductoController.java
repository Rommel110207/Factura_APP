package ni.edu.uam.factura_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.factura_app.model.Categoria;
import ni.edu.uam.factura_app.model.Producto;

import java.io.File;
import java.math.BigDecimal;

public class ProductoController {
    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colImagen;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ObservableList<Producto> productosOriginales = FXCollections.observableArrayList();
    private final ObservableList<Producto> productosFiltrados = FXCollections.observableArrayList();
    private String rutaImagen;

    @FXML
    private void initialize() {
        cmbCategoria.setItems(FXCollections.observableArrayList(
            new Categoria(1, "Alimentos", true),
            new Categoria(2, "Bebidas", true),
            new Categoria(3, "Limpieza", true)
        ));
        
        colImagen.setCellValueFactory(new PropertyValueFactory<>("rutaImagen"));
        colImagen.setCellFactory(column -> new TableCell<Producto, String>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image(item, 45, 45, true, true);
                        imageView.setImage(img);
                        imageView.setFitWidth(45);
                        imageView.setFitHeight(45);
                        imageView.setPreserveRatio(true);
                        
                        // Opcional: Centrar la imagen en la celda
                        VBox box = new VBox(imageView);
                        box.setAlignment(javafx.geometry.Pos.CENTER);
                        setGraphic(box);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
        
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        tblProductos.setItems(productosFiltrados);
        chkActivo.setSelected(true);
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
            || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()
            || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());
            if (precio.signum() <= 0 || existencia < 0) {
                mensaje(Alert.AlertType.WARNING, "Precio mayor que cero y existencia no negativa.");
                return;
            }
            Producto nuevo = new Producto(null, txtCodigo.getText().trim(),
                txtNombre.getText().trim(), "", cmbCategoria.getValue(), precio,
                existencia, rutaImagen, chkActivo.isSelected());
            productosOriginales.add(nuevo);
            buscar();
            mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            limpiar();
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
        }
    }

    @FXML
    private void buscar() {
        String filtro = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().toLowerCase().trim() : "";
        if (filtro.isEmpty()) {
            productosFiltrados.setAll(productosOriginales);
        } else {
            java.util.List<Producto> filtrados = productosOriginales.stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(filtro))
                          || (p.getCodigo() != null && p.getCodigo().toLowerCase().contains(filtro)))
                .collect(java.util.stream.Collectors.toList());
            productosFiltrados.setAll(filtrados);
        }
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    @FXML
    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        if (txtBuscar != null) txtBuscar.clear();
        buscar();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
