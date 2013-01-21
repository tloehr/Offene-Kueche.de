package entity;

import Main.Main;

import javax.persistence.Query;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.11
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class StoffartTools {
    public static Stoffart add(String text, short einheit, Warengruppe warengruppe) {
        Stoffart stoffart = null;
        Query query = Main.getEM().createNamedQuery("Stoffart.findByBezeichnung");
        query.setParameter("bezeichnung", text.trim());
        if (query.getResultList().isEmpty()){
            stoffart = new Stoffart(text.trim(), einheit, warengruppe);
            EntityTools.persist(stoffart);
        } else {
            stoffart = (Stoffart) query.getResultList().get(0);
        }
        return stoffart;
    }

    public static void loadStoffarten(JComboBox cmb) {
        Query query = Main.getEM().createNamedQuery("Stoffart.findAllSorted");
        try {
            java.util.List stoffarten = query.getResultList();
            cmb.setModel(tools.Tools.newComboboxModel(stoffarten));
        } catch (Exception e) { // nicht gefunden
            //
        }
    }
}
