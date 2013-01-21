package entity;

import Main.Main;

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
        try {
            Main.getEM().getTransaction().begin();
            Main.getEM().persist(entity);
            Main.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            Main.getEM().getTransaction().rollback();
        }

        return success;
    }

    public static boolean merge(Object entity) {
        boolean success = false;

        try {
            Main.getEM().getTransaction().begin();
            Main.getEM().merge(entity);
            Main.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            Main.getEM().getTransaction().rollback();
        }
        return success;
    }

    public static boolean store(Object entity) {
        boolean success = false;

        try {
            Main.getEM().getTransaction().begin();
            if (Main.getEM().contains(entity)) {
                Main.getEM().merge(entity);
            } else {
                Main.getEM().persist(entity);
            }
            Main.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            Main.getEM().getTransaction().rollback();
        }
        return success;
    }

    public static boolean delete(Object entity) {
        boolean success = false;

        try {
            Main.getEM().getTransaction().begin();
            if (Main.getEM().contains(entity)) {
                Main.getEM().remove(entity);
            }
            Main.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            Main.getEM().getTransaction().rollback();
        }
        return success;
    }

}
