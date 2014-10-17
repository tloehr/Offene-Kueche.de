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
            pnlMain.add(new PnlMenuWeek(new Menuweek()));
        } else {
            for (Menuweek menuweek : menus) {
                pnlMain.add(new PnlMenuWeek(menuweek));
            }
        }


    }

    private void jdcWeekPropertyChange(PropertyChangeEvent e) {
        week = new LocalDate(e.getNewValue()).dayOfWeek().withMinimumValue();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label8 = new JLabel();
        jdcWeek = new JDateChooser();
        scrlMain = new JScrollPane();
        pnlMain = new JPanel();

        //======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, pref:grow",
            "default, $lgap, fill:default:grow"));

        //---- label8 ----
        label8.setText("text");
        contentPane.add(label8, CC.xywh(1, 1, 2, 1));

        //---- jdcWeek ----
        jdcWeek.setDateFormatString("'KW'w yyyy");
        jdcWeek.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcWeekPropertyChange(e);
            }
        });
        contentPane.add(jdcWeek, CC.xy(3, 1));

        //======== scrlMain ========
        {

            //======== pnlMain ========
            {
                pnlMain.setLayout(new GridLayout(1, 1, 5, 5));
            }
            scrlMain.setViewportView(pnlMain);
        }
        contentPane.add(scrlMain, CC.xywh(1, 3, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label8;
    private JDateChooser jdcWeek;
    private JScrollPane scrlMain;
    private JPanel pnlMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
