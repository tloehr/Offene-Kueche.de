package entity;

import Main.Main;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by tloehr on 11.11.14.
 */
public class MenuTools {


    public static ListCellRenderer<Menu> getListCellRenderer() {
        return new ListCellRenderer<Menu>() {
            //            SimpleDateFormat week = new SimpleDateFormat("'KW'w");
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

            @Override
            public Component getListCellRendererComponent(JList<? extends Menu> list, Menu value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, df.format(value.getDate()) + ": " + value.getText(), index, isSelected, cellHasFocus);
            }
        };
    }

    public static ArrayList<Menu> getAllLike(String text) {
        ArrayList<Menu> list = new ArrayList<Menu>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menu t WHERE t.text IS NOT NULL AND t.text <> '' AND t.text like :text ORDER BY t.text, t.date DESC ");
        queryMin.setParameter("text", "%" + text + "%");
        list.addAll(queryMin.getResultList());

        em.close();

        return list;
    }


    public static String getStocksAsHTMLList(Menu menu) {

        if (menu.getStocks().isEmpty()) return "";

        String html = "<html><ul>";

        for (Stock stock : menu.getStocks()) {
            html += "<li>" + stock.getId() + " " + Tools.left(stock.getProdukt().getBezeichnung(), 20) + "</li>";
        }

        html += "</ul></html>";

        return html;

    }

    public static String getPrettyString(Menu menu) {
        String text = "";

        if (menu.getStarter() != null) {
            text += menu.getStarter().getTitle() + ", ";
        }

        if (menu.getMaincourse() != null) {
            text += menu.getMaincourse().getTitle() + ", ";
        }

        if (menu.getSauce() != null) {
            text += menu.getSauce().getTitle() + ", ";
        }

        if (menu.getSideveggie() != null) {
            text += menu.getSideveggie().getTitle() + ", ";
        }

        if (menu.getSidedish() != null) {
            text += menu.getSidedish().getTitle() + ", ";
        }

        if (menu.getDessert() != null) {
            text += menu.getDessert().getTitle() + ", ";
        }

        if (!text.isEmpty()) {
            text = Tools.left(text, text.length() - 2, "");
        }


        return text;
    }

}
