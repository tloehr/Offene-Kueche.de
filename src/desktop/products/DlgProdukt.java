/*
 * Created by JFormDesigner on Wed Feb 12 15:59:44 CET 2014
 */

package desktop.products;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import tools.MyJDialog;
import tools.PnlAssign;
import tools.PnlIngTradeType;
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
public class DlgProdukt extends MyJDialog {
    private final DlgProdukt thisDialog;
    //    private ArrayList<Produkte> myProducts;
    String gtin = null;
    BigDecimal groesse;
    PnlAssign<Allergene> pnlAllergenes;
    PnlAssign<Additives> pnlAdditives;

    Produkte product;

    boolean dialogShouldStayOpenedUntilClosed = false;
    boolean initPhase = true;
    private PnlIngTradeType pnlIngTradeType;

//    ArrayList<Produkte> editedProducts;

//    public ArrayList<Produkte> getEditedProducts() {
//        return editedProducts;
//    }

    public DlgProdukt(Frame owner, Produkte product) {
        super(owner);
        this.product = product;
        thisDialog = this;

        initPhase = true;
        initComponents();
        initDialog();
        initPhase = false;

        txtBezeichnung.requestFocus();

        setVisible(true);


//        setMaximized();

    }

    private void initDialog() {

        setTitle(Tools.getWindowTitle("Produkt(e) bearbeiten"));

        pnlIngTradeType = new PnlIngTradeType(product.getIngTypes(), new Closure() {
            @Override
            public void execute(Object o) {
                if (o == null) {
                    return;
                } else if (o instanceof IngTypes) {
                    product.setIngTypes((IngTypes) o);
                    lblEinheit.setText(IngTypesTools.EINHEIT[product.getIngTypes().getEinheit()]);
                } else {
                    Main.debug(o);
                }
            }
        }, PnlIngTradeType.SIZE_MEDIUM);

        panel4.add(pnlIngTradeType, CC.xywh(1, 14, 8, 1));
        lblEinheit.setText(IngTypesTools.EINHEIT[product.getIngTypes().getEinheit()]);

        dialogShouldStayOpenedUntilClosed = false;
        xSearchField1.setEnabled(dialogShouldStayOpenedUntilClosed);
        btnNew.setEnabled(dialogShouldStayOpenedUntilClosed);
//        if (dialogShouldStayOpenedUntilClosed) {
//            myProducts = new ArrayList<Produkte>();
//            cancelButton.setText("Schliessen");
//            cancelButton.setIcon(Const.icon24stop);
//        } else {
        fillEditor();
//        }

    }

    private void txtBezeichnungFocusLost(FocusEvent e) {
//        myProducts.setBezeichnung(txtBezeichnung.getText().trim());
    }


    private void txtGTINFocusLost(FocusEvent e) {
//        if (myProducts.get(0).getGtin() == null && myProducts.get(0).getGtin() != txtGTIN.getText())
        if (ProdukteTools.isGTIN(txtGTIN.getText())) {
            if (ProdukteTools.isGTINinUse(txtGTIN.getText())) {
                txtGTIN.setText("(GTIN wird schon verwendet)");
                gtin = null;
            } else {
                gtin = txtGTIN.getText();
            }
        } else {
            txtGTIN.setText(null);
            gtin = null;
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

        if (pnlAllergenes != null) panel4.remove(pnlAllergenes);
        if (pnlAdditives != null) panel4.remove(pnlAdditives);
        pnlAdditives = null;
        pnlAllergenes = null;

        pnlAllergenes = new PnlAssign<Allergene>(new ArrayList<Allergene>(product.getAllergenes()), AllergeneTools.getAll(), AllergeneTools.getListCellRenderer());
        pnlAssignment.add(pnlAllergenes, CC.xy(1, 3));
        pnlAdditives = new PnlAssign<Additives>(new ArrayList<Additives>(product.getAdditives()), AdditivesTools.getAll(), AdditivesTools.getListCellRenderer());
        pnlAssignment.add(pnlAdditives, CC.xy(1, 7));


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                revalidate();
                repaint();
                pack();
                Tools.centerOnParent(thisDialog);
            }
        });

    }


    private void fillBezeichnungGTIN() {

        initPhase = true;


        txtBezeichnung.setText(product.getBezeichnung());
        txtBezeichnung.setEnabled(true);
        txtGTIN.setText(product.isLoseWare() ? "(lose Ware)" : product.getGtin());
        txtGTIN.setEnabled(!product.isLoseWare());
        btnUnverpackt.setEnabled(true);
        btnUnverpackt.setSelected(product.isLoseWare());
//        } else if (myProducts.size() > 1) {
//            txtBezeichnung.setText("(unterschiedliche Werte)");
//            txtBezeichnung.setEnabled(false);
//            txtGTIN.setText("(unterschiedliche Werte)");
//            txtGTIN.setEnabled(false);
//            btnUnverpackt.setEnabled(false);
//        } else {
//            txtBezeichnung.setText("");
//            txtBezeichnung.setEnabled(false);
//            txtGTIN.setText("");
//            txtGTIN.setEnabled(false);
//            btnUnverpackt.setEnabled(false);
//        }
        initPhase = false;
    }

    private void fillPackgroesse() {

//        if (myProducts.size() == 1) {
        txtPackGroesse.setText(product.getGtin() == null ? "(lose Ware)" : product.getPackGroesse().toString());
        txtPackGroesse.setEnabled(!product.isLoseWare());
//        } else {
//            // Testen ob alle markierten Produkte dieselbe Packungsgröße haben und nicht lose sind.
//            boolean allegleich = true;
//            boolean loseware = false;
//
//            for (Produkte produkt : myProducts) {
//                if (produkt.isLoseWare()) {
//                    loseware = true;
//                    break;
//                }
//
//                if (myProducts.get(0).getPackGroesse().compareTo(produkt.getPackGroesse()) != 0) {
//                    allegleich = false;
//                    break;
//                }
//            }
//            txtPackGroesse.setText(loseware ? "(lose Ware enthalten)" : (allegleich ? myProducts.get(0).getPackGroesse().toString() : "(unterschiedliche Werte)"));
//            txtPackGroesse.setEnabled(!loseware);
//        }
    }


