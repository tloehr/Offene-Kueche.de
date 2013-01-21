/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import printer.Printers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author tloehr
 */
public class PrinterToggleButtonRenderer extends JButton implements TableCellRenderer {

    public Component getTableCellRendererComponent(
            final JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        int printer = (Integer) value;
        if (printer == Printers.DRUCK_KEIN_DRUCK) {
            this.setText("Kein");
        } else if (printer == Printers.DRUCK_ETI1) {
            this.setText("Etikett");
        } else {
            this.setText("Laser");
        }

        return this;
    }
}
