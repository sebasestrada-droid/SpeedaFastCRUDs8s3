package dao;

import model.Cliente;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public int create(Cliente cliente) throws SQLException {

        String sql = "INSERT INTO clientes (nombre) VALUES (?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<Cliente> readAll() throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id, nombre FROM clientes ORDER BY id";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );

                clientes.add(cliente);
            }
        }

        return clientes;
    }

    public boolean update(Cliente cliente) throws SQLException {

        String sql =
                "UPDATE clientes SET nombre = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setInt(2, cliente.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {

        String sql =
                "DELETE FROM clientes WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}