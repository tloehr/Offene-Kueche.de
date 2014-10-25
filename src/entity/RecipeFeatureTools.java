package entity;

import Main.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 25.10.14.
 */
public class RecipeFeatureTools {

    public static ArrayList<Recipefeature> getAll() {
        ArrayList recipeFeatures = new ArrayList<Recipefeature>();
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT rf FROM Recipefeature rf ORDER BY rf.text ");


        recipeFeatures = new ArrayList<Recipefeature>(query.getResultList());

        em.close();


        return recipeFeatures;
    }


}
