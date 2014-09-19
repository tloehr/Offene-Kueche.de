package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.11
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public class WarengruppeTools {


    public static TableCellRenderer getTableCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return new DefaultTableCellRenderer().getTableCellRendererComponent(table, ((Warengruppe) value).getBezeichnung(), isSelected, hasFocus, row, column);
            }
        };
    }

    public static class MyTableCellEditor extends DefaultCellEditor {
        MyTableCellEditor() {
            super(new JComboBox<Warengruppe>(new DefaultComboBoxModel<Warengruppe>(getAll())));
            ((JComboBox<Warengruppe>) editorComponent).setRenderer(new ListCellRenderer<Warengruppe>() {
                @Override
                public Component getListCellRendererComponent(JList<? extends Warengruppe> list, Warengruppe warengruppe, int index, boolean isSelected, boolean cellHasFocus) {
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, warengruppe.getBezeichnung(), index, isSelected, cellHasFocus);
                }
            });
        }
    }

    public static TableCellEditor getTableCellEditor() {
        return new MyTableCellEditor();
    }


    public static Warengruppe add(String text) {
        Warengruppe warengruppe = null;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createNamedQuery("Warengruppe.findByBezeichnung");
            query.setParameter("bezeichnung", text.trim());
            if (query.getResultList().isEmpty()) {
                em.getTransaction().begin();
                warengruppe = em.merge(new Warengruppe(text.trim()));
                em.getTransaction().commit();
            } else {
                warengruppe = (Warengruppe) query.getResultList().get(0);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return warengruppe;
    }


    public static long getNumOfProducts(Warengruppe warengruppe) {
        long num = 0;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT count(p) FROM Produkte p WHERE p.stoffart.warengruppe = :warengruppe");
            query.setParameter("warengruppe", warengruppe);

            num = (Long) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.fatal(e);
        } finally {
            em.close();
        }
        return num;
    }


    public static Warengruppe[] getAll() {
        ArrayList<Warengruppe> list = new ArrayList<Warengruppe>();
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung");
            list.addAll(query.getResultList());
        } catch (Exception e) { // nicht gefunden
            Main.fatal(e);
        } finally {
            em.close();
        }
        return list.toArray(new Warengruppe[]{});
    }
}
