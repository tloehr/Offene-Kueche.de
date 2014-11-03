package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
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
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class IngTypesTools {


    public static final String[] EINHEIT = {"kg", "liter", "Stück"};

//    public static TableCellRenderer getStorageRenderer() {
//        return new TableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                return new DefaultTableCellRenderer().getTableCellRendererComponent(table, LagerTools.LAGERART[(Short) value], isSelected, hasFocus, row, column);
//            }
//        };
//    }
//
//    public static class MyTableCellEditor extends DefaultCellEditor {
//        MyTableCellEditor() {
//            super(new JComboBox<String>(new DefaultComboBoxModel<String>(LagerTools.LAGERART)));
//            setClickCountToStart(2);
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//            return new Integer(((JComboBox<String>) editorComponent).getSelectedIndex()).shortValue();
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//            ((JComboBox) editorComponent).setSelectedIndex((Short) value);
//            return editorComponent;
//        }
//    }
//


    public static TableCellRenderer getTableCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return new DefaultTableCellRenderer().getTableCellRendererComponent(table, ((IngTypes) value).getBezeichnung(), isSelected, hasFocus, row, column);
            }
        };
    }

    public static class MyTableCellEditor extends DefaultCellEditor {
        MyTableCellEditor() {
            super(new JComboBox<IngTypes>(new DefaultComboBoxModel<IngTypes>(getAll().toArray(new IngTypes[]{}))));
            setClickCountToStart(2);
            ((JComboBox<IngTypes>) editorComponent).setRenderer(new ListCellRenderer<IngTypes>() {
                @Override
                public Component getListCellRendererComponent(JList<? extends IngTypes> list, IngTypes ingType, int index, boolean isSelected, boolean cellHasFocus) {
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, ingType.getBezeichnung(), index, isSelected, cellHasFocus);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            ((JComboBox) editorComponent).setSelectedItem(value);
            return editorComponent;
        }
    }

    public static TableCellEditor getTableCellEditor() {
        return new MyTableCellEditor();
    }

//    public static TableCellEditor getStorageEditor() {
//        return new MyTableCellEditor();
//    }

    public static IngTypes add(String text, short einheit, short storagetype, Warengruppe warengruppe) {
        IngTypes ingTypes = null;

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT s FROM IngTypes s WHERE s.bezeichnung = :bezeichnung");
            query.setParameter("bezeichnung", text.trim());
            if (query.getResultList().isEmpty()) {
                em.getTransaction().begin();
                ingTypes = em.merge(new IngTypes(text.trim(), einheit, storagetype, warengruppe));
                em.getTransaction().commit();
            } else {
                ingTypes = (IngTypes) query.getResultList().get(0);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return ingTypes;
    }

    public static ArrayList<IngTypes> getAll() {
        ArrayList<IngTypes> list = new ArrayList<IngTypes>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT s FROM IngTypes s ORDER BY s.bezeichnung");
        try {
            list.addAll(query.getResultList());
        } catch (Exception e) { // nicht gefunden
            Main.fatal(e);
        } finally {
            em.close();
        }
        return list;
    }

    public static void loadInto(JComboBox cmb) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT s FROM IngTypes s ORDER BY s.bezeichnung");
        try {
            java.util.List stoffarten = query.getResultList();
            cmb.setModel(tools.Tools.newComboboxModel(stoffarten));
        } catch (Exception e) { // nicht gefunden
            //
        } finally {
            em.close();
        }
    }


    public static IngTypes getFirstType() {
        IngTypes result = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT s FROM IngTypes s ORDER BY s.bezeichnung");
        query.setMaxResults(1);

        try {
            result = (IngTypes) query.getResultList().get(0);

        } catch (Exception e) { // nicht gefunden
            result = null;
        } finally {
            em.close();
        }

        return result;
    }


    public static long getNumOfProducts(IngTypes ingTypes) {
        long num = 0;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT count(p) FROM Produkte p WHERE p.ingTypes = :ingTypes");
            query.setParameter("ingTypes", ingTypes);

            num = (Long) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.fatal(e);
        } finally {
            em.close();
        }
        return num;
    }


    public static boolean mergeUs(ArrayList<IngTypes> listTypes2Merge, IngTypes target) {

        boolean success = false;

        listTypes2Merge.remove(target);

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            IngTypes myTarget = em.merge(target);
            em.lock(myTarget, LockModeType.OPTIMISTIC);
            for (IngTypes t : listTypes2Merge) {
                IngTypes oldType = em.merge(t);
                em.lock(oldType, LockModeType.OPTIMISTIC);

                for (Additives additive : oldType.getAdditives()) {
                    myTarget.getAdditives().add(em.merge(additive));
                }

                for (Allergene allergene : oldType.getAllergenes()) {
                    myTarget.getAllergenes().add(em.merge(allergene));
                }

                for (Produkte product : oldType.getProdukteCollection()) {
                    Produkte myProduct = em.merge(product);
                    em.lock(myProduct, LockModeType.OPTIMISTIC);
                    myProduct.setIngTypes(myTarget);
                }

                oldType.getAdditives().clear();
                oldType.getAllergenes().clear();
                oldType.getProdukteCollection().clear();

                em.remove(oldType);
            }
            em.getTransaction().commit();
            success = true;


        } catch (OptimisticLockException ole) {
            Main.warn(ole);
            em.getTransaction().rollback();
        } catch (Exception e) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
        }
        return success;
    }
}
