package tablerenderer;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 08.03.11
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 *
 * http://www.exampledepot.com/egs/javax.swing.table/ComboBox.html
 *
 */
public class ComboBoxEditor extends DefaultCellEditor {

    public ComboBoxEditor(ComboBoxModel model) {
        super(new JComboBox(model));
    }


}
