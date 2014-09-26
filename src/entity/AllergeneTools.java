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


    public static ArrayList<Allergene> getAll() {
        ArrayList<Allergene> list = new ArrayList<Allergene>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Allergene t ORDER BY t.kennung ASC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }

}
