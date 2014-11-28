/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package printer;

import Main.Main;
import beans.HashBean;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import tools.DlgException;
import tools.Tools;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Diese Klasse befasst sich mit der Handhabung der speziellen Drucker für Etiketten und Kassenbons.
 * Sie enthält auch den XML Parser, der die Drucker und Vorlagen Konfigurationsdatei %workdir%/printers.xml einliest.
 *
 * @author tloehr
 */
public class Printers {

    public static final String ESCPOS_INIT_PRINTER = new String(new char[]{27, 40});
    public static final String ESCPOS_DOUBLE_HEIGHT_ON = new String(new char[]{27, 33, 16});
    public static final String ESCPOS_DOUBLE_HEIGHT_OFF = new String(new char[]{27, 33, 0});
    public static final String ESCPOS_DOUBLE_WIDTH_ON = new String(new char[]{27, 33, 32});
    public static final String ESCPOS_DOUBLE_WIDTH_OFF = new String(new char[]{27, 33, 0});
    public static final String ESCPOS_EMPHASIZED_ON = new String(new char[]{27, 33, 8});
    public static final String ESCPOS_EMPHASIZED_OFF = new String(new char[]{27, 33, 0});
    public static final String ESCPOS_UNDERLINE_ON = new String(new char[]{27, 45, 1});
    public static final String ESCPOS_UNDERLINE_OFF = new String(new char[]{27, 45, 0});
    public static final String ESCPOS_DOUBLE_STRIKE_ON = new String(new char[]{27, 71, 1});
    public static final String ESCPOS_DOUBLE_STRIKE_OFF = new String(new char[]{27, 71, 0});
    public static final String ESCPOS_PRINT_COLOR1 = new String(new char[]{27, 114, 0});
    public static final String ESCPOS_PRINT_COLOR2 = new String(new char[]{27, 114, 1});
    public static final String ESCPOS_CHARACTER_TABLE_PC437 = new String(new char[]{27, 116, 0});
    public static final String ESCPOS_CHARACTER_TABLE_PC850 = new String(new char[]{27, 116, 2});
    public static final String ESCPOS_FULL_CUT = new String(new char[]{29, 86, 65});
    public static final String ESCPOS_PARTIAL_CUT = new String(new char[]{29, 86, 66});
    public static final int DRUCK_KEIN_DRUCK = 0;
    public static final int DRUCK_ETI1 = 1;
    public static final int DRUCK_ETI2 = 2;
    public static final int DRUCK_BON1 = 3;
    public static final int DRUCK_BON2 = 4;
    public static final int DRUCK_LASER = 5;
    private final String CONFIGFILE = "printers.xml";
    //public static int[] drucker
    private HashMap<String, Printer> printers;
    private HashMap tags;
    private HashBean[] printerTypeArray;

    public Printers() {
        initTags();
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            InputSource is = new org.xml.sax.InputSource(new FileInputStream(new File(Main.props.getProperty("workdir") + System.getProperty("file.separator") + CONFIGFILE)));
            XMLHandler xml = new XMLHandler();
            parser.setContentHandler(xml);
            parser.parse(is);
        } catch (SAXException sAXException) {
            Main.logger.fatal(sAXException);
        } catch (IOException iOException) {
            Main.logger.fatal(iOException);
        }
    }

    public static String getAllPrinters() {

        String text = "";
        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);
        for (int i = 0; i < prservices.length; i++) {
            text += "  " + i + ":  " + prservices[i] + "\n";

        }
        return text;
    }

