package dao;

import model.EstadoPedido;
import model.Pedido;
import model.TipoPedido;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public int create(Pedido pedido) throws SQLException {

        String sql =
                "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<Pedido> readAll() throws SQLException {

        return readAll(null, null);
    }

    public List<Pedido> readAll(TipoPedido tipo,
                                EstadoPedido estado) throws SQLException {

        List<Pedido> pedidos = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1"
        );

        List<Object> parametros = new ArrayList<>();

        if (tipo != null) {

            sql.append(" AND tipo = ?");
            parametros.add(tipo.name());
        }

        if (estado != null) {

            sql.append(" AND estado = ?");
            parametros.add(estado.name());
        }

        sql.append(" ORDER BY id");

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    pedidos.add(new Pedido(
                            rs.getInt("id"),
                            rs.getString("direccion"),
                            TipoPedido.valueOf(rs.getString("tipo")),
                            EstadoPedido.valueOf(rs.getString("estado"))
                    ));
                }
            }
        }

        return pedidos;
    }

    public boolean update(Pedido pedido) throws SQLException {

        String sql =
                "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {

        String sql =
                "DELETE FROM pedidos WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}