package dao;

import model.Repartidor;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    public int create(Repartidor repartidor) throws SQLException {

        String sql =
                "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, repartidor.getNombre());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<Repartidor> readAll() throws SQLException {

        List<Repartidor> lista = new ArrayList<>();

        String sql =
                "SELECT id, nombre FROM repartidores ORDER BY id";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(new Repartidor(
                        rs.getInt("id"),
                        rs.getString("nombre")
                ));
            }
        }

        return lista;
    }

    public boolean update(Repartidor repartidor) throws SQLException {

        String sql =
                "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {

        String sql =
                "DELETE FROM repartidores WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}