//    public Printer getPrinter(String type) {
//        Printer p = null;
//        Iterator<Printer> it = printers.iterator();
//        boolean found = false;
//        while (!found && it.hasNext()) {
//            p = it.next();
//            found = p.getType().equals(type);
//        }
//        return (!found ? null : p);
//    }

    /**
     * Standard Druck Routine. Nimmt einen HTML Text entgegen und öffnet den lokal installierten Browser damit.
     * Erstellt temporäre Dateien im temp Verzeichnis kueche<irgendwas>.html
     *
     * @param parent
     * @param html
     * @param addPrintJScript - Auf Wunsch kann an das HTML automatisch eine JScript Druckroutine angehangen werden.
     */
    public static void print(Component parent, String html, boolean addPrintJScript) {
        try {
            // Create temp file.
            File temp = File.createTempFile("kueche", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            if (addPrintJScript) {
                html = "<html><head><script type=\"text/javascript\">"
                        + "window.onload = function() {"
                        + "window.print();"
                        + "}</script>"
                        + Main.getCSS()
                        + "</head><body id=\"fonttext\">" + Tools.htmlUmlautConversion(html)
                        + "<hr/><b><div id=\"fontsmall\">Ende des Berichtes</b></div></body></html>";
            } else {
                html = "<html><head>" + Main.getCSS() + "</head><body id=\"fonttext\">" + Tools.htmlUmlautConversion(html)
                        + "<hr/><b><div id=\"fontsmall\">Ende des Berichtes</b></div></body></html>";
            }

            Main.logger.debug(html);

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(html);

            out.close();
            Tools.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
            new DlgException(e);
        }
    }

    public HashBean[] getPrinterTypeArray() {
        return printerTypeArray;
    }

    public HashMap<String, Printer> getPrinters() {
        return printers;
    }

    private void initTags() {
        tags = new HashMap(17);
        tags.put("EscposInitPrinter", ESCPOS_INIT_PRINTER);
        tags.put("EscposDoubleHeightOn", ESCPOS_DOUBLE_HEIGHT_ON);
        tags.put("EscposDoubleHeightOff", ESCPOS_DOUBLE_STRIKE_OFF);
        tags.put("EscposDoubleWidthOn", ESCPOS_DOUBLE_WIDTH_ON);
        tags.put("EscposDoubleWidthOff", ESCPOS_DOUBLE_WIDTH_OFF);
        tags.put("EscposEmphasizedOn", ESCPOS_EMPHASIZED_ON);
        tags.put("EscposEmphasizedOff", ESCPOS_EMPHASIZED_OFF);
        tags.put("EscposUnderlineOn", ESCPOS_UNDERLINE_ON);
        tags.put("EscposUnderlineOff", ESCPOS_UNDERLINE_OFF);
        tags.put("EscposDoubleStrikeOn", ESCPOS_DOUBLE_STRIKE_ON);
        tags.put("EscposDoubleStrikeOff", ESCPOS_DOUBLE_STRIKE_OFF);
        tags.put("EscposPrintColor1", ESCPOS_PRINT_COLOR1);
        tags.put("EscposPrintColor2", ESCPOS_PRINT_COLOR2);
        tags.put("EscposCharacterTablePC437", ESCPOS_CHARACTER_TABLE_PC437);
        tags.put("EscposCharacterTablePC850", ESCPOS_CHARACTER_TABLE_PC850);
        tags.put("EscposFullCut", ESCPOS_FULL_CUT);
        tags.put("EscposPartialCut", ESCPOS_PARTIAL_CUT);
    }

    /**
     *
     */
    public void print(Object printData, String printer, DocFlavor flavor) {

        // Set print attributes:
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.ISO_A4);

        try {
            DocPrintJob pj = getPrintService(printer).createPrintJob();
            Doc doc = new SimpleDoc(printData, flavor, null);
            pj.print(doc, aset);
        } catch (PrintException pe) {
            Main.logger.fatal(pe);
        }
    }

    public PrintService getPrintService(String printername) throws PrintException {

        PrintService ps = null;

//        if (psprinter) {
//            ps = postscript;
//        } else {
//            ps = label;
//        }

        if (ps == null) {

            PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);
            int idxPrintService = -1;
            for (int i = 0; i < prservices.length; i++) {
                Main.debug("  " + i + ":  " + prservices[i]);
                if (prservices[i].getName().equals(printername)) {
                    idxPrintService = i;
                }
            }
            if (idxPrintService < 0) {
                throw new PrintException("Service for " + printername + " not found.");
            }
            ps = prservices[idxPrintService];
        }

