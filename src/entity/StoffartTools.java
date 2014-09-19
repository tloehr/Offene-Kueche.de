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
 * Date: 19.07.11
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class StoffartTools {


    public static TableCellRenderer getStorageRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return new DefaultTableCellRenderer().getTableCellRendererComponent(table, LagerTools.LAGERART[(Short) value], isSelected, hasFocus, row, column);
            }
        };
    }

    public static class MyTableCellEditor extends DefaultCellEditor {
        MyTableCellEditor() {
            super(new JComboBox<String>(new DefaultComboBoxModel<String>(LagerTools.LAGERART)));
        }

        @Override
        public Object getCellEditorValue() {
            return new Integer(((JComboBox<String>) editorComponent).getSelectedIndex()).shortValue();
        }
    }

    public static TableCellEditor getStorageEditor() {
        return new MyTableCellEditor();
    }

    public static Stoffart add(String text, short einheit, Warengruppe warengruppe) {
        Stoffart stoffart = null;

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createNamedQuery("Stoffart.findByBezeichnung");
            query.setParameter("bezeichnung", text.trim());
            if (query.getResultList().isEmpty()) {
                em.getTransaction().begin();
                stoffart = em.merge(new Stoffart(text.trim(), einheit, warengruppe));
                em.getTransaction().commit();
            } else {
                stoffart = (Stoffart) query.getResultList().get(0);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return stoffart;
    }

    public static void loadStoffarten(JComboBox cmb) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Stoffart.findAllSorted");
        try {
            java.util.List stoffarten = query.getResultList();
            cmb.setModel(tools.Tools.newComboboxModel(stoffarten));
        } catch (Exception e) { // nicht gefunden
            //
        } finally {
            em.close();
        }
    }


    public static long getNumOfProducts(Stoffart stoffart) {
        long num = 0;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT count(p) FROM Produkte p WHERE p.stoffart = :stoffart");
            query.setParameter("stoffart", stoffart);

            num = (Long) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.fatal(e);
        } finally {
            em.close();
        }
        return num;
    }
}
