/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.toedter.calendar.JDateChooser;
import events.PropertyChangeListenerDevelop;
import org.joda.time.LocalDate;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

//import javax.smartcardio.*;

/**
 * @author tloehr
 */
public class Tools {

    public static final boolean LEFT_UPPER_SIDE = false;
    public static final boolean RIGHT_LOWER_SIDE = true;

    /**
     * see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4380536
     * Puh, das hier ist aus der Sun Bug Datenbank. Etwas krude... Ich hoffe
     * die lassen sich mal was besseres einfallen.
     */
    static private void removeListeners(Component comp) {
        Method[] methods = comp.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if (name.startsWith("remove") && name.endsWith("Listener")) {

                Class[] params = method.getParameterTypes();
                if (params.length == 1) {
                    EventListener[] listeners = null;
                    try {
                        listeners = comp.getListeners(params[0]);
                    } catch (Exception e) {
                        // It is possible that someone could create a listener
                        // that doesn't extend from EventListener.  If so,
                        // ignore it
                        //OPDE.logger.debug("Listener " + params[0] + " does not extend EventListener");
                        continue;
                    }
                    for (int j = 0; j < listeners.length; j++) {
                        try {
                            method.invoke(comp, new Object[]{listeners[j]});
                            //OPDE.logger.debug("removed Listener " + name + "for comp " + comp + "\n");
                        } catch (Exception e) {
                            //OPDE.logger.debug("Cannot invoke removeListener method " + e);
                            // Continue on.  The reason for removing all listeners is to
                            // make sure that we don't have a listener holding on to something
                            // which will keep it from being garbage collected. We want to
                            // continue freeing listeners to make sure we can free as much
                            // memory has possible
                        }
                    }
                } else {
                    // The only Listener method that I know of that has more
                    // one argument is removePropertyChangeListener.  If it is
                    // something other than that, flag it and move on.
                    if (!name.equals("removePropertyChangeListener")) {
                        // OPDE.logger.debug("    Wrong number of Args " + name);
                    }
                }
            }
        }
    }

    /**
     * läuft rekursiv durch alle Kinder eines Containers und entfernt evtl. vorhandene Listener.
     */
    public static void unregisterListeners(JComponent container) {
        if (container == null) {
            return;
        }
        removeListeners(container);
        if (container.getComponentCount() > 0) {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    unregisterListeners((JComponent) c[i]);
                }
            }
        }
    }

    public static <T> boolean isLastElement(T element, List<T> list) {
        if (list == null) return false;
        if (element == null) return false;
        if (list.isEmpty()) return false;

        return list.get(list.size() - 1).equals(element);
    }

    /**
     * läuft rekursiv durch alle Kinder eines JFrames und entfernt evtl. vorhandene Listener.
     */
    public static void unregisterListeners(JDialog container) {
        if (container == null) {
            return;
        }
        if (container.getComponentCount() > 0) {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    unregisterListeners((JComponent) c[i]);
                }
            }
        }
    }

    /**
     * läuft rekursiv durch alle Kinder eines JFrames und entfernt evtl. vorhandene Listener.
     */
    public static void unregisterListeners(JFrame container) {
        if (container == null) {
            return;
        }
        if (container.getComponentCount() > 0) {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    unregisterListeners((JComponent) c[i]);
                }
            }
        }
    }

    public static void scrollCellToVisible(JTable table, int row, int col) {
//        table.invalidate();
//        table.scrollRectToVisible(table.getCellRect(row, 0, true));


        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(row, col, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }

    public static String catchNull(Object in) {
        return catchNull(in, "");

    }

    public static String catchNull(Object in, Object showWhenNotNull, String neutral) {
        String result = neutral;
        if (in != null) {
            result = showWhenNotNull.toString();
            if (result.isEmpty()) {
                result = neutral;
            }
        }
        return result;

    }

    /**
     * Ermittelt die Zeichendarstellung eines Objekts (toString). Ist das Ergebnis null oder eine leere Zeichenkette, dann wird
     * der String neutral zurück gegeben.
     *
     * @param in
     * @param neutral
     * @return
     */
    public static String catchNull(Object in, String neutral) {
        String result = neutral;
        if (in != null) {
            result = in.toString();
            if (result.equals("")) {
                result = neutral;
            }
        }
        return result;
    }

    public static String catchNull(String in) {
        return (in == null ? "" : in);
    }

    public static String catchNull(String in, String prefix, String suffix) {
        String result = "";
        if (!catchNull(in).equals("")) {
            result = prefix + catchNull(in) + suffix;
        }
        return result;
    }

    public static void center(java.awt.Window w) {
        Dimension us = w.getSize();
        Dimension them = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (them.width - us.width) / 2;
        int newY = (them.height - us.height) / 2;
        w.setLocation(newX, newY);
    } // center

    public static void centerOnParent(Component parent, Component child) {

        if (parent != null && child != null) {

            Dimension dimParent = parent.getSize();
            Dimension dimChild = child.getSize();


            //Dimension them = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            int newX = (dimParent.width - dimChild.width) / 2;
            int newY = (dimParent.height - dimChild.height) / 2;
            newX += parent.getX();
            newY += parent.getY();
            child.setLocation(newX, newY);
        }
    }

    public static void centerOnParent(Component comp) {
        centerOnParent(comp.getParent(), comp);
    }

    public static double roundScale2(double d) {
        return Math.rint(d * 100) / 100.;
    }

    public static long idInString(String str) {
        long id;
        try {
            id = Math.max(1, Long.parseLong(str));
        } catch (NumberFormatException numberFormatException) {
            id = 0;
        }
        return id;
    }

    public static String toString(byte[] bytes) {
        String result = "";
        for (int b = 0; b < bytes.length; b++) {
            result += Integer.toHexString(bytes[b]) + "  ";
        }
        return result;
    }

    /**
     * Konvertiert ein ByteArray in einen Long. 8 Bytes.
     * Das LSB kommt zuerst.
     *
     * @param by
     * @return
     */
    public static long byteToLongLSB(byte[] by) {
        long value = 0;
        for (int i = 0; i < by.length; i++) {
            value += (by[i] & 0xff) << (8 * i);
        }
        return value;
    }

    /**
     * Konvertiert ein ByteArray in einen Long. 8 Bytes.
     * Das MSB kommt zuerst.
     *
     * @param by
     * @return
     */
    public static long byteToLongMSB(byte[] by) {
        long value = 0;
        for (int i = 0; i < by.length; i++) {
            value = (value << 8) + (by[i] & 0xff);
        }
        return value;
    }

    /**
     * Wandelt ein ByteArray von LSB nach MSB um.
     *
     * @param by
     * @param minLength - mindestlänge des neuen ByteArrays
     * @return
     */
    public static byte[] lSBToMSB(byte[] by, int minLength) {
        byte[] result = new byte[Math.max(by.length, minLength)];
        for (int b = 0; b < by.length; b++) {
            result[b] = by[by.length - b - 1];
        }
        // Wenn nötig, bytes mit 00 auffüllen.
        if (by.length < minLength) {
            for (int b = by.length; b < minLength; b++) {
                result[b] = 0;
            }
        }
        return result;
    }

    /**
     * Erstellt ein MD5 Passwort anhand der übergebenen Zeichenkette.
     *
     * @param password
     * @return
     */
    public static String hashword(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
            if (hashword.length() == 31) {
                hashword = "0" + hashword;
            }
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }

    public static void markAllTxt(JTextComponent jtf) {
        jtf.setSelectionStart(0);
        jtf.setSelectionEnd(jtf.getText().length());
    }

    public static <T> DefaultListModel<T> newListModel(List<T> list) {
        DefaultListModel<T> listModel = new DefaultListModel<T>();
        if (list != null) {
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                listModel.addElement(it.next());
            }
        }
        return listModel;
    }


    public static <T> DefaultListModel<T> newListModel(List<T> listAll, HashSet<T> without) {
        DefaultListModel<T> listModel = new DefaultListModel<T>();
        if (listAll != null) {
            Iterator<T> it = listAll.iterator();
            while (it.hasNext()) {
                T t = it.next();
                if (!without.contains(t)) {
                    listModel.addElement(t);
                }
            }
        }
        return listModel;
    }


