package app;

import util.ConexionDB;
import view.VentanaPrincipal;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            if (!ConexionDB.probarConexion()) {

                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible conectar con MySQL.\n\n" +
                                "Revisa:\n" +
                                "- MySQL esté iniciado.\n" +
                                "- La base speedfast_db exista.\n" +
                                "- Usuario y contraseña sean correctos.\n" +
                                "- El conector JDBC esté agregado.",
                        "Error de conexión",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            new VentanaPrincipal().setVisible(true);
        });
    }
}