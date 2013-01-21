/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablemodels;

import beans.Buchung;
import entity.Lager;
import tools.Const;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 *
 * @author tloehr
 */
public class BuchungTableModel extends DefaultTableModel implements DeletableTableModel {

    ArrayList model = new ArrayList();

    public BuchungTableModel(ArrayList model) {
        this.model = model;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Buchung buchung = (Buchung) model.get(row);
        Object value;
        switch (column) {
            case 0: {
                value = buchung.getProdukt();
                break;
            }
            case 1: {
                String v;
                v = buchung.getFaktor() + "x";
                v += " " + buchung.getMenge() + " " + Const.EINHEIT[buchung.getProdukt().getEinheit()];
                value = v;
                break;
            }
            case 2: {
                value = buchung.getLager();
                //Main.Main.logger.debug(buchung.getLager().toString());
                break;
            }
            case 3: {
                value = "";
                break;
            }
            case 4: {
                value = buchung.getPrinter();
                break;
            }
            default: {
                value = "";
            }
        }
        return value;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        Class<?> myclass;
        switch (column) {
            case 2: {
                myclass = Lager.class;
                break;
            }
            case 4: {
                myclass = Integer.class;
                break;
            }
//            case 2: {
//                myclass = Boolean.class;
//                break;
//            }
            default: {
                myclass = String.class;
            }
        }
        return myclass;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column >= 2;
    }

    @Override
    public void removeRow(int row) {
        model.remove(row);
        fireTableRowsDeleted(row, row);
        //super.removeRow(row);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column == 2) { // Brauche ich nur für die ComboBox. Die Buttons helfen sich mit ihren Actionlistenern selbst.
            ((Buchung) model.get(row)).setLager((Lager) aValue);
        } else if (column == 2) { // Brauche ich nur für die ComboBox. Die Buttons helfen sich mit ihren Actionlistenern selbst.
            ((Buchung) model.get(row)).setLager((Lager) aValue);
        } else if (column == 4) {
            ((Buchung) model.get(row)).setPrinter((Integer) aValue);
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public String getColumnName(int column) {
        String name;
        switch (column) {
            case 0: {
                name = "Produkt";
                break;
            }
            case 1: {
                name = "Menge / Anzahl";
                break;
            }
            case 2: {
                name = "Lagerort";
                break;
            }
            case 3: {
                name = "Löschen";
                break;
            }
            case 4: {
                name = "Drucker";
                break;
            }
            default: {
                name = Integer.toString(column);
            }
        }
        return name;
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (model != null) {
            rowcount = model.size();
        }
        return rowcount;
    }
}
