package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 06.11.14.
 */
public class CustomerTools {


    public static ListCellRenderer<Customer> getListCellRenderer() {
        return new ListCellRenderer<Customer>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Customer> list, Customer value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, value.getAbbrev() + " - " + value.getOrgname(), index, isSelected, cellHasFocus);
            }
        };
    }

    public static ArrayList<Customer> getAll() {
        ArrayList<Customer> list = new ArrayList<Customer>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT c FROM Customer c ORDER BY c.abbrev ASC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }

}
