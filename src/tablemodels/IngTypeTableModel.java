package tablemodels;

import Main.Main;
import desktop.FrmDesktop;
import entity.Stoffart;
import entity.Warengruppe;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.util.List;

/**
 * Created by tloehr on 19.09.14.
 */
public class IngTypeTableModel extends DefaultTableModel {

    public static final int COL_ID = 0;
    public static final int COL_BEZEICHNUNG = 1;
    public static final int COL_EINHEIT = 2;
    public static final int COL_LAGERART = 3;
    public static final int COL_WARENGRUPPE = 4;

    private List<Stoffart> data;
    private boolean editable = false;


    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<Stoffart> getData() {
        return data;
    }

    private DateFormat df;

    public IngTypeTableModel(List<Stoffart> data) {
        this.data = data;

        df = DateFormat.getDateInstance(DateFormat.DEFAULT);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            final Stoffart myStoffart = em.merge(data.get(row));

            switch (column) {

                case COL_BEZEICHNUNG: {
                    if (!aValue.toString().isEmpty()) {
                        myStoffart.setBezeichnung(aValue.toString());
                    }
                    break;
                }
                case COL_EINHEIT: {
                    myStoffart.setEinheit((Short) aValue);
                    break;
                }
                case COL_LAGERART: {
                    myStoffart.setLagerart((Short) aValue);
                    break;
                }
                case COL_WARENGRUPPE: {
                    myStoffart.setWarengruppe((Warengruppe) aValue);
                    break;
                }
                default: {
                    // nothing
                }
            }

            em.getTransaction().commit();
            data.set(row, myStoffart);
            fireTableCellUpdated(row, column);

        } catch (OptimisticLockException ole) {
            JOptionPane.showMessageDialog(Main.mainframe,
                    "Ein oder mehere Objekt(e) wurden in der Zwischenzeit verändert. Änderung wird nicht angenommen.",
                    "Zugriffskonflikt",
                    JOptionPane.WARNING_MESSAGE);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            data.clear();
            ((FrmDesktop) Main.getMainframe()).getTypesFrame().reload();

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
    public int getRowCount() {
        int rowcount = 0;

        if (data != null) {
            rowcount = data.size();
        }
        return rowcount;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class c;
        switch (columnIndex) {

            case COL_ID: {
                c = Long.class;
                break;
            }
            case COL_BEZEICHNUNG: {
                c = String.class;
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
            case COL_WARENGRUPPE: {
                c = Warengruppe.class;
                break;
            }
            default: {
                c = null;
            }

        }
        return c;
    }


    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        Stoffart stoffart = data.get(row);
        switch (column) {
            case COL_ID: {
                value = stoffart.getId();
                break;
            }
            case COL_BEZEICHNUNG: {
                value = stoffart.getBezeichnung();
                break;
            }
            case COL_EINHEIT: {
                value = stoffart.getEinheit();
                break;
            }
            case COL_LAGERART: {
                value = stoffart.getLagerart();
                break;
            }
            case COL_WARENGRUPPE: {
                value = stoffart.getWarengruppe();
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
        return column != COL_ID && editable;
    }

}
