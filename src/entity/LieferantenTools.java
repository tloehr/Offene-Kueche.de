package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

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
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT l FROM Lieferanten l WHERE l.firma = :firma");
            query.setParameter("firma", firma.trim());
            if (query.getResultList().isEmpty()) {
                em.getTransaction().begin();
                lieferant = em.merge(new Lieferanten(firma.trim()));
                em.getTransaction().commit();
            } else {
                lieferant = (Lieferanten) query.getResultList().get(0);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return lieferant;
    }


    public static ArrayList<Lieferanten> getAll() {
        ArrayList<Lieferanten> list = new ArrayList<Lieferanten>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT l FROM Lieferanten l ORDER BY l.firma ASC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }
}
