/*
 * Created by JFormDesigner on Wed Nov 05 12:02:58 CET 2014
 */

package printer;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Torsten Löhr
 */
public class DlgPrinterSetup extends JDialog {
    public DlgPrinterSetup(Frame owner) {
        super(owner);
        initComponents();
    }

    public DlgPrinterSetup(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void cmbPhysicalPrintersItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cmbLogicalPrintersItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cmbFormItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cmbPhysicalPrinter1ItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cmbLogicalPrinter1ItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cmbForm1ItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblPrt1 = new JLabel();
        cmbPhysicalPrinter1 = new JComboBox();
        cmbLogicalPrinter1 = new JComboBox();
        cmbForm1 = new JComboBox();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "default, $lgap, pref, 3*($lgap, default)"));

        //---- lblPrt1 ----
        lblPrt1.setText("Drucker 1");
        contentPane.add(lblPrt1, CC.xy(1, 1));

        //---- cmbPhysicalPrinter1 ----
        cmbPhysicalPrinter1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbPhysicalPrinter1ItemStateChanged(e);
            }
        });
        contentPane.add(cmbPhysicalPrinter1, CC.xy(1, 3));

        //---- cmbLogicalPrinter1 ----
        cmbLogicalPrinter1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbLogicalPrinter1ItemStateChanged(e);
            }
        });
        contentPane.add(cmbLogicalPrinter1, CC.xy(1, 5));

        //---- cmbForm1 ----
        cmbForm1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbForm1ItemStateChanged(e);
            }
        });
        contentPane.add(cmbForm1, CC.xy(1, 7));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblPrt1;
    private JComboBox cmbPhysicalPrinter1;
    private JComboBox cmbLogicalPrinter1;
    private JComboBox cmbForm1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
