/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablemodels;

import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

/**
 *
 * @author tloehr
 */
public class VorratTableStringConverter extends TableStringConverter {

    @Override
    public String toString(TableModel model, int row, int column) {
        return model.getValueAt(row, column).toString();
    }



}
