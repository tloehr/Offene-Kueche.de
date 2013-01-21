/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import tablemodels.DeletableTableModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DelButtonEditor extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener {

    int row = -1;
    DeletableTableModel tm;
    Object value = null;
    JButton button;
    protected static final String EDIT = "edit";
    protected static final String DEL = "delete";

    public DelButtonEditor() {
        button = new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/32x32/edit_remove.png")));
        button.setActionCommand(DEL);
        button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (DEL.equals(e.getActionCommand())
                && JOptionPane.showConfirmDialog(button, "Datensatz löschen ?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            tm.removeRow(row);
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

        tm = (DeletableTableModel) table.getModel();



        this.value = value;


        this.row = row;


        return button;

    }
}
