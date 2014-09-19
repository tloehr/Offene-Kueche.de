/*
 * Created by JFormDesigner on Wed Feb 12 15:59:44 CET 2014
 */

package desktop;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Produkte;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class DlgType extends JDialog {
    private ArrayList<Produkte> myProducts;
    String gtin = null;
    BigDecimal groesse;

    public DlgType(Frame owner, ArrayList<Produkte> myProducts) {
        super(owner);
        this.myProducts = myProducts;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {
//        cmbLagerart.setModel(new DefaultComboBoxModel(LagerTools.LAGERART));
//        ((DefaultComboBoxModel) cmbLagerart.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
//        cmbEinheit.setModel(new DefaultComboBoxModel(ProdukteTools.EINHEIT));
//        ((DefaultComboBoxModel) cmbEinheit.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
//        StoffartTools.loadStoffarten(cmbStoffart);
//        ((DefaultComboBoxModel) cmbStoffart.getModel()).insertElementAt("(unterschiedliche Werte)", 0);
//        fillEditor();
//        setTitle(Tools.getWindowTitle("Produkt(e) bearbeiten"));
    }

    private void txtBezeichnungFocusLost(FocusEvent e) {
//        myProducts.setBezeichnung(txtBezeichnung.getText().trim());
    }

    private void cmbEinheitItemStateChanged(ItemEvent e) {

//        lblEinheit.setText(cmbEinheit.getSelectedItem().toString());

    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {

//        myProducts.setLagerart(cmbLagerart.getSelectedIndex() == 0 ? -1 : (short) (cmbLagerart.getSelectedIndex() - 1));
    }





    private void okButtonActionPerformed(ActionEvent evt) {
//        EntityManager em = Main.getEMF().createEntityManager();
//
//        try {
//            em.getTransaction().begin();
//            for (Produkte p : myProducts) {
//                Produkte produkt = em.merge(p);
//                em.lock(produkt, LockModeType.OPTIMISTIC);
//
//                if (txtBezeichnung.isEnabled()) {
//                    produkt.setBezeichnung(Tools.catchNull(txtBezeichnung.getText()).trim());
//                }
//
//                if (gtin != null) {
//                    produkt.setGtin(gtin);
//                }
//
//                if (groesse != null) {
//                    produkt.setPackGroesse(groesse);
//                    VorratTools.setzePackungsgroesse(em, produkt);
//                }
//
//                if (cmbEinheit.getSelectedIndex() > 0) {
//                    produkt.setEinheit((short) (cmbEinheit.getSelectedIndex() - 1));
//                }
//
//                if (cmbLagerart.getSelectedIndex() > 0) {
//                    produkt.setLagerart((short) (cmbLagerart.getSelectedIndex() - 1));
//                }
//
//                if (cmbStoffart.getSelectedIndex() > 0) {
//                    produkt.setStoffart(em.merge( (Stoffart) cmbStoffart.getSelectedItem()));
//                }
//            }
//            em.getTransaction().commit();
//        } catch (OptimisticLockException ole) {
//            Main.logger.info(ole);
//            em.getTransaction().rollback();
//        } catch (Exception e) {
//            Main.debug(e);
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//            dispose();
//        }

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel4 = new JPanel();
        label8 = new JLabel();
        label1 = new JLabel();
        txtBezeichnung = new JTextField();
        label2 = new JLabel();
        cmbEinheit = new JComboBox();
        label3 = new JLabel();
        cmbLagerart = new JComboBox();
        label6 = new JLabel();
        cmbWarengruppe = new JComboBox();
        label7 = new JLabel();
        textField1 = new JTextField();
        scrollPane1 = new JScrollPane();
        lstAllergics = new JList();
        label9 = new JLabel();
        textField2 = new JTextField();
        panel1 = new JScrollPane();
        lstAllergics2 = new JList();
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
                        "$rgap, 7*($lgap, default), $lgap, default:grow, 2*($lgap, default), $lgap, default:grow, $lgap, default"));

                    //---- label8 ----
                    label8.setText(" Stoffart Daten");
                    label8.setFont(new Font("Arial", Font.PLAIN, 22));
                    label8.setBackground(new Color(51, 51, 255));
                    label8.setOpaque(true);
                    label8.setForeground(new Color(102, 204, 255));
                    panel4.add(label8, CC.xywh(1, 3, 11, 1));

                    //---- label1 ----
                    label1.setText("Bezeichnung");
                    label1.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label1, CC.xy(2, 5));

                    //---- txtBezeichnung ----
                    txtBezeichnung.setFont(new Font("arial", Font.PLAIN, 18));
                    txtBezeichnung.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtBezeichnungFocusLost(e);
                        }
                    });
                    panel4.add(txtBezeichnung, CC.xywh(5, 5, 5, 1));

                    //---- label2 ----
                    label2.setText("Einheit");
                    label2.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label2, CC.xy(2, 7));

                    //---- cmbEinheit ----
                    cmbEinheit.setFont(new Font("arial", Font.PLAIN, 18));
                    cmbEinheit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbEinheitItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbEinheit, CC.xywh(5, 7, 5, 1));

                    //---- label3 ----
                    label3.setText("Lagerart");
                    label3.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label3, CC.xy(2, 9));

                    //---- cmbLagerart ----
                    cmbLagerart.setFont(new Font("arial", Font.PLAIN, 18));
                    cmbLagerart.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbLagerartItemStateChanged(e);
                        }
                    });
                    panel4.add(cmbLagerart, CC.xywh(5, 9, 5, 1));

                    //---- label6 ----
                    label6.setText("Warengruppe");
                    label6.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label6, CC.xy(2, 11));

                    //---- cmbWarengruppe ----
                    cmbWarengruppe.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(cmbWarengruppe, CC.xywh(5, 11, 5, 1));

                    //---- label7 ----
                    label7.setText(" Allergene");
                    label7.setFont(new Font("Arial", Font.PLAIN, 22));
                    label7.setBackground(new Color(204, 0, 204));
                    label7.setOpaque(true);
                    label7.setForeground(Color.cyan);
                    panel4.add(label7, CC.xywh(1, 13, 11, 1));
                    panel4.add(textField1, CC.xy(2, 15, CC.FILL, CC.TOP));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(lstAllergics);
                    }
                    panel4.add(scrollPane1, CC.xywh(5, 15, 5, 3));

                    //---- label9 ----
                    label9.setText(" Zusatzstoffe");
                    label9.setFont(new Font("Arial", Font.PLAIN, 22));
                    label9.setBackground(Color.green);
                    label9.setOpaque(true);
                    label9.setForeground(new Color(93, 73, 1));
                    panel4.add(label9, CC.xywh(1, 19, 11, 1));
                    panel4.add(textField2, CC.xy(2, 21, CC.FILL, CC.TOP));

                    //======== panel1 ========
                    {
                        panel1.setViewportView(lstAllergics2);
                    }
                    panel4.add(panel1, CC.xywh(5, 21, 5, 3));
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
    private JLabel label8;
    private JLabel label1;
    private JTextField txtBezeichnung;
    private JLabel label2;
    private JComboBox cmbEinheit;
    private JLabel label3;
    private JComboBox cmbLagerart;
    private JLabel label6;
    private JComboBox cmbWarengruppe;
    private JLabel label7;
    private JTextField textField1;
    private JScrollPane scrollPane1;
    private JList lstAllergics;
    private JLabel label9;
    private JTextField textField2;
    private JScrollPane panel1;
    private JList lstAllergics2;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
