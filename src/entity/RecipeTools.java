package entity;

import Main.Main;
import tools.HTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 28.11.14.
 */
public class RecipeTools {


    public static String getIngTypesAsHTMLList(Recipes recipe, String title) {

        if (recipe == null) return "";


        if (recipe.getIngTypes2Recipes().isEmpty()) return "";

        String html = title+"<ul>";

        for (Ingtypes2Recipes its : recipe.getIngTypes2Recipes()) {
            html += "<li>" + its.getIngType().getBezeichnung() + ": " + its.getAmount() + " " + IngTypesTools.EINHEIT[its.getIngType().getEinheit()] + "</li>";
        }

        html += "</ul>";

        return html;

    }


    public static String getSubRecipesAsHTML(Recipes recipe, String title) {

        if (recipe == null) return "";


        if (recipe.getSubrecipes().isEmpty()) return "";

        String html = title + "<ul>";

        for (Recipes r : recipe.getSubrecipes()) {
            html += "<li>" + HTML.bold(r.getTitle());

            if (!r.getIngTypes2Recipes().isEmpty()) {
                html += "<ul>";
                for (Ingtypes2Recipes its : r.getIngTypes2Recipes()) {
                    html += "<li>" + its.getIngType().getBezeichnung() + ": " + its.getAmount() + " " + IngTypesTools.EINHEIT[its.getIngType().getEinheit()] + "</li>";
                }
                html += "</ul>";
            }
            html += "</li>";
        }


        html += "</ul>";

        return html;

    }

    public static ArrayList<Recipes> getAll() {
        ArrayList<Recipes> list = new ArrayList<Recipes>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT r FROM Recipes r ORDER BY r.title");
        try {
            list.addAll(query.getResultList());
        } catch (Exception e) { // nicht gefunden
            Main.fatal(e);
        } finally {
            em.close();
        }
        return list;
    }


    public static boolean contains(Recipes recipes, IngTypes ingType) {
        for (Ingtypes2Recipes ingtypes2Recipes : recipes.getIngTypes2Recipes()) {
            if (ingtypes2Recipes.getIngType().equals(ingType)) {
                return true;
            }
        }
        return false;
    }

//    public static String getAsHTML(Recipes recipe, ) {
//            String html = "";
//            HashSet<Allergene> setAllergenes = new HashSet<Allergene>();
//            HashSet<Additives> setAdditives = new HashSet<Additives>();
//
//            if (menu.getStarter() != null) {
//                html += "<font size=\"+1\">" + menu.getStarter().getTitle() + "<font>";
//                if (!menu.getStarterStocks().isEmpty()) {
//                    ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getStarterStocks());
//                    if (!allergenes.isEmpty()) {
//                        setAllergenes.addAll(allergenes);
//
//                    }
//                }
//            }
//
//            for (int weekday = 0; weekday < 7; weekday++) {
//                menu.g
//            }
//
//            if (stocks.isEmpty()) return "";
//
//
//            for (Stock stock : stocks) {
//                html += "<li>" + stock.getId() + " " + Tools.left(stock.getProdukt().getBezeichnung(), 30) + "</li>";
//            }
//
//            html += "</ul></html>";
//
//            return html;
//
//        }
}
