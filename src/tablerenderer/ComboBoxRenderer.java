package tablerenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 08.03.11
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 * <p/>
 * <p/>
 * http://www.exampledepot.com/egs/javax.swing.table/ComboBox.html
 */
public class ComboBoxRenderer extends JComboBox implements TableCellRenderer {


    public ComboBoxRenderer(ComboBoxModel model) {
        super(model);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        return this;
    }


}
