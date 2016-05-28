package entity;

import Main.Main;
import org.joda.time.LocalDate;
import tools.HTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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


    public static String getIngTypesAndStocksAsHTML(Menuweekall menuweekall) {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. MMMM yyyy");

        String html = "<h1 id=\"fonth1\">Zutaten und Vorräte</h1>";


        HashSet<Additives> setAdditives = new HashSet<Additives>();
        HashSet<Allergene> setAllergenes = new HashSet<Allergene>();

        LocalDate startDay = new LocalDate(menuweekall.getWeek());

        String outerTable = "";

        for (int day = 0; day < 6; day++) {
            LocalDate thisDay = startDay.plusDays(day);
            outerTable += HTML.table_tr(HTML.table_th(HTML.fontface + thisDay.toString("EEEE, d. MMMM yyyy") + "</font>", 2));

            String midTable = "";
            for (Menuweek menuweek : menuweekall.getMenuweeks()) {
                Menuweek2Menu menuweek2Menu = menuweek.getMenuweek2menus().get(day);
                Menu menu = menuweek2Menu.getMenu();

                if (!menu.isEmpty()) {
                    outerTable += HTML.table_tr(HTML.table_th(menuweek2Menu.getMenu().getText() + " (" + menuweek.getRecipefeature().getText() + ")", 2));

                    for (int dish : MenuTools.DISHES) {
                        Recipes recipe = MenuTools.getDish(menu, dish);

                        if (recipe != null) {

                            outerTable += HTML.table_tr(HTML.table_td(recipe.getTitle(), 2));

                            outerTable += HTML.table_tr(
                                    HTML.table_td("Zutaten") +
                                            HTML.table_td("Vorräte")
                            );

                            String ingList = recipe.getIngTypes2Recipes().isEmpty() ? HTML.li(HTML.italic("leer...")) : "";
                            for (Ingtypes2Recipes it2r : recipe.getIngTypes2Recipes()) {
                                ingList += HTML.li(it2r.getIngType().getBezeichnung() + (it2r.getAmount().compareTo(BigDecimal.ZERO) > 0 ? ", " + it2r.getAmount() + " " + IngTypesTools.EINHEIT[it2r.getIngType().getEinheit()] : ""));
                            }

                            Set<Stock> stocks = MenuTools.getStocklist(menu, dish);
                            String stocklist = stocks.isEmpty() ? HTML.li(HTML.italic("leer...")) : "";
                            for (Stock stock : stocks) {
                                stocklist += HTML.li(stock.getId() + ": " + stock.getProdukt().getBezeichnung());
                            }

                            outerTable += HTML.table_tr(
                                    HTML.table_td(HTML.ul(ingList)) +
                                            HTML.table_td(HTML.ul(stocklist))
                            );


                        }
                    }
                }

//                outerTable += HTML.table_tr(
//                        HTML.table(
//                                midTable
//                                ,"1"
//                        )
//                );

            }
        }

        html += HTML.table(outerTable, "1");


        //        // print a keymap on the next page
        //        if (!setAdditives.isEmpty() || !setAllergenes.isEmpty()) {
        //            html += "<h1 id=\"fonth1\" style=\"page-break-before:always\">Legende</h1>";
        //
        //            if (!setAllergenes.isEmpty()) {
        //                html += "<h2 id=\"fonth2\">Allergene</h2>";
        //                html += "<ul>";
        //
        //                ArrayList<Allergene> list = new ArrayList<Allergene>(setAllergenes);
        //                Collections.sort(list);
        //
        //                for (Allergene allergene : list) {
        //                    html += "<li><b>" + allergene.getKennung() + "</b> " + allergene.getText();
        //                }
        //
        //                html += "</ul>";
        //            }
        //
        //            if (!setAdditives.isEmpty()) {
        //                html += "<h2 id=\"fonth2\">Zusatzstoffe</h2>";
        //                html += "<ul>";
        //
        //                ArrayList<Additives> list = new ArrayList<Additives>(setAdditives);
        //                Collections.sort(list);
        //
        //                for (Additives additive : list) {
        //                    html += "<li><b>" + additive.getSymbol() + "</b> " + additive.getName() + (additive.getText().isEmpty() ? "" : " <i>" + additive.getText() + "</i>");
        //                }
        //
        //                html += "</ul>";
        //            }
        //        }


        return html;
    }


    public static String getIngTypesAndStocksAsHTML2(Menuweekall menuweekall) {
        //        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. MMMM yyyy");

        String html = "";

        LocalDate startDay = new LocalDate(menuweekall.getWeek());


        for (int day = 0; day < 7; day++) {
            LocalDate thisDay = startDay.plusDays(day);

            String dayHTML = "";//HTML.h1(thisDay.toString("EEEE, d. MMMM yyyy"));


            boolean firstMenuThisDay = true; // just for the pagebreak
            for (Menuweek menuweek : menuweekall.getMenuweeks()) {
                Menuweek2Menu menuweek2Menu = menuweek.getMenuweek2menus().get(day);
                Menu menu = menuweek2Menu.getMenu();

                if (!menu.isEmpty()) {
                    if (!firstMenuThisDay) dayHTML += HTML.pagebreak();
                    firstMenuThisDay = false;
                    dayHTML += HTML.h1(menuweek.getRecipefeature().getText() + " (" + thisDay.toString("EEEE, d. MMMM yyyy") + ")");

                    for (int dish : MenuTools.DISHES) {
                        Recipes recipe = MenuTools.getDish(menu, dish);

                        if (recipe != null) {

                            dayHTML += HTML.h2(recipe.getTitle());
                            String list = RecipeTools.getSubRecipesAsHTML(MenuTools.getDish(menu, dish));

                            Set<Stock> stocks = MenuTools.getStocklist(menu, dish);
                            for (Stock stock : stocks) {
                                list += HTML.li(stock.getId() + ": " + stock.getProdukt().getBezeichnung());
                            }
                            dayHTML += HTML.ul(list);

                        }
                    }
                }

            }
            html += dayHTML;
            if (day < 6) html += HTML.pagebreak();
        }


        return html;
    }


}
