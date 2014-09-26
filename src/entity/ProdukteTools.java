package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 12.02.11
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 * <p/>
 * Hier habe ich Informationen aus der folgenden Publikation übernommen:
 * GS1 General Specifications, Version 10
 * Jan-2010
 * Issue 1
 * The GS1 General Specifications are the core standards document describing how barcodes and identification keys should be used to comply with GS1 standards. They are used throughout the GS1 System.
 * www.gs1.com
 * <ul>
 * <li>The GTIN-8 is the 8-digit GS1 Identification Key composed of a GS1-8 Prefix, Item Reference, and Check Digit used to identify trade items.</li>
 * <li>The GTIN-12 is the 12-digit GS1 Identification Key composed of a U.P.C. Company Prefix, Item Reference, and Check Digit used to identify trade items.</li>
 * <li>The GTIN-13 is the 13-digit GS1 Identification Key composed of a GS1 Company Prefix, Item Reference, and Check Digit used to identify trade items.</li>
 * <li>The GTIN-14 is the 14-digit GS1 Identification Key composed of an Indicator digit (1-9), GS1 Company Prefix, Item Reference, and Check Digit used to identify trade items.</li>
 * </ul>
 * <p/>
 * <h3>Identification of a Variable Measure Trade Item (GTIN): AI (01)</h3>
 * <p>The Application Identifier (01) indicates that the GS1 Application Identifier data field contains a GTIN. The GTIN is used to identify trade items (see Section 4).</p>
 * <p>The GTIN for Variable Measure Trade Items is a special application of the GTIN-14 Data Structure. The digit 9 in the Indicator position indicates that the item identified is a Variable Measure Trade Item.</p>
 * <p>Unlike GTIN-14s used to identify fixed measure trade items (see Section 2, Identification of Uniform Groupings of Trade Items), this GTIN-14 is not derived from the GTIN (without check digit) of the contained trade items.</p>
 * <p>The GTIN-14 must be processed in its entirety and not broken down into its constituent elements.</p>
 * <p>Each standard average measurement grouping must be assigned its own GTIN-14 according to the GTIN Allocation Rules.</p>
 * <p>The Check Digit is explained in Section 7.10. Its verification, which must be carried out in the application software, ensures that the number is correctly composed.</p>
 */
public class ProdukteTools {

    // EAN / GTIN Code Präfix für die Strichcodes
    public static final String IN_STORE_PREFIX = "20";

    public static final Pattern gtin8 = Pattern.compile("^\\d{8}$");
    public static final Pattern gtin12 = Pattern.compile("^\\d{12}$");
    public static final Pattern gtin13 = Pattern.compile("^\\d{13}$");
    //public static final Pattern gtin14 = Pattern.compile("^\\d{14}$");
    public static final Pattern gs1128 = Pattern.compile("^(01|02)\\d{14}");

    public static final String EINHEIT[] = {"kg", "liter", "Stueck"};


    public static ListCellRenderer<Produkte> getListCellRenderer() {
           return new ListCellRenderer<Produkte>() {
               @Override
               public Component getListCellRendererComponent(JList<? extends Produkte> list, Produkte value, int index, boolean isSelected, boolean cellHasFocus) {
                   return new DefaultListCellRenderer().getListCellRendererComponent(list, "[" + value.getId() + "] " + value.getBezeichnung(), index, isSelected, cellHasFocus);
               }
           };
       }


    public static List<Produkte> searchProdukte(String search) {
        List produkte = null;
        if (!search.equals("")) {
            if (isGTIN(search)) {
                EntityManager em = Main.getEMF().createEntityManager();
                Query query = em.createNamedQuery("Produkte.findByGtin");
                query.setParameter("gtin", getGTIN(search));

                try {
                    produkte = query.getResultList();
                } catch (Exception e1) { // nicht gefunden
                    produkte = null;
                } finally {
                    em.close();
                }
            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.
                EntityManager em = Main.getEMF().createEntityManager();
                Query query = em.createNamedQuery("Produkte.findByBezeichnungLike");
                query.setParameter("bezeichnung", "%" + search + "%");
                try {
                    produkte = query.getResultList();
                } catch (Exception e1) { // nicht gefunden
                    produkte = null;
                } finally {
                    em.close();
                }
            }
        }
        return produkte;
    }

