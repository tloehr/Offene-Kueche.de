/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.Menuweek;
import entity.MenuweekTools;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class FrmMenu extends JInternalFrame {
    LocalDate week;


    public FrmMenu() {
        week = new LocalDate().dayOfWeek().withMinimumValue();
        initComponents();
        initFrame();
    }

    private void initFrame() {
        ArrayList<Menuweek> menus = MenuweekTools.getAll(week);

        pnlMain.removeAll();
        pnlMain.setLayout(new GridLayout(1, Math.max(1, menus.size()), 5, 5));


        if (menus.isEmpty()) {
            pnlMain.add(new PnlMenuWeek(new Menuweek(week.toDate())));
        } else {
            for (Menuweek menuweek : menus) {
                pnlMain.add(new PnlMenuWeek(menuweek));
            }
        }

        jdcWeek.setDate(week.toDate());
        jdcWeek.setFont(new Font("SansSerif", Font.PLAIN, 18));


    }

    private void jdcWeekPropertyChange(PropertyChangeEvent e) {
        week = new LocalDate(e.getNewValue()).dayOfWeek().withMinimumValue();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jdcWeek = new JDateChooser();
        scrlMain = new JScrollPane();
        pnlMain = new JPanel();

        //======== this ========
        setVisible(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "pref:grow",
            "fill:pref, $lgap, fill:default:grow"));

        //---- jdcWeek ----
        jdcWeek.setDateFormatString("'KW'w yyyy");
        jdcWeek.setFont(new Font("Dialog", Font.PLAIN, 20));
        jdcWeek.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcWeekPropertyChange(e);
            }
        });
        contentPane.add(jdcWeek, CC.xy(1, 1));

        //======== scrlMain ========
        {

            //======== pnlMain ========
            {
                pnlMain.setLayout(new GridLayout(1, 1, 5, 5));
            }
            scrlMain.setViewportView(pnlMain);
        }
        contentPane.add(scrlMain, CC.xy(1, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JDateChooser jdcWeek;
    private JScrollPane scrlMain;
    private JPanel pnlMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
