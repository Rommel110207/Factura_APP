package ni.edu.uam.factura_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ni.edu.uam.factura_app.DAO.CategoriaDAO;
import ni.edu.uam.factura_app.DAO.ProductoDAO;
import ni.edu.uam.factura_app.model.Categoria;
import ni.edu.uam.factura_app.model.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoController {
    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    
    // Controles de Busqueda y Filtro
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;
    
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colImagen;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private FilteredList<Producto> productosFiltrados;
    private final ObservableList<Categoria> categoriasList = FXCollections.observableArrayList();
    
    private String rutaImagen;
    private Producto productoSeleccionado = null;
    
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML
    private void initialize() {
        cargarCategorias();
        
        // Configuracion del combobox del formulario
        cmbCategoria.setItems(categoriasList);
        StringConverter<Categoria> converterCategoria = new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return c != null ? c.getNombre() : "Seleccione una categoría";
            }
            @Override
            public Categoria fromString(String string) {
                return null;
            }
        };
        cmbCategoria.setConverter(converterCategoria);
        
        // Configuracion de columnas
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
        
        // Configuracion de filtros
        if(cmbFiltroEstado != null) {
            cmbFiltroEstado.getItems().addAll("Todos", "Activos", "Inactivos");
            cmbFiltroEstado.setValue("Todos");
            cmbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }
        if(cmbFiltroCategoria != null) {
            ObservableList<Categoria> categoriasFiltro = FXCollections.observableArrayList();
            Categoria catTodas = new Categoria(-1, "Todas las categorías", true);
            categoriasFiltro.add(catTodas);
            categoriasFiltro.addAll(categoriasList);
            cmbFiltroCategoria.setItems(categoriasFiltro);
            cmbFiltroCategoria.setConverter(converterCategoria);
            cmbFiltroCategoria.setValue(catTodas);
            cmbFiltroCategoria.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }
        if(txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }

        // Carga de datos inicial
        cargarProductos();
        chkActivo.setSelected(true);

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                txtCodigo.setText(newSelection.getCodigo());
                txtNombre.setText(newSelection.getNombre());
                txtPrecio.setText(String.valueOf(newSelection.getPrecioVenta()));
                txtExistencia.setText(String.valueOf(newSelection.getExistencia()));
                chkActivo.setSelected(newSelection.isActivo());
                
                if (newSelection.getCategoria() != null) {
                    for (Categoria c : categoriasList) {
                        if (c.getId().equals(newSelection.getCategoria().getId())) {
                            cmbCategoria.setValue(c);
                            break;
                        }
                    }
                }

                rutaImagen = newSelection.getRutaImagen();
                if (rutaImagen != null && !rutaImagen.isBlank()) {
                    try {
                        imgProducto.setImage(new Image(rutaImagen));
                    } catch (Exception e) {
                        imgProducto.setImage(null);
                    }
                } else {
                    imgProducto.setImage(null);
                }
            }
        });
    }

    private void cargarCategorias() {
        try {
            List<Categoria> lista = categoriaDAO.obtenerTodas();
            categoriasList.setAll(lista);
        } catch (Exception e) {
            mensaje(Alert.AlertType.ERROR, "Error al conectar con la base de datos para cargar categorias.\nVerifique las credenciales en DatabaseConnection.java.");
            e.printStackTrace();
        }
    }

    private void cargarProductos() {
        try {
            List<Producto> lista = productoDAO.obtenerTodos();
            productosList.setAll(lista);
            
            // Implementacion de FilteredList como manda la guia
            productosFiltrados = new FilteredList<>(productosList, p -> true);
            tblProductos.setItems(productosFiltrados);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        if(productosFiltrados == null) return;
        
        productosFiltrados.setPredicate(producto -> {
            // Filtro de texto (codigo o nombre)
            String texto = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().toLowerCase().trim() : "";
            boolean coincideTexto = true;
            if (!texto.isEmpty()) {
                String nombreStr = producto.getNombre() != null ? producto.getNombre().toLowerCase() : "";
                String codigoStr = producto.getCodigo() != null ? producto.getCodigo().toLowerCase() : "";
                String categoriaStr = (producto.getCategoria() != null && producto.getCategoria().getNombre() != null) ? producto.getCategoria().getNombre().toLowerCase() : "";
                coincideTexto = nombreStr.contains(texto) || codigoStr.contains(texto) || categoriaStr.contains(texto);
            }

            // Filtro de Estado
            boolean coincideEstado = true;
            if (cmbFiltroEstado != null && cmbFiltroEstado.getValue() != null) {
                String estado = cmbFiltroEstado.getValue();
                if (estado.equals("Activos")) coincideEstado = producto.isActivo();
                else if (estado.equals("Inactivos")) coincideEstado = !producto.isActivo();
            }

            // Filtro de Categoria
            boolean coincideCategoria = true;
            if (cmbFiltroCategoria != null && cmbFiltroCategoria.getValue() != null) {
                Categoria catSeleccionada = cmbFiltroCategoria.getValue();
                if (catSeleccionada.getId() != -1) {
                    coincideCategoria = producto.getCategoria() != null && producto.getCategoria().getId().equals(catSeleccionada.getId());
                }
            }

            return coincideTexto && coincideEstado && coincideCategoria;
        });
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void buscar() {
        aplicarFiltros();
    }

    @FXML
    private void guardar() {
        try {
            Producto producto = obtenerProductoFormulario();

            if (productoSeleccionado != null) {
                // UPDATE
                if (productoDAO.existeCodigo(producto.getCodigo(), productoSeleccionado.getId())) {
                    mensaje(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
                    return;
                }
                
                productoSeleccionado.setCodigo(producto.getCodigo());
                productoSeleccionado.setNombre(producto.getNombre());
                productoSeleccionado.setCategoria(producto.getCategoria());
                productoSeleccionado.setPrecioVenta(producto.getPrecioVenta());
                productoSeleccionado.setExistencia(producto.getExistencia());
                productoSeleccionado.setRutaImagen(producto.getRutaImagen());
                productoSeleccionado.setActivo(producto.isActivo());
                productoDAO.actualizar(productoSeleccionado);
                mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
            } else {
                // CREATE
                if (productoDAO.existeCodigo(producto.getCodigo(), null)) {
                    mensaje(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
                    return;
                }
                productoDAO.insertar(producto);
                mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            }
            cargarProductos(); // Refresca ObservableList y TableView
            limpiar();
        } catch (IllegalArgumentException e) {
            mensaje(Alert.AlertType.WARNING, e.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            mensaje(Alert.AlertType.ERROR, "No fue posible completar la operación.");
        }
    }

    private Producto obtenerProductoFormulario() {
        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";

        if (codigo.isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        
        Categoria categoria = cmbCategoria.getValue();
        if (categoria == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }
        
        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("El precio debe ser un valor numérico.");
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }

        int existencia;
        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La existencia debe ser un número entero.");
        }
        if (existencia < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }

        return new Producto(
            null, 
            codigo, 
            nombre,
            null, // descripcion
            categoria, 
            precio, 
            existencia, 
            rutaImagen,
            chkActivo.isSelected() 
        );
    }

    @FXML
    private void eliminar() {
        if (productoSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro que desea eliminar este producto?");
            alert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    productoDAO.eliminar(productoSeleccionado.getId());
                    cargarProductos();
                    limpiar();
                    mensaje(Alert.AlertType.INFORMATION, "Producto eliminado correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    mensaje(Alert.AlertType.ERROR, "No fue posible completar la operación.");
                }
            }
        } else {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto para eliminar.");
        }
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    @FXML
    private void limpiar() {
        productoSeleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        if (txtBuscar != null) txtBuscar.clear();
        if (cmbFiltroEstado != null) cmbFiltroEstado.setValue("Todos");
        if (cmbFiltroCategoria != null && cmbFiltroCategoria.getItems().size() > 0) {
            cmbFiltroCategoria.getSelectionModel().selectFirst();
        }
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
        tblProductos.getSelectionModel().clearSelection();
        aplicarFiltros();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
