package view;

import dao.RepartidorDAO;
import model.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class PanelRepartidores extends JPanel {

    private JTextField txtNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private int idSeleccionado = -1;

    private final Runnable alCambiarDatos;

    public PanelRepartidores(Runnable alCambiarDatos) {

        this.alCambiarDatos = alCambiarDatos;

        setLayout(new BorderLayout(10, 10));

        crearFormulario();
        crearTabla();

        cargarTabla();
    }

    private void crearFormulario() {

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));

        formulario.add(new JLabel("Nombre:"));

        txtNombre = new JTextField(18);
        formulario.add(txtNombre);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        formulario.add(btnRegistrar);
        formulario.add(btnActualizar);
        formulario.add(btnEliminar);
        formulario.add(btnLimpiar);

        add(formulario, BorderLayout.NORTH);

        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void crearTabla() {

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre"}, 0) {

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

                    txtNombre.setText(
                            modeloTabla.getValueAt(fila, 1).toString()
                    );
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void registrar() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del repartidor es obligatorio."
            );

            return;
        }

        try {

            repartidorDAO.create(new Repartidor(nombre));

            JOptionPane.showMessageDialog(
                    this,
                    "Repartidor registrado correctamente."
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
                    "Selecciona un repartidor."
            );

            return;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El nombre es obligatorio."
            );

            return;
        }

        try {

            repartidorDAO.update(
                    new Repartidor(idSeleccionado, nombre)
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Repartidor actualizado correctamente."
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
                    "Selecciona un repartidor."
            );

            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el repartidor seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            repartidorDAO.delete(idSeleccionado);

            JOptionPane.showMessageDialog(
                    this,
                    "Repartidor eliminado correctamente."
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

            for (Repartidor r : repartidorDAO.readAll()) {

                modeloTabla.addRow(new Object[]{
                        r.getId(),
                        r.getNombre()
                });
            }

        } catch (SQLException e) {

            mostrarError(e);
        }
    }

    private void limpiar() {

        idSeleccionado = -1;
        txtNombre.setText("");
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