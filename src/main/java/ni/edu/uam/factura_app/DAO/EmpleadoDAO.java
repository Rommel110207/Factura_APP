package ni.edu.uam.factura_app.DAO;

import ni.edu.uam.factura_app.model.Cargo;
import ni.edu.uam.factura_app.model.Empleado;
import ni.edu.uam.factura_app.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public List<Empleado> obtenerTodos() {
        return buscar("");
    }

    public List<Empleado> buscar(String filtro) {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombres, e.apellidos, e.cargo_id, e.fecha_contratacion, e.activo, " +
                     "c.nombre as cargo_nombre, c.descripcion as cargo_desc, c.salario_base as cargo_sal " +
                     "FROM empleado e LEFT JOIN cargo c ON e.cargo_id = c.id " +
                     "WHERE LOWER(e.nombres) LIKE ? OR LOWER(e.apellidos) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String parametro = "%" + filtro.toLowerCase() + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cargo cargo = null;
                    int cargoId = rs.getInt("cargo_id");
                    if (!rs.wasNull()) {
                        cargo = new Cargo(cargoId, rs.getString("cargo_nombre"), rs.getString("cargo_desc"), rs.getDouble("cargo_sal"));
                    }
                    
                    Date dbDate = rs.getDate("fecha_contratacion");
                    LocalDate fecha = dbDate != null ? dbDate.toLocalDate() : null;
                    
                    lista.add(new Empleado(
                        rs.getInt("id"), rs.getString("nombres"), rs.getString("apellidos"),
                        cargo, fecha, rs.getBoolean("activo")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void insertar(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleado (nombres, apellidos, cargo_id, fecha_contratacion, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empleado.getNombres());
            pstmt.setString(2, empleado.getApellidos());
            if (empleado.getCargo() != null) {
                pstmt.setInt(3, empleado.getCargo().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            if (empleado.getFechaContratacion() != null) {
                pstmt.setDate(4, Date.valueOf(empleado.getFechaContratacion()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setBoolean(5, empleado.isActivo());
            pstmt.executeUpdate();
        }
    }

    public void actualizar(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleado SET nombres=?, apellidos=?, cargo_id=?, fecha_contratacion=?, activo=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empleado.getNombres());
            pstmt.setString(2, empleado.getApellidos());
            if (empleado.getCargo() != null) {
                pstmt.setInt(3, empleado.getCargo().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            if (empleado.getFechaContratacion() != null) {
                pstmt.setDate(4, Date.valueOf(empleado.getFechaContratacion()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setBoolean(5, empleado.isActivo());
            pstmt.setInt(6, empleado.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
