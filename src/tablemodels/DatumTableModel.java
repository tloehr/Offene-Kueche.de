/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablemodels;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.table.DefaultTableModel;

/**
 * Dieses TableModel zeigt einfach eine Tabelle mit einer Spalte
 * und sieben Zeilen an. In den Zellen steht jeweils das Tagesdatum.
 * Der Konstruktor erhält jeweils ein Datum aus der KW das angezeigt werden
 * soll.
 * @author tloehr
 */
public class DatumTableModel extends DefaultTableModel {
    GregorianCalendar datum;
    Format formatter;

    public DatumTableModel(GregorianCalendar datum) {
        this.datum = startOfWeek(datum);
        datum.get(GregorianCalendar.WEEK_OF_YEAR);
        formatter = new SimpleDateFormat("EEEE, dd.MM.yyyy");
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        GregorianCalendar gc = (GregorianCalendar) datum.clone();
        gc.add(GregorianCalendar.DATE, row);
        Date date = new Date(gc.getTimeInMillis());

        return formatter.format(date);
    }

    private GregorianCalendar startOfWeek(GregorianCalendar someDate){
        int day = someDate.get(GregorianCalendar.DAY_OF_WEEK);
        if (day == 1) {day = 8;}
        int offset = (day-2)*-1;
        someDate.add(GregorianCalendar.DATE, offset);
        return someDate;
    }

    @Override
    public int getRowCount() {
        int rowcount = 7;
        return rowcount;
    }

}
