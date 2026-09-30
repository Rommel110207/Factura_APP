package ni.edu.uam.factura_app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CLASE DE CONEXION JDBC CLASICA
 * Aquí debes poner TUS CREDENCIALES de PostgreSQL.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/TiendaJava";
    

    private static final String USER = "postgres";
    

    private static final String PASSWORD = "2103";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
