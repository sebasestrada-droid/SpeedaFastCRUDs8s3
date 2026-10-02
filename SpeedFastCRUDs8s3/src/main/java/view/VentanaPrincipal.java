package view;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private PanelClientes panelClientes;
    private PanelRepartidores panelRepartidores;
    private PanelPedidos panelPedidos;
    private PanelEntregas panelEntregas;

    public VentanaPrincipal() {

        setTitle("SpeedFast - Gestión de Pedidos");

        setSize(1000, 650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializar();
    }

    private void inicializar() {

        JTabbedPane pestañas = new JTabbedPane();

        panelEntregas = new PanelEntregas();

        panelClientes =
                new PanelClientes(this::actualizarEntregas);

        panelRepartidores =
                new PanelRepartidores(this::actualizarEntregas);

        panelPedidos =
                new PanelPedidos(this::actualizarEntregas);

        pestañas.addTab(
                "Clientes",
                panelClientes
        );

        pestañas.addTab(
                "Repartidores",
                panelRepartidores
        );

        pestañas.addTab(
                "Pedidos",
                panelPedidos
        );

        pestañas.addTab(
                "Entregas",
                panelEntregas
        );

        add(pestañas, BorderLayout.CENTER);
    }

    private void actualizarEntregas() {

        panelEntregas.cargarCombos();
        panelEntregas.revalidate();
        panelEntregas.repaint();
    }
}