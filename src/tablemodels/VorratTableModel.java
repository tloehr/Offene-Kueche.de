/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablemodels;

import Main.Main;
import entity.LagerTools;
import entity.Vorrat;
import entity.VorratTools;
import tools.Const;

import javax.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Diese Modell wird in FrmVorrat gebraucht.
 *
 * @author tloehr
 */
public class VorratTableModel extends DefaultTableModel implements DeletableTableModel {

    public static final int COL_VORRAT_ID = 0;
    public static final int COL_BEZEICHNUNG = 1;
    public static final int COL_LAGER = 2;
    public static final int COL_LIEFERANT = 3;
    public static final int COL_GTIN = 4;
    public static final int COL_EINGANGSMENGE = 5;
    public static final int COL_RESTMENGE = 6;
    //public static final int COL_EINHEIT = 6;
    public static final int COL_STOFFART = 7;
    public static final int COL_WARENGRUPPE = 8;
    public static final int COL_EINGANG = 9;
    public static final int COL_ANBRUCH = 10;
    public static final int COL_AUSGANG = 11;

    private List data;
    private DecimalFormat decf;

    private DateFormat df;

    public VorratTableModel(List data, Object[] columnNames) {
        this.data = data;
        // Und wieder mal Torsten Horn: http://www.torsten-horn.de/techdocs/java-basics.htm
        decf = (DecimalFormat) DecimalFormat.getInstance();
        decf.applyPattern("#,###,##0.00");


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

    @Override
    public void removeRow(int row) {
        Vorrat vorrat = (Vorrat) ((Object[]) data.get(row))[0];
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(vorrat);
            data.remove(row);
            em.getTransaction().commit();
            fireTableRowsDeleted(row, row);
        } catch (Exception e) {
            Main.logger.fatal(e.getMessage(), e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public Vorrat getVorrat(int row) {
        return (Vorrat) ((Object[]) data.get(row))[0];
    }

//    public void replaceVorrat(int row, Vorrat vorrat){
//
//    }

    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        Vorrat vorrat = (Vorrat) ((Object[]) data.get(row))[0];
        BigDecimal menge = (BigDecimal) ((Object[]) data.get(row))[1];
        switch (column) {
            case COL_VORRAT_ID: {
                value = vorrat.getId();
                break;
            }
            case COL_BEZEICHNUNG: {
                //Main.debug(vorrat);
                value = vorrat.getProdukt().getBezeichnung();
                break;
            }
            case COL_LAGER: {
                value = vorrat.getLager();
                break;
            }
            case COL_LIEFERANT: {
                value = vorrat.getLieferant();
                break;
            }
            case COL_GTIN: {
                value = tools.Tools.catchNull(vorrat.getProdukt().getGtin(), "--");
                break;
            }
            case COL_EINGANGSMENGE: {
                value = decf.format(VorratTools.getEingangsbestand(vorrat)) + " " + LagerTools.EINHEIT[vorrat.getProdukt().getIngTypes().getEinheit()];
                break;
            }
            case COL_RESTMENGE: {
                value = decf.format(menge) + " " + LagerTools.EINHEIT[vorrat.getProdukt().getIngTypes().getEinheit()];
                break;
            }
            case COL_STOFFART: {
                value = vorrat.getProdukt().getIngTypes().getBezeichnung();
                break;
            }
            case COL_WARENGRUPPE: {
                value = vorrat.getProdukt().getIngTypes().getWarengruppe().getBezeichnung();
                break;
            }
            case COL_EINGANG: {
                value = df.format(vorrat.getEingang());
                break;
            }
            case COL_ANBRUCH: {
                if (vorrat.getAnbruch().equals(Const.DATE_BIS_AUF_WEITERES)) {
                    value = "--";
                } else {
                    value = df.format(vorrat.getAnbruch());
                }
                break;
            }
            case COL_AUSGANG: {
                if (vorrat.getAusgang().equals(Const.DATE_BIS_AUF_WEITERES)) {
                    value = "--";
                } else {
                    value = df.format(vorrat.getAusgang());
                }
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

//    @Override
//    public void setValueAt(Object aValue, int row, int column) {
//        if (column == COL_BTN_PRINTER) {
//            printer.set(row, aValue);
//        } else if (column == COL_LAGER) {
//            Vorrat vorrat = (Vorrat) ((Object[]) data.get(row))[0];
//            vorrat.setLager((Lager) aValue);
//
//            Main.em.getTransaction().begin();
//            try {
//                Main.em.merge(vorrat);
//                Main.em.getTransaction().commit();
//            } catch (Exception e) {
//                Main.Main.logger.fatal(e.getMessage(), e);
//                Main.em.getTransaction().rollback();
//            }
//        } else if (column == COL_LIEFERANT) {
//            Vorrat vorrat = (Vorrat) ((Object[]) data.get(row))[0];
//            vorrat.setLieferant((Lieferanten) aValue);
//
//            Main.em.getTransaction().begin();
//            try {
//                Main.em.merge(vorrat);
//                Main.em.getTransaction().commit();
//            } catch (Exception e) {
//                Main.Main.logger.fatal(e.getMessage(), e);
//                Main.em.getTransaction().rollback();
//            }
//        }
//        fireTableCellUpdated(row, column);
//    }

    @Override
    public void fireTableCellUpdated(int row, int column) {

        super.fireTableCellUpdated(row, column);
    }
}
