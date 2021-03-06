package tablemodels;

import Main.Main;
import desktop.FrmDesktop;
import entity.IngTypes;
import entity.Produkte;
import entity.ProdukteTools;
import entity.Warengruppe;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.08.11
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public class ProdukteTableModel extends DefaultTableModel {
    private DateFormat df;
    public static final int COL_ID = 0;
    public static final int COL_BEZEICHNUNG = 1;
    public static final int COL_GTIN = 2;
    public static final int COL_PACKGROESSE = 3;
    public static final int COL_ALLADD = 4;
    //    public static final int COL_ADDITIVES = 5;
    public static final int COL_INGTYPE = 5;
    public static final int COL_EINHEIT = 6;
    public static final int COL_LAGERART = 7;
    public static final int COL_WARENGRUPPE = 8;
    private boolean editable;

    private Object[] colID = new Object[]{"Produkt Nr.", "Bezeichnung", "GTIN", "Packungsgröße", "Allergene, Zusatzstoffe", "Stoffart", "Einheit", "Lagerart", "Warengruppe"};


    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    private List<Produkte> data;

    public List getData() {
        return data;
    }

    public void setData(List<Produkte> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public ProdukteTableModel(List<Produkte> data, boolean editable) {
        this.data = data;
        this.editable = editable;
        setColumnIdentifiers(colID);
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
        if (!data.contains(produkt)) {
            add(produkt);
            return;
        }
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
            case COL_WARENGRUPPE: {
                c = Warengruppe.class;
                break;
            }
            case COL_EINHEIT: {
                c = Short.class;
                break;
            }
            case COL_LAGERART: {
                c = Short.class;
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
    public void setValueAt(Object aValue, int row, int column) {

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            final Produkte produkte = em.merge(data.get(row));

            ArrayList<Produkte> updateList = new ArrayList<Produkte>();

            em.lock(produkte, LockModeType.OPTIMISTIC);

            switch (column) {

                case COL_BEZEICHNUNG: {
                    if (!aValue.toString().isEmpty()) {
                        produkte.setBezeichnung(aValue.toString());
                        updateList.add(produkte);
                    }
                    break;
                }
                case COL_GTIN: {
                    if (ProdukteTools.isGTIN(aValue.toString())) {
                        produkte.setGtin(aValue.toString());
                        updateList.add(produkte);
                    }
                    break;
                }
                case COL_PACKGROESSE: {
                    try {
                        BigDecimal packGroesse = new BigDecimal(aValue.toString());
                        if (packGroesse.compareTo(BigDecimal.ZERO) > 0) {
                            produkte.setPackGroesse(packGroesse);
                            updateList.add(produkte);
                        }
                    } catch (NumberFormatException nfe) {

                    }
                    break;
                }
                case COL_INGTYPE: {
                    produkte.setIngTypes((IngTypes) aValue);
                    updateList.add(produkte);
                    break;
                }
                case COL_WARENGRUPPE: {
                    Warengruppe warengruppe = em.merge((Warengruppe) aValue);
                    IngTypes changedIngType = em.merge(produkte.getIngTypes());
                    changedIngType.setWarengruppe(warengruppe);
                    for (Produkte p : data) {
                        if (p.getIngTypes().equals(changedIngType)) { // equals compares only the IDs.
                            p.setIngTypes(changedIngType);
                            updateList.add(p);
                        }
                    }
                    break;
                }
                case COL_LAGERART: {
                    IngTypes changedIngType = em.merge(produkte.getIngTypes());
                    changedIngType.setLagerart((Short) aValue);
                    for (Produkte p : data) {
                        if (p.getIngTypes().equals(changedIngType)) { // equals compares only the IDs.
                            p.setIngTypes(changedIngType);
                            updateList.add(p);
                        }
                    }
                    break;
                }
                case COL_EINHEIT: {
                    IngTypes changedIngType = em.merge(produkte.getIngTypes());
                    changedIngType.setEinheit((Short) aValue);
                    for (Produkte p : data) {
                        if (p.getIngTypes().equals(changedIngType)) { // equals compares only the IDs.
                            p.setIngTypes(changedIngType);
                            updateList.add(p);
                        }
                    }
                    break;
                }
                default: {
                    // nothing
                }
            }

            em.getTransaction().commit();
            update(updateList);
            updateList.clear();

        } catch (OptimisticLockException ole) {
            Main.warn(ole);

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            data.clear();
            ((FrmDesktop) Main.getMainframe()).getProductsFrame().reload();
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
                value = produkte.getIngTypes().getLagerart();
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
                value = produkte.getIngTypes().getEinheit();
                break;
            }
            case COL_INGTYPE: {
                value = produkte.getIngTypes();
                break;
            }
            case COL_ALLADD: {
                value = (produkte.getAllergenes().size() == 0 ? "" : "A." + Integer.toString(produkte.getAllergenes().size())) + "  " +
                        (produkte.getAdditives().size() == 0 ? "" : "Z." + Integer.toString(produkte.getAdditives().size()));
                break;
            }
//            case COL_ALLERGENES: {
//                value = produkte.getAllergenes().size() == 0 ? "" : Integer.toString(produkte.getAllergenes().size());
//                break;
//            }
//            case COL_ADDITIVES: {
//                value = produkte.getAdditives().size() == 0 ? "" : Integer.toString(produkte.getAdditives().size());
//                break;
//            }
            case COL_WARENGRUPPE: {
                value = produkte.getIngTypes().getWarengruppe();
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
        Produkte produkte = data.get(row);
        return editable && (column == COL_BEZEICHNUNG || (!produkte.isLoseWare() && (column == COL_GTIN || column == COL_PACKGROESSE)) || column == COL_INGTYPE || column == COL_WARENGRUPPE || column == COL_EINHEIT || column == COL_LAGERART);
    }


}
