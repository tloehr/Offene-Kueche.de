package Main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jidesoft.utils.Lm;
import desktop.FrmDesktop;
import entity.Mitarbeiter;
import org.apache.commons.cli.*;
import org.apache.log4j.*;
import printer.Printers;
import tools.DlgException;
import tools.SortedProperties;
import tools.Tools;
import touch.FrmTouch;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;


/**
 * @author tloehr
 */
public class Main {

    public static int TIMEOUT = 30;
    private static final int UNKNOWN = 0;
    private static final int DESKTOP = 1;
    private static final int TOUCH = 2;
    private static int mode;

    public static Logger logger;
    private static EntityManagerFactory emf;
//    private static EntityManager em1;
    private static Mitarbeiter currentUser;
    public static SortedProperties props = new SortedProperties();
    public static SortedProperties appinfo = new SortedProperties();
    public static Printers printers;
    public static javax.swing.JFrame mainframe;
    private static boolean animation;
    private static boolean debug = false;
    private static String css = "";

    /**
     * Dieser Cache dient dazu, dass man die Suchergebnisse von Datenbankzugriffen, die man u.U. sehr häufig braucht und
     * die recht teuer sind dort ablegen kann. Dann muss man sie wenigstens während der Programmausführung nicht noch mal
     * neu laden.
     */
    public static HashMap cache;

    public static EntityManagerFactory getEMF() {
        return emf;
    }

