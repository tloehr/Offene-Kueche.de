package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.11
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public class WarengruppeTools {
    public static Warengruppe add(String text) {
        Warengruppe warengruppe = null;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createNamedQuery("Warengruppe.findByBezeichnung");
            query.setParameter("bezeichnung", text.trim());
            if (query.getResultList().isEmpty()) {
                em.getTransaction().begin();
                warengruppe = em.merge(new Warengruppe(text.trim()));
                em.getTransaction().commit();
            } else {
                warengruppe = (Warengruppe) query.getResultList().get(0);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return warengruppe;
    }



    public static long getNumOfProducts(Warengruppe warengruppe) {
          long num = 0;
          EntityManager em = Main.getEMF().createEntityManager();
          try {
              Query query = em.createQuery("SELECT count(p) FROM Produkte p WHERE p.stoffart.warengruppe = :warengruppe");
              query.setParameter("warengruppe", warengruppe);

              num = (Long) query.getSingleResult();
          } catch (Exception e) { // nicht gefunden
              Main.fatal(e);
          } finally {
              em.close();
          }
          return num;
      }
}
