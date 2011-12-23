/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Main.Main;
import exceptions.OutOfRangeException;
import tools.Const;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.TransactionRequiredException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
        Query query = Main.getEM().createNamedQuery("Vorrat.Buchungen.summeBestand");
        BigDecimal bestand = new BigDecimal(-1);
        query.setParameter("vorrat", vorrat);
        try {
            bestand = (BigDecimal) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.getEM().getTransaction().rollback();
            Main.logger.fatal(e.getMessage(), e);
        }
        return bestand;
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
        buchungen.setMitarbeiter(Main.currentUser);
        buchungen.setText(buchungstext);

        if (summe.compareTo(menge) == 0) {
            vorrat.setAusgang(ausgang);
            buchungen.setStatus(BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG);
        } else {
            buchungen.setStatus(BuchungenTools.BUCHEN_MANUELLE_KORREKTUR);
        }

        Main.getEM().persist(buchungen);


        // Mit dieser Ausbuchung ist der Vorrat aufgebraucht.
        // Also Ausgang setzen.
        if (summe.compareTo(menge) == 0) {

            vorrat.setAusgang(ausgang);
        }

        // Falls noch nicht angebrochen.
        if (vorrat.getAnbruch().equals(tools.Const.DATE_BIS_AUF_WEITERES)) {
            vorrat.setAnbruch(ausgang);
        }
        Main.getEM().merge(vorrat);
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

    public static HashMap getVorrat4Printing(Vorrat vorrat) {
        Query query = Main.getEM().createNamedQuery("Vorrat.findMitarbeiter");
        query.setParameter("vorrat", vorrat);
        Mitarbeiter mitarbeiter = null;

        try {
            mitarbeiter = (Mitarbeiter) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.logger.fatal(e.getMessage(), e);
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
            hm.put("vorrat.info", getSummeBestand(vorrat) + " " + ProdukteTools.EINHEIT[vorrat.getProdukt().getEinheit()]);
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
    public static void tauscheProdukt(Produkte altesProdukt, Produkte neuesProdukt)
            throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {
        Query query = Main.getEM().createQuery("UPDATE Vorrat v SET v.produkt = :neu WHERE v.produkt = :alt");
        query.setParameter("neu", neuesProdukt);
        query.setParameter("alt", altesProdukt);
        Main.debug("Tausche Produkt aus: alt(" + altesProdukt + "-" + altesProdukt.getId() + ")\nneu(" + neuesProdukt + "-" + neuesProdukt.getId() + ")");
        query.executeUpdate();
    }

    /**
     * Passt die Eingangsbuchung in allen aktiven Vorräten dem Wert aus dem übergebenen Produkt an.
     *
     * @param produkt
     */
    public static void setzePackungsgroesse(Produkte produkt)
            throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {
        Query query = Main.getEM().createQuery("SELECT b FROM Buchungen b WHERE b.status = :status AND b.vorrat.ausgang = :ausgang AND b.vorrat.produkt = :produkt");
        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
        query.setParameter("produkt", produkt);
        query.setParameter("ausgang", Const.DATE_BIS_AUF_WEITERES);

        List<Buchungen> buchungen = query.getResultList();
        for (Buchungen buchung : buchungen) {
            buchung.setMenge(produkt.getPackGroesse());
            Main.getEM().merge(buchung);
        }

        Main.debug("Korrigiere Eingangsbuchungen für  Produkt(" + produkt + "-" + produkt.getId() + ")");
    }

    public static BigDecimal getEingangsbestand(Vorrat vorrat)
            throws PersistenceException {
        Query query = Main.getEM().createQuery("SELECT b FROM Buchungen b WHERE b.vorrat = :vorrat AND b.status = :status");
        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
        query.setParameter("vorrat", vorrat);

        Buchungen buchung = (Buchungen) query.getSingleResult();

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
                Query query1 = Main.getEM().createNamedQuery("Vorrat.findById");

                query1.setParameter("id", id);

                try {
                    vorrat = (Vorrat) query1.getSingleResult();
                } catch (Exception e1) {
                    vorrat = null;
                }
            }
        }
        return vorrat;
    }


    /**
     * Passt die Eingangsbuchung in allen aktiven Vorräten dem Wert aus dem übergebenen Produkt an.
     *
     * @param vorrat
     */
    public static void korregiereAnfangsbestand(Vorrat vorrat)
            throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {
        Query query = Main.getEM().createQuery("SELECT b FROM Buchungen b WHERE b.status = :status AND b.vorrat = :vorrat");
        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
        query.setParameter("vorrat", vorrat);

        List<Buchungen> buchungen = query.getResultList();
        for (Buchungen buchung : buchungen) {
            buchung.setMenge(vorrat.getProdukt().getPackGroesse());
            Main.getEM().merge(buchung);
        }

        Main.debug("Korrigiere Eingangsbuchungen für Vorrat(" + vorrat + "-" + vorrat.getId() + ")");
    }

    public static void reaktivieren(Vorrat vorrat) throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (!vorrat.isAusgebucht()) return;


        Collection<Buchungen> buchungen = vorrat.getBuchungenCollection();
        for (Buchungen buchung : buchungen) {
            if (buchung.getStatus() == BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG) {
                Main.getEM().remove(buchung);
                break;
            }
        }

        vorrat.setAusgang(Const.DATE_BIS_AUF_WEITERES);
        Main.getEM().persist(vorrat);

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