//    public static DefaultComboBoxModel newComboboxModel(String jpql, Object[]... params) {
//        EntityManager em = Main.getEMF().createEntityManager();
//        Query query = em.createQuery(jpql);
//        if (params != null) {
//            for (Object[] param : params) {
//                query.setParameter(param[0].toString(), param[1]);
//            }
//        }
//
//        DefaultComboBoxModel dcbm = newComboboxModel(query.getResultList());
//
//        em.close();
//        return dcbm;
//    }

    public static <T> DefaultComboBoxModel<T> newComboboxModel(ArrayList<T> list) {
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<T>();
        if (list != null) {
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                model.addElement(it.next());
            }
        }
        return model;
    }

    public static double parseDouble(String text) {
        text = text.replace(",", ".");
        text = text.replace("1/4", "0.25");
        text = text.replace("1/2", "0.5");
        text = text.replace("3/4", "0.75");
        text = text.replace("1/3", "0.33");
        text = text.replace("2/3", "0.66");

        Double d;

        try {
            d = Double.parseDouble(text);
        } catch (NumberFormatException nfe) {
            d = 0d;
        }

        return d;
    }

    public static double checkDouble(javax.swing.event.CaretEvent evt, boolean mustBePositive) {
        double dbl = 0d;
        JTextComponent txt = (JTextComponent) evt.getSource();
        Action toolTipAction = txt.getActionMap().get("hideTip");
        if (toolTipAction != null) {
            ActionEvent hideTip = new ActionEvent(txt, ActionEvent.ACTION_PERFORMED, "");
            toolTipAction.actionPerformed(hideTip);
        }
        try {
            dbl = Double.parseDouble(txt.getText().replaceAll(",", "\\."));
            Main.logger.debug("Double: " + dbl);
            if (mustBePositive && dbl <= 0) {
                txt.setToolTipText("<html><font color=\"red\"><b>Sie können nur Zahlen größer 0 eingeben</b></font></html>");
                toolTipAction = txt.getActionMap().get("postTip");
                dbl = 1d;
            } else {
                txt.setToolTipText(null);
            }

        } catch (NumberFormatException ex) {
            if (mustBePositive) {
                dbl = 1d;
            } else {
                dbl = 0d;
            }

            txt.setToolTipText("<html><font color=\"red\"><b>Sie haben eine ungültige Zahl eingegeben.</b></font></html>");
            toolTipAction = txt.getActionMap().get("postTip");
            if (toolTipAction != null) {
                ActionEvent postTip = new ActionEvent(txt, ActionEvent.ACTION_PERFORMED, "");
                toolTipAction.actionPerformed(postTip);
            }
        }
        return dbl;
    }

    /**
     * @param filePath name of file to open. The file can reside
     *                 anywhere in the classpath
     *                 http://snippets.dzone.com/posts/show/4480
     */
    public static String readFileAsString(String filePath) {
        StringBuffer fileData = new StringBuffer(1000);
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            new DlgException(e);
        }
        return fileData.toString();
    }


    public static void handleFile(Component parent, String filename, java.awt.Desktop.Action action) {
        Desktop desktop = null;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (action == Desktop.Action.OPEN && desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.open(new File(filename));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht angezeigt werden.)",
                            "Kein Anzeigeprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
                try {
                    desktop.print(new File(filename));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht gedruckt werden.)",
                            "Kein Druckprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht bearbeitet werden.)",
                        "Keine passende Anwendung vorhanden", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent, "JAVA Desktop Unterstützung nicht vorhanden", "JAVA Desktop API", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String htmlUmlautConversion(String in) {
        String result = in;
        result = replace(result, "Ä", "&Auml;");
        result = replace(result, "ä", "&auml;");
        result = replace(result, "Ö", "&Ouml;");
        result = replace(result, "ö", "&ouml;");
        result = replace(result, "Ü", "&Uuml;");
        result = replace(result, "ü", "&uuml;");
        result = replace(result, "ß", "&szlig;");
        return result;
    }

    /**
     * Tauscht Zeichen in einem String in bester Textverarbeitungsmanier ;-)<br/>
     * <b>Beispiel:</b> replace("AABBCC", "BB", "DD") = "AADDCC"
     *
     * @param str     - Eingang
     * @param pattern - Muster nach dem gesucht werden soll
     * @param replace - Ersatzzeichenkette
     * @return String mit Ersetzung
     */
    public static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    public static void saveProperties() {
        try {
            FileOutputStream out = new FileOutputStream(new File(Main.props.getProperty("workdir") + System.getProperty("file.separator") + "kueche.properties"));
            Main.props.remove("workdir");

            Main.props.store(out, "Property File fuer das Kuechen System");
            Main.logger.debug("Saving the Property File");
            out.close();
        } catch (Exception ex) {
            Main.logger.fatal(ex.getMessage(), ex);
            System.exit(1);
        }
    }

    public static String left(String text, int size) {
        return left(text, size, "...");
    }

    public static String left(String text, int size, String abrev) {
        //        OPDE.debug("IN: " + text);
        int originalLaenge = text.length();
        int max = Math.min(size, originalLaenge);
        text = text.substring(0, max);
        if (max < originalLaenge) {
            text += abrev;
        }
        return text;
    }


    /**
     * Gibt eine einheitliche Titelzeile für alle Fenster zurück.
     */
    public static String getWindowTitle(String moduleName) {
        if (!moduleName.equals("")) {
            moduleName = ", " + moduleName;
        }
        return Main.appinfo.getProperty("program.PROGNAME") + moduleName + ", v" + Main.appinfo.getProperty("program.VERSION")
                + "/" + Main.appinfo.getProperty("program.BUILDNUM") + "/" + Main.appinfo.getProperty("program.BUILDDATE") +
                (Main.isDebug() ? " (" + Main.getJdbcurl() + ")" : "");

//                +
//                (OPDE.localocprops.getProperty("debug").equalsIgnoreCase("true") ? " !! DEBUG !!" : "");
    }

    public static Date addDate(Date date, int amount) {
        GregorianCalendar gc = toGC(date);
        gc.add(GregorianCalendar.DATE, amount);
        return new Date(gc.getTimeInMillis());
    }

    public static GregorianCalendar toGC(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        return gc;
    }

    public static GregorianCalendar toGC(long l) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(l);
        return gc;
    }

    /**
     * gibt das heutige Datum zurück, allerdings um die Uhrzeitanteile bereinigt.
     *
     * @return das ein um die Uhrzeit bereinigtes Datum.
     */
    public static GregorianCalendar today() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.HOUR, 0);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc;
    }

    public static GregorianCalendar erkenneDatum(String input) throws NumberFormatException {
        if (input == null || input.equals("")) {
            throw new NumberFormatException("leere Eingabe");
        }
        if (input.indexOf(".") + input.indexOf(",") + input.indexOf("-") == -3) {
            input += "."; // er war zu faul auch nur einen punkt anzuhängen.
        }
        StringTokenizer st = new StringTokenizer(input, ",.-");
        if (st.countTokens() == 1) { // Vielleicht fehlen ja nur die Monats- und Jahresangaben. Dann hängen wir sie einach an.
            input += (today().get(GregorianCalendar.MONTH) + 1) + "." + today().get(GregorianCalendar.YEAR);
            st = new StringTokenizer(input, ",.-"); // dann nochmal aufteilen...
        }
        if (st.countTokens() == 2) { // Vielleicht fehlt ja nur die Jahresangabe. Dann hängen wir es einfach an.

            if (!input.trim().substring(input.length() - 1).equals(".") && !input.trim().substring(input.length() - 1).equals(",")) {
                input += "."; // er war zu faul den letzten Punkt anzuhängen.
            }
            input += today().get(GregorianCalendar.YEAR);
            st = new StringTokenizer(input, ",.-"); // dann nochmal aufteilen...
        }
        if (st.countTokens() != 3) {
            throw new NumberFormatException("falsches Format");
        }
        String sTag = st.nextToken();
        String sMonat = st.nextToken();
        String sJahr = st.nextToken();
        int tag, monat, jahr;
        // Hier ist das Jahr 2010 Problem
        GregorianCalendar now = new GregorianCalendar();
        int decade = (now.get(GregorianCalendar.YEAR) / 10) * 10;
        int century = (now.get(GregorianCalendar.YEAR) / 100) * 100;

        try {
            tag = Integer.parseInt(sTag);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("tag");
        }
        try {
            monat = Integer.parseInt(sMonat);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("monat");
        }
        try {
            jahr = Integer.parseInt(sJahr);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("jahr");
        }

        if (jahr < 0) {
            throw new NumberFormatException("jahr");
        }
        if (jahr > 9999) {
            throw new NumberFormatException("jahr");
        }
        if (jahr < 10) {
            jahr += decade;
        }
        if (jahr < 100) {
            jahr += century;
        }
        if (monat < 1 || monat > 12) {
            throw new NumberFormatException("monat");
        }

        if (tag < 1 || tag > eom(new GregorianCalendar(jahr, monat - 1, 1))) {
            throw new NumberFormatException("monat");
        }

        return new GregorianCalendar(jahr, monat - 1, tag, 0, 0, 0);
    }

    public static Date eom(Date d) {
        GregorianCalendar gc = toGC(d);
        int ieom = eom(gc);
        gc.set(GregorianCalendar.DATE, ieom);
        return new Date(endOfDay(new Date(gc.getTimeInMillis())));
    }

    public static int eom(GregorianCalendar d) {
        return d.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
    }

    public static long endOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
        gc.set(GregorianCalendar.MINUTE, 59);
        gc.set(GregorianCalendar.SECOND, 59);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc.getTimeInMillis();
    }

    public static String padL(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.insert(0, padChar);
        }
        return padded.toString();
    }

    public static String padC(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.insert(0, padChar);
            // Dadurch sitzt das Ergebnis nicht unbedingt in der Mitte. Aber doch so mittig wie möglich.
            if (padded.length() < size) {
                padded.append(padChar);
            }
        }
        return padded.toString();
    }

    public static String padR(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.append(padChar);
        }
        return padded.toString();
    }

    /**
     * Nur für die Entwicklung. Hilft mir die Spaltenbreiten zu ermitteln.
     *
     * @param tbl
     */
    public static void showPropertiesOfColumns(JTable tbl) {
        for (int i = 0; i < tbl.getColumnCount(); i++) {
            tbl.getColumnModel().getColumn(i).addPropertyChangeListener(new PropertyChangeListenerDevelop());
        }
    }

    public static int sum(int[] array) {
        int sum = 0;
        for (int k = 0; k < array.length; k++) {
            sum = sum + array[k];
        }
        return sum;
    }

    public static void setTableColumnPreferredWidth(JTable tbl, int[] width) {
        for (int k = 0; k < width.length; k++) {
            tbl.getColumnModel().getColumn(k).setPreferredWidth(width[k]);
        }
    }

    public static void unregisterAllListeners(EventListenerList listenerList) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            listenerList.remove((Class) listeners[i], (EventListener) listeners[i + 1]);
        }
    }

