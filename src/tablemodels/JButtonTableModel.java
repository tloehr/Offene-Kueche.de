/*
 * http://tips4java.wordpress.com/2008/11/27/bean-table-model/
 */

package tablemodels;

import tablemodels.RowTableModel;

import java.util.*;
import javax.swing.*;

public class JButtonTableModel extends RowTableModel<JButton>
{
	private static String[] COLUMN_NAMES =
	{
		"Text",
		"Tool Tip Text",
		"Enabled",
		"Visible"
	};

	JButtonTableModel()
	{
		super( Arrays.asList(COLUMN_NAMES) );
		setRowClass( JButton.class );

		setColumnClass(2, Boolean.class);
		setColumnClass(3, Boolean.class);
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		JButton button = getRow(row);

		switch (column)
        {
            case 0: return button.getText();
            case 1: return button.getToolTipText();
            case 2: return button.isEnabled();
            case 3: return button.isVisible();
            default: return null;
        }
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		JButton button = getRow(row);

		switch (column)
        {
            case 0: button.setText((String)value); break;
            case 1: button.setToolTipText((String)value); break;
            case 2: button.setEnabled((Boolean)value); break;
            case 3: button.setVisible((Boolean)value); break;
        }
	}

}