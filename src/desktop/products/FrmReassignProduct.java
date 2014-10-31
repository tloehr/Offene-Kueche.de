/*
 * Created by JFormDesigner on Fri Oct 31 16:19:42 CET 2014
 */

package desktop.products;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Vorrat;
import entity.VorratTools;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Torsten Löhr
 */
public class FrmReassignProduct extends JInternalFrame {

    private Vorrat vorrat;

    public FrmReassignProduct(Vorrat vorrat) {
        this.vorrat = vorrat;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        if (!Main.getProps().containsKey("reassign1printer")) {
            Main.getProps().put("reassign1printer", "3");
        }

        btnPrt1.setSelected(Main.getProps().getProperty("touch1printer").equals("0"));
        btnPrt2.setSelected(Main.getProps().getProperty("touch1printer").equals("1"));
        btnPrtPage.setSelected(Main.getProps().getProperty("touch1printer").equals("2"));
        btnPrtNone.setSelected(Main.getProps().getProperty("touch1printer").equals("3"));
    }

    private void btnPrt1ItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "0");
        xSearchField1.requestFocus();
    }

    private void btnPrt2ItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "1");
        xSearchField1.requestFocus();
    }

    private void btnPrtPageItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "2");
        xSearchField1.requestFocus();
    }

    private void btnPrtNoneItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "3");
        xSearchField1.requestFocus();
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        vorrat = VorratTools.findByIDORScanner(xSearchField1.getText().trim());

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        xSearchField1 = new JXSearchField();
        scrollPane1 = new JScrollPane();
        label1 = new JTextArea();
        btnChangeSingle = new JButton();
        btnChangeAll = new JButton();
        panel1 = new JPanel();
        btnPrt1 = new JToggleButton();
        btnPrt2 = new JToggleButton();
        btnPrtPage = new JToggleButton();
        btnPrtNone = new JToggleButton();

        //======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "default:grow",
                "default, $lgap, fill:default:grow, 3*($lgap, default)"));

        //---- xSearchField1 ----
        xSearchField1.setFont(new Font("SansSerif", Font.PLAIN, 18));
        xSearchField1.setSearchMode(JXSearchField.SearchMode.REGULAR);
        xSearchField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xSearchField1ActionPerformed(e);
            }
        });
        contentPane.add(xSearchField1, CC.xy(1, 1));

        //======== scrollPane1 ========
        {

            //---- label1 ----
            label1.setText("text");
            label1.setFont(new Font("SansSerif", Font.PLAIN, 18));
            label1.setEditable(false);
            label1.setWrapStyleWord(true);
            label1.setLineWrap(true);
            scrollPane1.setViewportView(label1);
        }
        contentPane.add(scrollPane1, CC.xy(1, 3));

        //---- btnChangeSingle ----
        btnChangeSingle.setText("Nur diesen einen \u00c4ndern");
        btnChangeSingle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        contentPane.add(btnChangeSingle, CC.xy(1, 5));

        //---- btnChangeAll ----
        btnChangeAll.setText("Alle \u00e4ndern, die da sind");
        btnChangeAll.setFont(new Font("SansSerif", Font.PLAIN, 18));
        contentPane.add(btnChangeAll, CC.xy(1, 7));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnPrt1 ----
            btnPrt1.setText("Drucker 1");
            btnPrt1.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/labelprinter2.png")));
            btnPrt1.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrt1.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrt1ItemStateChanged(e);
                }
            });
            panel1.add(btnPrt1);

            //---- btnPrt2 ----
            btnPrt2.setText("Drucker 2");
            btnPrt2.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/labelprinter2.png")));
            btnPrt2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrt2.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrt2ItemStateChanged(e);
                }
            });
            panel1.add(btnPrt2);

            //---- btnPrtPage ----
            btnPrtPage.setText("Seitendrucker");
            btnPrtPage.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/printer.png")));
            btnPrtPage.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrtPage.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrtPageItemStateChanged(e);
                }
            });
            panel1.add(btnPrtPage);

            //---- btnPrtNone ----
            btnPrtNone.setText("Kein Druck");
            btnPrtNone.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/noprinter.png")));
            btnPrtNone.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrtNone.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrtNoneItemStateChanged(e);
                }
            });
            panel1.add(btnPrtNone);
        }
        contentPane.add(panel1, CC.xy(1, 9, CC.FILL, CC.DEFAULT));

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(btnPrt1);
        buttonGroup1.add(btnPrt2);
        buttonGroup1.add(btnPrtPage);
        buttonGroup1.add(btnPrtNone);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField xSearchField1;
    private JScrollPane scrollPane1;
    private JTextArea label1;
    private JButton btnChangeSingle;
    private JButton btnChangeAll;
    private JPanel panel1;
    private JToggleButton btnPrt1;
    private JToggleButton btnPrt2;
    private JToggleButton btnPrtPage;
    private JToggleButton btnPrtNone;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
