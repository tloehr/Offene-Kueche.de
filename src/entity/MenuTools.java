package entity;

import tools.Tools;

/**
 * Created by tloehr on 11.11.14.
 */
public class MenuTools {

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
            text = Tools.left(text, text.length() - 2);
        }


        return text;
    }

}
