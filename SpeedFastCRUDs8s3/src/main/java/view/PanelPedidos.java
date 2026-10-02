package view;

import dao.PedidoDAO;
import model.EstadoPedido;
import model.Pedido;
import model.TipoPedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class PanelPedidos extends JPanel {

    private JTextField txtDireccion;

    private JComboBox<TipoPedido> cmbTipo;
    private JComboBox<EstadoPedido> cmbEstado;

    private JComboBox<String> cmbFiltroTipo;
    private JComboBox<String> cmbFiltroEstado;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private int idSeleccionado = -1;

    private final Runnable alCambiarDatos;

    public PanelPedidos(Runnable alCambiarDatos) {

        this.alCambiarDatos = alCambiarDatos;

        setLayout(new BorderLayout(10, 10));

        crearFormulario();
        crearTabla();

        cargarTabla();
    }

    private void crearFormulario() {

        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));

        formulario.add(new JLabel("Dirección:"));

        txtDireccion = new JTextField(18);
        formulario.add(txtDireccion);

        formulario.add(new JLabel("Tipo:"));

        cmbTipo = new JComboBox<>(TipoPedido.values());
        formulario.add(cmbTipo);

        formulario.add(new JLabel("Estado:"));

        cmbEstado = new JComboBox<>(EstadoPedido.values());
        formulario.add(cmbEstado);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        formulario.add(btnRegistrar);
        formulario.add(btnActualizar);
        formulario.add(btnEliminar);
        formulario.add(btnLimpiar);

        superior.add(formulario);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filtros.add(new JLabel("Filtrar tipo:"));

        cmbFiltroTipo = new JComboBox<>();
        cmbFiltroTipo.addItem("TODOS");

        for (TipoPedido tipo : TipoPedido.values()) {
            cmbFiltroTipo.addItem(tipo.name());
        }

        filtros.add(cmbFiltroTipo);

        filtros.add(new JLabel("Filtrar estado:"));

        cmbFiltroEstado = new JComboBox<>();
        cmbFiltroEstado.addItem("TODOS");

        for (EstadoPedido estado : EstadoPedido.values()) {
            cmbFiltroEstado.addItem(estado.name());
        }

        filtros.add(cmbFiltroEstado);

        superior.add(filtros);

        add(superior, BorderLayout.NORTH);

        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cmbFiltroTipo.addActionListener(e -> cargarTabla());
        cmbFiltroEstado.addActionListener(e -> cargarTabla());
    }

    private void crearTabla() {

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int fila = tabla.getSelectedRow();

                if (fila >= 0) {

                    idSeleccionado =
                            Integer.parseInt(
                                    modeloTabla.getValueAt(fila, 0).toString()
                            );

                    txtDireccion.setText(
                            modeloTabla.getValueAt(fila, 1).toString()
                    );

                    cmbTipo.setSelectedItem(
                            TipoPedido.valueOf(
                                    modeloTabla.getValueAt(fila, 2).toString()
                            )
                    );

                    cmbEstado.setSelectedItem(
                            EstadoPedido.valueOf(
                                    modeloTabla.getValueAt(fila, 3).toString()
                            )
                    );
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void registrar() {

        String direccion = txtDireccion.getText().trim();

        if (direccion.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La dirección es obligatoria."
            );

            return;
        }

        TipoPedido tipo = (TipoPedido) cmbTipo.getSelectedItem();
        EstadoPedido estado = (EstadoPedido) cmbEstado.getSelectedItem();

        if (tipo == null || estado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar tipo y estado."
            );

            return;
        }

        try {

            pedidoDAO.create(
                    new Pedido(direccion, tipo, estado)
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido registrado correctamente."
            );

            cargarTabla();
            limpiar();

            alCambiarDatos.run();

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void actualizar() {

        if (idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un pedido."
            );

            return;
        }

        String direccion = txtDireccion.getText().trim();

        if (direccion.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La dirección es obligatoria."
            );

            return;
        }

        TipoPedido tipo = (TipoPedido) cmbTipo.getSelectedItem();
        EstadoPedido estado = (EstadoPedido) cmbEstado.getSelectedItem();

        try {

            pedidoDAO.update(
                    new Pedido(
                            idSeleccionado,
                            direccion,
                            tipo,
                            estado
                    )
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido actualizado correctamente."
            );

            cargarTabla();
            limpiar();

            alCambiarDatos.run();

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void eliminar() {

        if (idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un pedido."
            );

            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el pedido seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            pedidoDAO.delete(idSeleccionado);

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido eliminado correctamente."
            );

            cargarTabla();
            limpiar();

            alCambiarDatos.run();

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void cargarTabla() {

        try {

            modeloTabla.setRowCount(0);

            TipoPedido tipo = null;
            EstadoPedido estado = null;

            String tipoSeleccionado =
                    (String) cmbFiltroTipo.getSelectedItem();

            String estadoSeleccionado =
                    (String) cmbFiltroEstado.getSelectedItem();

            if (tipoSeleccionado != null &&
                    !tipoSeleccionado.equals("TODOS")) {

                tipo = TipoPedido.valueOf(tipoSeleccionado);
            }

            if (estadoSeleccionado != null &&
                    !estadoSeleccionado.equals("TODOS")) {

                estado = EstadoPedido.valueOf(estadoSeleccionado);
            }

            for (Pedido pedido :
                    pedidoDAO.readAll(tipo, estado)) {

                modeloTabla.addRow(new Object[]{
                        pedido.getId(),
                        pedido.getDireccion(),
                        pedido.getTipo(),
                        pedido.getEstado()
                });
            }

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void limpiar() {

        idSeleccionado = -1;
        txtDireccion.setText("");

        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);

        tabla.clearSelection();
    }

    private void mostrarError(SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Error de base de datos:\n" + e.getMessage(),
                "Error SQL",
                JOptionPane.ERROR_MESSAGE
        );
    }
}