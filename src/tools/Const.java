/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L�hr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie �bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder modifizieren, gem�� Version 2 der Lizenz.
 *
 * Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package tools;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class Const {

    public static Color darkgreen = new Color(0x00, 0x76, 0x00);
    public static Color darkred = new Color(0xbd, 0x00, 0x00);
    public static Color gold7 = new Color(0xff, 0xaa, 0x00);
    public static Color darkorange = new Color(0xff, 0x8c, 0x00);
    public static Color khaki1 = new Color(0xFF, 0xF3, 0x80);
    public static Color khaki2 = new Color(0xed, 0xe2, 0x75);
    public static Color khaki3 = new Color(0xc9, 0xbe, 0x62);
    public static Color khaki4 = new Color(0x82, 0x78, 0x39);
    public static Color deepskyblue = new Color(0, 191, 255);
    public static Color bluegrey = new Color(230, 230, 255);
    public static Color lightblue = new Color(192, 217, 217);
    public static Color bermuda_sand = new Color(246, 201, 204);
    public static Color melonrindgreen = new Color(223, 255, 165);
    public static Color orangered = new Color(255, 36, 0);
    public static Color sun3 = new Color(153, 153, 204);
    public static String html_darkgreen = "color=\"#007600\"";
    public static String html_darkred = "color=\"#bd0000\"";
    public static String html_gold7 = "color=\"#ffaa00\"";
    public static String html_darkorange = "color=\"#ff8c00\"";
    public static String html_khaki1 = "color=\"#fffg8f\"";
    public static String html_silver = "color=\"#C0C0C0\"";
    public static String html_lightslategrey = "color=\"#778899\"";
    public static String html_grey80 = "color=\"#c7c7c5\"";
    public static String html_grey50 = "color=\"#747170\"";
    public static String html_cyan = "color=\"#00ffff\"";
    public static String html_mediumpurple3 = "color=\"#8968cd\"";
    public static String html_mediumorchid3 = "color=\"#b452cd\"";
    public static Color salmon1 = new Color(0xFF, 0x8C, 0x69);
    public static Color salmon2 = new Color(0xEE, 0x82, 0x62);
    public static Color salmon3 = new Color(0xCD, 0x70, 0x54);
    public static Color salmon4 = new Color(0x8B, 0x4C, 0x39);
    public static Color lightsteelblue1 = new Color(0xc6, 0xde, 0xff);
    public static Color lightsteelblue2 = new Color(188, 210, 238);
    public static Color lightsteelblue3 = new Color(0x9a, 0xad, 0xc7);
    public static Color lightsteelblue4 = new Color(0x6E, 0x7B, 0x8B);
    public static Color darkolivegreen1 = new Color(0xcc, 0xfb, 0x5d);
    public static Color darkolivegreen2 = new Color(0xBC, 0xEE, 0x68);
    public static Color darkolivegreen3 = new Color(0xa0, 0xc5, 0x44);
    public static Color darkolivegreen4 = new Color(0x6E, 0x8B, 0x3D);
    public static Color gold2 = new Color(0xEE, 0xC9, 0x00);
    public static Color gold4 = new Color(0x8B, 0x75, 0x00);
    public static Color mediumpurple1 = new Color(0xAB, 0x82, 0xFF);
    public static Color mediumpurple2 = new Color(0x9F, 0x79, 0xEE);
    public static Color mediumpurple3 = new Color(0x89, 0x68, 0xCD);
    public static Color mediumpurple4 = new Color(0x5D, 0x47, 0x8B);
    public static Color mediumorchid1 = new Color(0xE0, 0x66, 0xFF);
    public static Color mediumorchid3 = new Color(0xB4, 0x52, 0xCD);
    public static Color mediumorchid2 = new Color(0xC4, 0x5A, 0xEC);
    public static Color mediumorchid4 = new Color(0x6A, 0x28, 0x7E);
    public static Color thistle1 = new Color(0xfc, 0xdf, 0xFF);
    public static Color thistle2 = new Color(0xe9, 0xcf, 0xEC);
    public static Color thistle3 = new Color(0xc6, 0xae, 0xc7);
    public static Color thistle4 = new Color(0x80, 0x6d, 0x7E);
    public static Color yellow1 = new Color(0xFF, 0xFF, 0x00);
    public static Color yellow2 = new Color(0xEE, 0xEE, 0x00);
    public static Color yellow3 = new Color(0xCD, 0xCD, 0x00);
    public static Color yellow4 = new Color(0x8B, 0x8B, 0x00);
    public static Color grey80 = new Color(0xc7, 0xc7, 0xc5);
    public static Color grey50 = new Color(0x74, 0x71, 0x70);
    //Gray50  	747170
    public static char eurosymbol = '\u20AC';
    public static final GregorianCalendar VON_ANFANG_AN = new GregorianCalendar(1970, GregorianCalendar.JANUARY, 1, 0, 0, 0);
    public static final GregorianCalendar BIS_AUF_WEITERES = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 23, 59, 59);
    public static final GregorianCalendar BIS_AUF_WEITERES_WO_TIME = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 0, 0, 0);
    public static final Date DATE_VON_ANFANG_AN = new Date(VON_ANFANG_AN.getTimeInMillis());
    public static final Date DATE_BIS_AUF_WEITERES = new Date(BIS_AUF_WEITERES.getTimeInMillis());
    public static final Date DATE_BIS_AUF_WEITERES_WO_TIME = new Date(BIS_AUF_WEITERES_WO_TIME.getTimeInMillis());
    public static final Timestamp TS_VON_ANFANG_AN = new Timestamp(VON_ANFANG_AN.getTimeInMillis());
    public static final Timestamp TS_BIS_AUF_WEITERES = new Timestamp(BIS_AUF_WEITERES.getTimeInMillis());
    public static final String MYSQL_DATETIME_VON_ANFANG_AN = "'1000-01-01 00:00:00'";
    public static final String MYSQL_DATETIME_BIS_AUF_WEITERES = "'9999-12-31 23:59:59'";
    public static final String EINHEIT[] = {"kg", "liter", "Stück"};
    public static final int MENU_ART_VORSPEISE = 0;
    public static final int MENU_ART_HAUPTGANG = 1;
    public static final int MENU_ART_Nachtisch = 2;
    //
    //
    // Bedeutung des Wertes bei einer Suche
    public static final int DATUM = 0;
    public static final int PRODUKTNAME = 1;
    public static final int VORRAT = 2;
    public static final int GTIN = 3;
    public static final int LAGER = 4;
    public static final int WARENGRUPPE = 5;
    public static final int PRODUKT = 6;
    public static final int LIEFERANT = 7;
    public static final int AKTIVE = 8;
    public static final int ANGEBROCHENE = 9;
    public static final int AUSGEBUCHTE = 10;
    public static final int ALLE = 11;
    public static final int NAME_NR = 12;
    public static final int LAGERART = 13;
    public static final int STOFFART = 14;


    public static final Icon icon24truck = new ImageIcon(Const.class.getResource("/artwork/24x24/truck.png"));
    public static final Icon icon24box = new ImageIcon(Const.class.getResource("/artwork/24x24/ark.png"));
    public static final Icon icon242rightarrow = new ImageIcon(Const.class.getResource("/artwork/24x24/2rightarrow.png"));
    public static final Icon icon24reload = new ImageIcon(Const.class.getResource("/artwork/24x24/reload.png"));
    public static final Icon icon24remove = new ImageIcon(Const.class.getResource("/artwork/24x24/editdelete.png"));
    public static final Icon icon24stop = new ImageIcon(Const.class.getResource("/artwork/24x24/player_stop.png"));
    public static final Icon icon24undo = new ImageIcon(Const.class.getResource("/artwork/24x24/undo.png"));
    public static final Icon icon24start = new ImageIcon(Const.class.getResource("/artwork/24x24/player_start.png"));
    public static final Icon icon24games = new ImageIcon(Const.class.getResource("/artwork/24x24/games.png"));
    public static final Icon icon24Pageprinter = new ImageIcon(Const.class.getResource("/artwork/24x24/printer.png"));
    public static final Icon icon24labelPrinter2 = new ImageIcon(Const.class.getResource("/artwork/24x24/labelprinter2.png"));

    public static final Icon icon48box = new ImageIcon(Const.class.getResource("/artwork/48x48/ark.png"));
    public static final Icon icon48remove = new ImageIcon(Const.class.getResource("/artwork/48x48/editdelete.png"));
    public static final Icon icon48stop = new ImageIcon(Const.class.getResource("/artwork/48x48/player_stop.png"));
    public static final Icon icon48undo = new ImageIcon(Const.class.getResource("/artwork/48x48/undo.png"));
    public static final Icon icon48start = new ImageIcon(Const.class.getResource("/artwork/48x48/player_start.png"));


    public static final Icon icon48games = new ImageIcon(Const.class.getResource("/artwork/48x48/games.png"));
    public static final Icon icon64games = new ImageIcon(Const.class.getResource("/artwork/64x64/games.png"));

}
