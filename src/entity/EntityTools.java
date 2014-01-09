package entity;

import Main.Main;

import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 09.07.11
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class EntityTools {

    public static boolean persist(Object entity) {
        boolean success = false;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return success;
    }

    public static boolean merge(Object entity) {
        boolean success = false;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    public static boolean delete(Object entity) {
        boolean success = false;

        EntityManager em = Main.getEMF().createEntityManager();

        try {
            em.getTransaction().begin();
            if (em.contains(entity)) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

}
