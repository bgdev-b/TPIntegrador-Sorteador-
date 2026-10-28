package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import services.SorteoService;
import java.time.Month;
import java.util.OptionalInt;

public class Principal {
    private JPanel VentanaPrincipal;
    private JPanel panelIzquierda;
    private JPanel panelDerecha;
    private JPanel panelSuperior;
    private JLabel nombreApp;
    private JLabel carritoIcon;
    private JButton btnSortear;
    private JTable tblGanadores;
    private JPanel panelConfiguration;
    private JComboBox<Month> cmbComboMes;
    private JTextField txtCantidadGanadores;
    private JButton btnCerrar;

    private final SorteoService sorteoService = new SorteoService();
    private final DefaultTableModel modeloGanadores = new DefaultTableModel(
            new Object[]{"Posición", "Ganador"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private Month mesSorteo;
    private int cantidadMaximaGanadores;
    private boolean sorteoEnCurso = false;

    public Principal() {
        tblGanadores.setModel(modeloGanadores);
        cmbComboMes.setModel(new DefaultComboBoxModel<>(Month.values()));

        btnSortear.addActionListener(e -> sortear());
        btnCerrar.addActionListener(e -> cerrarSorteo());
    }

    private void sortear() {
        if (!sorteoEnCurso && !iniciarSorteo()) {
            return;
        }

        if (modeloGanadores.getRowCount() >= cantidadMaximaGanadores) {
            mostrarMensaje("Ya se alcanzó la cantidad de ganadores.");
            return;
        }

        String numeroGanador = sorteoService.generarGanadorUnico(mesSorteo);
        modeloGanadores.addRow(new Object[]{
                modeloGanadores.getRowCount() + 1,
                numeroGanador
        });
    }

    private boolean iniciarSorteo() {
        OptionalInt cantidad = leerCantidad();
        if (cantidad.isEmpty()) {
            return false;
        }

        mesSorteo = (Month) cmbComboMes.getSelectedItem();
        cantidadMaximaGanadores = cantidad.getAsInt();
        sorteoEnCurso = true;

        cmbComboMes.setEnabled(false);
        txtCantidadGanadores.setEnabled(false);
        return true;
    }

    private OptionalInt leerCantidad() {
        String texto = txtCantidadGanadores.getText().trim();
        if (texto.isEmpty()) {
            mostrarMensaje("Debes indicar la cantidad de ganadores.");
            return OptionalInt.empty();
        }
        try {
            int cantidad = Integer.parseInt(texto);
            if (cantidad <= 0) {
                mostrarMensaje("La cantidad de ganadores debe ser mayor que cero.");
                return OptionalInt.empty();
            }
            return OptionalInt.of(cantidad);
        } catch (NumberFormatException e) {
            mostrarMensaje("La cantidad debe ser un número entero.");
            return OptionalInt.empty();
        }
    }

    private void cerrarSorteo() {
        int respuesta = JOptionPane.showConfirmDialog(
                VentanaPrincipal,
                "¿Cerrar el sorteo? Se perderán los ganadores mostrados.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        modeloGanadores.setRowCount(0);
        sorteoService.reiniciar();
        mesSorteo = null;
        cantidadMaximaGanadores = 0;
        sorteoEnCurso = false;

        cmbComboMes.setEnabled(true);
        txtCantidadGanadores.setEnabled(true);
        mostrarMensaje("El sorteo se ha cerrado correctamente.");
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(VentanaPrincipal, mensaje);
    }

    public JPanel getVentanaPrincipal() {
        return VentanaPrincipal;
    }
}