//    public static void flashLabel(JLabel lbl, String[] message) {
//        final JLabel lbl1 = lbl;
//        final String[] msg = message;
//
//        Thread thread = new Thread() {
//            public void run() {
//                Timeline timeline = null;
//                //Color defColor = flashME.getBackground();
//                try {
//                    timeline = fadeinout(lbl1);
//                    for (int i = 0; i < num; i++) {
//                        if (flashME.getBackground() != Color.RED) {
//                            flashME.setBackground(Color.RED);
//                        } else {
//                            flashME.setBackground(defColor);
//                        }
//                        Thread.sleep(150);
//                    }
//                    flashME.setBackground(defColor);
//                } catch (InterruptedException e) {
//                    flashME.setBackground(defColor);
//                }
//            }
//        };
//        thread.start();
//    }


    public static byte[] longToByteMSB(long wert) {
        byte[] buf = new byte[8];
        for (int i = 0; i < 8; i++) {
            buf[7 - i] = (byte) (wert >>> (i << 3));// Big Endian
        }
        return buf;
    }

    public static byte[] longToByteLSB(long wert) {
        byte[] buf = new byte[8];
        for (int i = 0; i < 8; i++) {
            buf[i] = (byte) (wert >>> (i << 3));// Little Endian
        }
        return buf;
    }

    public static final int APDU_READ = 0;
    public static final int APDU_UPDATE = 1;
    public static final int APDU_VERIFY = 2;
    public static final int APDU_SWITCH_TO_USERMODE = 3;
    public static final int APDU_GET_USERMODE = 4;

