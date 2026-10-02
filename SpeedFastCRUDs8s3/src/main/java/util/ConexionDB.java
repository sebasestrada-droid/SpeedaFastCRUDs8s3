package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL =
            "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&serverTimezone=UTC";

    private static final String USUARIO = "root";

    private static final String PASSWORD = "clavemysql";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    public static boolean probarConexion() {

        try (Connection conexion = conectar()) {

            System.out.println("Conexión a MySQL exitosa.");
            return true;

        } catch (SQLException e) {

            System.out.println("Error de conexión a MySQL:");
            System.out.println(e.getMessage());

            return false;
        }
    }
}