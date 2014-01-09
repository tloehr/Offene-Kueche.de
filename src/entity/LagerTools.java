package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.07.11
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class LagerTools {

    public static final String LAGERART[] = {"Gekühlt unter 7°C", "Gekühlt unter 4°C", "TK (-18°C)", "Trockenlager", "Normal, unter 20°C"};
    public static final short LAGERART_UNBEKANNT = 0; // Spezielle Art, die nur zur Markierung des "UNBEKANNT" Lagers verwendet wird.
    public static final short LAGERART_UNTER_7 = 1;
    public static final short LAGERART_UNTER_4 = 2;
    public static final short LAGERART_TK = 3;
    public static final short LAGERART_TROCKENLAGER = 4;
    public static final short LAGERART_NORMAL = 5;

    public static Lager add(String bezeichnung) {
        Lager lager = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Lager.findByBezeichnung");
        query.setParameter("bezeichnung", bezeichnung.trim());
        if (query.getResultList().isEmpty()) {
            lager = new Lager(bezeichnung, LAGERART_TROCKENLAGER, "");
            EntityTools.persist(lager);
        } else {
            lager = (Lager) query.getResultList().get(0);
        }
        em.close();
        return lager;
    }

    /**
     * Gibt genau das eine Lager zurück, dass als Unbekannt verwendet wird.
     * (Lagerart == 0)
     * @return
     */
    public static Lager getUnbekannt(){
        Lager lager = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Lager.findByLagerart");
        query.setParameter("lagerart", LAGERART_UNBEKANNT);
        lager = (Lager) query.getSingleResult();
        em.close();
        return lager;
    }
}
