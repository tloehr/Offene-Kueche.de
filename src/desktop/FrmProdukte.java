/*
 * Created by JFormDesigner on Thu Aug 11 16:45:25 CEST 2011
 */

package desktop;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.pushingpixels.trident.Timeline;
import tablemodels.ProdukteTableModel;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class FrmProdukte extends JInternalFrame {

    private final int LAUFENDE_OPERATION_NICHTS = 0;
    private final int LAUFENDE_OPERATION_EDITING = 1;
    private final int LAUFENDE_OPERATION_ZUSAMMENFASSUNG = 2;

    private Object[] spalten = new Object[]{"Produkt Nr.", "Bezeichnung", "Lagerart", "GTIN", "Packungsgröße", "Einheit", "Stoffart", "Warengruppe"};
    private ListSelectionListener vlsl;
    private int laufendeOperation;
    private Timeline timelineMessage;
    private Pair<Integer, Object> lastCriteria;

    private JPopupMenu menu;

    private Produkte zwischenProdukt;

    private int MINIMUM_HEIGHT_OF_EDITOR_PANEL = 254;

    // Das hier sind die markierten Zeilen aus der Tabelle, falls jemand die Produkte zusammenfassen möchte.
    private int[] mergeUs;
    private int target4Merge;

    public FrmProdukte() {
        initComponents();
        myInit();
        pack();
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        Pair<Integer, Object> criteria = new Pair<Integer, Object>(Const.NAME_NR, xSearchField1.getText());
        loadTable(criteria);
    }

    private void loadTable(Pair<Integer, Object> criteria) {

        if (btnEdit.isSelected()) {
            btnDontSave.doClick();
        }

        lastCriteria = criteria;

        List list = null;

        if (criteria.getFirst() == Const.ALLE) {
            Query query = Main.getEM().createNamedQuery("Produkte.findAllSorted");
            list = query.getResultList();
        } else if (criteria.getFirst() == Const.NAME_NR) {
            list = ProdukteTools.searchProdukte(criteria.getSecond().toString());
        }

        if (vlsl != null) {
            tblProdukt.getSelectionModel().removeListSelectionListener(vlsl);
        } else {
            vlsl = getLSL();
        }

        tblProdukt.setModel(new ProdukteTableModel(list, spalten));
        tblProdukt.getSelectionModel().addListSelectionListener(vlsl);

        TableRowSorter sorter = new TableRowSorter(tblProdukt.getModel());
//        sorter.setComparator(ProdukteTableModel.COL_LAGERART, new Comparator<Short>() {
//            public int compare(Short l1, Short l2) {
//                return LagerTools.LAGERART[l1].compareTo(LagerTools.LAGERART[l2]);
//            }
//        });

        tblProdukt.setRowSorter(sorter);

        tblProdukt.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //pnlVorratComponentResized(null);

    }

    private void btnSearchAllActionPerformed(ActionEvent e) {
        loadTable(new Pair<Integer, Object>(Const.ALLE, null));
    }

    private void thisComponentResized(ComponentEvent e) {
        Tools.showSide(splitMain, btnEdit.isSelected() ? splitMain.getHeight() - MINIMUM_HEIGHT_OF_EDITOR_PANEL : splitMain.getHeight());
        Tools.showSide(splitButtons, laufendeOperation != LAUFENDE_OPERATION_NICHTS);
        Tools.showSide(splitButtonsLeft, !btnEdit.isSelected());
        Tools.packTable(tblProdukt, 0);
    }

//    private void btnShowEditorItemStateChanged(ItemEvent e) {
//        fillEditor();
//        Tools.showSide(splitMain, btnShowEditor.isSelected() ? splitMain.getHeight() - MINIMUM_HEIGHT_OF_EDITOR_PANEL : splitMain.getHeight(), 700);
//        btnEdit.setEnabled(btnShowEditor.isSelected());
//        //tblProdukt.scrollRectToVisible(tblProdukt.getCellRect(tblProdukt.getSelectedRow(), 0, true));
//    }

    private void btnEditItemStateChanged(ItemEvent e) {
        zwischenProdukt = btnEdit.isSelected() ? new Produkte() : null;
        if (btnEdit.isSelected()) {
            fillEditor();
        }
        //Tools.showSide(splitMain, btnEdit.isSelected() ? splitMain.getHeight() - MINIMUM_HEIGHT_OF_EDITOR_PANEL : splitMain.getHeight(), 700);
        Tools.showSide(splitMain, btnEdit.isSelected() ? 0.7d : 1.0d, 700);
        laufendeOperation = LAUFENDE_OPERATION_EDITING;
        Tools.showSide(splitButtonsLeft, !btnEdit.isSelected(), 500);
    }

    private void btnDontSaveActionPerformed(ActionEvent e) {
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
        btnEdit.setSelected(false);
    }

    private void cmbEinheitItemStateChanged(ItemEvent e) {
        if (zwischenProdukt == null) return;
        lblEinheit.setText(cmbEinheit.getSelectedItem().toString());
        zwischenProdukt.setEinheit((short) (cmbEinheit.getSelectedIndex() - 1));
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        timelineMessage = Tools.flashLabel(lblMessage, "Änderungen speichern ?");
        Tools.showSide(splitButtons, Tools.RIGHT_LOWER_SIDE, 500);
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        timelineMessage.cancel();
        timelineMessage = null;
        Tools.showSide(splitButtons, Tools.LEFT_UPPER_SIDE, 500);


        if (laufendeOperation == LAUFENDE_OPERATION_EDITING) {
            saveProductEdit();
            Tools.showSide(splitButtonsLeft, Tools.LEFT_UPPER_SIDE, 500);
        } else if (laufendeOperation == LAUFENDE_OPERATION_ZUSAMMENFASSUNG) {
            mergeUs();
        }
        loadTable(lastCriteria);
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
        btnEdit.setEnabled(false);
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        timelineMessage.cancel();
        timelineMessage = null;
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
        Tools.showSide(splitButtons, Tools.LEFT_UPPER_SIDE, 500);
    }

    private void mergeUs() {
        int neu = tblProdukt.convertRowIndexToModel(target4Merge);
        Produkte neuesProdukt = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(neu);
        try {
            Main.getEM().getTransaction().begin();
            for (int r = 0; r < mergeUs.length; r++) {
                int alt = tblProdukt.convertRowIndexToModel(mergeUs[r]);
                Produkte altesProdukt = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(alt);
                VorratTools.tauscheProdukt(altesProdukt, neuesProdukt);
                Main.debug("Lösche Produkt (wegen Merge): " + altesProdukt + " (" + altesProdukt.getId() + ")");
                Main.getEM().remove(altesProdukt);
            }
            Main.getEM().getTransaction().commit();
        } catch (Exception e) {
            Main.getEM().getTransaction().rollback();
            Main.fatal(e);
        }

        target4Merge = -1;
        mergeUs = null;
    }

    private void tblProduktMousePressed(MouseEvent e) {

        final ProdukteTableModel tm = (ProdukteTableModel) tblProdukt.getModel();
        if (tm.getRowCount() == 0 || tblProdukt.getSelectedRowCount() < 2) {
            return;
        }
        Point p = e.getPoint();
        final int col = tblProdukt.columnAtPoint(p);
        final int row = tblProdukt.rowAtPoint(p);
        //ListSelectionModel lsm = tblProdukt.getSelectionModel();

        //lsm.setSelectionInterval(row, row);

        if (e.isPopupTrigger()) {
            Tools.unregisterListeners(menu);
            menu = new JPopupMenu();

            JMenu miPopupMerge = new JMenu("Markierte Produkte zusammenfassen zu");
            miPopupMerge.setFont(new Font("sansserif", Font.PLAIN, 18));
            final int[] rows = tblProdukt.getSelectedRows();
            for (int r = 0; r < rows.length; r++) {
                final int finalR = r;
                final int thisRow = tblProdukt.convertRowIndexToModel(rows[finalR]);
                final Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow);
                JMenuItem mi = new JMenuItem("[" + produkte.getId() + "] " + produkte.getBezeichnung());
                mi.setFont(new Font("sansserif", Font.PLAIN, 18));

                mi.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        laufendeOperation = LAUFENDE_OPERATION_ZUSAMMENFASSUNG;
                        target4Merge = rows[finalR];
                        mergeUs = ArrayUtils.remove(rows, finalR);
                        timelineMessage = Tools.flashLabel(lblMessage, "Produkte zusammenfassen ?");
                        Tools.showSide(splitButtons, Tools.RIGHT_LOWER_SIDE, 500);
                    }
                });

                miPopupMerge.add(mi);
            }


            menu.add(miPopupMerge);

            menu.show(tblProdukt, (int) p.getX(), (int) p.getY());
        }


    }

    private void btnUnverpacktActionPerformed(ActionEvent e) {
        txtGTIN.setText("(lose Ware)");
        zwischenProdukt.setGtin(null);
        txtPackGroesse.setText("(lose Ware)");
        zwischenProdukt.setPackGroesse(BigDecimal.ZERO);
        btnUnverpackt.setEnabled(false);
        txtPackGroesse.setEnabled(false);
        txtGTIN.setEnabled(false);
    }

    private void txtBezeichnungFocusLost(FocusEvent e) {
        zwischenProdukt.setBezeichnung(txtBezeichnung.getText().trim());
    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {
        if (zwischenProdukt == null) return;
        zwischenProdukt.setLagerart(cmbLagerart.getSelectedIndex() == 0 ? -1 : (short) (cmbLagerart.getSelectedIndex() - 1));
    }

    private void txtGTINFocusLost(FocusEvent e) {
        if (ProdukteTools.isGTIN(txtGTIN.getText())) {
            if (ProdukteTools.isGTINinUse(txtGTIN.getText())) {
                txtGTIN.setText("(GTIN wird schon verwendet)");
                zwischenProdukt.setGtin(null);
            } else {
                zwischenProdukt.setGtin(txtGTIN.getText());
            }
        }
    }

    private void txtPackGroesseFocusLost(FocusEvent e) {
        try {
            BigDecimal bd = new BigDecimal(txtPackGroesse.getText().replaceAll(",", "\\."));
            zwischenProdukt.setPackGroesse(bd);
            //Main.Main.logger.debug("Double: " + dbl);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                txtPackGroesse.setText("Packungsgrößen müssen größer 0 sein.");
            }
        } catch (NumberFormatException e1) {
            zwischenProdukt.setPackGroesse(new BigDecimal(0d));
            txtPackGroesse.setText("Ungültiger Zahlenwert bei der Packungsgröße.");
        }
    }

    private void cmbStoffartItemStateChanged(ItemEvent e) {
        if (zwischenProdukt == null) return;
        zwischenProdukt.setStoffart(cmbStoffart.getSelectedIndex() == 0 ? null : (Stoffart) cmbStoffart.getSelectedItem());
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspSearch = new JScrollPane();
        pnlSearch = new JXTaskPaneContainer();
        xTaskPane1 = new JXTaskPane();
        btnSearchAll = new JButton();
        xSearchField1 = new JXSearchField();
        pnlMain = new JPanel();
        splitMain = new JSplitPane();
        panel3 = new JPanel();
        jspProdukt = new JScrollPane();
        tblProdukt = new JTable();
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
        splitButtons = new JSplitPane();
        splitButtonsLeft = new JSplitPane();
        panel5 = new JPanel();
        btnSave = new JButton();
        hSpacer3 = new JPanel(null);
        btnDontSave = new JButton();
        panel1 = new JPanel();
        btnEdit = new JToggleButton();
        panel2 = new JPanel();
        btnApply = new JButton();
        hSpacer1 = new JPanel(null);
        lblMessage = new JLabel();
        hSpacer2 = new JPanel(null);
        btnCancel = new JButton();

        //======== this ========
        setVisible(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setClosable(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== jspSearch ========
        {

            //======== pnlSearch ========
            {

                //======== xTaskPane1 ========
                {
                    xTaskPane1.setSpecial(true);
                    xTaskPane1.setTitle("Suchen");
                    xTaskPane1.setFont(new Font("sansserif", Font.BOLD, 18));
                    Container xTaskPane1ContentPane = xTaskPane1.getContentPane();
                    xTaskPane1ContentPane.setLayout(new VerticalLayout(10));

                    //---- btnSearchAll ----
                    btnSearchAll.setText("Alle");
                    btnSearchAll.setFont(new Font("sansserif", Font.PLAIN, 18));
                    btnSearchAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSearchAllActionPerformed(e);
                        }
                    });
                    xTaskPane1ContentPane.add(btnSearchAll);

                    //---- xSearchField1 ----
                    xSearchField1.setPrompt("Suchtext hier eingeben");
                    xSearchField1.setFont(new Font("sansserif", Font.PLAIN, 18));
                    xSearchField1.setMinimumSize(new Dimension(230, 36));
                    xSearchField1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            xSearchField1ActionPerformed(e);
                        }
                    });
                    xTaskPane1ContentPane.add(xSearchField1);
                }
                pnlSearch.add(xTaskPane1);
            }
            jspSearch.setViewportView(pnlSearch);
        }
        contentPane.add(jspSearch);

        //======== pnlMain ========
        {
            pnlMain.setLayout(new FormLayout(
                    "default:grow",
                    "fill:default:grow, 25dlu"));

            //======== splitMain ========
            {
                splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitMain.setDividerLocation(300);
                splitMain.setDividerSize(0);
                splitMain.setEnabled(false);

                //======== panel3 ========
                {
                    panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));

                    //======== jspProdukt ========
                    {

                        //---- tblProdukt ----
                        tblProdukt.setFont(new Font("sansserif", Font.PLAIN, 18));
                        tblProdukt.setRowHeight(20);
                        tblProdukt.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent e) {
                                tblProduktMousePressed(e);
                            }
                        });
                        jspProdukt.setViewportView(tblProdukt);
                    }
                    panel3.add(jspProdukt);
                }
                splitMain.setTopComponent(panel3);

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
                splitMain.setBottomComponent(panel4);
            }
            pnlMain.add(splitMain, CC.xy(1, 1));

            //======== splitButtons ========
            {
                splitButtons.setEnabled(false);
                splitButtons.setDividerSize(0);
                splitButtons.setDividerLocation(300);

                //======== splitButtonsLeft ========
                {
                    splitButtonsLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    splitButtonsLeft.setDividerSize(0);
                    splitButtonsLeft.setDividerLocation(0);

                    //======== panel5 ========
                    {
                        panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));

                        //---- btnSave ----
                        btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/filesave3.png")));
                        btnSave.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnSaveActionPerformed(e);
                            }
                        });
                        panel5.add(btnSave);
                        panel5.add(hSpacer3);

                        //---- btnDontSave ----
                        btnDontSave.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/actionfail.png")));
                        btnDontSave.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnDontSaveActionPerformed(e);
                            }
                        });
                        panel5.add(btnDontSave);
                    }
                    splitButtonsLeft.setTopComponent(panel5);

                    //======== panel1 ========
                    {
                        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                        //---- btnEdit ----
                        btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit.png")));
                        btnEdit.setEnabled(false);
                        btnEdit.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnEditItemStateChanged(e);
                            }
                        });
                        panel1.add(btnEdit);
                    }
                    splitButtonsLeft.setBottomComponent(panel1);
                }
                splitButtons.setLeftComponent(splitButtonsLeft);

                //======== panel2 ========
                {
                    panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                    //---- btnApply ----
                    btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/apply.png")));
                    btnApply.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnApplyActionPerformed(e);
                        }
                    });
                    panel2.add(btnApply);
                    panel2.add(hSpacer1);

                    //---- lblMessage ----
                    lblMessage.setText("text");
                    lblMessage.setFont(new Font("sansserif", Font.PLAIN, 18));
                    panel2.add(lblMessage);
                    panel2.add(hSpacer2);

                    //---- btnCancel ----
                    btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cancel.png")));
                    btnCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnCancelActionPerformed(e);
                        }
                    });
                    panel2.add(btnCancel);
                }
                splitButtons.setRightComponent(panel2);
            }
            pnlMain.add(splitButtons, CC.xy(1, 2));
        }
        contentPane.add(pnlMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void saveProductEdit() {

        int[] rows = tblProdukt.getSelectedRows();

        Main.getEM().getTransaction().begin();

        try {
            for (int r = 0; r < rows.length; r++) {

                int row = tblProdukt.convertRowIndexToModel(rows[r]);
                Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row);

                if (!zwischenProdukt.getBezeichnung().isEmpty()) {
                    produkte.setBezeichnung(zwischenProdukt.getBezeichnung());
                }

                if (txtGTIN.isEnabled()) {
                    produkte.setGtin(zwischenProdukt.getGtin());
                }

                if (zwischenProdukt.getPackGroesse().compareTo(BigDecimal.ZERO) >= 0) {
                    produkte.setPackGroesse(zwischenProdukt.getPackGroesse());
                    VorratTools.setzePackungsgroesse(produkte);
                }

                if (zwischenProdukt.getEinheit() >= 0) {
                    produkte.setEinheit(zwischenProdukt.getEinheit());
                }

                if (zwischenProdukt.getLagerart() >= 0) {
                    produkte.setLagerart(zwischenProdukt.getLagerart());
                }

                if (zwischenProdukt.getStoffart() != null) {
                    produkte.setStoffart(zwischenProdukt.getStoffart());
                }

                Main.getEM().merge(produkte);
            }
            Main.getEM().getTransaction().commit();
        } catch (Exception e) {
            Main.debug(e);
            Main.getEM().getTransaction().rollback();
        }
    }

    private void myInit() {
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

    private void fillEditor() {
        fillBezeichnungGTIN();
        fillPackgroesse();
        fillCMBEinheit();
        fillCMBLagerart();
        fillStoffart();
    }

    private void fillBezeichnungGTIN() {
        int[] rows = tblProdukt.getSelectedRows();

        if (rows.length == 1) {
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

    private ListSelectionListener getLSL() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // wenn nur eine Zeile gewählt wurde,
                    // dann kann die rechte Seite mit den Einzelbuchungen angezeigt werden.

                    int rowcount = tblProdukt.getSelectedRowCount();


                    if (btnEdit.isSelected()) {
                        if (tblProdukt.getSelectionModel().isSelectionEmpty()) {
                            btnDontSave.doClick();
                        } else {
                            fillEditor();
                        }

                    }

                    btnEdit.setEnabled(rowcount > 0);

//                    if (btnShowEditor.isSelected()) {
//                        if (rowcount == 0) {
//                            btnShowEditor.setSelected(false);
//                        } else {
//                            fillEditor();
//                        }
//                    }
//
//                    btnShowEditor.setEnabled(rowcount > 0);

                    //tblProdukt.getSelectedRows()


//                    btnDeleteVorrat.setEnabled(rowcount > 0);
//                    btnAusbuchen.setEnabled(rowcount > 0);
//                    btnPrintEti1.setEnabled(rowcount > 0);
//                    btnPrintEti2.setEnabled(rowcount > 0);
//                    btnPageprinter.setEnabled(rowcount > 0);
//                    btnEditVorrat.setEnabled(rowcount > 0);
//
//                    if (rowcount <= 1 && btnShowRightSide.isSelected()) {
//                        btnShowRightSide.setSelected(false);
//                    }
//
//                    //btnShowRightSide.setEnabled(false);
//                    btnShowRightSide.setEnabled(rowcount == 1);

                }
            }
        };

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspSearch;
    private JXTaskPaneContainer pnlSearch;
    private JXTaskPane xTaskPane1;
    private JButton btnSearchAll;
    private JXSearchField xSearchField1;
    private JPanel pnlMain;
    private JSplitPane splitMain;
    private JPanel panel3;
    private JScrollPane jspProdukt;
    private JTable tblProdukt;
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
    private JSplitPane splitButtons;
    private JSplitPane splitButtonsLeft;
    private JPanel panel5;
    private JButton btnSave;
    private JPanel hSpacer3;
    private JButton btnDontSave;
    private JPanel panel1;
    private JToggleButton btnEdit;
    private JPanel panel2;
    private JButton btnApply;
    private JPanel hSpacer1;
    private JLabel lblMessage;
    private JPanel hSpacer2;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
