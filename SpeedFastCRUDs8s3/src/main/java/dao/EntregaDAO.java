package dao;

import model.Entrega;
import util.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    public int create(Entrega entrega) throws SQLException {

        String sql =
                "INSERT INTO entregas " +
                        "(id_pedido, id_repartidor, fecha, hora) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<Entrega> readAll() throws SQLException {

        return readAll(null, null);
    }

    public List<Entrega> readAll(Integer idPedido,
                                 Integer idRepartidor)
            throws SQLException {

        List<Entrega> entregas = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, id_pedido, id_repartidor, fecha, hora " +
                        "FROM entregas WHERE 1=1"
        );

        List<Integer> parametros = new ArrayList<>();

        if (idPedido != null) {

            sql.append(" AND id_pedido = ?");
            parametros.add(idPedido);
        }

        if (idRepartidor != null) {

            sql.append(" AND id_repartidor = ?");
            parametros.add(idRepartidor);
        }

        sql.append(" ORDER BY id");

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {

                ps.setInt(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    entregas.add(new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora").toLocalTime()
                    ));
                }
            }
        }

        return entregas;
    }

    public boolean update(Entrega entrega) throws SQLException {

        String sql =
                "UPDATE entregas SET " +
                        "id_pedido = ?, " +
                        "id_repartidor = ?, " +
                        "fecha = ?, " +
                        "hora = ? " +
                        "WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.setInt(5, entrega.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {

        String sql =
                "DELETE FROM entregas WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}