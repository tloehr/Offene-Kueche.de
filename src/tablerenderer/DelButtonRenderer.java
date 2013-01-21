/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 *
 * @author tloehr
 */
public class DelButtonRenderer extends JButton implements TableCellRenderer {

    public Component getTableCellRendererComponent(
            final JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/32x32/edit_remove.png")));
        return this;
    }
}
