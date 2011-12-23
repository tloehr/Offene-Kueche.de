/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author tloehr
 */
public class TextAreaRenderer extends JTextArea
        implements TableCellRenderer {

    Color bg;

    public TextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));
        this.bg = Color.WHITE;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        bg = isSelected ? Color.LIGHT_GRAY : Color.WHITE;
        return this;
    }

    @Override
    public Color getBackground() {
        return bg;
    }
}
