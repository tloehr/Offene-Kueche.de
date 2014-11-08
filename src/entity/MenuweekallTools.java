package entity;

import Main.Main;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 08.11.14.
 */
public class MenuweekallTools {


    public static Menuweekall get(LocalDate week) {
        ArrayList<Menuweekall> list = new ArrayList<Menuweekall>();

        EntityManager em = Main.getEMF().createEntityManager();
        Query queryMin = em.createQuery("SELECT t FROM Menuweekall t WHERE t.week = :week ");

        queryMin.setParameter("week", week.toDate());

        list.addAll(queryMin.getResultList());

        em.close();

        return list.isEmpty() ? null : list.get(0);

    }

}
