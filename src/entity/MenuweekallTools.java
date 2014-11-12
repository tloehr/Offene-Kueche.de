package entity;

import Main.Main;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by tloehr on 08.11.14.
 */
public class MenuweekallTools {

    public static ListCellRenderer<Menuweekall> getListCellRenderer() {
        return new ListCellRenderer<Menuweekall>() {
            SimpleDateFormat week = new SimpleDateFormat("'KW'w");
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

            @Override
            public Component getListCellRendererComponent(JList<? extends Menuweekall> list, Menuweekall value, int index, boolean isSelected, boolean cellHasFocus) {
                String text = week.format(value.getWeek()) + " " + df.format(value.getWeek()) + " -> " + df.format(new LocalDate(value.getWeek()).dayOfWeek().withMaximumValue().toDate());
                return new DefaultListCellRenderer().getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        };
    }

    public static ArrayList<Menuweekall> getAll() {
        ArrayList<Menuweekall> list = new ArrayList<Menuweekall>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menuweekall t ORDER BY t.week DESC ");

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
