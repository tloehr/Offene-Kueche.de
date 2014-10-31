package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 24.09.14.
 */
public class AllergeneTools {

    public static ListCellRenderer<Allergene> getListCellRenderer() {
        return new ListCellRenderer<Allergene>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Allergene> list, Allergene value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, value.getKennung() + " " + value.getText(), index, isSelected, cellHasFocus);
            }
        };
    }


//    public static class MyTableCellEditor implements TableCellEditor {
//
//
//
//
//        MyTableCellEditor() {
////            super(new JComboBox<IngTypes>(new DefaultComboBoxModel<IngTypes>(getAll().toArray(new IngTypes[]{}))));
////            setClickCountToStart(2);
////            ((JComboBox<IngTypes>) editorComponent).setRenderer(new ListCellRenderer<IngTypes>() {
////                @Override
////                public Component getListCellRendererComponent(JList<? extends IngTypes> list, IngTypes ingType, int index, boolean isSelected, boolean cellHasFocus) {
////                    return new DefaultListCellRenderer().getListCellRendererComponent(list, ingType.getBezeichnung(), index, isSelected, cellHasFocus);
////                }
////            });
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//            ((JComboBox) editorComponent).setSelectedItem(value);
//            return editorComponent;
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//            return null;
//        }
//
//        @Override
//        public boolean isCellEditable(EventObject anEvent) {
//            return false;
//        }
//
//        @Override
//        public boolean shouldSelectCell(EventObject anEvent) {
//            return false;
//        }
//
//        @Override
//        public boolean stopCellEditing() {
//            return false;
//        }
//
//        @Override
//        public void cancelCellEditing() {
//
//        }
//
//        @Override
//        public void addCellEditorListener(CellEditorListener l) {
//
//        }
//
//        @Override
//        public void removeCellEditorListener(CellEditorListener l) {
//
//        }
//    }
//
//    public static TableCellEditor getTableCellEditor() {
//        return new MyTableCellEditor();
//    }


    public static ArrayList<Allergene> getAll() {
        ArrayList<Allergene> list = new ArrayList<Allergene>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Allergene t ORDER BY t.kennung ASC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }

}
