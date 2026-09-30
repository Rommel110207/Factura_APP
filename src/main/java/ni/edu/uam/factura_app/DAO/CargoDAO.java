package ni.edu.uam.factura_app.DAO;

import ni.edu.uam.factura_app.model.Cargo;
import ni.edu.uam.factura_app.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    public List<Cargo> obtenerTodos() {
        return buscar("");
    }

    public List<Cargo> buscar(String filtro) {
        List<Cargo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, salario_base FROM cargo WHERE LOWER(nombre) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + filtro.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cargo(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"), rs.getDouble("salario_base")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void insertar(Cargo cargo) throws SQLException {
        String sql = "INSERT INTO cargo (nombre, descripcion, salario_base) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cargo.getNombre());
            pstmt.setString(2, cargo.getDescripcion());
            pstmt.setDouble(3, cargo.getSalarioBase());
            pstmt.executeUpdate();
        }
    }

    public void actualizar(Cargo cargo) throws SQLException {
        String sql = "UPDATE cargo SET nombre = ?, descripcion = ?, salario_base = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cargo.getNombre());
            pstmt.setString(2, cargo.getDescripcion());
            pstmt.setDouble(3, cargo.getSalarioBase());
            pstmt.setInt(4, cargo.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cargo WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