    public static JFrame getMainframe() {
        return mainframe;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Lm.verifyLicense("Torsten Loehr", "Open-Pflege.de", "G9F4JW:Bm44t62pqLzp5woAD4OCSUAr2");

        currentUser = null;
        mode = UNKNOWN;
        cache = new HashMap();

        animation = false;

        // Das hier fängt alle ungefangenen Exceptions auf.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                new DlgException(e);
                Main.logger.error(e.getMessage());
            }
        });

        auswertungKommandozeile(args);

        try {
            // Lade Build Informationen   2
            InputStream in2 = null;
            //Class clazz = getClass();
            in2 = Main.class.getResourceAsStream("/appinfo.properties");
            appinfo.load(in2);
            in2.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        props.put("workdir", System.getProperty("user.home") + System.getProperty("file.separator") + ".kueche"); // working dir
        // LogSystem initialisieren.
        logger = Logger.getRootLogger();
        //SimpleLayout layout = new SimpleLayout();
        PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        logger.addAppender(consoleAppender);
        logger.setLevel(debug ? Level.ALL : Level.INFO);

        try {
            FileAppender fileAppender = new FileAppender(layout, props.getProperty("workdir") + System.getProperty("file.separator") + "kueche.log", true);
            logger.addAppender(fileAppender);
        } catch (IOException ex) {
            logger.fatal(props.getProperty("workdir") + ": falscher Pfad.");
            System.exit(1);
        }

        logger.info("######### START ###########");
        logger.info(Tools.getWindowTitle(""));

        loadProperties();

        if (mode == UNKNOWN) {
            mode = props.getProperty("startup").equalsIgnoreCase("desktop") ? DESKTOP : TOUCH;
        }

        // timeout
        if (props.containsKey("timeout")){
            try {
                TIMEOUT = Integer.parseInt(props.getProperty("timeout"));
            } catch (NumberFormatException nfe){
                TIMEOUT = 30;
            }
        }

        if (mode != UNKNOWN) {
            props.put("startup", mode == DESKTOP ? "desktop" : "touch");
        }
        props.put("eclipselink.session.customizer", "tools.JPAEclipseLinkSessionCustomizer");

        emf = Persistence.createEntityManagerFactory("KuechePU", props);

//        em1 = emf.createEntityManager();
        printers = new Printers();

//        logger.info(UIManager.getInstalledLookAndFeels());
//        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        css = Tools.readFileAsString(Main.props.getProperty("workdir") + System.getProperty("file.separator") + "standard.css");


//        Query q = em1.createNamedQuery("Vorrat.findById");
//        q.setParameter("id", 1l);
//        List<Vorrat> list = q.getResultList();
//        HashMap attrib = new HashMap();
//        attrib.put("produkt.bezeichnung","Hähnchenbrustfilet, natur, zerlegt, gegart, gerührt, geschüttelt und paniert");
//        attrib.put("produkt.gtin","20006419");
//        attrib.put("vorrat.eingang","12.08.2011");
//        attrib.put("vorrat.lieferant","Möllers, Köln");
//        attrib.put("system.in-store-prefix","20");
//        attrib.put("vorrat.userlang","Siepermann, Thorsten");
//        attrib.put("vorrat.id","4561");
//        logger.debug(printers.getPrinter("epl2").getForms().get("etikett-vorrat-95x48").getForm(attrib));
//
//        System.exit(0);


        //mainframe = new FrmDesktop();

        if (mode == DESKTOP) {
            mainframe = new FrmDesktop();

        } else {
            mainframe = new FrmTouch();
        }
        mainframe.setSize(1280, 1024);
        mainframe.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                logger.debug(evt.getPropertyName());
            }
        });

        mainframe.setVisible(true);

    }

    /**
     * Lädt alle Einträge aus der lokalen Properties Datei "kueche.properties"
     */
    private static void loadProperties() {
        try {
            FileInputStream in = new FileInputStream(new File(Main.props.getProperty("workdir") + System.getProperty("file.separator") + "kueche.properties"));
            Properties p = new Properties();
            p.load(in);
            Main.props.putAll(p);
            p.clear();
            in.close();
        } catch (FileNotFoundException ex) {
            // Keine local.properties. Nicht gut....
            Main.logger.fatal(Main.props.getProperty("workdir") + System.getProperty("file.separator") + "kueche.properties existiert nicht. Bitte legen Sie diese Datei an.");
            System.exit(1);
        } catch (IOException ex) {
            Main.logger.fatal(Main.props.getProperty("workdir") + System.getProperty("file.separator") + "kueche.properties nicht lesbar. Bitte korrigieren Sie das Problem.");
            System.exit(1);
        }

        checkForDefaultProps();
    }

    public static void setCurrentUser(Mitarbeiter currentUser) {
        Main.currentUser = currentUser;
    }

    public static void debug(Object msg) {
        logger.debug(msg);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void error(Object msg) {
        logger.error(msg);
    }


    public static void fatal(Object msg) {
        logger.fatal(msg);
        new DlgException((Throwable) msg);
        System.exit(1);
    }

//    public static EntityManager getEM() {
//        return em1;
//    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean isAnimation() {
        return animation;
    }

    public static Properties getProps() {
        return props;
    }

    public static Mitarbeiter getCurrentUser() {
        return currentUser;
    }

    public static String getCSS() {
        return css;
    }

    static void auswertungKommandozeile(String[] args) {
        // Hier erfolgt die Unterscheidung, in welchem Modus OPDE gestartet wurde.
        Options opts = new Options();
        //opts.addOption("m", "mode", true, "Legt den Modus fest, in dem das Programm gestartet wird. Mögliche Werte sind desktop oder touch.");

        Option desktopmode = OptionBuilder
                .hasOptionalArg()
                .withLongOpt("mode")
                .withDescription("Legt den Modus fest, in dem das Programm gestartet wird. Mögliche Werte sind desktop oder touch.")
                .create("m");
        opts.addOption(desktopmode);

        Option debugmode = OptionBuilder
                .hasOptionalArg()
                .withLongOpt("debug")
                .withDescription("Schaltet die Debug-Ausgaben ein.")
                .create("d");
        opts.addOption(debugmode);

        BasicParser parser = new BasicParser();
        CommandLine cl = null;
        String footer = "http://www.Offene-Pflege.de";

        try {
            cl = parser.parse(opts, args);
        } catch (ParseException ex) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("Kueche.jar [OPTION]", "Version " + getProps().getProperty("program.VERSION")
                    + " Build:" + getProps().getProperty("program.BUILDNUM"), opts, footer);

//            f.printHelp("Kueche.jar [OPTION]", "Offene-Pflege.de, Version " + OPDE.getLocalProps().getProperty("program.VERSION")
//                    + " Build:" + OPDE.getLocalProps().getProperty("program.BUILDNUM"), opts, footer);
            System.exit(0);
        }

        if (cl.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("Kueche.jar [OPTION]", "Version " + getProps().getProperty("program.VERSION")
                    + " Build:" + getProps().getProperty("program.BUILDNUM"), opts, footer);
            System.exit(0);
        }

        debug = cl.hasOption("d");

        if (cl.hasOption("m")) {
            String climode = cl.getOptionValue("m");
            mode = climode.equalsIgnoreCase("desktop") ? DESKTOP : TOUCH;
        } else {
            mode = UNKNOWN;
        }

    }

    private static void checkForDefaultProps() {
        if (!props.containsKey("startup")) {
            props.put("startup", "desktop");
        }

        if (!props.containsKey("touch1einheit")) {
            props.put("touch1einheit", "0");
        }

        if (!props.containsKey("touch1lagerart")) {
            props.put("touch1lagerart", "0");
        }

        if (!props.containsKey("touch1lager")) {
            props.put("touch1lager", "0");
        }

        if (!props.containsKey("touch3lager")) {
            props.put("touch3lager", "0");
        }

        if (!props.containsKey("touch1lieferant")) {
            props.put("touch1lieferant", "0");
        }

        if (!props.containsKey("touch1checkinprintlabel")) {
            props.put("touch1printlabel", "true");
        }

        if (!props.containsKey("maxfaktor")) {
            props.put("maxfaktor", "110");
        }

        if (!props.containsKey("sound")) {
            props.put("sound", "off");
        }
    }
}
