/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablemodels;

import entity.IngTypesTools;
import entity.Stock;
import entity.StockTools;
import tools.Tools;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;

/**
 * Diese Modell wird in FrmVorrat gebraucht.
 *
 * @author tloehr
 */
public class StockTableModel2 extends DefaultTableModel implements DeletableTableModel {

    public static final int COL_VORRAT_ID = 0;
    public static final int COL_BEZEICHNUNG = 1;
    public static final int COL_MENGE = 2;
    public static final int COL_ICON = 3;

    public static final long STATUS_FRAGLICH = 0; // Computer glaubt, dass das im Lager steht. Bestätigtung steht noch aus.
    public static final long STATUS_NEU = 1; // Steht im Lager, Computer wusste das aber nicht.
    public static final long STATUS_OK = 2; // Stand im Lager, so wie der Computer es geglaubt hatte.
    public static final long STATUS_ZOMBIE = 3; // Vorrat ist noch da, obwohl der eigentlich ausgebucht sein sollte.

    private List data;

    public List getData() {
        return data;
    }

    private DateFormat df;

    public StockTableModel2(List data, Object[] columnNames) {
        this.data = data;
        setColumnIdentifiers(columnNames);
        df = DateFormat.getDateInstance(DateFormat.DEFAULT);
    }


    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (data != null) {
            rowcount = data.size();
        }
        return rowcount;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class c;
        if (columnIndex == 0) {
            c = Long.class;
        } else if (columnIndex == 4) {
            c = BigDecimal.class;
        } else if (columnIndex == 12) {
            c = Integer.class;
        } else {
            c = String.class;
        }
        return c;
    }

    public Stock getVorrat(int row) {
        return (Stock) ((Object[]) data.get(row))[0];
    }

    public int addVorrat(Stock stock) {
        int pos = findVorrat(stock);
        if (pos >= 0) {
            setStatus(pos, STATUS_OK);
        } else {
            data.add(new Object[]{stock, StockTools.getSummeBestand(stock), STATUS_NEU});
        }
        fireTableDataChanged();
        return pos >= 0 ? pos : data.size()-1;
    }

    private int findVorrat(Stock stock) {
        int position = -1;
        for (int i = 0; i < data.size(); i++) {
            Stock stock1 = (Stock) ((Object[]) data.get(i))[0];
            if (stock.equals(stock1)) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void setStatus(int row, long status) {
        ((Object[]) data.get(row))[2] = status;
    }

    public long getStatus(int row) {
        return (Long) ((Object[]) data.get(row))[2];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        Stock stock = (Stock) ((Object[]) data.get(row))[0];
        BigDecimal menge = (BigDecimal) ((Object[]) data.get(row))[1];
        switch (column) {
            case COL_VORRAT_ID: {
                value = stock.getId();
                break;
            }
            case COL_BEZEICHNUNG: {
                //Main.debug(vorrat);
                value = stock.getProdukt().getBezeichnung();
                break;
            }
            case COL_MENGE: {
                value = Tools.roundScale2(menge.doubleValue()) + " " + IngTypesTools.EINHEIT[stock.getProdukt().getIngTypes().getEinheit()];
                break;
            }
            default: {
                value = "";
            }
        }
        return value;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void fireTableCellUpdated(int row, int column) {

        super.fireTableCellUpdated(row, column);
    }
}