//    private void fillStoffart() {
//        if (myProducts.size() == 1) {
//            cmbStoffart.setSelectedItem(myProducts.get(0).getIngTypes());
//            lblEinheit.setText(IngTypesTools.EINHEIT[myProducts.get(0).getIngTypes().getEinheit()]);
//        } else {
//            // Testen ob alle markierten Produkte dieselbe Einheit haben
//            boolean allegleich = true;
//            for (Produkte produkt : myProducts) {
//                if (!myProducts.get(0).getIngTypes().equals(produkt.getIngTypes())) {
//                    allegleich = false;
//                    break;
//                }
//            }
//            if (allegleich) {
//                cmbStoffart.setSelectedItem(myProducts.get(0));
//                lblEinheit.setText(IngTypesTools.EINHEIT[myProducts.get(0).getIngTypes().getEinheit()]);
//            } else {
//                cmbStoffart.setSelectedIndex(0);
//                lblEinheit.setText("--");
//            }
//
//
//        }
//    }

    private void okButtonActionPerformed(ActionEvent evt) {

//        if (myProducts.isEmpty()) return;
        if (txtBezeichnung.isEnabled() && txtBezeichnung.getText().trim().isEmpty()) return;

        EntityManager em = Main.getEMF().createEntityManager();

        try {
            em.getTransaction().begin();
//            for (Produkte p : myProducts) {

            Produkte produkt = em.merge(product);
            em.lock(produkt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            if (txtBezeichnung.isEnabled()) {
                produkt.setBezeichnung(Tools.catchNull(txtBezeichnung.getText()).trim());
            }

            if (gtin != null) {
                produkt.setGtin(gtin);
            }

            if (groesse != null) {
                produkt.setPackGroesse(groesse);
                StockTools.setzePackungsgroesse(produkt);
            }
//
//                if (cmbStoffart.getSelectedIndex() > 0) {
//                    IngTypes ingType = (IngTypes) cmbStoffart.getSelectedItem();
//                    if (!ingType.equals(produkt.getIngTypes())) {
//                        produkt.setIngTypes(em.merge(ingType));
//                    }
//                }

            produkt.getAllergenes().clear();
            for (Allergene allergene : pnlAllergenes.getAssigned()) {
                produkt.getAllergenes().add(em.merge(allergene));
            }

            produkt.getAdditives().clear();
            for (Additives additives : pnlAdditives.getAssigned()) {
                produkt.getAdditives().add(em.merge(additives));
            }

            product = produkt;

//                editedProducts.add(produkt);

//            }
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception e) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
            if (!dialogShouldStayOpenedUntilClosed) {
                dispose();
            } else {

                emptyEditor();
            }
        }

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void emptyEditor() {
        xSearchField1.setText("");
        lblSearch.setText(" Suchen");
        txtBezeichnung.setText("");
        txtBezeichnung.setEnabled(false);
        txtGTIN.setText("");
        txtGTIN.setEnabled(false);
        txtPackGroesse.setText("");
        txtPackGroesse.setEnabled(false);
        btnUnverpackt.setEnabled(false);
        txtPackGroesse.setText("");
        txtPackGroesse.setEnabled(false);
        if (pnlAllergenes != null) pnlAssignment.remove(pnlAllergenes);
        if (pnlAdditives != null) pnlAssignment.remove(pnlAdditives);
        pnlAdditives = null;
        pnlAllergenes = null;
//        cmbStoffart.setSelectedIndex(0);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                revalidate();
                repaint();
            }
        });

    }

    private void btnUnverpacktItemStateChanged(ItemEvent e) {


        btnUnverpackt.setText(e.getStateChange() == ItemEvent.SELECTED ? "unverpackt" : "verpackt");

        txtPackGroesse.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
        txtGTIN.setEnabled(e.getStateChange() != ItemEvent.SELECTED);

        if (initPhase) return;

        if (e.getStateChange() == ItemEvent.SELECTED) {

            txtGTIN.setText("(lose Ware)");
            txtPackGroesse.setText("(lose Ware)");
        } else {
//            btnUnverpackt.setText("verpackt");
            txtGTIN.setText(Tools.catchNull(product.getGtin()));
            txtPackGroesse.setText("1");
            txtGTIN.requestFocus();
        }


//        myProducts.setGtin(null);
//        myProducts.setPackGroesse(BigDecimal.ZERO);


    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        String suchtext = xSearchField1.getText().trim();

        if (!suchtext.isEmpty()) {
            // Was genau wird gesucht ?

            Produkte foundProduct = null;
            Stock foundStock = null;

            if (ProdukteTools.isGTIN(suchtext)) {
                foundProduct = ProdukteTools.getProduct(suchtext);
            }

            if (foundProduct == null) {
                foundStock = StockTools.findByIDORScanner(suchtext);
                foundProduct = foundStock == null ? null : foundStock.getProdukt();
            }

            if (foundProduct == null) {
                emptyEditor();
//                myProducts.clear();

                lblSearch.setText(" Suchen");

            } else {
//                myProducts.clear();
//                myProducts.add(foundProduct);

                lblSearch.setText(" Gefunden: ProdID #" + foundProduct.getId() + (foundStock == null ? "" : " // VorratID #" + foundStock.getId()));

                fillEditor();
            }
        } else {
//            myProducts.clear();
            emptyEditor();
        }
    }

    public Produkte getProduct() {
        return product;
    }


    private void createUIComponents() {
        // TODO: add custom component creation code here
    }


    private void btnNewActionPerformed(ActionEvent e) {
//        myProducts.clear();
        emptyEditor();
        xSearchField1.setEnabled(false);

//        myProducts.add(new Produkte(IngTypesTools.getFirstType()));
        lblSearch.setText(" Neueingabe");
        fillEditor();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel4 = new JPanel();
        lblSearch = new JLabel();
        panel1 = new JPanel();
        pnlAssignment = new JPanel();
        label7 = new JLabel();
        label9 = new JLabel();
        xSearchField1 = new JXSearchField();
        label8 = new JLabel();
        label1 = new JLabel();
        txtBezeichnung = new JTextField();
        label4 = new JLabel();
        txtGTIN = new JTextField();
        btnUnverpackt = new JToggleButton();
        label5 = new JLabel();
        txtPackGroesse = new JTextField();
        lblEinheit = new JLabel();
        buttonBar = new JPanel();
        btnNew = new JButton();
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
                        "pref, 3dlu, $lcgap, pref:grow, $rgap, default, $lcgap, pref, $lcgap, default, $ugap, pref:grow",
                        "6*($lgap, default), $lgap, fill:default:grow"));

                    //---- lblSearch ----
                    lblSearch.setText(" Suchen");
                    lblSearch.setFont(new Font("Arial", Font.PLAIN, 22));
                    lblSearch.setBackground(new Color(204, 204, 0));
                    lblSearch.setOpaque(true);
                    lblSearch.setForeground(new Color(0, 0, 102));
                    panel4.add(lblSearch, CC.xywh(1, 2, 8, 1));

                    //======== panel1 ========
                    {
                        panel1.setBackground(new Color(102, 102, 0));
                        panel1.setLayout(new FlowLayout());
                    }
                    panel4.add(panel1, CC.xywh(10, 2, 1, 13));

                    //======== pnlAssignment ========
                    {
                        pnlAssignment.setLayout(new FormLayout(
                            "default:grow",
                            "default, $lgap, fill:default:grow, $lgap, default, $lgap, fill:default:grow"));

                        //---- label7 ----
                        label7.setText(" Allergene");
                        label7.setFont(new Font("Arial", Font.PLAIN, 22));
                        label7.setBackground(new Color(204, 0, 204));
                        label7.setOpaque(true);
                        label7.setForeground(Color.cyan);
                        pnlAssignment.add(label7, CC.xy(1, 1));

                        //---- label9 ----
                        label9.setText(" Zusatzstoffe");
                        label9.setFont(new Font("Arial", Font.PLAIN, 22));
                        label9.setBackground(Color.green);
                        label9.setOpaque(true);
                        label9.setForeground(new Color(93, 73, 1));
                        pnlAssignment.add(label9, CC.xy(1, 5));
                    }
                    panel4.add(pnlAssignment, CC.xywh(12, 2, 1, 13));

                    //---- xSearchField1 ----
                    xSearchField1.setFont(new Font("Dialog", Font.BOLD, 18));
                    xSearchField1.setPrompt("GTIN oder Vorrat Nummer");
                    xSearchField1.setInstantSearchDelay(2000);
                    xSearchField1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            xSearchField1ActionPerformed(e);
                        }
                    });
                    panel4.add(xSearchField1, CC.xywh(1, 4, 8, 1));

                    //---- label8 ----
                    label8.setText(" Produkt Daten");
                    label8.setFont(new Font("Arial", Font.PLAIN, 22));
                    label8.setBackground(new Color(51, 51, 255));
                    label8.setOpaque(true);
                    label8.setForeground(new Color(102, 204, 255));
                    panel4.add(label8, CC.xywh(1, 6, 8, 1));

                    //---- label1 ----
                    label1.setText("Bezeichnung");
                    label1.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label1, CC.xy(1, 8));

                    //---- txtBezeichnung ----
                    txtBezeichnung.setFont(new Font("arial", Font.PLAIN, 18));
                    txtBezeichnung.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtBezeichnungFocusLost(e);
                        }
                    });
                    panel4.add(txtBezeichnung, CC.xywh(4, 8, 5, 1));

                    //---- label4 ----
                    label4.setText("GTIN");
                    label4.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label4, CC.xy(1, 10));

                    //---- txtGTIN ----
                    txtGTIN.setFont(new Font("arial", Font.PLAIN, 18));
                    txtGTIN.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtGTINFocusLost(e);
                        }
                    });
                    panel4.add(txtGTIN, CC.xywh(4, 10, 3, 1));

                    //---- btnUnverpackt ----
                    btnUnverpackt.setText("unverpackt");
                    btnUnverpackt.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            btnUnverpacktItemStateChanged(e);
                        }
                    });
                    panel4.add(btnUnverpackt, CC.xywh(8, 10, 1, 3));

                    //---- label5 ----
                    label5.setText("Packungsgr\u00f6\u00dfe");
                    label5.setFont(new Font("arial", Font.PLAIN, 18));
                    panel4.add(label5, CC.xy(1, 12));

                    //---- txtPackGroesse ----
                    txtPackGroesse.setFont(new Font("arial", Font.PLAIN, 18));
                    txtPackGroesse.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtPackGroesseFocusLost(e);
                        }
                    });
                    panel4.add(txtPackGroesse, CC.xy(4, 12));

                    //---- lblEinheit ----
                    lblEinheit.setText("text");
                    lblEinheit.setFont(new Font("Dialog", Font.PLAIN, 18));
                    panel4.add(lblEinheit, CC.xy(6, 12));
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

                //---- btnNew ----
                btnNew.setText("Neu");
                btnNew.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
                btnNew.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnNewActionPerformed(e);
                    }
                });
                buttonBar.add(btnNew, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 0, 5), 0, 0));

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
    private JLabel lblSearch;
    private JPanel panel1;
    private JPanel pnlAssignment;
    private JLabel label7;
    private JLabel label9;
    private JXSearchField xSearchField1;
    private JLabel label8;
    private JLabel label1;
    private JTextField txtBezeichnung;
    private JLabel label4;
    private JTextField txtGTIN;
    private JToggleButton btnUnverpackt;
    private JLabel label5;
    private JTextField txtPackGroesse;
    private JLabel lblEinheit;
    private JPanel buttonBar;
    private JButton btnNew;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
