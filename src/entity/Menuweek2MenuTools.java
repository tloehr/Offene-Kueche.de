package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by tloehr on 14.11.14.
 */
public class Menuweek2MenuTools {


    public static ListCellRenderer<Menuweek2Menu> getListCellRenderer() {
        return new ListCellRenderer<Menuweek2Menu>() {
            //            SimpleDateFormat week = new SimpleDateFormat("'KW'w");
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

            @Override
            public Component getListCellRendererComponent(JList<? extends Menuweek2Menu> list, Menuweek2Menu value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, df.format(value.getDate()) + ": " + value.getMenu().getText(), index, isSelected, cellHasFocus);
            }
        };
    }


    public static ArrayList<Menuweek2Menu> getAllLike(String text) {
        ArrayList<Menuweek2Menu> list = new ArrayList<Menuweek2Menu>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menuweek2Menu t WHERE t.menu.text IS NOT NULL AND t.menu.text <> '' AND t.menu.text like :text ORDER BY t.menu.text, t.date DESC ");
        queryMin.setParameter("text", "%" + text + "%");
        list.addAll(queryMin.getResultList());

        em.close();

        return list;
    }

}
