/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Main.Main;
import exceptions.OutOfRangeException;
import tools.Const;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.TransactionRequiredException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author tloehr
 */
public class VorratTools {

    /**
     * Ermittelt die Bestandssumme eines bestimmten Vorrats.
     *
     * @param vorrat
     * @return
     */
    public static BigDecimal getSummeBestand(Vorrat vorrat) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT SUM(b.menge) FROM Buchungen b JOIN b.vorrat v WHERE b.vorrat = :vorrat");
        BigDecimal bestand = new BigDecimal(-1);
        query.setParameter("vorrat", vorrat);
        try {
            bestand = (BigDecimal) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            em.getTransaction().rollback();
            Main.logger.fatal(e.getMessage(), e);
        } finally {
            em.close();
        }
        return bestand;
    }


    public static BigDecimal getSumme(Vorrat vorrat) {
        BigDecimal bestand = BigDecimal.ZERO;

        for (Buchungen buchung : vorrat.getBuchungenCollection()) {
            bestand = bestand.add(buchung.getMenge());
        }

        return bestand;
    }


    public static Vorrat ausbuchen(EntityManager em, Vorrat vorrat, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        return ausbuchen(em, vorrat, getSumme(vorrat), buchungstext);

    }


    public static Vorrat ausbuchen(EntityManager em, Vorrat vorrat, BigDecimal menge, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (vorrat.isAusgebucht()) return null;

        BigDecimal summe = getSumme(vorrat);
        if (summe.compareTo(menge) < 0) {
            throw new OutOfRangeException(BigDecimal.ZERO, summe, menge);
        }

        Vorrat myVorrat = em.merge(vorrat);
        em.lock(myVorrat, LockModeType.OPTIMISTIC);

        Date ausgang = new Date();

        Buchungen buchungen = em.merge(new Buchungen(menge.negate(), ausgang));
        em.lock(buchungen, LockModeType.OPTIMISTIC);
        buchungen.setVorrat(myVorrat);
        buchungen.setMitarbeiter(Main.getCurrentUser());
        buchungen.setText(buchungstext);

        if (summe.compareTo(menge) == 0) {
            myVorrat.setAusgang(ausgang);
            buchungen.setStatus(BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG);
        } else {
            buchungen.setStatus(BuchungenTools.BUCHEN_MANUELLE_KORREKTUR);
        }

        // Mit dieser Ausbuchung ist der Vorrat aufgebraucht.
        // Also Ausgang setzen.
        if (summe.compareTo(menge) == 0) {
            myVorrat.setAusgang(ausgang);
        }

        // Falls noch nicht angebrochen.
        if (myVorrat.getAnbruch().equals(tools.Const.DATE_BIS_AUF_WEITERES)) {
            myVorrat.setAnbruch(ausgang);
        }

        return myVorrat;

    }

    /**
     * Bucht eine bestimmte Menge von einem Vorrat aus.
     *
     * @param vorrat
     * @param menge
     * @param buchungstext
     * @throws OutOfRangeException
     */
    public static void ausbuchen(Vorrat vorrat, BigDecimal menge, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (vorrat.isAusgebucht()) return;

        BigDecimal summe = getSummeBestand(vorrat);
        if (summe.compareTo(menge) < 0) {
            throw new OutOfRangeException(BigDecimal.ZERO, summe, menge);
        }

        Date ausgang = new Date();


        Buchungen buchungen = new Buchungen(menge.negate(), ausgang);
        buchungen.setVorrat(vorrat);
        buchungen.setMitarbeiter(Main.getCurrentUser());
        buchungen.setText(buchungstext);

        if (summe.compareTo(menge) == 0) {
            vorrat.setAusgang(ausgang);
            buchungen.setStatus(BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG);
        } else {
            buchungen.setStatus(BuchungenTools.BUCHEN_MANUELLE_KORREKTUR);
        }

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(buchungen);
            Vorrat myVorrat = em.merge(vorrat);

            em.lock(myVorrat, LockModeType.OPTIMISTIC);

            // Mit dieser Ausbuchung ist der Vorrat aufgebraucht.
            // Also Ausgang setzen.
            if (summe.compareTo(menge) == 0) {
                myVorrat.setAusgang(ausgang);
            }

            // Falls noch nicht angebrochen.
            if (myVorrat.getAnbruch().equals(tools.Const.DATE_BIS_AUF_WEITERES)) {
                myVorrat.setAnbruch(ausgang);
            }

            em.getTransaction().commit();
        } catch (Exception e1) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Bucht den gesamten Bestand aus einem Vorrat aus und schließt diesen damit ab.
     *
     * @param vorrat
     * @param buchungstext
     */
    public static void ausbuchen(Vorrat vorrat, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {
        if (vorrat.isAusgebucht()) return;

        ausbuchen(vorrat, getSummeBestand(vorrat), buchungstext);
    }


    public static ArrayList<Vorrat> getActiveStocks(Produkte product) {
        ArrayList stocks = new ArrayList<Vorrat>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT v FROM Vorrat v WHERE v.produkt = :product AND v.ausgang = :tfn");
        query.setParameter("product", product);
        query.setParameter("tfn", Const.DATE_BIS_AUF_WEITERES);
        try {
            stocks = new ArrayList<Produkte>(query.getResultList());
        } catch (Exception e1) { // nicht gefunden
            stocks = null;
        } finally {
            em.close();
        }

        return stocks;
    }

    public static HashMap getVorrat4Printing(Vorrat vorrat) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT b.mitarbeiter FROM Buchungen b JOIN b.vorrat v WHERE b.status = 1 and b.vorrat = :vorrat");
        query.setParameter("vorrat", vorrat);
        Mitarbeiter mitarbeiter = null;

        try {
            mitarbeiter = (Mitarbeiter) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.logger.fatal(e.getMessage(), e);
        } finally {
            em.close();
        }

        Main.logger.debug("Vorrat ID: " + vorrat.getId());

        HashMap hm = new HashMap();
        hm.put("produkt.bezeichnung", vorrat.getProdukt().getBezeichnung());
        hm.put("system.in-store-prefix", ProdukteTools.IN_STORE_PREFIX);
        hm.put("vorrat.id", vorrat.getId());
        hm.put("vorrat.lieferant", vorrat.getLieferant().getFirma());
        if (vorrat.getProdukt().getGtin() != null) {
            hm.put("produkt.gtin", vorrat.getProdukt().getGtin());
            hm.put("vorrat.info", vorrat.getProdukt().getGtin());
        } else {
            hm.put("produkt.gtin", "--");
            hm.put("vorrat.info", getSummeBestand(vorrat) + " " + IngTypesTools.EINHEIT[vorrat.getProdukt().getIngTypes().getEinheit()]);
        }


        hm.put("vorrat.eingang", vorrat.getEingang());
        hm.put("vorrat.userlang", MitarbeiterTools.getUserString(mitarbeiter));
        hm.put("vorrat.userkurz", mitarbeiter.getUsername());
        hm.put("vorrat.lieferant", vorrat.getLieferant().getFirma() + ", " + vorrat.getLieferant().getOrt());
        return hm;
    }


    /**
     * Ersetzt ein Produkt durch ein anderes in allen Vorräten.
     *
     * @param altesProdukt Produkt, dass ersetzt werden soll.
     * @param neuesProdukt Produkt, das als Austausch vorgesehen ist.
     */
