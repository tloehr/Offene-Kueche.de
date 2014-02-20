/*
 * Created by JFormDesigner on Wed Feb 12 15:59:44 CET 2014
 */

package desktop;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
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
    String gtin = null;
    BigDecimal groesse;

    public DlgProdukt(Frame owner, ArrayList<Produkte> myProducts) {
        super(owner);
        this.myProducts = myProducts;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {
        cmbLagerart.setModel(new DefaultComboBoxModel(LagerTools.LAGERART));
        ((DefaultComboBoxModel) cmbLagerart.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
        cmbEinheit.setModel(new DefaultComboBoxModel(ProdukteTools.EINHEIT));
        ((DefaultComboBoxModel) cmbEinheit.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
        StoffartTools.loadStoffarten(cmbStoffart);
        ((DefaultComboBoxModel) cmbStoffart.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
        fillEditor();
        setTitle(Tools.getWindowTitle("Produkt(e) bearbeiten"));
    }

    private void txtBezeichnungFocusLost(FocusEvent e) {
//        myProducts.setBezeichnung(txtBezeichnung.getText().trim());
    }

    private void cmbEinheitItemStateChanged(ItemEvent e) {

        lblEinheit.setText(cmbEinheit.getSelectedItem().toString());

    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {

//        myProducts.setLagerart(cmbLagerart.getSelectedIndex() == 0 ? -1 : (short) (cmbLagerart.getSelectedIndex() - 1));
    }

    private void txtGTINFocusLost(FocusEvent e) {

        if (myProducts.get(0).getGtin() == null && myProducts.get(0).getGtin() != txtGTIN.getText())

            if (ProdukteTools.isGTIN(txtGTIN.getText())) {
                if (ProdukteTools.isGTINinUse(txtGTIN.getText())) {
                    txtGTIN.setText("(GTIN wird schon verwendet)");
                    gtin = null;
                } else {
                    gtin = txtGTIN.getText();
                }
            }
    }

    private void txtPackGroesseFocusLost(FocusEvent e) {
        try {
            groesse = new BigDecimal(txtPackGroesse.getText().replaceAll(",", "\\."));
//            myProducts.setPackGroesse(bd);
            //Main.Main.logger.debug("Double: " + dbl);
            if (groesse.compareTo(BigDecimal.ZERO) <= 0) {
                txtPackGroesse.setText("Packungsgrößen müssen größer 0 sein.");
                groesse = null;
            }
        } catch (NumberFormatException e1) {
//            myProducts.setPackGroesse(new BigDecimal(0d));
            txtPackGroesse.setText("Ungültiger Zahlenwert bei der Packungsgröße.");
            groesse = null;
        }
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
            txtBezeichnung.setText(myProducts.get(0).getBezeichnung());
            txtGTIN.setText(myProducts.get(0).isLoseWare() ? "(lose Ware)" : myProducts.get(0).getGtin());
            txtGTIN.setEnabled(!myProducts.get(0).isLoseWare());

        } else if (myProducts.size() > 1) {
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

//        btnUnverpackt.setEnabled(txtGTIN.isEnabled());
    }

    private void fillPackgroesse() {

        if (myProducts.size() == 1) {

            txtPackGroesse.setText(myProducts.get(0).getGtin() == null ? "(lose Ware)" : myProducts.get(0).getPackGroesse().toString());
            txtPackGroesse.setEnabled(!myProducts.get(0).isLoseWare());
        } else {
            // Testen ob alle markierten Produkte dieselbe Packungsgröße haben und nicht lose sind.
            boolean allegleich = true;
            boolean loseware = false;

            for (Produkte produkt : myProducts) {
                if (produkt.isLoseWare()) {
                    loseware = true;
                    break;
                }

                if (myProducts.get(0).getPackGroesse().compareTo(produkt.getPackGroesse()) != 0) {
                    allegleich = false;
                    break;
                }
            }
            txtPackGroesse.setText(loseware ? "(lose Ware enthalten)" : (allegleich ? myProducts.get(0).getPackGroesse().toString() : "(unterschiedliche Werte)"));
            txtPackGroesse.setEnabled(!loseware);
        }
    }


    private void fillCMBLagerart() {
        if (myProducts.size() == 1) {
            cmbLagerart.setSelectedIndex(myProducts.get(0).getLagerart() + 1);
        } else {
            // Testen ob alle markierten Produkte dieselbe Einheit haben
            boolean allegleich = true;
            for (Produkte produkt : myProducts) {
                if (myProducts.get(0).getLagerart() != produkt.getLagerart()) {
                    allegleich = false;
                    break;
                }
            }
            cmbLagerart.setSelectedIndex(allegleich ? myProducts.get(0).getLagerart() + 1 : 0);
        }
    }

    private void fillCMBEinheit() {
        if (myProducts.size() == 1) {
            cmbEinheit.setSelectedIndex(myProducts.get(0).getEinheit() + 1);
        } else {
            // Testen ob alle markierten Produkte dieselbe Einheit haben
            boolean allegleich = true;
            short einheit = -1;
            for (Produkte produkt : myProducts) {
                if (myProducts.get(0).getEinheit() != produkt.getEinheit()) {
                    allegleich = false;
                    break;
                }
            }
            cmbEinheit.setSelectedIndex(allegleich ? einheit + 1 : 0);
        }
        lblEinheit.setText(cmbEinheit.getSelectedItem().toString());
    }

    private void fillStoffart() {
        if (myProducts.size() == 1) {
            cmbStoffart.setSelectedItem(myProducts.get(0).getStoffart());
        } else {
            // Testen ob alle markierten Produkte dieselbe Einheit haben
            boolean allegleich = true;
            for (Produkte produkt : myProducts) {
                if (!myProducts.get(0).getStoffart().equals(produkt.getStoffart())) {
                    allegleich = false;
                    break;
                }
            }
            if (allegleich) {
                cmbStoffart.setSelectedItem(myProducts.get(0));
            } else {
                cmbStoffart.setSelectedIndex(0);
            }

        }
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        EntityManager em = Main.getEMF().createEntityManager();

        try {
            em.getTransaction().begin();
            for (Produkte p : myProducts) {
                Produkte produkt = em.merge(p);
                em.lock(produkt, LockModeType.OPTIMISTIC);

                if (txtBezeichnung.isEnabled()) {
                    produkt.setBezeichnung(Tools.catchNull(txtBezeichnung.getText()).trim());
                }

                if (gtin != null) {
                    produkt.setGtin(gtin);
                }

                if (groesse != null) {
                    produkt.setPackGroesse(groesse);
                    VorratTools.setzePackungsgroesse(em, produkt);
                }

                if (cmbEinheit.getSelectedIndex() > 0) {
                    produkt.setEinheit((short) (cmbEinheit.getSelectedIndex() - 1));
                }

                if (cmbLagerart.getSelectedIndex() > 0) {
                    produkt.setLagerart((short) (cmbLagerart.getSelectedIndex() - 1));
                }

                if (cmbStoffart.getSelectedIndex() > 0) {
                    produkt.setStoffart(em.merge( (Stoffart) cmbStoffart.getSelectedItem()));
                }
            }
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            Main.logger.info(ole);
            em.getTransaction().rollback();
        } catch (Exception e) {
            Main.debug(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
            dispose();
        }

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void btnUnverpacktItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtGTIN.setText("(lose Ware)");
            txtPackGroesse.setText("(lose Ware)");
        } else {
            txtGTIN.setText(Tools.catchNull(myProducts.get(0).getGtin()));
            txtPackGroesse.setText(myProducts.get(0).getPackGroesse().toString());
        }


//        myProducts.setGtin(null);
//        myProducts.setPackGroesse(BigDecimal.ZERO);

        txtPackGroesse.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);
        txtGTIN.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);


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
        btnUnverpackt = new JToggleButton();
        label5 = new JLabel();
        txtPackGroesse = new JTextField();
        lblEinheit = new JLabel();
        label6 = new JLabel();
        cmbStoffart = new JComboBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
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
                    label1.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label1, CC.xy(2, 3));

                    //---- txtBezeichnung ----
                    txtBezeichnung.setFont(new Font("arial", Font.PLAIN, 18));
                    txtBezeichnung.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtBezeichnungFocusLost(e);
                        }
                    });
                    panel4.add(txtBezeichnung, CC.xywh(5, 3, 5, 1));

                    //---- label2 ----
                    label2.setText("Einheit");
                    label2.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label2, CC.xy(2, 5));

                    //---- cmbEinheit ----
                    cmbEinheit.setFont(new Font("arial", Font.PLAIN, 18));
                    cmbEinheit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbEinheitItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbEinheit, CC.xywh(5, 5, 5, 1));

                    //---- label3 ----
                    label3.setText("Lagerart");
                    label3.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label3, CC.xy(2, 7));

                    //---- cmbLagerart ----
                    cmbLagerart.setFont(new Font("arial", Font.PLAIN, 18));
                    cmbLagerart.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbLagerartItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbLagerart, CC.xywh(5, 7, 5, 1));

                    //---- label4 ----
                    label4.setText("GTIN");
                    label4.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label4, CC.xy(2, 9));

                    //---- txtGTIN ----
                    txtGTIN.setFont(new Font("arial", Font.PLAIN, 18));
                    txtGTIN.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtGTINFocusLost(e);
                        }
                    });
                    panel4.add(txtGTIN, CC.xywh(5, 9, 3, 1));

                    //---- btnUnverpackt ----
                    btnUnverpackt.setText("unverpackt");
                    btnUnverpackt.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            btnUnverpacktItemStateChanged(e);
                        }
                    });
                    panel4.add(btnUnverpackt, CC.xywh(9, 9, 1, 3));

                    //---- label5 ----
                    label5.setText("Packungsgr\u00f6\u00dfe");
                    label5.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label5, CC.xy(2, 11));

                    //---- txtPackGroesse ----
                    txtPackGroesse.setFont(new Font("arial", Font.PLAIN, 18));
                    txtPackGroesse.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtPackGroesseFocusLost(e);
                        }
                    });
                    panel4.add(txtPackGroesse, CC.xy(5, 11));

                    //---- lblEinheit ----
                    lblEinheit.setText("liter");
                    lblEinheit.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(lblEinheit, CC.xy(7, 11));

                    //---- label6 ----
                    label6.setText("Stoffart");
                    label6.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label6, CC.xy(2, 13));

                    //---- cmbStoffart ----
                    cmbStoffart.setFont(new Font("arial", Font.PLAIN, 18));
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
                okButton.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/apply.png")));
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Abbrechen");
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cancel.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
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
    private JToggleButton btnUnverpackt;
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
