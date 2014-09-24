package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.07.11
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class LagerTools {

    public static final String LAGERART[] = {"Gekühlt unter 7°C", "Gekühlt unter 4°C", "TK (-18°C)", "Trockenlager", "Normal, unter 20°C"};
    public static final short LAGERART_UNBEKANNT = 0; // Spezielle Art, die nur zur Markierung des "UNBEKANNT" Lagers verwendet wird.
    public static final short LAGERART_UNTER_7 = 1;
    public static final short LAGERART_UNTER_4 = 2;
    public static final short LAGERART_TK = 3;
    public static final short LAGERART_TROCKENLAGER = 4;
    public static final short LAGERART_NORMAL = 5;

    public static final String EINHEIT[] = {"kg", "liter", "Stück"};


    public static TableCellRenderer getEinheitTableCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return new DefaultTableCellRenderer().getTableCellRendererComponent(table, EINHEIT[(Short) value], isSelected, hasFocus, row, column);
            }
        };
    }

    public static class MyEinheitTableCellEditor extends DefaultCellEditor {
        MyEinheitTableCellEditor() {
            super(new JComboBox<String>(new DefaultComboBoxModel<String>(EINHEIT)));
            setClickCountToStart(2);
        }

        @Override
        public Object getCellEditorValue() {
            return new Integer(((JComboBox<String>) editorComponent).getSelectedIndex()).shortValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            ((JComboBox) editorComponent).setSelectedIndex((Short) value);
            return editorComponent;
        }
    }

    public static TableCellEditor getEinheitTableCellEditor() {
        return new MyEinheitTableCellEditor();
    }


    public static Lager add(String bezeichnung) {
        Lager lager = null;


        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createNamedQuery("Lager.findByBezeichnung");
            query.setParameter("bezeichnung", bezeichnung.trim());
            if (query.getResultList().isEmpty()) {
                em.getTransaction().begin();
                lager = em.merge(new Lager(bezeichnung, LAGERART_TROCKENLAGER, ""));
                em.getTransaction().commit();
            } else {
                lager = (Lager) query.getResultList().get(0);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return lager;
    }

    /**
     * Gibt genau das eine Lager zurück, dass als Unbekannt verwendet wird.
     * (Lagerart == 0)
     *
     * @return
     */
    public static Lager getUnbekannt() {
        Lager lager = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Lager.findByLagerart");
        query.setParameter("lagerart", LAGERART_UNBEKANNT);
        lager = (Lager) query.getSingleResult();
        em.close();
        return lager;
    }
}
