package entity;

import Main.Main;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 17.10.14.
 */
public class MenuweekTools {


    public static ArrayList<Menuweek> getAll(LocalDate week) {
        ArrayList<Menuweek> list = new ArrayList<Menuweek>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menuweek t WHERE t.week = :week ");

        queryMin.setParameter("week", week.toDate());

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }


}
