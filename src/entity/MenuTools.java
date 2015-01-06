package entity;

import tools.Const;
import tools.HTML;
import tools.Tools;

import java.util.ArrayList;
import java.util.Collections;
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

    public static final int[] DISHES = new int[]{STARTER, MAIN, SAUCE, VEGGIE, SIDEDISH, DESSERT};


    public static String getStocksAsHTMLList(Set<Stock> stocks) {

        if (stocks.isEmpty()) return "";

        String html = "<h2>Vorräte</h2><ul>";

        for (Stock stock : stocks) {
            html += "<li>" + stock.getId() + " " + Tools.left(stock.getProdukt().getBezeichnung(), 30) + "</li>";
        }

        html += "</ul>";

        return html;

    }


    private static String getAllergeneLine(Set<Allergene> allergenes) {
        String line = "";

        ArrayList<Allergene> list = new ArrayList<Allergene>(allergenes);
        Collections.sort(list);
        for (Allergene allergene : list) {
            line += allergene.getKennung() + " ";
        }
        return line;
    }

    private static String getAdditiveLine(Set<Additives> additives) {
        String line = "";
        ArrayList<Additives> list = new ArrayList<Additives>(additives);
        Collections.sort(list);
        for (Additives additive : additives) {
            line += additive.getSymbol() + " ";
        }
        return line;
    }


    private static String getAdditiveGroupLine(Set<Additives> additives) {

        ArrayList<Additivegroups> myAdditivesGroups = new ArrayList<Additivegroups>();
        for (Additives additive : additives) {
            if (!myAdditivesGroups.contains(additive.getAdditivegroups())) {
                myAdditivesGroups.add(additive.getAdditivegroups());
            }
        }

        Collections.sort(myAdditivesGroups);


        String line = "";
        for (Additivegroups addgroup : myAdditivesGroups) {
            line += addgroup.getId() + " ";
        }
        return line;
    }


    public static String getAsHTML(Menu menu, HashSet<Additives> setGlobalAdditives, HashSet<Allergene> setGlobalAllergenes, boolean groupAdditives) {

        if (menu.isEmpty()) {
            return "<i>nichts geplant</i>";
        }

        String html = "";
        HashSet<Allergene> setAllergenes = new HashSet<Allergene>();
        HashSet<Additives> setAdditives = new HashSet<Additives>();


        for (int dish : DISHES) {
            if (getDish(menu, dish) != null) {

                if (!html.isEmpty()) html += ", ";

                html += "<font size=\"+1\">" + getDish(menu, dish).getTitle() + "<font>";


                HashSet<Allergene> myAllergenes = new HashSet<Allergene>();
                HashSet<Additives> myAdditives = new HashSet<Additives>();


//                if (!getDish(menu, dish).getIngTypes2Recipes().isEmpty()) {
//                    myAllergenes.addAll(StockTools.getAllergenes(getStocklist(menu, dish)));
//                    myAdditives.addAll(StockTools.getAdditives(getStocklist(menu, dish)));
//                }

                if (!getStocklist(menu, dish).isEmpty()) {
                    myAllergenes.addAll(StockTools.getAllergenes(getStocklist(menu, dish)));
                    myAdditives.addAll(StockTools.getAdditives(getStocklist(menu, dish)));
                }


                if (!myAllergenes.isEmpty()) {
                    setAllergenes.addAll(myAllergenes);
                    html += "<sub id=\"fontsmall\">" + getAllergeneLine(myAllergenes) + "</sub>";
                }
                if (!myAdditives.isEmpty()) {
                    setAdditives.addAll(myAdditives);
                    html += "<sub id=\"fontsmall\">" + (groupAdditives ? getAdditiveGroupLine(myAdditives) : getAdditiveLine(myAdditives)) + "</sub>\n";
                }
            }
        }

        if (!setAllergenes.isEmpty()) {
            html += "<br/>Allergene: " + getAllergeneLine(setAllergenes) + "\n";
            setGlobalAllergenes.addAll(setAllergenes);
        }

        if (!setAdditives.isEmpty()) {
            html += (setAllergenes.isEmpty() ? "" : " // ") + "Zusatzstoffe: " + (groupAdditives ? getAdditiveGroupLine(setAdditives) : getAdditiveLine(setAdditives)) + "\n";
            setGlobalAdditives.addAll(setAdditives);
        }


        return html;

    }


    public static String getIngredientsAsHTML(Menu menu) {

        if (menu.isEmpty()) {
            return "<i>nichts geplant</i>";
        }


        String html = "";
//        HashSet<IngTypes> setIngTypes = new HashSet<IngTypes>();
//        HashSet<Stock> setStocks = new HashSet<Stock>();

        for (int dish : DISHES) {
            if (getDish(menu, dish) != null) {

//                if (!html.isEmpty()) html += "<br/>";

                html += HTML.color(Const.mediumpurple3, HTML.bold(getDish(menu, dish).getTitle())) + "<br/>";

                String innerHTML = RecipeTools.getSubRecipesAsHTML(getDish(menu, dish), HTML.bold("Andere Rezepte, die als Zutat verwendet werden"));

                innerHTML += RecipeTools.getIngTypesAsHTMLList(getDish(menu, dish), HTML.bold("Zutaten"));

                Set<Stock> stocks = MenuTools.getStocklist(menu, dish);
                if (!stocks.isEmpty()) {
                    String stocklist = "";
                    for (Stock stock : stocks) {
                        stocklist += HTML.li(stock.getId() + ": " + stock.getProdukt().getBezeichnung() +
                                        "&nbsp;<font  size=\"-3\">(" + ProdukteTools.getAllergenesAndAdditivesAsCompactHTML(stock.getProdukt()) + ")</font>"
                        );

                    }

                    innerHTML += HTML.bold("Vorräte");
                    innerHTML += HTML.ul(stocklist);
                }


                html += innerHTML.isEmpty() ? "keine Zutaten<br/>" : innerHTML;


            }
        }


        return html;

    }


    public static Integer[] indicesOf(Menu menu, Recipes recipe) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int dish : DISHES) {
            if (getDish(menu, dish) != null && getDish(menu, dish).equals(recipe)) {
                list.add(dish);
            }
        }
        return list.toArray(new Integer[]{});
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
