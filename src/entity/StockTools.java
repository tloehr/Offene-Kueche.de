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
import javax.swing.*;
import javax.transaction.TransactionRequiredException;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class StockTools {

    public static ListCellRenderer<Stock> getListCellRenderer() {
        return new ListCellRenderer<Stock>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Stock> list, Stock value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, value.toString(), index, isSelected, cellHasFocus);
            }
        };
    }


    /**
     * Ermittelt die Bestandssumme eines bestimmten Vorrats.
     *
     * @param stock
     * @return
     */
    public static BigDecimal getSummeBestand(Stock stock) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT SUM(b.menge) FROM Buchungen b JOIN b.stock v WHERE b.stock = :stock");
        BigDecimal bestand = new BigDecimal(-1);
        query.setParameter("stock", stock);
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


    public static BigDecimal getSumme(Stock stock) {
        BigDecimal bestand = BigDecimal.ZERO;

        for (Buchungen buchung : stock.getBuchungenCollection()) {
            bestand = bestand.add(buchung.getMenge());
        }

        return bestand;
    }


    public static Stock ausbuchen(EntityManager em, Stock stock, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        return ausbuchen(em, stock, getSumme(stock), buchungstext);

    }


    public static Stock ausbuchen(EntityManager em, Stock stock, BigDecimal menge, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (stock.isAusgebucht()) return null;

        BigDecimal summe = getSumme(stock);
        if (summe.compareTo(menge) < 0) {
            throw new OutOfRangeException(BigDecimal.ZERO, summe, menge);
        }

        Stock myStock = em.merge(stock);
        em.lock(myStock, LockModeType.OPTIMISTIC);

        Date ausgang = new Date();

        Buchungen buchungen = em.merge(new Buchungen(menge.negate(), ausgang));
        em.lock(buchungen, LockModeType.OPTIMISTIC);
        buchungen.setStock(myStock);
        buchungen.setMitarbeiter(Main.getCurrentUser());
        buchungen.setText(buchungstext);

        if (summe.compareTo(menge) == 0) {
            myStock.setAusgang(ausgang);
            buchungen.setStatus(BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG);
        } else {
            buchungen.setStatus(BuchungenTools.BUCHEN_MANUELLE_KORREKTUR);
        }

        // Mit dieser Ausbuchung ist der Vorrat aufgebraucht.
        // Also Ausgang setzen.
        if (summe.compareTo(menge) == 0) {
            myStock.setAusgang(ausgang);
        }

        // Falls noch nicht angebrochen.
        if (myStock.getAnbruch().equals(tools.Const.DATE_BIS_AUF_WEITERES)) {
            myStock.setAnbruch(ausgang);
        }

        return myStock;

    }

    /**
     * Bucht eine bestimmte Menge von einem Vorrat aus.
     *
     * @param stock
     * @param menge
     * @param buchungstext
     * @throws OutOfRangeException
     */
    public static void ausbuchen(Stock stock, BigDecimal menge, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (stock.isAusgebucht()) return;

        BigDecimal summe = getSummeBestand(stock);
        if (summe.compareTo(menge) < 0) {
            throw new OutOfRangeException(BigDecimal.ZERO, summe, menge);
        }

        Date ausgang = new Date();


        Buchungen buchungen = new Buchungen(menge.negate(), ausgang);
        buchungen.setStock(stock);
        buchungen.setMitarbeiter(Main.getCurrentUser());
        buchungen.setText(buchungstext);

        if (summe.compareTo(menge) == 0) {
            stock.setAusgang(ausgang);
            buchungen.setStatus(BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG);
        } else {
            buchungen.setStatus(BuchungenTools.BUCHEN_MANUELLE_KORREKTUR);
        }

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(buchungen);
            Stock myStock = em.merge(stock);

            em.lock(myStock, LockModeType.OPTIMISTIC);

            // Mit dieser Ausbuchung ist der Vorrat aufgebraucht.
            // Also Ausgang setzen.
            if (summe.compareTo(menge) == 0) {
                myStock.setAusgang(ausgang);
            }

            // Falls noch nicht angebrochen.
            if (myStock.getAnbruch().equals(tools.Const.DATE_BIS_AUF_WEITERES)) {
                myStock.setAnbruch(ausgang);
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
     * @param stock
     * @param buchungstext
     */
    public static void ausbuchen(Stock stock, String buchungstext) throws OutOfRangeException, IllegalStateException,
            TransactionRequiredException,
            PersistenceException {
        if (stock.isAusgebucht()) return;

        ausbuchen(stock, getSummeBestand(stock), buchungstext);
    }


    public static ArrayList<Stock> getActiveStocks(Produkte product) {
        ArrayList stocks = new ArrayList<Stock>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT v FROM Stock v WHERE v.produkt = :product AND v.ausgang = :tfn");
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

    public static ArrayList<Stock> getAll() {
           ArrayList stocks = new ArrayList<Stock>();
           EntityManager em = Main.getEMF().createEntityManager();
           Query query = em.createQuery("SELECT v FROM Stock v ORDER BY v.produkt.bezeichnung ");
           try {
               stocks = new ArrayList<Produkte>(query.getResultList());
           } catch (Exception e1) { // nicht gefunden
               stocks = null;
           } finally {
               em.close();
           }

           return stocks;
       }

    public static ArrayList<Stock> getActiveStocks() {
        ArrayList stocks = new ArrayList<Stock>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT v FROM Stock v WHERE v.ausgang = :tfn ORDER BY v.produkt.bezeichnung ");
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

    public static HashMap getVorrat4Printing(Stock stock) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT b.mitarbeiter FROM Buchungen b JOIN b.stock s WHERE b.status = 1 and b.stock = :stock");
        query.setParameter("stock", stock);
        Mitarbeiter mitarbeiter = null;

        try {
            mitarbeiter = (Mitarbeiter) query.getSingleResult();
        } catch (Exception e) { // nicht gefunden
            Main.logger.fatal(e.getMessage(), e);
        } finally {
            em.close();
        }

        Main.logger.debug("Vorrat ID: " + stock.getId());

        HashMap hm = new HashMap();
        hm.put("produkt.bezeichnung", stock.getProdukt().getBezeichnung());
        hm.put("system.in-store-prefix", ProdukteTools.IN_STORE_PREFIX);
        hm.put("vorrat.id", stock.getId());
        hm.put("vorrat.lieferant", stock.getLieferant().getFirma());
        if (stock.getProdukt().getGtin() != null) {
            hm.put("produkt.gtin", stock.getProdukt().getGtin());
            hm.put("vorrat.info", stock.getProdukt().getGtin());
        } else {
            hm.put("produkt.gtin", "--");
            hm.put("vorrat.info", getSummeBestand(stock) + " " + IngTypesTools.EINHEIT[stock.getProdukt().getIngTypes().getEinheit()]);
        }


        hm.put("vorrat.eingang", stock.getEingang());
        hm.put("vorrat.userlang", MitarbeiterTools.getUserString(mitarbeiter));
        hm.put("vorrat.userkurz", mitarbeiter.getUsername());
        hm.put("vorrat.lieferant", stock.getLieferant().getFirma() + ", " + stock.getLieferant().getOrt());
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
        Query query = em.createQuery("SELECT b FROM Buchungen b WHERE b.status = :status AND b.stock.ausgang = :ausgang AND b.stock.produkt = :produkt");
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
        for (Stock stock : produkt.getStockCollection()) {
            if (!stock.isAusgebucht()) {
                for (Buchungen buchung : stock.getBuchungenCollection()) {
                    if (buchung.getStatus() == BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
                        buchung.setMenge(produkt.getPackGroesse());
                    }
                }
            }
        }

//        Main.debug("Korrigiere Eingangsbuchungen für  Produkt(" + produkt + "-" + produkt.getId() + ")");
    }

    public static BigDecimal getEingangsbestand(Stock stock)
            throws PersistenceException {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT b FROM Buchungen b WHERE b.stock = :stock AND b.status = :status");
        query.setParameter("status", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
        query.setParameter("stock", stock);

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
    public static Stock findByIDORScanner(String search) {
        String suchtext = search.trim();
        Stock stock = null;
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
                    stock = em.find(Stock.class, id);
                } catch (Exception e1) {
                    stock = null;
                } finally {
                    em.close();
                }
            }
        }
        return stock;
    }


    public static HashSet<Additives> getAdditives(Set<Stock> stocks) {
        HashSet<Additives> mySet = new HashSet<Additives>();
        for (Stock stock : stocks) {
            mySet.addAll(stock.getProdukt().getAdditives());
        }
//        ArrayList<Additives> list = new ArrayList<Additives>(mySet);
//        Collections.sort(list);
        return mySet;
    }

    public static HashSet<Allergene> getAllergenes(Set<Stock> stocks) {

        HashSet<Allergene> mySet = new HashSet<Allergene>();
        for (Stock stock : stocks) {
            mySet.addAll(stock.getProdukt().getAllergenes());
        }

        Main.debug(mySet.size());
//        ArrayList<Allergene> list = new ArrayList<Allergene>(mySet);
//        Collections.sort(list);
        return mySet;
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

    public static void reaktivieren(EntityManager em, Stock stock) throws java.lang.IllegalStateException,
            TransactionRequiredException,
            PersistenceException {

        if (!stock.isAusgebucht()) return;


        Collection<Buchungen> buchungen = stock.getBuchungenCollection();
        for (Buchungen b : buchungen) {
            if (b.getStatus() == BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG) {
                Buchungen buchung = em.merge(b);
                em.lock(buchung, LockModeType.OPTIMISTIC);
                em.remove(buchung);
                break;
            }
        }

        stock.setAusgang(Const.DATE_BIS_AUF_WEITERES);
        em.persist(stock);

        Main.debug("Reaktiviere Vorrat(" + stock + "-" + stock.getId() + ")");
    }

//    public static Mitarbeiter getEingangMA(Stock stock) {
//        Mitarbeiter ma = null;
//        Collection<Buchungen> buchungen = stock.getBuchungenCollection();
//        for (Buchungen buchung : buchungen) {
//            if (buchung.getStatus() == BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
//                ma = buchung.getMitarbeiter();
//                break;
//            }
//        }
//        return ma;
//    }
//
//    public static Mitarbeiter getAusgangMA(Stock stock) {
//        Mitarbeiter ma = null;
//        Collection<Buchungen> buchungen = stock.getBuchungenCollection();
//        for (Buchungen buchung : buchungen) {
//            if (buchung.getStatus() == BuchungenTools.BUCHEN_ABSCHLUSSBUCHUNG) {
//                ma = buchung.getMitarbeiter();
//                break;
//            }
//        }
//        return ma;
//    }
}
