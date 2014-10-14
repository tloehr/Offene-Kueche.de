/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;

/**
 * @author Torsten Löhr
 */
public class FrmMenu extends JInternalFrame {
    public FrmMenu() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label8 = new JLabel();
        dateChooser1 = new JDateChooser();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();

        //======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "3*(default, $lcgap), pref, $lcgap, default",
            "9*(default, $lgap), default"));

        //---- label8 ----
        label8.setText("text");
        contentPane.add(label8, CC.xywh(3, 1, 3, 1));

        //---- dateChooser1 ----
        dateChooser1.setDateFormatString("'KW'w yyyy");
        contentPane.add(dateChooser1, CC.xy(7, 1));

        //---- label1 ----
        label1.setText("text");
        contentPane.add(label1, CC.xy(1, 5));

        //---- label2 ----
        label2.setText("text");
        contentPane.add(label2, CC.xy(1, 7));

        //---- label3 ----
        label3.setText("text");
        contentPane.add(label3, CC.xy(1, 9));

        //---- label4 ----
        label4.setText("text");
        contentPane.add(label4, CC.xy(1, 11));

        //---- label5 ----
        label5.setText("text");
        contentPane.add(label5, CC.xy(1, 13));

        //---- label6 ----
        label6.setText("text");
        contentPane.add(label6, CC.xy(1, 15));

        //---- label7 ----
        label7.setText("text");
        contentPane.add(label7, CC.xy(1, 17));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label8;
    private JDateChooser dateChooser1;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
