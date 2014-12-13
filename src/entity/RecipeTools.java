package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 28.11.14.
 */
public class RecipeTools {


    public static String getIngTypesAsHTMLList(Recipes recipe) {

        if (recipe == null) return "";


        if (recipe.getIngTypes2Recipes().isEmpty()) return "";

        String html = "<h2>Zutaten</h2><ul>";

        for (Ingtypes2Recipes its : recipe.getIngTypes2Recipes()) {
            html += "<li>" + its.getIngType().getBezeichnung() + ": " + its.getAmount() + " " + IngTypesTools.EINHEIT[its.getIngType().getEinheit()] + "</li>";
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
