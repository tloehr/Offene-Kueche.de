/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablemodels;

import Main.Main;
import entity.Buchungen;
import entity.BuchungenTools;
import entity.Stock;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class BuchungenTableModel extends AbstractTableModel implements DeletableTableModel {

    private List data, header;
    private DateFormat df;
    private boolean newRowMode = false;
    public static final int COL_DATUM = 0;
    public static final int COL_TEXT = 1;
    public static final int COL_MENGE = 2;
    public static final int COL_MA = 3;

    public BuchungenTableModel(List data, Object[] columnNames) {
        this.data = data;
        this.header = new ArrayList(columnNames.length);
        this.header.addAll(Arrays.asList(columnNames));
        df = DateFormat.getDateInstance(DateFormat.DEFAULT);

    }

    public int getColumnCount() {
        return header.size();
    }

    @Override
    public String getColumnName(int column) {
        return header.get(column).toString();
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
    public Object getValueAt(int row, int column) {
        Object value;
        Buchungen buchung = (Buchungen) data.get(row);
        switch (column) {
            case COL_DATUM: {
                value = df.format(buchung.getDatum());
                break;
            }
            case COL_TEXT: {
                value = buchung.getText();
                break;
            }
            case COL_MENGE: {
                value = Tools.roundScale2(buchung.getMenge().doubleValue());
                break;
            }
            case COL_MA: {
                value = buchung.getMitarbeiter().getUsername();
                break;
            }
            default: {
                value = "";
            }
        }
        return value;
    }

    public void removeRow(int row) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            //Main.Main.logger.debug("removeRow: "+row);
            Buchungen buchung = (Buchungen) data.get(row);

            em.remove(buchung);
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

    @Override
    public boolean isCellEditable(int row, int column) {
        Buchungen buchung = (Buchungen) data.get(row);
        boolean cellEditable = buchung.getStatus() == BuchungenTools.BUCHEN_MANUELLE_KORREKTUR
                && column != COL_MA;

        return cellEditable;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Buchungen buchung = (Buchungen) data.get(row);
        switch (column) {
            case COL_DATUM: {
                try {
                    Date date = new Date(Tools.erkenneDatum(aValue.toString()).getTimeInMillis());
                    buchung.setDatum(date);
                } catch (NumberFormatException e) {
                    aValue = df.format(buchung.getDatum());
                }
                break;
            }
            case COL_TEXT: {
                buchung.setText(aValue.toString());
                break;
            }
            case COL_MENGE: {
                try {
                    double dbl = Double.parseDouble(aValue.toString().replaceAll(",", "\\."));
                    buchung.setMenge(new BigDecimal(dbl));
                } catch (NumberFormatException e) {
                    aValue = buchung.getMenge();
                }
                break;
            }
            default: {
            }
        }
        if (!newRowMode) {
            saveRow(row);
            fireTableCellUpdated(row, column);
        }
    }

    public void addEmptyRow() {
        Buchungen buchungen = new Buchungen(BigDecimal.ZERO, new Date());
        buchungen.setMitarbeiter(Main.getCurrentUser());
        buchungen.setText("Manuelle Buchung");
        // Die gibt es immer
        Stock stock = ((Buchungen) data.get(0)).getStock();
        buchungen.setStock(stock);
        data.add(buchungen);
        newRowMode = true;
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void saveRow(int row) {
        EntityManager em = Main.getEMF().createEntityManager();

        try {
            em.getTransaction().begin();
            Buchungen buchung = (Buchungen) data.get(row);
            if (buchung.getId() > 0) {
                em.persist(buchung);
                newRowMode = false;
            } else {
                em.merge(buchung);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            Main.logger.fatal(e.getMessage(), e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void cancelNewRow() {
        if (newRowMode) {
            data.remove(getRowCount() - 1);
            newRowMode = false;
            fireTableRowsDeleted(getRowCount() - 1, getRowCount() - 1);
        }
    }
}
