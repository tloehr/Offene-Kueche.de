package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 25.09.14.
 */
public class AdditivesTools {

    public static ListCellRenderer<Additives> getListCellRenderer() {
        return new ListCellRenderer<Additives>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Additives> list, Additives value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, value.getSymbol() + " (" + value.getAdditivegroups().getId() + ") " + value.getName(), index, isSelected, cellHasFocus);
            }
        };
    }


    public static ArrayList<Additives> getAll() {
        ArrayList<Additives> list = new ArrayList<Additives>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Additives t ORDER BY t.symbol ASC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;
    }

}
