package entity;

import Main.Main;

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
        Query query = Main.getEM().createNamedQuery("Warengruppe.findByBezeichnung");
        query.setParameter("bezeichnung", text.trim());
        if (query.getResultList().isEmpty()) {
            warengruppe = new Warengruppe(text.trim());
            EntityTools.persist(warengruppe);
        } else {
            warengruppe = (Warengruppe) query.getResultList().get(0);
        }
        return warengruppe;
    }
}
