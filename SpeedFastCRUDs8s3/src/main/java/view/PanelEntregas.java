package view;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import model.Entrega;
import model.Pedido;
import model.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class PanelEntregas extends JPanel {

    private JComboBox<ComboItem> cmbPedido;
    private JComboBox<ComboItem> cmbRepartidor;

    private JComboBox<ComboItem> cmbFiltroPedido;
    private JComboBox<ComboItem> cmbFiltroRepartidor;

    private JTextField txtFecha;
    private JTextField txtHora;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private int idSeleccionado = -1;

    public PanelEntregas() {

        setLayout(new BorderLayout(10, 10));

        crearFormulario();
        crearTabla();

        cargarCombos();
        cargarTabla();
    }

    private void crearFormulario() {

        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));

        formulario.add(new JLabel("Pedido:"));

        cmbPedido = new JComboBox<>();
        cmbPedido.setPreferredSize(new Dimension(220, 25));
        formulario.add(cmbPedido);

        formulario.add(new JLabel("Repartidor:"));

        cmbRepartidor = new JComboBox<>();
        cmbRepartidor.setPreferredSize(new Dimension(180, 25));
        formulario.add(cmbRepartidor);

        formulario.add(new JLabel("Fecha:"));

        txtFecha = new JTextField(10);
        formulario.add(txtFecha);

        formulario.add(new JLabel("Hora:"));

        txtHora = new JTextField(8);
        formulario.add(txtHora);

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

        filtros.add(new JLabel("Filtrar pedido:"));

        cmbFiltroPedido = new JComboBox<>();
        cmbFiltroPedido.setPreferredSize(new Dimension(220, 25));
        filtros.add(cmbFiltroPedido);

        filtros.add(new JLabel("Filtrar repartidor:"));

        cmbFiltroRepartidor = new JComboBox<>();
        cmbFiltroRepartidor.setPreferredSize(new Dimension(180, 25));
        filtros.add(cmbFiltroRepartidor);

        superior.add(filtros);

        add(superior, BorderLayout.NORTH);

        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cmbFiltroPedido.addActionListener(e -> cargarTabla());
        cmbFiltroRepartidor.addActionListener(e -> cargarTabla());
    }

    private void crearTabla() {

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "ID Pedido",
                        "ID Repartidor",
                        "Fecha",
                        "Hora"
                }, 0) {

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

                    int idPedido =
                            Integer.parseInt(
                                    modeloTabla.getValueAt(fila, 1).toString()
                            );

                    int idRepartidor =
                            Integer.parseInt(
                                    modeloTabla.getValueAt(fila, 2).toString()
                            );

                    seleccionarComboPorId(
                            cmbPedido,
                            idPedido
                    );

                    seleccionarComboPorId(
                            cmbRepartidor,
                            idRepartidor
                    );

                    txtFecha.setText(
                            modeloTabla.getValueAt(fila, 3).toString()
                    );

                    txtHora.setText(
                            modeloTabla.getValueAt(fila, 4).toString()
                    );
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public void cargarCombos() {

        try {

            cmbPedido.removeAllItems();
            cmbFiltroPedido.removeAllItems();

            cmbFiltroPedido.addItem(
                    new ComboItem(0, "TODOS")
            );

            for (Pedido pedido : pedidoDAO.readAll()) {

                ComboItem item = new ComboItem(
                        pedido.getId(),
                        pedido.getId() + " - " + pedido.getDireccion()
                );

                cmbPedido.addItem(item);

                cmbFiltroPedido.addItem(
                        new ComboItem(
                                pedido.getId(),
                                pedido.getId() + " - " + pedido.getDireccion()
                        )
                );
            }

            cmbRepartidor.removeAllItems();
            cmbFiltroRepartidor.removeAllItems();

            cmbFiltroRepartidor.addItem(
                    new ComboItem(0, "TODOS")
            );

            for (Repartidor repartidor :
                    repartidorDAO.readAll()) {

                ComboItem item = new ComboItem(
                        repartidor.getId(),
                        repartidor.getId() + " - " +
                                repartidor.getNombre()
                );

                cmbRepartidor.addItem(item);

                cmbFiltroRepartidor.addItem(
                        new ComboItem(
                                repartidor.getId(),
                                repartidor.getId() + " - " +
                                        repartidor.getNombre()
                        )
                );
            }

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void registrar() {

        try {

            ComboItem pedidoSeleccionado =
                    (ComboItem) cmbPedido.getSelectedItem();

            ComboItem repartidorSeleccionado =
                    (ComboItem) cmbRepartidor.getSelectedItem();

            if (pedidoSeleccionado == null ||
                    repartidorSeleccionado == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debes seleccionar un pedido y un repartidor."
                );

                return;
            }

            LocalDate fecha =
                    LocalDate.parse(txtFecha.getText().trim());

            LocalTime hora =
                    convertirHora(txtHora.getText().trim());

            Entrega entrega = new Entrega(
                    pedidoSeleccionado.getId(),
                    repartidorSeleccionado.getId(),
                    fecha,
                    hora
            );

            entregaDAO.create(entrega);

            JOptionPane.showMessageDialog(
                    this,
                    "Entrega registrada correctamente."
            );

            cargarTabla();
            limpiar();

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Fecha u hora inválida.\n" +
                            "Fecha: yyyy-MM-dd\n" +
                            "Hora: HH:mm o HH:mm:ss",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void actualizar() {

        if (idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una entrega."
            );

            return;
        }

        try {

            ComboItem pedidoSeleccionado =
                    (ComboItem) cmbPedido.getSelectedItem();

            ComboItem repartidorSeleccionado =
                    (ComboItem) cmbRepartidor.getSelectedItem();

            LocalDate fecha =
                    LocalDate.parse(txtFecha.getText().trim());

            LocalTime hora =
                    convertirHora(txtHora.getText().trim());

            Entrega entrega = new Entrega(
                    idSeleccionado,
                    pedidoSeleccionado.getId(),
                    repartidorSeleccionado.getId(),
                    fecha,
                    hora
            );

            entregaDAO.update(entrega);

            JOptionPane.showMessageDialog(
                    this,
                    "Entrega actualizada correctamente."
            );

            cargarTabla();
            limpiar();

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Fecha u hora inválida.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void eliminar() {

        if (idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una entrega."
            );

            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la entrega seleccionada?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            entregaDAO.delete(idSeleccionado);

            JOptionPane.showMessageDialog(
                    this,
                    "Entrega eliminada correctamente."
            );

            cargarTabla();
            limpiar();

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void cargarTabla() {

        try {

            modeloTabla.setRowCount(0);

            Integer idPedido = obtenerFiltroId(cmbFiltroPedido);
            Integer idRepartidor =
                    obtenerFiltroId(cmbFiltroRepartidor);

            for (Entrega entrega :
                    entregaDAO.readAll(idPedido, idRepartidor)) {

                modeloTabla.addRow(new Object[]{
                        entrega.getId(),
                        entrega.getIdPedido(),
                        entrega.getIdRepartidor(),
                        entrega.getFecha(),
                        entrega.getHora()
                });
            }

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private Integer obtenerFiltroId(
            JComboBox<ComboItem> combo) {

        ComboItem item =
                (ComboItem) combo.getSelectedItem();

        if (item == null || item.getId() == 0) {
            return null;
        }

        return item.getId();
    }

    private void seleccionarComboPorId(
            JComboBox<ComboItem> combo,
            int id) {

        for (int i = 0; i < combo.getItemCount(); i++) {

            ComboItem item = combo.getItemAt(i);

            if (item.getId() == id) {

                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private LocalTime convertirHora(String texto) {

        if (texto.matches("\\d{2}:\\d{2}")) {

            texto += ":00";
        }

        return LocalTime.parse(texto);
    }

    private void limpiar() {

        idSeleccionado = -1;

        if (cmbPedido.getItemCount() > 0) {
            cmbPedido.setSelectedIndex(0);
        }

        if (cmbRepartidor.getItemCount() > 0) {
            cmbRepartidor.setSelectedIndex(0);
        }

        txtFecha.setText("");
        txtHora.setText("");

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