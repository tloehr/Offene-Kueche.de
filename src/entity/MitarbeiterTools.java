/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

/**
 *
 * @author tloehr
 */
public class MitarbeiterTools {
    public static String getUserString(Mitarbeiter mitarbeiter){
        return mitarbeiter.getName() + ", " + mitarbeiter.getVorname() + " ("+mitarbeiter.getUsername()+")";
    }

}
