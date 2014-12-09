package entity;

import java.util.HashSet;
import java.util.List;

/**
 * Created by tloehr on 09.12.14.
 */
public class IngTypes2RecipesTools {

    public static HashSet<Additives> getAdditives(List<Ingtypes2Recipes> it2rs) {
          HashSet<Additives> mySet = new HashSet<Additives>();
          for (Ingtypes2Recipes it2r : it2rs) {
              mySet.addAll(it2r.getIngType().getAdditives());
          }
  //        ArrayList<Additives> list = new ArrayList<Additives>(mySet);
  //        Collections.sort(list);
          return mySet;
      }

    public static HashSet<Allergene> getAllergenes(List<Ingtypes2Recipes> it2rs) {
             HashSet<Allergene> mySet = new HashSet<Allergene>();
             for (Ingtypes2Recipes it2r : it2rs) {
                 mySet.addAll(it2r.getIngType().getAllergenes());
             }
     //        ArrayList<Additives> list = new ArrayList<Additives>(mySet);
     //        Collections.sort(list);
             return mySet;
         }

}
