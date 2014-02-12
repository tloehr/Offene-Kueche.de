/*
 * Created by JFormDesigner on Wed Feb 12 15:59:44 CET 2014
 */

package desktop;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import tablemodels.ProdukteTableModel;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class DlgProdukt extends JDialog {
    private ArrayList<Produkte> myProducts;

    public DlgProdukt(Frame owner, ArrayList<Produkte> myProducts) {
        super(owner);
        this.myProducts = myProducts;
        initComponents();
        initDialog();
    }

    private void initDialog() {
        cmbLagerart.setModel(new DefaultComboBoxModel(LagerTools.LAGERART));
               ((DefaultComboBoxModel) cmbLagerart.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
               cmbEinheit.setModel(new DefaultComboBoxModel(ProdukteTools.EINHEIT));
               ((DefaultComboBoxModel) cmbEinheit.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
               StoffartTools.loadStoffarten(cmbStoffart);
               ((DefaultComboBoxModel) cmbStoffart.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
               laufendeOperation = LAUFENDE_OPERATION_NICHTS;
               loadTable(new Pair<Integer, Object>(Const.ALLE, null));
               setTitle(Tools.getWindowTitle("Produkte-Verwaltung"));
    }

    private void txtBezeichnungFocusLost(FocusEvent e) {
//        myProducts.setBezeichnung(txtBezeichnung.getText().trim());
    }

    private void cmbEinheitItemStateChanged(ItemEvent e) {

//        lblEinheit.setText(cmbEinheit.getSelectedItem().toString());
//        myProducts.setEinheit((short) (cmbEinheit.getSelectedIndex() - 1));
    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {

//        myProducts.setLagerart(cmbLagerart.getSelectedIndex() == 0 ? -1 : (short) (cmbLagerart.getSelectedIndex() - 1));
    }

    private void txtGTINFocusLost(FocusEvent e) {
//        if (ProdukteTools.isGTIN(txtGTIN.getText())) {
//            if (ProdukteTools.isGTINinUse(txtGTIN.getText())) {
//                txtGTIN.setText("(GTIN wird schon verwendet)");
//                myProducts.setGtin(null);
//            } else {
//                myProducts.setGtin(txtGTIN.getText());
//            }
//        }
    }

    private void btnUnverpacktActionPerformed(ActionEvent e) {
        txtGTIN.setText("(lose Ware)");
        myProducts.setGtin(null);
        txtPackGroesse.setText("(lose Ware)");
        myProducts.setPackGroesse(BigDecimal.ZERO);
        btnUnverpackt.setEnabled(false);
        txtPackGroesse.setEnabled(false);
        txtGTIN.setEnabled(false);
    }

    private void txtPackGroesseFocusLost(FocusEvent e) {
        try {
            BigDecimal bd = new BigDecimal(txtPackGroesse.getText().replaceAll(",", "\\."));
            myProducts.setPackGroesse(bd);
            //Main.Main.logger.debug("Double: " + dbl);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                txtPackGroesse.setText("Packungsgrößen müssen größer 0 sein.");
            }
        } catch (NumberFormatException e1) {
            myProducts.setPackGroesse(new BigDecimal(0d));
            txtPackGroesse.setText("Ungültiger Zahlenwert bei der Packungsgröße.");
        }
    }

    private void cmbStoffartItemStateChanged(ItemEvent e) {
        myProducts.setStoffart(cmbStoffart.getSelectedIndex() == 0 ? null : (Stoffart) cmbStoffart.getSelectedItem());
    }

    private void fillEditor() {
            fillBezeichnungGTIN();
            fillPackgroesse();
            fillCMBEinheit();
            fillCMBLagerart();
            fillStoffart();
        }

        private void fillBezeichnungGTIN() {


            if (myProducts.size() == 1) {
                int row = tblProdukt.convertRowIndexToModel(rows[0]);
                Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                txtBezeichnung.setText(produkte.getBezeichnung());
                txtBezeichnung.setEnabled(btnEdit.isSelected());

                txtGTIN.setText(produkte.isLoseWare() ? "(lose Ware)" : produkte.getGtin());
                txtGTIN.setEnabled(!produkte.isLoseWare() && btnEdit.isSelected());
                zwischenProdukt.setGtin(produkte.getGtin());
            } else if (rows.length > 1) {
                txtBezeichnung.setText("(unterschiedliche Werte)");
                txtBezeichnung.setEnabled(false);

                txtGTIN.setText("(unterschiedliche Werte)");
                txtGTIN.setEnabled(false);
            } else {
                txtBezeichnung.setText("");
                txtBezeichnung.setEnabled(false);

                txtGTIN.setText("");
                txtGTIN.setEnabled(false);
            }

            btnUnverpackt.setEnabled(txtGTIN.isEnabled());
        }

        private void fillPackgroesse() {
            int[] rows = tblProdukt.getSelectedRows();
            if (rows.length == 1) {
                int row = tblProdukt.convertRowIndexToModel(rows[0]);
                Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                txtPackGroesse.setText(produkte.getGtin() == null ? "(lose Ware)" : produkte.getPackGroesse().toString());
                txtPackGroesse.setEnabled(!produkte.isLoseWare() && btnEdit.isSelected());
                zwischenProdukt.setPackGroesse(produkte.getPackGroesse());
            } else {
                // Testen ob alle markierten Produkte dieselbe Packungsgröße haben und nicht lose sind.
                boolean allegleich = true;
                boolean loseware = false;
                BigDecimal groesse = BigDecimal.ZERO;

                for (int r = 0; r < rows.length; r++) {
                    int row = tblProdukt.convertRowIndexToModel(rows[r]);
                    Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);

                    if (produkte.isLoseWare()) {
                        loseware = true;
                        break;
                    }

                    if (r == 0) {
                        groesse = produkte.getPackGroesse();
                    }

                    if (groesse.compareTo(produkte.getPackGroesse()) != 0) {
                        allegleich = false;
                        break;
                    }
                }
                txtPackGroesse.setText(loseware ? "(lose Ware enthalten)" : (allegleich ? groesse.toString() : "(unterschiedliche Werte)"));
                txtPackGroesse.setEnabled(!loseware && btnEdit.isSelected());
                zwischenProdukt.setPackGroesse(allegleich ? groesse : BigDecimal.ONE.negate());
            }
        }


        private void fillCMBLagerart() {
            int[] rows = tblProdukt.getSelectedRows();
            if (rows.length == 1) {
                int row = tblProdukt.convertRowIndexToModel(rows[0]);
                Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                cmbLagerart.setSelectedIndex(produkte.getLagerart() + 1);
            } else {
                // Testen ob alle markierten Produkte dieselbe Einheit haben
                boolean allegleich = true;
                short lagerart = -1;
                for (int r = 0; r < rows.length; r++) {
                    int row = tblProdukt.convertRowIndexToModel(rows[r]);
                    Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                    if (r == 0) {
                        lagerart = produkte.getLagerart();
                    }

                    if (lagerart != produkte.getLagerart()) {
                        allegleich = false;
                        break;
                    }
                }
                cmbLagerart.setSelectedIndex(allegleich ? lagerart + 1 : 0);
            }
            cmbLagerart.setEnabled(btnEdit.isSelected());
        }

        private void fillCMBEinheit() {
            int[] rows = tblProdukt.getSelectedRows();
            if (rows.length == 1) {
                int row = tblProdukt.convertRowIndexToModel(rows[0]);
                Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                cmbEinheit.setSelectedIndex(produkte.getEinheit() + 1);
            } else {
                // Testen ob alle markierten Produkte dieselbe Einheit haben
                boolean allegleich = true;
                short einheit = -1;
                for (int r = 0; r < rows.length; r++) {
                    int row = tblProdukt.convertRowIndexToModel(rows[r]);
                    Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                    if (r == 0) {
                        einheit = produkte.getEinheit();
                    }

                    if (einheit != produkte.getEinheit()) {
                        allegleich = false;
                        break;
                    }
                }
                cmbEinheit.setSelectedIndex(allegleich ? einheit + 1 : 0);
            }
            cmbEinheit.setEnabled(btnEdit.isSelected());
        }

        private void fillStoffart() {
            int[] rows = tblProdukt.getSelectedRows();
            if (rows.length == 1) {
                int row = tblProdukt.convertRowIndexToModel(rows[0]);
                Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                cmbStoffart.setSelectedItem(produkte.getStoffart());
            } else {
                // Testen ob alle markierten Produkte dieselbe Einheit haben
                boolean allegleich = true;
                Stoffart stoffart = null;
                for (int r = 0; r < rows.length; r++) {
                    int row = tblProdukt.convertRowIndexToModel(rows[r]);
                    Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);
                    if (r == 0) {
                        stoffart = produkte.getStoffart();
                    }

                    if (!stoffart.equals(produkte.getStoffart())) {
                        allegleich = false;
                        break;
                    }
                }
                if (allegleich) {
                    cmbStoffart.setSelectedItem(stoffart);
                } else {
                    cmbStoffart.setSelectedIndex(0);
                }

            }
            cmbStoffart.setEnabled(btnEdit.isSelected());
        }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel4 = new JPanel();
        label1 = new JLabel();
        txtBezeichnung = new JTextField();
        label2 = new JLabel();
        cmbEinheit = new JComboBox();
        label3 = new JLabel();
        cmbLagerart = new JComboBox();
        label4 = new JLabel();
        txtGTIN = new JTextField();
        btnUnverpackt = new JButton();
        label5 = new JLabel();
        txtPackGroesse = new JTextField();
        lblEinheit = new JLabel();
        label6 = new JLabel();
        cmbStoffart = new JComboBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

                //======== panel4 ========
                {
                    panel4.setLayout(new FormLayout(
                        "default, right:default, 3dlu, $lcgap, default:grow, $lcgap, default, $rgap, default, $lcgap, default",
                        "$rgap, 6*($lgap, default)"));

                    //---- label1 ----
                    label1.setText("Bezeichnung");
                    label1.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(label1, CC.xy(2, 3));

                    //---- txtBezeichnung ----
                    txtBezeichnung.setFont(new Font("sansserif", Font.PLAIN, 18));
                    txtBezeichnung.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtBezeichnungFocusLost(e);
                        }
                    });
                    panel4.add(txtBezeichnung, CC.xywh(5, 3, 5, 1));

                    //---- label2 ----
                    label2.setText("Einheit");
                    label2.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(label2, CC.xy(2, 5));

                    //---- cmbEinheit ----
                    cmbEinheit.setFont(new Font("sansserif", Font.PLAIN, 18));
                    cmbEinheit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbEinheitItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbEinheit, CC.xywh(5, 5, 5, 1));

                    //---- label3 ----
                    label3.setText("Lagerart");
                    label3.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(label3, CC.xy(2, 7));

                    //---- cmbLagerart ----
                    cmbLagerart.setFont(new Font("sansserif", Font.PLAIN, 18));
                    cmbLagerart.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbLagerartItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbLagerart, CC.xywh(5, 7, 5, 1));

                    //---- label4 ----
                    label4.setText("GTIN");
                    label4.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(label4, CC.xy(2, 9));

                    //---- txtGTIN ----
                    txtGTIN.setFont(new Font("sansserif", Font.PLAIN, 18));
                    txtGTIN.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtGTINFocusLost(e);
                        }
                    });
                    panel4.add(txtGTIN, CC.xywh(5, 9, 3, 1));

                    //---- btnUnverpackt ----
                    btnUnverpackt.setText("unverpackt");
                    btnUnverpackt.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnUnverpacktActionPerformed(e);
                        }
                    });
                    panel4.add(btnUnverpackt, CC.xywh(9, 9, 1, 3));

                    //---- label5 ----
                    label5.setText("Packungsgr\u00f6\u00dfe");
                    label5.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(label5, CC.xy(2, 11));

                    //---- txtPackGroesse ----
                    txtPackGroesse.setFont(new Font("sansserif", Font.PLAIN, 18));
                    txtPackGroesse.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtPackGroesseFocusLost(e);
                        }
                    });
                    panel4.add(txtPackGroesse, CC.xy(5, 11));

                    //---- lblEinheit ----
                    lblEinheit.setText("liter");
                    lblEinheit.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(lblEinheit, CC.xy(7, 11));

                    //---- label6 ----
                    label6.setText("Stoffart");
                    label6.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel4.add(label6, CC.xy(2, 13));

                    //---- cmbStoffart ----
                    cmbStoffart.setFont(new Font("sansserif", Font.PLAIN, 18));
                    cmbStoffart.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbStoffartItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbStoffart, CC.xywh(5, 13, 5, 1));
                }
                contentPanel.add(panel4);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel4;
    private JLabel label1;
    private JTextField txtBezeichnung;
    private JLabel label2;
    private JComboBox cmbEinheit;
    private JLabel label3;
    private JComboBox cmbLagerart;
    private JLabel label4;
    private JTextField txtGTIN;
    private JButton btnUnverpackt;
    private JLabel label5;
    private JTextField txtPackGroesse;
    private JLabel lblEinheit;
    private JLabel label6;
    private JComboBox cmbStoffart;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
