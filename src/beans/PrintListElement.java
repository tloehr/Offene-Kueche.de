package beans;

import entity.Vorrat;
import printer.Form;
import printer.Printer;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.02.11
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
public class PrintListElement implements Comparable {

    private Printer printer;
    private String printername; // Name des Druckers innerhalb des Betriebssystems.
    private Form form;

    @Override
    public int compareTo(Object o) {

        int result = 0;
        if (((PrintListElement) o).getObject() instanceof Vorrat){
            result = new Long(((Vorrat) object).getId()).compareTo(((Vorrat)((PrintListElement) o).getObject()).getId());
        }
        return result;
    }

    public Object getObject() {
        return object;
    }

    public PrintListElement(Object object, Printer printer, Form form, String printername) {
        this.object = object;
        this.printer = printer;
        this.form = form;
        this.printername = printername;
    }


    public String getPrintername() {
        return printername;
    }

    public Printer getPrinter() {
        return printer;
    }

    public Form getForm() {
        return form;
    }

    Object object;
}