//    /**
//     * APDU - Application Protocol Data Unit
//     * Sample Cards:
//     * CSC0 = aah aah aah aah
//     * CSC1 = 11h 11h 11h 11h
//     * CSC2 = 22h 22h 22h 22h
//     *
//     * @param command
//     * @return
//     */
//    public static byte[] command_apdu(Card card, int command, byte[] data) throws CardException {
//        // cla = Instruction Class
//        // ins = Instruction Code
//        // p1 = Parameter 1
//        // p2 = Parameter 2
//        // Lengthin =
//        // Length expected
//        // Data In
//        //
//        // Issuer Area
//        // Mode Bits:
//        // Address 04. Bits 31 and 31.
//        // 01b - issuer mode
//        // 10b - user mode (locked)
//        // Can only be modified in personalization phase (issuer mode)
//
//        int[] response;
//        CardChannel channel = card.getBasicChannel();
//
//
//        int cla, ins, p1, p2, le;
//        switch (command) {
//            case APDU_VERIFY: {
//                cla = (int) 0x00;
//                ins = (int) 0x20;
//                p1 = (int) 0x00;
//                p2 = (int) 0x07;
//                le = (int) 0x04;
//                if (data == null) {
//                    // Standard Passwort, CSC0
//                    data = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA};
//                }
//                break;
//            }
//            case APDU_UPDATE: {
//
//                // Data for Updates must be Little Endian (LSB)
//
//                cla = (int) 0x80;
//                ins = (int) 0xDE;
//                p1 = (int) 0x00;
//                p2 = (int) 0x01; // nur für den Issuer Bereich
//                le = (int) 0x08;
//                if (data == null) {
//                    // Standard Passwort, CSC0
//                    throw new CardException("Can't Update Issuer Area without Data");
//                }
//                break;
//            }
//            case APDU_READ: {
//                cla = (int) 0x80;
//                ins = (int) 0xBE;
//                p1 = (int) 0x00;
//                p2 = (int) 0x01; // nur für den Issuer Bereich
//                le = (int) 0x08;
//                data = null;
//                break;
//            }
//            case APDU_SWITCH_TO_USERMODE: {
//                cla = (int) 0x80;
//                ins = (int) 0xDE;
//                p1 = (int) 0x00;
//                p2 = (int) 0x04;
//                le = (int) 0x04;
//                data = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x80};
//                break;
//            }
//            case APDU_GET_USERMODE: {
//                cla = (int) 0x80;
//                ins = (int) 0xBE;
//                p1 = (int) 0x00;
//                p2 = (int) 0x04; // nur für den Issuer Bereich
//                le = (int) 0x04;
//                data = null;
//                break;
//            }
//            default: {
//                throw new CardException("Unknown Command");
//            }
//        }
//
////        int[] baCommandAPDU;
////        if (data == null){
////            baCommandAPDU = new int[]{cla, ins, p1, p2, le};
////        } else {
////
////            baCommandAPDU = new int[]{cla, ins, p1, p2, le, (int) 0xAA, (int) 0xAA, (int) 0xAA, (int) 0xAA};
////        }
//
//        CommandAPDU cmd;
//
//        if (data == null) {
//            cmd = new CommandAPDU(cla, ins, p1, p2, le);
//        } else {
//            cmd = new CommandAPDU(cla, ins, p1, p2, data);
//        }
//
//        ResponseAPDU r = channel.transmit(cmd);
//
//        if (command == APDU_SWITCH_TO_USERMODE) {
//            Main.logger.debug("Card disconnected");
//            //card.disconnect(true); // Reset Card. Locks MemoCard permanently to UserMode.
//        }
//
//        return r.getBytes();
//
//    }

    public static double showSide(JSplitPane split, boolean leftUpper) {
        return showSide(split, leftUpper, 0);
    }

    public static double showSide(JSplitPane split, boolean leftUpper, int speedInMillis) {
        double stop = leftUpper ? 0.0d : 1.0d;
        return showSide(split, stop, speedInMillis);
    }

    public static double showSide(JSplitPane split, double pos) {
        return showSide(split, pos, 0);
    }

    /**
     * Setzt eine Split Pane (animiert oder nicht animiert) auf eine entsprechende Position (Prozentual zwischen 0 und 1)
     *
     * @param split
     * @param pos
     * @param speedInMillis
     * @return Die neue, relative Position (zwischen 0 und 1)
     */
    public static double showSide(JSplitPane split, double pos, int speedInMillis) {

        if (Main.isAnimation() && speedInMillis > 0) {
            Main.debug("ShowSide double-version");
            Object start;
            Object stop;

            if (Tools.isMac() || Tools.isWindows()) {
                start = split.getDividerLocation();
                stop = getDividerInAbsolutePosition(split, pos);
            } else {
                Main.debug("*nix running");
                start = new Double(split.getDividerLocation()) / new Double(getDividerInAbsolutePosition(split, 1.0d));
                stop = pos;
            }

            Main.debug(start.getClass().toString());
            Main.debug(stop.getClass().toString());

            final Timeline timeline1 = new Timeline(split);
            timeline1.setEase(new Spline(0.9f));
            timeline1.addPropertyToInterpolate("dividerLocation", start, stop);
            timeline1.setDuration(speedInMillis);
            timeline1.play();
        } else {
            split.setDividerLocation(pos);
        }
        return pos;
    }

    public static double showSide(JSplitPane split, int pos) {
        return showSide(split, pos, 0);
    }

    public static double showSide(JSplitPane split, int pos, int speedInMillis) {

        if (Main.isAnimation() && speedInMillis > 0) {
            Main.debug("ShowSide int-version");
            Object start;
            Object stop;

            if (Tools.isMac() || Tools.isWindows()) {
                start = split.getDividerLocation();
                stop = pos;
            } else {
                Main.debug("*nix running");
                start = new Double(split.getDividerLocation()) / new Double(getDividerInAbsolutePosition(split, 1.0d));
                stop = getDividerInRelativePosition(split, pos);
            }
            Main.debug(start.getClass().toString());
            Main.debug(stop.getClass().toString());

            final Timeline timeline1 = new Timeline(split);
            timeline1.setEase(new Spline(0.9f));
            timeline1.addPropertyToInterpolate("dividerLocation", start, stop);
            timeline1.setDuration(speedInMillis);
            timeline1.play();
        } else {
            split.setDividerLocation(pos);
        }
        return pos;
    }