//    public static void tauscheProdukt(EntityManager em, Produkte altesProdukt, Produkte neuesProdukt)
//            throws java.lang.IllegalStateException,
//            TransactionRequiredException,
//            PersistenceException {
//        Query query = em.createQuery("UPDATE Vorrat v SET v.produkt = :neu WHERE v.produkt = :alt");
//        query.setParameter("neu", neuesProdukt);
//        query.setParameter("alt", altesProdukt);
//        Main.debug("Tausche Produkt aus: alt(" + altesProdukt + "-" + altesProdukt.getId() + ")\nneu(" + neuesProdukt + "-" + neuesProdukt.getId() + ")");
//        query.executeUpdate();
//    }

    /**
     * Passt die Eingangsbuchung in allen aktiven Vorräten dem Wert aus dem übergebenen Produkt an.
     *
     * @param produkt
     */
    public static void setzePackungsgroesse(EntityManager em, Produkte produkt)
            throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {
        Query query = em.createQuery("SELECT b FROM Buchungen b WHERE b.status = :status AND b.vorrat.ausgang = :ausgang AND b.vorrat.produkt = :produkt");
        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
        query.setParameter("produkt", produkt);
        query.setParameter("ausgang", Const.DATE_BIS_AUF_WEITERES);

        List<Buchungen> buchungen = query.getResultList();
        for (Buchungen buchung : buchungen) {
            buchung.setMenge(produkt.getPackGroesse());
            em.merge(buchung);
        }

        Main.debug("Korrigiere Eingangsbuchungen für  Produkt(" + produkt + "-" + produkt.getId() + ")");
    }


    public static void setzePackungsgroesse(Produkte produkt) {
        for (Vorrat vorrat : produkt.getVorratCollection()) {
            if (!vorrat.isAusgebucht()) {
                for (Buchungen buchung : vorrat.getBuchungenCollection()) {
                    if (buchung.getStatus() == BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
                        buchung.setMenge(produkt.getPackGroesse());
                    }
                }
            }
        }

//        Main.debug("Korrigiere Eingangsbuchungen für  Produkt(" + produkt + "-" + produkt.getId() + ")");
    }

    public static BigDecimal getEingangsbestand(Vorrat vorrat)
            throws PersistenceException {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT b FROM Buchungen b WHERE b.vorrat = :vorrat AND b.status = :status");
        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
        query.setParameter("vorrat", vorrat);

        Buchungen buchung = (Buchungen) query.getSingleResult();
        em.close();
        return buchung.getMenge();
    }

    /**
     * Findet zu einem Such String (Nummer oder Barcode Scan) einen passenden Vorrat (PK).
     *
     * @param search
     * @return gefundener Vorrat, null sonst.
     */
    public static Vorrat findByIDORScanner(String search) {
        String suchtext = search.trim();
        Vorrat vorrat = null;
        long id = 0l;
        if (!suchtext.isEmpty()) {
            // Was genau wird gesucht ?
            if (suchtext.matches("^" + ProdukteTools.IN_STORE_PREFIX + "\\d{11}")) { // VorratID in EAN 13 Kodiert mit in-store Präfix (z.B. 20)
                // Ausschneiden der VorID aus dem EAN Code. IN-STORE-PREFIX und die Prüfsummenziffer weg.
                suchtext = Long.toString(Long.parseLong(suchtext.substring(2, 12)));
            }

            if (suchtext.matches("^\\d+")) { // Nur Ziffern, kann nur VorratID von Hand eingetippt sein oder bereits vorbearbeitet.
                try {
                    id = Long.parseLong(suchtext);
                } catch (NumberFormatException nfe) {
                    id = 0l;
                }
            }

            if (id != 0) {
                EntityManager em = Main.getEMF().createEntityManager();

                try {
                    vorrat = em.find(Vorrat.class, id);
                } catch (Exception e1) {
                    vorrat = null;
                } finally {
                    em.close();
                }
            }
        }
        return vorrat;
    }