//        if (psprinter) {
//            postscript = ps;
//        } else {
//            label = ps;
//        }

        return ps;
    }

    private class XMLHandler extends DefaultHandler {

        Printer printer = null;
        HashMap<String, Form> forms = null;
        Form form = null;
        String reset = null;
        String formtext = null;
        String line = null;
        HashMap<String, ArrayList> elemAttributes = null;

        @Override
        public void startDocument() throws SAXException {
            printers = new HashMap();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (line != null) {
                line += new String(ch, start, length);
            }
        }

        @Override
        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            String name = attributes.getValue("name");
            String label = attributes.getValue("label");
            //Main.logger.debug("startElement: " + this.toString() + ": " + tagName + "    name: " + name);
            if (tagName.equalsIgnoreCase("printer")) {
                printer = new Printer(attributes.getValue("name"), attributes.getValue("label"), attributes.getValue("type"), attributes.getValue("encoding"), attributes.getValue("pageprinter"));
            } else if (tagName.equalsIgnoreCase("reset")) {
                reset = "";
            } else if (tagName.equalsIgnoreCase("cr")) {
                line += System.getProperty("line.separator");
            } else if (tagName.equalsIgnoreCase("forms")) {
                forms = new HashMap<String, Form>();
            } else if (tagName.equalsIgnoreCase("form")) {
                elemAttributes = new HashMap();
                form = new Form(name, label, elemAttributes, printer.getEncoding());
                formtext = "";
            } else if (tagName.equalsIgnoreCase("line")) {
                line = "";
            } else if (tagName.equalsIgnoreCase("elem")) {

                HashMap<String, String> attribsCopy = new HashMap();
                for (int i = 0; i < attributes.getLength(); i++) {
                    attribsCopy.put(attributes.getQName(i), attributes.getValue(i));
                }

                ArrayList<HashMap> parameterPerLine = null;
                if (elemAttributes.containsKey(name)) {
                    parameterPerLine = elemAttributes.get(name);
                } else {
                    parameterPerLine = new ArrayList();
                }
                parameterPerLine.add(attribsCopy);
                // Wenn Elemente mehrfach an verschiedenen Stellen vorkommen, wird hier jeweils die Nummer des Auftretens angehangen.
                // Die Liste wächst dann ja jeweils um ein Element an.
                // Dadurch wird die Multiline Geschichte direkt mit abgebügelt.
                line += "$" + name + parameterPerLine.size() + "$";

                elemAttributes.put(name, parameterPerLine);

            } else if (tagName.equalsIgnoreCase("char")) {
                line += new Character((char) Integer.parseInt(attributes.getValue("code")));
            } else {
                line += Tools.catchNull(tags.get(tagName));
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("reset")) {
                printer.setReset(reset);
                reset = null;
            } else if (localName.equalsIgnoreCase("form")) {
                form.setForm(formtext);
                forms.put(form.getName(), form);
                form = null;
            } else if (localName.equalsIgnoreCase("forms")) {
                printer.setForms(forms);
                forms = null;
            } else if (localName.equalsIgnoreCase("printer")) {
                printers.put(printer.getName(), printer);
                printer = null;
            } else if (localName.equalsIgnoreCase("line")) {
                if (reset != null) {
                    reset += line + System.getProperty("line.separator");
                } else if (formtext != null) {
                    formtext += line + System.getProperty("line.separator");
                }
                line = null;
            }

        }
    } // private class HandlerFragenStruktur

//    private static void printPrintServiceAttributesAndDocFlavors(PrintService prserv) {
//        String s1 = null, s2;
//        Attribute[] prattr = prserv.getAttributes().toArray();
//        DocFlavor[] prdfl = prserv.getSupportedDocFlavors();
//        if (null != prattr && 0 < prattr.length) {
//            for (int i = 0; i < prattr.length; i++) {
//                Main.Main.logger.debug("      PrintService-Attribute[" + i + "]: " + prattr[i].getName() + " = " + prattr[i]);
//            }
//        }
//        if (null != prdfl && 0 < prdfl.length) {
//            for (int i = 0; i < prdfl.length; i++) {
//                s2 = prdfl[i].getMimeType();
//                if (null != s2 && !s2.equals(s1)) {
//                    Main.Main.logger.debug("      PrintService-DocFlavor-Mime[" + i + "]: " + s2);
//                }
//                s1 = s2;
//            }
//        }
//    }
}