    public static ArrayList<Produkte> searchProdukte(Warengruppe warengruppe) {
           ArrayList produkte = new ArrayList<Produkte>();
           EntityManager em = Main.getEMF().createEntityManager();
           Query query = em.createQuery("SELECT p FROM Produkte p WHERE p.ingTypes.warengruppe = :warengruppe");
           query.setParameter("warengruppe", warengruppe);
           try {
               produkte = new ArrayList<Produkte>(query.getResultList());
           } catch (Exception e1) { // nicht gefunden
               produkte = null;
           } finally {
               em.close();
           }

           return produkte;
       }

    public static ArrayList<Produkte> getProdukte(IngTypes ingTypes) {
        ArrayList produkte = new ArrayList<Produkte>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Produkte.findByStoffart");
        query.setParameter("ingTypes", ingTypes);
        try {
            produkte = new ArrayList<Produkte>(query.getResultList());
        } catch (Exception e1) { // nicht gefunden
            produkte = null;
        } finally {
            em.close();
        }

        return produkte;
    }

    public static boolean isGTIN(String s) {
        return getGTIN(s) != null;
    }

    public static String getGTIN(String search) {

        String result = null;
        if (!search.trim().equals("")) {
            StringBuffer sb = new StringBuffer(search.trim());
            Matcher mgtin8 = gtin8.matcher(sb);
            Matcher mgtin12 = gtin12.matcher(sb);
            Matcher mgtin13 = gtin13.matcher(sb);
            Matcher mgs1128 = gs1128.matcher(sb);

            if (mgtin8.find()) {
                result = mgtin8.group();
            } else if (mgtin12.find()) {
                result = mgtin12.group();
            } else if (mgtin13.find()) {
                result = mgtin13.group();
            } else if (mgs1128.find()) {
                result = mgs1128.group().substring(2); // die ersten zwei Zeichen ('01'|'02') und das Auffüllzeichen abschneiden

                // Bei Versandeinheiten wird die GTIN auf 14 Stellen aufgefüllt. Je nach Art der Einheit (Standard oder Variabel)
                // Steht hier eine 0 bzw. eine 9. Der restlichen 13 Stellen sind dann die GTIN.
                if (result.startsWith("0") | result.startsWith("9")) {
                    result = result.substring(1);
                } else {
                    result = result.substring(0, 13);
                }

            }
        }

        Main.logger.debug("GTIN parsed: " + result);

        return result;
    }

    /**
     * Bestimmt zu einem gegebenen Produkt, welcher Lieferant das zuletzt geliefert hat.
     *
     * @param produkt
     * @return der gesuchte Lieferant. NULL, wenn das Produkt noch nie geliefert wurde.
     */
    public static Lieferanten getLetzterLieferantFuer(Produkte produkt) {
        Lieferanten l;
        if (Main.cache.containsKey(produkt)) {
            l = ((Lieferanten) Main.cache.get(produkt));
        } else {
            EntityManager em = Main.getEMF().createEntityManager();
            Query query = em.createQuery(
                    " SELECT v FROM Vorrat v " +
                            " INNER JOIN v.produkt p " +
                            " WHERE v.produkt = :produkt" +
                            " ORDER BY v.eingang DESC ");
            query.setParameter("produkt", produkt);
            query.setMaxResults(1);

            try {
                Vorrat v = (Vorrat) query.getSingleResult();
                l = v.getLieferant();
                Main.cache.put(produkt, l);
            } catch (Exception e1) { // nicht gefunden
                Main.logger.fatal(e1.getMessage(), e1);
                l = null;
            } finally {
                em.close();
            }
        }
        Main.logger.debug(l.getFirma());
        return l;
    }

    public static boolean isGTINinUse(String gtin) {
        boolean gtininuse = false;
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Produkte.findByGtin");
        query.setParameter("gtin", gtin.trim());
        try {
            query.getSingleResult();
            gtininuse = true;
        } catch (NoResultException nre) { // nicht gefunden
            gtininuse = false;
        } catch (Exception exc) {
            gtininuse = true;
        } finally {
            em.close();
        }
        return gtininuse;
    }
}
