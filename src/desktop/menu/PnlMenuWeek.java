/*
 * Created by JFormDesigner on Tue Oct 14 15:21:24 CEST 2014
 */

package desktop.menu;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Menuweek;
import org.joda.time.LocalDate;

import javax.swing.*;

/**
 * @author Torsten Löhr
 */
public class PnlMenuWeek extends JPanel {


    private final Menuweek menuweek;
    private PnlSingleDayMenu mon, tue, wed, thu, fri, sat, sun;


    public PnlMenuWeek(Menuweek menuweek) {
        this.menuweek = menuweek;
        initComponents();

        initPanel();


    }


    private void initPanel(){

        mon = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()));
        tue = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()).plusDays(1));
        wed = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()).plusDays(2));
        thu = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()).plusDays(3));
        fri = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()).plusDays(4));
        sat = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()).plusDays(5));
        sun = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()).plusDays(6));

        int y = 3;
        add(mon, CC.xy(1, y));
        add(tue, CC.xy(1, y+2));
        add(wed, CC.xy(1, y+4));
        add(thu, CC.xy(1, y+6));
        add(fri, CC.xy(1, y+8));
        add(sat, CC.xy(1, y+10));
        add(sun, CC.xy(1, y+12));



    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbFeature = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow",
            "8*(default, $lgap), fill:default:grow"));
        add(cmbFeature, CC.xy(1, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbFeature;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
