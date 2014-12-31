package tools;

import java.awt.*;

/**
 * Created by tloehr on 10.12.14.
 */
public class HTML {

    public static final Font ARIAL14 = new Font("Arial", Font.PLAIN, 14);
    public static final String arial14 = "face=\"" + ARIAL14.getFamily() + "\"";
    public static final String fontface = "<font " + arial14 + " >";

    public static String ul(String content) {
        return "<ul id=\"fonttext\">\n" + Tools.xx(content) + "</ul>\n";
    }

    public static String ol(String content) {
        return "<ol id=\"fonttext\">\n" + Tools.xx(content) + "</ol>\n";
    }

    public static String li(String content) {
        return "<li>" + Tools.xx(content) + "</li>\n";
    }

    public static String table_th(String content, String align) {
        return "<th " + Tools.catchNull(align, "align=\"", "\"") + ">" + Tools.xx(content) + "</th>\n";
    }

    public static String table_th(String content) {
        return "<th>" + Tools.xx(content) + "</th>\n";
    }

    public static String table_th(String content, int colspan) {
            return table_th(content, null, colspan);
        }

    public static String table_th(String content, String align, int span) {
           return "<th" + (span > 1 ? " colspan=\"" + span + "\" " : "") + Tools.catchNull(align, "align=\"", "\"") + ">" + Tools.xx(content) + "</th>\n";
       }

    public static String table_td(String content, String align, int span) {
        return "<td" + (span > 1 ? " colspan=\"" + span + "\" " : "") + Tools.catchNull(align, "align=\"", "\"") + ">" + Tools.xx(content) + "</td>\n";
    }

    public static String table_td(String content, String align, String valign) {
        return "<td " + Tools.catchNull(align, "align=\"", "\"") + " " + Tools.catchNull(valign, "valign=\"", "\"") + ">" + Tools.xx(content) + "</td>\n";
    }

    public static String table_td(String content) {
        return table_td(content, null, 0);
    }

    public static String table_td(String content, int colspan) {
            return table_td(content, null, colspan);
        }

    public static String table_td(String content, boolean bold) {
        return table_td((bold ? "<b>" : "") + content + (bold ? "</b>" : ""), null, 0);
    }


    public static String table_tr(String content) {
        return "<tr>" + Tools.xx(content) + "</tr>\n";
    }

    public static String table_tr(String content, boolean highlight) {
        return "<tr " + (highlight ? "id=\"fonttextgray\"" : "") + ">" + Tools.xx(content) + "</tr>\n";
    }

    public static String bold(String content) {
        return "<b>" + Tools.xx(content) + "</b>";
    }

    public static String underline(String content) {
            return "<u>" + Tools.xx(content) + "</u>";
        }

    public static String italic(String content) {
        return "<i>" + Tools.xx(content) + "</i>";
    }

    public static String paragraph(String content) {
        return "<p id=\"fonttext\">\n" + Tools.xx(content) + "</p>\n";
    }

    public static String div(String content) {
        return "<div id=\"fonttext\">\n" + Tools.xx(content) + "</div>\n";
    }

    public static String h1(String content) {
        return "<h1 id=\"fonth1\" >" + Tools.xx(content) + "</h1>\n";
    }

    public static String h2(String content) {
        return "<h2 id=\"fonth2\" >" + Tools.xx(content) + "</h2>\n";
    }

    public static String h3(String content) {
        return "<h3 id=\"fonth3\" >" + Tools.xx(content) + "</h3>\n";
    }


    public static String h4(String content) {
        return "<h4 id=\"fontsmall\">" + Tools.xx(content) + "</h4>\n";
    }

    public static String table(String content, String border) {
        return "<table id=\"fonttext\" border=\"" + border + "\">" + Tools.xx(content) + "</table>\n";
    }

    public static final String div_open = "<div id=\"fonttext\">";
    public static final String div_close = "</div>";

    public static final String color(Color color, String in) {
        return "<font color=#" + GUITools.toHexString(color) + ">" + in + "</font>";
    }

}
