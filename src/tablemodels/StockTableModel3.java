/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablemodels;

import Main.Main;
import entity.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Modell wird in FrmVorrat gebraucht.
 *
 * @author tloehr
 */
public class StockTableModel3 extends DefaultTableModel implements DeletableTableModel {

    public static final int COL_VORRAT_ID = 0;
    public static final int COL_BEZEICHNUNG = 1;
    public static final int COL_RESTMENGE = 2;
    public static final int COL_ALLADD = 3;
    //    public static final int COL_ADDITIVES = 4;
    public static final int COL_LAGER = 4;
    public static final int COL_LIEFERANT = 5;
    public static final int COL_INGTYPE = 6;
    public static final int COL_WARENGRUPPE = 7;
    public static final int COL_GTIN = 8;

    private List<Stock> data;
    private DecimalFormat decf;
    private Object[] colID = new Object[]{"VorratID", "Bezeichnung", "Menge", "Allergene, Zusatzstoffe", "Lager", "Lieferant", "Stoffart", "Warengruppe", "GTIN"};
    private DateFormat df;

    public StockTableModel3(List<Stock> data) {
        this.data = data;
        // Und wieder mal Torsten Horn: http://www.torsten-horn.de/techdocs/java-basics.htm
        decf = (DecimalFormat) DecimalFormat.getInstance();
        decf.applyPattern("#,###,##0.00");


        setColumnIdentifiers(colID);
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
    public void removeRow(int row) {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public List<Stock> getData() {
        return data;
    }

    public void remove(Stock stock) {
        if (!data.contains(stock)) return;
        removeRow(data.indexOf(stock));

    }

    public void add(Stock stock) {
        data.add(stock);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void update(Produkte produkt) {

        ArrayList<Stock> myStocks = new ArrayList<Stock>();

        for (Stock stock : data) {
            if (stock.getProdukt().equals(produkt)) {
                stock.setProdukt(produkt);
                myStocks.add(stock);
            }
        }

        for (Stock stock : myStocks) {
            update(stock);
        }

        myStocks.clear();
    }


    public void update(Stock stock) {
        if (!data.contains(stock)) {
            add(stock);
            return;
        }
        int row = data.indexOf(stock);
        data.set(row, stock);
        fireTableRowsUpdated(row, row);
    }

    public void update(List<Stock> update) {
        for (Stock stock : update) {
            if (data.contains(stock)) {
                update(stock);
            } else {
                add(stock);
            }
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class c;
        if (columnIndex == COL_VORRAT_ID) {
            c = Long.class;
        } else if (columnIndex == COL_INGTYPE) {
            c = IngTypes.class;
        } else if (columnIndex == COL_LAGER) {
            c = Lager.class;
        } else {
            c = String.class;
        }
        return c;
    }


    public Stock getStock(int row) {
        return data.get(row);
    }

//    public void replaceVorrat(int row, Vorrat vorrat){
//
//    }

    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        Stock stock = data.get(row);

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
            case COL_LAGER: {
                value = stock.getLager();
                break;
            }
            case COL_LIEFERANT: {
                value = stock.getLieferant();
                break;
            }
            case COL_GTIN: {
                value = tools.Tools.catchNull(stock.getProdukt().getGtin(), "--");
                break;
            }

            case COL_RESTMENGE: {
                value = decf.format(StockTools.getSumme(stock)) + " " + IngTypesTools.EINHEIT[stock.getProdukt().getIngTypes().getEinheit()];
                break;
            }
            case COL_INGTYPE: {
                value = stock.getProdukt().getIngTypes();
                break;
            }
            case COL_WARENGRUPPE: {
                value = stock.getProdukt().getIngTypes().getWarengruppe().getBezeichnung();
                break;
            }
            case COL_ALLADD: {
                value = (stock.getProdukt().getAllergenes().size() == 0 ? "" : "A." + Integer.toString(stock.getProdukt().getAllergenes().size())) + "  " +
                (stock.getProdukt().getAdditives().size() == 0 ? "" : "Z." + Integer.toString(stock.getProdukt().getAdditives().size()));

//                value = stock.getProdukt().getAllergenes().size() == 0 ? "" : "A." + Integer.toString(stock.getProdukt().getAllergenes().size());
                break;
            }
//            case COL_ADDITIVES: {
//                value = stock.getProdukt().getAdditives().size() == 0 ? "" : Integer.toString(stock.getProdukt().getAdditives().size());
//                break;
//            }
            default: {
                value = null;
            }
        }
        return value;
    }


    @Override
    public void setValueAt(Object aValue, int row, int column) {

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            final Stock stock = em.merge(data.get(row));
            em.lock(stock, LockModeType.OPTIMISTIC);

            switch (column) {

                case COL_INGTYPE: {
                    stock.getProdukt().setIngTypes((IngTypes) aValue);
                    break;
                }

                case COL_LAGER: {
                    stock.setLager((Lager) aValue);
                    break;
                }
                default: {
                    // nothing
                }
            }

            em.getTransaction().commit();

            if (column == COL_INGTYPE) {
                update(stock.getProdukt());
            } else {
                update(stock);
            }

        } catch (OptimisticLockException ole) {
            Main.warn(ole);

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            data.clear();
//            ((FrmDesktop) Main.getMainframe()).getProductsFrame().reload();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            Main.fatal(e);
        } finally {
            em.close();
        }


    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COL_INGTYPE || column == COL_LAGER;
    }


    @Override
    public void fireTableCellUpdated(int row, int column) {

        super.fireTableCellUpdated(row, column);
    }
}
