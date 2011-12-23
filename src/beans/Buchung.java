/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import Main.Main;
import entity.Lager;
import entity.Produkte;
import printer.Printers;
import tools.Const;

import java.math.BigDecimal;

/**
 *
 * @author tloehr
 */
public class Buchung {

    Produkte produkt;

    public void setFaktor(int faktor) {
        this.faktor = faktor;
        Main.debug(toString());
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
        Main.debug(toString());
    }

    public void setProdukt(Produkte produkt) {
        this.produkt = produkt;
        Main.debug(toString());
    }
    Lager lager;

    public Lager getLager() {
        return lager;
    }

    public void setLager(Lager lager) {
        this.lager = lager;
        Main.debug(toString());
    }

    public void setPrinter(int printer) {
        this.printer = printer;
        Main.debug(toString());
    }

    public int getFaktor() {
        return faktor;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public int getPrinter() {
        return printer;
    }

    public Produkte getProdukt() {
        return produkt;
    }
    int faktor, printer;
    BigDecimal menge;

    public Buchung(Produkte produkt, int faktor, BigDecimal menge, int printer, Lager lager) {
        this.produkt = produkt;
        this.faktor = faktor;
        this.menge = menge;
        this.printer = printer;
        this.lager = lager;
        Main.debug(toString());
    }

    public Buchung() {
        produkt = null;
        faktor = 0;
        menge = BigDecimal.ZERO;
        printer = Printers.DRUCK_ETI1;
        lager = null;
        Main.debug(toString());
    }

    @Override
    public String toString() {
        return "Buchung{" +
                "produkt=" + produkt +
                ", lager=" + lager +
                ", faktor=" + faktor +
                ", printer=" + printer +
                ", menge=" + menge +
                '}';
    }
}
