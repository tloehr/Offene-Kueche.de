package entity;

import tools.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tloehr on 11.11.14.
 */
public class MenuTools {

    public static final int STARTER = 0;
    public static final int MAIN = 1;
    public static final int SAUCE = 2;
    public static final int VEGGIE = 3;
    public static final int SIDEDISH = 4;
    public static final int DESSERT = 5;


    public static String getStocksAsHTMLList(Set<Stock> stocks) {

        if (stocks.isEmpty()) return "";

        String html = "<h2>Vorräte</h2><ul>";

        for (Stock stock : stocks) {
            html += "<li>" + stock.getId() + " " + Tools.left(stock.getProdukt().getBezeichnung(), 30) + "</li>";
        }

        html += "</ul>";

        return html;

    }


    private static String getAllergeneLine(ArrayList<Allergene> allergenes) {
        String line = "";
        for (Allergene allergene : allergenes) {
            line += allergene.getKennung() + " ";
        }
        return line;
    }

    private static String getAdditiveLine(ArrayList<Additives> additives) {
        String line = "";
        for (Additives additive : additives) {
            line += additive.getSymbol() + " ";
        }
        return line;
    }


    public static String getAsHTML(Menu menu, HashSet<Additives> setGlobalAdditives, HashSet<Allergene> setGlobalAllergenes) {

        if (menu.isEmpty()) {
            return "<i>nichts geplant</i>";
        }

        String html = "";
        HashSet<Allergene> setAllergenes = new HashSet<Allergene>();
        HashSet<Additives> setAdditives = new HashSet<Additives>();

        if (menu.getStarter() != null) {

            if (!html.isEmpty()) html += ", ";

            html += "<font size=\"+1\">" + menu.getStarter().getTitle() + "<font>";
            if (!menu.getStarterStocks().isEmpty()) {
                ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getStarterStocks());
                if (!allergenes.isEmpty()) {
                    setAllergenes.addAll(allergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(allergenes) + "</sub>";
                }

                ArrayList<Additives> additives = StockTools.getAdditives(menu.getStarterStocks());
                if (!additives.isEmpty()) {
                    setAdditives.addAll(additives);
                    html += "<sub id=\"fontsmall\">" + getAdditiveLine(additives) + "</sub>\n";
                }
            }
        }

        if (menu.getMaincourse() != null) {
            if (!html.isEmpty()) html += ", ";
            html += "<font size=\"+1\">" + menu.getMaincourse().getTitle() + "<font>";
            if (!menu.getMainStocks().isEmpty()) {
                ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getMainStocks());
                if (!allergenes.isEmpty()) {
                    setAllergenes.addAll(allergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(allergenes) + "</sub>";
                }
                ArrayList<Additives> additives = StockTools.getAdditives(menu.getMainStocks());
                if (!additives.isEmpty()) {
                    setAdditives.addAll(additives);
                    html += "<sub id=\"fontsmall\">" + getAdditiveLine(additives) + "</sub>\n";
                }
            }
        }

        if (menu.getSauce() != null) {
            if (!html.isEmpty()) html += ", ";
            html += "<font size=\"+1\">" + menu.getSauce().getTitle() + "<font>";
            if (!menu.getSauceStocks().isEmpty()) {
                ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getSauceStocks());
                if (!allergenes.isEmpty()) {
                    setAllergenes.addAll(allergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(allergenes) + "</sub>";
                }
                ArrayList<Additives> additives = StockTools.getAdditives(menu.getSauceStocks());
                if (!additives.isEmpty()) {
                    setAdditives.addAll(additives);
                    html += "<sub id=\"fontsmall\">" + getAdditiveLine(additives) + "</sub>\n";
                }
            }
        }

        if (menu.getSideveggie() != null) {
            if (!html.isEmpty()) html += ", ";
            html += "<font size=\"+1\">" + menu.getSideveggie().getTitle() + "<font>";
            if (!menu.getSideveggieStocks().isEmpty()) {
                ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getSideveggieStocks());
                if (!allergenes.isEmpty()) {
                    setAllergenes.addAll(allergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(allergenes) + "</sub>";
                }
                ArrayList<Additives> additives = StockTools.getAdditives(menu.getSideveggieStocks());
                if (!additives.isEmpty()) {
                    setAdditives.addAll(additives);
                    html += "<sub id=\"fontsmall\">" + getAdditiveLine(additives) + "</sub>\n";
                }
            }
        }

        if (menu.getSidedish() != null) {
            if (!html.isEmpty()) html += ", ";
            html += "<font size=\"+1\">" + menu.getSidedish().getTitle() + "<font>";
            if (!menu.getSidedishStocks().isEmpty()) {
                ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getSidedishStocks());
                if (!allergenes.isEmpty()) {
                    setAllergenes.addAll(allergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(allergenes) + "</sub>";
                }
                ArrayList<Additives> additives = StockTools.getAdditives(menu.getSidedishStocks());
                if (!additives.isEmpty()) {
                    setAdditives.addAll(additives);
                    html += "<sub id=\"fontsmall\">" + getAdditiveLine(additives) + "</sub>\n";
                }
            }
        }

        if (menu.getDessert() != null) {
            if (!html.isEmpty()) html += ", ";
            html += "<font size=\"+1\">" + menu.getDessert().getTitle() + "<font>";
            if (!menu.getDessertStocks().isEmpty()) {
                ArrayList<Allergene> allergenes = StockTools.getAllergenes(menu.getDessertStocks());
                if (!allergenes.isEmpty()) {
                    setAllergenes.addAll(allergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(allergenes) + "</sub>";
                }
                ArrayList<Additives> additives = StockTools.getAdditives(menu.getDessertStocks());
                if (!additives.isEmpty()) {
                    setAdditives.addAll(additives);
                    html += "<sub id=\"fontsmall\">" + getAdditiveLine(additives) + "</sub>\n";
                }
            }
        }


        if (!setAllergenes.isEmpty()) {
            html += "<br/>Allergene: " + getAllergeneLine(new ArrayList<Allergene>(setAllergenes)) + "\n";
            setGlobalAllergenes.addAll(setAllergenes);
        }

        if (!setAdditives.isEmpty()) {
            html += (setAllergenes.isEmpty() ? "" : " // ") + "Zusatzstoffe: " + getAdditiveLine(new ArrayList<Additives>(setAdditives)) + "\n";
            setGlobalAdditives.addAll(setAdditives);
        }


        return html;

    }


    public static Menu setDish(Menu menu, Recipes recipes, int index) {
        switch (index) {
            case STARTER: {
                menu.setStarter(recipes);
                break;
            }
            case MAIN: {
                menu.setMaincourse(recipes);
                break;
            }
            case SAUCE: {
                menu.setSauce(recipes);
                break;
            }
            case VEGGIE: {
                menu.setSideveggie(recipes);
                break;
            }
            case SIDEDISH: {
                menu.setSidedish(recipes);
                break;
            }
            case DESSERT: {
                menu.setDessert(recipes);
                break;
            }
            default: {

            }
        }
        return menu;
    }

    public static Recipes getDish(Menu menu, int index) {
        Recipes recipe = null;
        switch (index) {
            case STARTER: {
                recipe = menu.getStarter();
                break;
            }
            case MAIN: {
                recipe = menu.getMaincourse();
                break;
            }
            case SAUCE: {
                recipe = menu.getSauce();
                break;
            }
            case VEGGIE: {
                recipe = menu.getSideveggie();
                break;
            }
            case SIDEDISH: {
                recipe = menu.getSidedish();
                break;
            }
            case DESSERT: {
                recipe = menu.getDessert();
                break;
            }
            default: {

            }
        }
        return recipe;
    }

    public static Set<Stock> getStocklist(Menu menu, int index) {
        Set<Stock> stocks = null;
        switch (index) {
            case STARTER: {
                stocks = menu.getStarterStocks();
                break;
            }
            case MAIN: {
                stocks = menu.getMainStocks();
                break;
            }
            case SAUCE: {
                stocks = menu.getSauceStocks();
                break;
            }
            case VEGGIE: {
                stocks = menu.getSideveggieStocks();
                break;
            }
            case SIDEDISH: {
                stocks = menu.getSidedishStocks();
                break;
            }
            case DESSERT: {
                stocks = menu.getDessertStocks();
                break;
            }
            default: {

            }
        }
        return stocks;
    }

    public static Menu clearStocklist(Menu menu, int index) {


        switch (index) {
            case STARTER: {
                menu.getStarterStocks().clear();

                break;
            }
            case MAIN: {
                menu.getMainStocks().clear();

                break;
            }
            case SAUCE: {
                menu.getSauceStocks().clear();

                break;
            }
            case VEGGIE: {
                menu.getSideveggieStocks().clear();

                break;
            }
            case SIDEDISH: {
                menu.getSidedishStocks().clear();

                break;
            }
            case DESSERT: {
                menu.getDessertStocks().clear();

                break;
            }
            default: {

            }
        }

        return menu;
    }


    public static Menu add2Stocklist(Menu menu, Stock stock, int index) {


        switch (index) {
            case STARTER: {

                menu.getStarterStocks().add(stock);
                break;
            }
            case MAIN: {

                menu.getMainStocks().add(stock);
                break;
            }
            case SAUCE: {

                menu.getSauceStocks().add(stock);
                break;
            }
            case VEGGIE: {

                menu.getSideveggieStocks().add(stock);
                break;
            }
            case SIDEDISH: {

                menu.getSidedishStocks().add(stock);
                break;
            }
            case DESSERT: {

                menu.getDessertStocks().add(stock);
            }
            default: {

            }
        }

        return menu;
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
