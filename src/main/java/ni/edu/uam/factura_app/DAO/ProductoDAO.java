package ni.edu.uam.factura_app.DAO;

import ni.edu.uam.factura_app.model.Categoria;
import ni.edu.uam.factura_app.model.Producto;
import ni.edu.uam.factura_app.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        return buscar("");
    }

    public List<Producto> buscar(String filtro) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.codigo, p.nombre, p.descripcion, p.categoria_id, p.precio_venta, p.existencia, p.ruta_imagen, p.activo, " +
                     "c.nombre as cat_nombre, c.activa as cat_activa " +
                     "FROM producto p LEFT JOIN categoria c ON p.categoria_id = c.id " +
                     "WHERE LOWER(p.nombre) LIKE ? OR LOWER(p.codigo) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String parametro = "%" + filtro.toLowerCase() + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = null;
                    int catId = rs.getInt("categoria_id");
                    if (!rs.wasNull()) {
                        cat = new Categoria(catId, rs.getString("cat_nombre"), rs.getBoolean("cat_activa"));
                    }
                    
                    lista.add(new Producto(
                        rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), rs.getString("descripcion"),
                        cat, rs.getBigDecimal("precio_venta"), rs.getInt("existencia"),
                        rs.getString("ruta_imagen"), rs.getBoolean("activo")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            if (producto.getCategoria() != null) {
                pstmt.setInt(3, producto.getCategoria().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setBigDecimal(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getExistencia());
            pstmt.setString(6, producto.getRutaImagen());
            pstmt.setBoolean(7, producto.isActivo());
            pstmt.executeUpdate();
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE producto SET codigo=?, nombre=?, categoria_id=?, precio_venta=?, existencia=?, ruta_imagen=?, activo=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            if (producto.getCategoria() != null) {
                pstmt.setInt(3, producto.getCategoria().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setBigDecimal(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getExistencia());
            pstmt.setString(6, producto.getRutaImagen());
            pstmt.setBoolean(7, producto.isActivo());
            pstmt.setInt(8, producto.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
