package tablemodels;

import entity.IngTypesTools;
import entity.LagerTools;
import entity.Produkte;
import tools.Tools;

import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.08.11
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public class ProdukteTableModel extends DefaultTableModel {

    public static final int COL_ID = 0;
    public static final int COL_BEZEICHNUNG = 1;
    public static final int COL_LAGERART = 2;
    public static final int COL_GTIN = 3;
    public static final int COL_PACKGROESSE = 4;
    public static final int COL_ZUSATZSTOFFE = 5;
    public static final int COL_EINHEIT = 6;
    public static final int COL_STOFFART = 7;
    public static final int COL_WARENGRUPPE = 8;

    private List<Produkte> data;

    public List getData() {
        return data;
    }

    public void setData(List<Produkte> data) {
        this.data = data;
        fireTableDataChanged();
    }

    private DateFormat df;

    public ProdukteTableModel(List<Produkte> data, Object[] columnNames) {
        this.data = data;
        setColumnIdentifiers(columnNames);
        df = DateFormat.getDateInstance(DateFormat.DEFAULT);
    }


    @Override
    public void removeRow(int row) {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void remove(Produkte produkt) {
        if (!data.contains(produkt)) return;
        removeRow(data.indexOf(produkt));

    }

    public void add(Produkte produkt) {
        data.add(produkt);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void update(Produkte produkt) {
        if (!data.contains(produkt)) return;
        int row = data.indexOf(produkt);
        data.set(row, produkt);
        fireTableRowsUpdated(row, row);
    }


    public void update(List<Produkte> update) {
        for (Produkte prod : update) {
            if (data.contains(prod)) {
                update(prod);
            } else {
                add(prod);
            }
        }
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
        switch (columnIndex) {
            case COL_ID: {
                c = Long.class;
                break;
            }
            default: {
                c = String.class;
            }

        }
        return c;
    }

    public Produkte getProdukt(int row) {
        return data.get(row);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        Produkte produkte = data.get(row);
        switch (column) {
            case COL_ID: {
                value = produkte.getId();
                break;
            }
            case COL_BEZEICHNUNG: {
                value = produkte.getBezeichnung();
                break;
            }
            case COL_LAGERART: {
                value = LagerTools.LAGERART[produkte.getIngTypes().getLagerart()];
                break;
            }
            case COL_GTIN: {
                value = Tools.catchNull(produkte.getGtin(), "[lose Ware]");
                break;
            }
            case COL_PACKGROESSE: {
                value = Tools.catchNull(produkte.getPackGroesse(), "--");
                break;
            }
            case COL_EINHEIT: {
                value = IngTypesTools.EINHEIT[produkte.getIngTypes().getEinheit()];
                break;
            }
            case COL_STOFFART: {
                value = produkte.getIngTypes().getBezeichnung();
                break;
            }
            case COL_ZUSATZSTOFFE: {
                value = "A" + produkte.getAllergenes().size() + "/Z" + produkte.getAdditives().size();
                break;
            }
            case COL_WARENGRUPPE: {
                value = produkte.getIngTypes().getWarengruppe().getBezeichnung();
                break;
            }
            default: {
                value = null;
            }
        }
        return value;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
