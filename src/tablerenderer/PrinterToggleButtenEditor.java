/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import printer.Printers;
import tools.Const;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tloehr
 */
public class PrinterToggleButtenEditor extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener {

    int row = -1, column = -1;
    DefaultTableModel tm;
    Object value = null;
    JButton button;
    protected static final String DEL = "delete";

    public PrinterToggleButtenEditor() {
        button = new JButton();
        button.setActionCommand(DEL);
        button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (DEL.equals(e.getActionCommand())) {
            int printer = (Integer) value;

            if (printer == Printers.DRUCK_KEIN_DRUCK) {
                value = Printers.DRUCK_ETI1;
            } else if (printer == Printers.DRUCK_ETI2) {
                value = Printers.DRUCK_LASER;
            } else {
                value = Printers.DRUCK_KEIN_DRUCK;
            }
            fireEditingStopped();
        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return value;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {

        tm = (DefaultTableModel) table.getModel();

        int printer = (Integer) value;
        button.setText(printer == Printers.DRUCK_LASER ? "Laser" : "Etikett");

        this.value = value;
        this.row = row;
        this.column = column;
        return button;
    }
}