//    public static double showSide(JSplitPane split, int pos, int speedInMillis) {
//
//        int start = split.getDividerLocation();
//        int stop = pos;
//
//        if (Main.isAnimation()) {
//            final Timeline timeline1 = new Timeline(split);
//            timeline1.setEase(new Spline(0.9f));
//            timeline1.addPropertyToInterpolate("dividerLocation", start, stop);
//            timeline1.setDuration(speedInMillis);
//            timeline1.play();
//        } else {
//            split.setDividerLocation(stop);
//        }
//
//        return new Double(stop) / new Double(split.getWidth());
//
//    }

    public static int getDividerInAbsolutePosition(JSplitPane mysplit, double pos) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        return new Double(max * pos).intValue();
    }

    public static double getDividerInRelativePosition(JSplitPane mysplit, int pos) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        return new Double(max) / new Double(pos);
    }

    public static void log(JTextComponent txt, String text) {
        log(txt, 0, "", text);
    }

    public static void log(JTextComponent txt, long someID, String someText, String text) {
        DateFormat df = DateFormat.getDateTimeInstance();
        String newTxt = txt.getText() + "\n(" + df.format(new Date()) + ") ";
        newTxt += someID > 0 ? " [" + someID + "] " : "";
        newTxt += !someText.isEmpty() ? " \"" + someText + "\" " : "";
        newTxt += text;
//        if (newTxt.length() > 2000) {
//            newTxt = newTxt.substring(newTxt.length() - 750);
//        }
        txt.setText(newTxt);

    }

    public static void storeStringToFile(String filename, String str) {
        try {
            FileOutputStream out = new FileOutputStream(new File(Main.props.getProperty("workdir") + System.getProperty("file.separator") + filename));
            out.write(str.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static long startOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc.getTimeInMillis();
    }

    public static void setXEnabled(JComponent container, boolean enabled) {
        // Bei einer Combobox muss die Rekursion ebenfalls enden.
        // Sie besteht aus weiteren Unterkomponenten
        // "disabled" wird sie aber bereits hier.
        if (container.getComponentCount() == 0 || container instanceof JComboBox) {
            // Rekursionsanker
            container.setEnabled(enabled);
        } else {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    JComponent jc = (JComponent) c[i];
                    setXEnabled(jc, enabled);
                }
            }
        }
    }

    public static void resetBackground(JComponent container) {
        // Bei einer Combobox muss die Rekursion ebenfalls enden.
        // Sie besteht aus weiteren Unterkomponenten
        // "disabled" wird sie aber bereits hier.
        if (container.getComponentCount() == 0 || container instanceof JComboBox || container instanceof JDateChooser) {
            // Rekursionsanker
            container.setBackground(new Color(214, 217, 223));
        } else {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    JComponent jc = (JComponent) c[i];
                    resetBackground(jc);
                }
            }
        }
    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.indexOf("win") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.indexOf("mac") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }

    public static Timeline flashLabel(final JLabel lbl, String text) {
        lbl.setText(text);
        final Color fg = lbl.getForeground();
        Timeline textmessageTL = new Timeline(lbl);
        textmessageTL.addPropertyToInterpolate("foreground", fg, Color.red);
        textmessageTL.setDuration(600);

        textmessageTL.addCallback(new TimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                if (newState == Timeline.TimelineState.CANCELLED) {
                    lbl.setForeground(fg);
                }
            }
        });

        textmessageTL.playLoop(Timeline.RepeatBehavior.REVERSE);

        return textmessageTL;
    }

    public static void packTable(JTable table, int margin) {
        for (int colindex = 0; colindex < table.getColumnCount(); colindex++) {
            packColumn(table, colindex, margin);
        }
    }

    /*
     * http://exampledepot.com/egs/javax.swing.table/PackCol.html
     */
    public static void packColumn(JTable table, int vColIndex, int margin) {
        TableModel model = table.getModel();
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                    table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2 * margin;
        // Set the width
        col.setPreferredWidth(width);
    }

    public static List<AbstractButton> getSelection(ButtonGroup bg) {
        Enumeration<AbstractButton> buttons = bg.getElements();
        ArrayList<AbstractButton> list = new ArrayList<AbstractButton>();

        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                list.add(button);
            }
        }
        return list;
    }

    public static void fadeout(JLabel lbl) {
        //lbl.setIcon(null);
        final JLabel lbl1 = lbl;
        final Color foreground = lbl.getForeground();
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), lbl.getBackground());
        timeline1.setDuration(500);
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    lbl1.setText("");
                    lbl1.setForeground(foreground);
                    lbl1.setIcon(null);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.play();
    }

    public static void fadein(JLabel lbl, String text) {
        final JLabel lbl1 = lbl;
        final Color foreground = Color.black;
        lbl1.setForeground(lbl1.getBackground());
        lbl1.setText(text);
        Timeline timeline1 = new Timeline(lbl1);
        timeline1.addPropertyToInterpolate("foreground", lbl1.getBackground(), foreground);
        timeline1.setDuration(500);
        timeline1.play();
    }

    public static void fadeinout(JLabel lbl, String text) {
        final JLabel lbl1 = lbl;
        final Color foreground = Color.black;
        final Color background = lbl.getBackground();
        lbl.setForeground(lbl.getBackground());
        lbl.setText(text);
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", background, foreground);
        timeline1.setDuration(400);
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    lbl1.setText("");
                    lbl1.setForeground(foreground);
                    lbl1.setIcon(null);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
    }


    // @author santhosh kumar T - santhosh@in.fiorano.com
    // is path1 descendant of path2
    public static boolean isDescendant(TreePath path1, TreePath path2) {
        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if (count1 <= count2)
            return false;
        while (count1 != count2) {
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }

    // @author santhosh kumar T - santhosh@in.fiorano.com
    public static String getExpansionState(JTree tree, int row) {
        TreePath rowPath = tree.getPathForRow(row);
        StringBuffer buf = new StringBuffer();
        int rowCount = tree.getRowCount();
        for (int i = row; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (i == row || isDescendant(path, rowPath)) {
                if (tree.isExpanded(path))
                    buf.append("," + String.valueOf(i - row));
            } else
                break;
        }
        return buf.toString();
    }

    // @author santhosh kumar T - santhosh@in.fiorano.com
    public static void restoreExpansionState(JTree tree, int row, String expansionState) {
        StringTokenizer stok = new StringTokenizer(expansionState, ",");
        while (stok.hasMoreTokens()) {
            int token = row + Integer.parseInt(stok.nextToken());
            tree.expandRow(token);
        }
    }


    /**
     * Shows a JidePopup in relation to its owner. Calculates the new position that it leaves the owner
     * visible. The popup is placed according to the <code>location</code> setting. The size of the content
     * pane is taken into the calculation in order to find the necessary <code>x, y</code> coordinates on the screen.
     * <p/>
     * <ul>
     * <li>SwingConstants.CENTER <i>You can use this, but I fail to see the sense in it.</i></li>
     * <li>SwingConstants.SOUTH</li>
     * <li>SwingConstants.NORTH</li>
     * <li>SwingConstants.WEST</li>
     * <li>SwingConstants.EAST</li>
     * <li>SwingConstants.NORTH_EAST</li>
     * <li>SwingConstants.NORTH_WEST</li>
     * <li>SwingConstants.SOUTH_EAST</li>
     * <li>SwingConstants.SOUTH_WEST</li>
     * </ul>
     *
     * @param popup    the JidePopup to show
     * @param location where to show the popup in relation to the <code>reference</code>. Use the SwingConstants above.
     */
    public static void showPopup(JidePopup popup, int location, boolean keepOnScreen) {

        Point desiredPosition = getDesiredPosition(popup, location);

        if (keepOnScreen && !isFullyVisibleOnScreen(popup, desiredPosition)) {
            int[] positions = new int[]{SwingConstants.SOUTH_EAST, SwingConstants.SOUTH_WEST, SwingConstants.NORTH_EAST, SwingConstants.NORTH_WEST, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.SOUTH_WEST, SwingConstants.NORTH, SwingConstants.CENTER};
            boolean found = false;

            for (int pos : positions) {
                desiredPosition = getDesiredPosition(popup, pos);
                if (isFullyVisibleOnScreen(popup, desiredPosition)) {
                    found = true;
                    Main.debug("fits on screen");
                    break;
                }
            }

            if (!found) {
                // desiredPosition = getDesiredPosition(popup, location);
                desiredPosition = centerOnScreen(popup);
                Main.debug("didnt find any position thats on the screen");
            }

        }

        popup.showPopup(desiredPosition.x, desiredPosition.y);

    }

    public static void showPopup(JidePopup popup, int location) {
        showPopup(popup, location, true);
    }

    private static Point centerOnScreen(JidePopup popup) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int midx = width / 2;
        int midy = height / 2;


        int x = midx - popup.getContentPane().getPreferredSize().width / 2;
        int y = midy - popup.getContentPane().getPreferredSize().height / 2;

        return new Point(x, y);
    }

    public static boolean isFullyVisibleOnScreen(JidePopup popup, Point point) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int spreadX = point.x + popup.getContentPane().getPreferredSize().width;
        int spreadY = point.y + popup.getContentPane().getPreferredSize().height;


        return point.x >= 0 && point.y >= 0 && width > spreadX && height > spreadY;

    }

    private static Point getDesiredPosition(JidePopup popup, int location) {
        Container content = popup.getContentPane();

        final Point screenposition = new Point(popup.getOwner().getLocationOnScreen().x, popup.getOwner().getLocationOnScreen().y);

        int x = screenposition.x;
        int y = screenposition.y;

        switch (location) {
            case SwingConstants.SOUTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y;
                break;
            }
            case SwingConstants.SOUTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y;
                break;
            }
            case SwingConstants.NORTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y - popup.getOwner().getPreferredSize().height - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.NORTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width - popup.getOwner().getPreferredSize().width;
                y = screenposition.y - popup.getOwner().getPreferredSize().height - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y - (popup.getOwner().getPreferredSize().height / 2);
                break;
            }
            case SwingConstants.WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y - (popup.getOwner().getPreferredSize().height / 2);
                break;
            }
            case SwingConstants.NORTH: {
                x = screenposition.x;
                y = screenposition.y - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.CENTER: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width / 2 - content.getPreferredSize().width / 2;
                y = screenposition.y + popup.getOwner().getPreferredSize().height / 2 - content.getPreferredSize().height / 2;
                break;
            }
            default: {
                // nop
            }
        }
        return new Point(x, y);
    }


    /**
     * http://stackoverflow.com/questions/2234476/how-to-detect-the-current-display-with-java
     *
     * @param myWindow
     * @return
     */
    public static GraphicsDevice getCurrentScreen(JFrame myWindow) {

        GraphicsConfiguration config = myWindow.getGraphicsConfiguration();
        return config.getDevice();

    }

    public static Rectangle getScreenSize(GraphicsDevice myScreen) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // AFAIK - there are no guarantees that screen devices are in order...
        // but they have been on every system I've used.
        GraphicsDevice[] allScreens = env.getScreenDevices();
        int myScreenIndex = -1;
        for (int i = 0; i < allScreens.length; i++) {
            if (allScreens[i].equals(myScreen)) {
                myScreenIndex = i;
                break;
            }
        }
        //        System.out.println("window is on screen" + myScreenIndex);

        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[myScreenIndex].getDefaultConfiguration().getBounds();


    }


    /**
     * Berechnet zu einem gegebenen Jahr den Ostersonntag. Dieser wird als GregorianCalendar zurückgegeben.
     * Der Algorhythmus wurde von der Internet-Seite www.th-o.de/kalender.htm entnommen.
     * Dort wurde er von Walter Irion beschrieben. Danke, Walter und Thomas.
     * <p/>
     * Ich habe leider nicht die geringste Ahnung, was hier passiert. ;-)
     *
     * @param year
     * @return Das Datum des Ostersonntags in dem angegebene Jahr.
     * @THO99
     */
    public static GregorianCalendar Ostersonntag(int year) {
        int a, b, c, d, e, f, g, h, i, k, l, m, n, p;

        a = year % 19;

        b = year / 100;
        c = year % 100;

        d = b / 4;
        e = b % 4;

        f = (b + 8) / 25;

        g = (b - f + 1) / 3;

        h = (19 * a + b - d - g + 15) % 30;

        i = c / 4;
        k = c % 4;

        l = (32 + 2 * e + 2 * i - h - k) % 7;

        m = (a + 11 * h + 22 * l) / 451;

        n = (h + l - 7 * m + 114) / 31;
        p = (h + l - 7 * m + 114) % 31;

        return new GregorianCalendar(year, n - 1, p + 1);
    }

    public static GregorianCalendar Aschermittwoch(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -46);
        return gc;
    }

    public static GregorianCalendar Rosenmontag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -48);
        return gc;
    }

    public static GregorianCalendar Weiberfastnacht(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -52);
        return gc;
    }

    public static GregorianCalendar Ostermontag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 1);
        return gc;
    }

    public static GregorianCalendar Karfreitag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -2);
        return gc;
    }

    public static GregorianCalendar Pfingstsonntag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 49);
        return gc;
    }

    public static GregorianCalendar Pfingstmontag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 50);
        return gc;
    }

    public static GregorianCalendar ChristiHimmelfahrt(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 39);
        return gc;
    }

    public static GregorianCalendar Fronleichnam(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 60);
        return gc;
    }


    /**
     * Sucht alle Feiertage in einem Jahr zusammen.
     *
     * @return Eine Hashmap, die je das Datum als Zeichenkette der PrinterForm "jjjj-mm-tt" enthält und dazu die Bezeichnung des Feiertags.
     */
    public static HashMap<LocalDate, String> getHolidays(int from, int to) {

        HashMap<LocalDate, String> hm = new HashMap<LocalDate, String>();

        // TODO: i18n
        for (int year = from; year <= to; year++) {
            // Feste Feiertage
            hm.put(new LocalDate(year, 1, 1), "Neujahrstag");
            hm.put(new LocalDate(year, 5, 1), "Maifeiertag");
            hm.put(new LocalDate(year, 10, 3), "Tag der Einheit");
            hm.put(new LocalDate(year, 11, 1), "Allerheiligen");
            hm.put(new LocalDate(year, 12, 25), "1. Weihnachtstag");
            hm.put(new LocalDate(year, 12, 26), "2. Weihnachtstag");

            // Bewegliche Feiertage
            hm.put(new LocalDate(Karfreitag(year)), "Karfreitag");
            hm.put(new LocalDate(Ostersonntag(year)), "Ostersonntag");
            hm.put(new LocalDate(Ostermontag(year)), "Ostermontag");
            hm.put(new LocalDate(ChristiHimmelfahrt(year)), "Christi Himmelfahrt");
            hm.put(new LocalDate(Pfingstsonntag(year)), "Pfingstsonntag");
            hm.put(new LocalDate(Pfingstmontag(year)), "Pfingstmontag");
            hm.put(new LocalDate(Fronleichnam(year)), "Fronleichnam");
        }

        return hm;
    }


    /**
     * tiny method to automatically find out if the message is a language key or not.
     * <i>still an empty stub</i>
     *
     * @param message
     * @return replaced message or the original message if there is no appropriate language key.
     */
    public static String xx(String message) {

        return catchNull(message);

//           String title = Tools.catchNull(message);
//           try {
//               title = OPDE.lang.getString(message);
//           } catch (Exception e) {
//               // ok, its not a langbundle key
//           }
//           return title;
    }

}
