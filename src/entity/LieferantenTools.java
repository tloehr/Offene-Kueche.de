package entity;

import Main.Main;

import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 09.07.11
 * Time: 12:24
 * To change this template use File | Settings | File Templates.
 */
public class LieferantenTools {

    public static Lieferanten add(String firma) {
        Lieferanten lieferant = null;
        Query query = Main.getEM().createNamedQuery("Lieferanten.findByFirma");
        query.setParameter("firma", firma.trim());
        if (query.getResultList().isEmpty()){
            lieferant = new Lieferanten(firma.trim());
            EntityTools.persist(lieferant);
        } else {
            lieferant = (Lieferanten) query.getResultList().get(0);
        }
        return lieferant;
    }
}
