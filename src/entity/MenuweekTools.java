package entity;

import org.joda.time.LocalDate;
import tools.HTML;
import tools.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by tloehr on 17.10.14.
 */
public class MenuweekTools {


//    public static ArrayList<Menuweek> getAll(LocalDate week) {
//        ArrayList<Menuweek> list = new ArrayList<Menuweek>();
//
//        EntityManager em = Main.getEMF().createEntityManager();
//        Query queryMin = em.createQuery("SELECT t FROM Menuweek t WHERE t.week = :week ");
//
//        queryMin.setParameter("week", week.toDate());
//
//        list.addAll(queryMin.getResultList());
//
//        em.close();
//
//        return list;
//
//    }


    public static String getAsHTML(Menuweek menuweek, boolean groupAdditives) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. MMMM yyyy");

        String html = "<div align=\"center\">\n" +
                "<h1 id=\"fonth1\">Peter Meis<br/>" +
                "Handels- und Dienstleistungen GmbH & Co. KG<br/>\n" +
                "02292-9137-0</h1>\n" +
                "</div>\n";

        html += "<div align=\"center\">\n" +
                "<h1 id=\"fonth1\">-" + menuweek.getRecipefeature().getText() + "-</h1>\n" +
                "</div>\n";

        if (!menuweek.getCustomers().isEmpty()) {
            html += "<p id=\"fontsmall\">Kunden: ";

            String line = "";

            for (Customer customer : menuweek.getCustomers()) {
                line += customer + ", ";
            }
            html += Tools.left(line, line.length() - 2, "");
            html += "</p>\n";
        }

        HashSet<Additives> setAdditives = new HashSet<Additives>();
        HashSet<Allergene> setAllergenes = new HashSet<Allergene>();

        for (Menuweek2Menu menuweek2Menu : menuweek.getMenuweek2menus()) {
            html += "<h1 id=\"fonth1\">" + sdf.format(menuweek2Menu.getDate()) + "</h1>";
            html += MenuTools.getAsHTML(menuweek2Menu.getMenu(), setAdditives, setAllergenes, groupAdditives);
        }


        // print a keymap on the next page
        if (!setAdditives.isEmpty() || !setAllergenes.isEmpty()) {
            html += "<h1 id=\"fonth1\" style=\"page-break-before:always\">Legende</h1>";

            if (!setAllergenes.isEmpty()) {
                html += "<h2 id=\"fonth2\">Allergene</h2>";
                html += "<ul>";

                ArrayList<Allergene> list = new ArrayList<Allergene>(setAllergenes);
                Collections.sort(list);

                for (Allergene allergene : list) {
                    html += "<li><b>" + allergene.getKennung() + "</b> " + allergene.getText();
                }

                html += "</ul>";
            }

            if (!setAdditives.isEmpty()) {


                if (groupAdditives) {


                    html += "<h2 id=\"fonth2\">Zusatzstoff-Gruppen</h2>";
                    html += "<ul>";

                    ArrayList<Additives> list = new ArrayList<Additives>(setAdditives);
//                    Collections.sort(list);

                    ArrayList<Additivegroups> listAdditiveGroups = new ArrayList<Additivegroups>();

                    for (Additives additive : list) {
                        if (!listAdditiveGroups.contains(additive.getAdditivegroups())) {
                            listAdditiveGroups.add(additive.getAdditivegroups());
                        }
                    }

                    Collections.sort(listAdditiveGroups);


                    for (Additivegroups group : listAdditiveGroups) {
                        html += "<li><b>" + group.getId() + "</b> " + group.getGroupname() + "</li>";
                    }

                    html += "</ul>";

                } else {


                    html += "<h2 id=\"fonth2\">Zusatzstoffe</h2>";
                    html += "<ul>";

                    ArrayList<Additives> list = new ArrayList<Additives>(setAdditives);
                    Collections.sort(list);

                    for (Additives additive : list) {
                        html += "<li><b>" + additive.getSymbol() + "</b> " + additive.getName() + (additive.getText().isEmpty() ? "" : " <i>" + additive.getText() + "</i></li>");
                    }

                    html += "</ul>";

                }


            }
        }


        return html;
    }


    public static String getIngredientsAsHTML(Menuweek menuweek) {
        String html = HTML.h1("Zutaten und Vorräte");
        LocalDate startDay = new LocalDate(menuweek.getMenuweekall().getWeek());

        for (int day = 0; day <= 6; day++) {
            LocalDate thisDay = startDay.plusDays(day);
            Menuweek2Menu menuweek2Menu = menuweek.getMenuweek2menus().get(day);
            Menu menu = menuweek2Menu.getMenu();

            html += HTML.h2(thisDay.toString("EEEE, d. MMMM yyyy") + Tools.catchNull(menu.getText(), ", ", ""));

            html += MenuTools.getIngredientsAsHTML(menu);

        }


        return html;
    }


}
