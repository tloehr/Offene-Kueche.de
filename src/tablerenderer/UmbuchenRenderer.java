package tablerenderer;

import tablemodels.VorratTableModel2;
import tools.Const;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 27.08.11
 * Time: 13:03
 * To change this template use File | Settings | File Templates.
 */
public class UmbuchenRenderer extends DefaultTableCellRenderer {

    private Icon fraglich, neu, ok;

    public UmbuchenRenderer() {
        super();
        this.fraglich = new ImageIcon(getClass().getResource("/artwork/16x16/lc_helperdialog.png"));
        this.neu = new ImageIcon(getClass().getResource("/artwork/16x16/edit_add.png"));
        this.ok = new ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        VorratTableModel2 model = (VorratTableModel2) table.getModel();

        if (model.getStatus(table.convertRowIndexToModel(row)) == VorratTableModel2.STATUS_FRAGLICH) {
            setForeground(Color.BLUE);
            if (column == VorratTableModel2.COL_ICON){
                setIcon(fraglich);
            }
        } else if (model.getStatus(table.convertRowIndexToModel(row)) == VorratTableModel2.STATUS_NEU) {
            setForeground(Color.RED);
            if (column == VorratTableModel2.COL_ICON){
                setIcon(neu);
            }
        } else if (model.getStatus(table.convertRowIndexToModel(row)) == VorratTableModel2.STATUS_OK) {
            setForeground(Color.BLACK);
            if (column == VorratTableModel2.COL_ICON){
                setIcon(ok);
            }
        } else if (model.getStatus(table.convertRowIndexToModel(row)) == VorratTableModel2.STATUS_ZOMBIE) {
            setForeground(Const.darkorange);
        } else {
            setForeground(Const.yellow4);
        }


        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