//
//    /**
//     * Passt die Eingangsbuchung in allen aktiven Vorräten dem Wert aus dem übergebenen Produkt an.
//     *
//     * @param vorrat
//     */
//    public static void korrigiereAnfangsbestand(EntityManager em, Vorrat vorrat)
//            throws java.lang.IllegalStateException,
//            TransactionRequiredException,
//            PersistenceException {
//
////        Query query = em.createQuery("SELECT b FROM Buchungen b WHERE b.status = :status AND b.vorrat = :vorrat");
////        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
////        query.setParameter("vorrat", vorrat);
//
////        List<Buchungen> buchungen = query.getResultList();
//        for (Buchungen buchung : vorrat.getBuchungenCollection()) {
//            if (buchung.getStatus() == BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
//                buchung.setMenge(vorrat.getProdukt().getPackGroesse());
//            }
//            em.merge(buchung);
//        }
//
//        Main.debug("Korrigiere Eingangsbuchungen für Vorrat(" + vorrat + "-" + vorrat.getId() + ")");
//    }

    public static void reaktivieren(EntityManager em, Vorrat vorrat) throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (!vorrat.isAusgebucht()) return;


        Collection<Buchungen> buchungen = vorrat.getBuchungenCollection();
        for (Buchungen b : buchungen) {
            if (b.getStatus() == BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG) {
                Buchungen buchung = em.merge(b);
                em.lock(buchung, LockModeType.OPTIMISTIC);
                em.remove(buchung);
                break;
            }
        }

        vorrat.setAusgang(Const.DATE_BIS_AUF_WEITERES);
        em.persist(vorrat);

        Main.debug("Reaktiviere Vorrat(" + vorrat + "-" + vorrat.getId() + ")");
    }

    public static Mitarbeiter getEingangMA(Vorrat vorrat) {
        Mitarbeiter ma = null;
        Collection<Buchungen> buchungen = vorrat.getBuchungenCollection();
        for (Buchungen buchung : buchungen) {
            if (buchung.getStatus() == BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
                ma = buchung.getMitarbeiter();
                break;
            }
        }
        return ma;
    }

    public static Mitarbeiter getAusgangMA(Vorrat vorrat) {
        Mitarbeiter ma = null;
        Collection<Buchungen> buchungen = vorrat.getBuchungenCollection();
        for (Buchungen buchung : buchungen) {
            if (buchung.getStatus() == BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG) {
                ma = buchung.getMitarbeiter();
                break;
            }
        }
        return ma;
    }
}
