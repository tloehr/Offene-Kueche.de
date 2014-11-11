package entity;

import Main.Main;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 08.11.14.
 */
public class MenuweekallTools {


    public static ListCellRenderer<Menuweekall> getListCellRenderer() {
           return new ListCellRenderer<Menuweekall>() {

               @Override
               public Component getListCellRendererComponent(JList<? extends Menuweekall> list, Menuweekall value, int index, boolean isSelected, boolean cellHasFocus) {
                   return new DefaultListCellRenderer().getListCellRendererComponent(list, value.)

                           datum...
               }

               @Override
               public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                   return new DefaultTableCellRenderer().getTableCellRendererComponent(table, IngTypesTools.EINHEIT[(Short) value], isSelected, hasFocus, row, column);
               }
           };
       }

    public static ArrayList<Menuweekall>  getAll() {
        ArrayList<Menuweekall> list = new ArrayList<Menuweekall>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menuweekall t ORDER BY t.week ASC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;
    }

    public static Menuweekall get(LocalDate week) {
        ArrayList<Menuweekall> list = new ArrayList<Menuweekall>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menuweekall t WHERE t.week = :week ");

        queryMin.setParameter("week", week.toDate());

        list.addAll(queryMin.getResultList());

        em.close();

        return list.isEmpty() ? null : list.get(0);

    }

}
