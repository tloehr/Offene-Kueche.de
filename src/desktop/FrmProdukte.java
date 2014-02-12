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
import tablemodels.ProdukteTableModel;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class FrmProdukte extends JInternalFrame {

    private final int LAUFENDE_OPERATION_NICHTS = 0;
    private final int LAUFENDE_OPERATION_EDITING = 1;
    private final int LAUFENDE_OPERATION_ZUSAMMENFASSUNG = 2;

    private Object[] spalten = new Object[]{"Produkt Nr.", "Bezeichnung", "Lagerart", "GTIN", "Packungsgröße", "Einheit", "Stoffart", "Warengruppe"};

    private Pair<Integer, Object> lastCriteria;

    private JPopupMenu menu;
    private JInternalFrame thisFrame;

    private Produkte zwischenProdukt;


    // Das hier sind die markierten Zeilen aus der Tabelle, falls jemand die Produkte zusammenfassen möchte.
    private int[] mergeUs;
    private int target4Merge;

    public FrmProdukte() {
        initComponents();
        myInit();
        thisFrame = this;
        pack();
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        Pair<Integer, Object> criteria = new Pair<Integer, Object>(Const.NAME_NR, xSearchField1.getText());
        loadTable(criteria);
    }

    private void loadTable(Pair<Integer, Object> criteria) {

        lastCriteria = criteria;

        List list = null;

        if (criteria.getFirst() == Const.ALLE) {
            EntityManager em = Main.getEMF().createEntityManager();
            Query query = em.createNamedQuery("Produkte.findAllSorted");
            list = query.getResultList();
            em.close();
        } else if (criteria.getFirst() == Const.NAME_NR) {
            list = ProdukteTools.searchProdukte(criteria.getSecond().toString());
        }

        tblProdukt.setModel(new ProdukteTableModel(list, spalten));

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


//    private void btnSaveActionPerformed(ActionEvent e) {
//        timelineMessage = Tools.flashLabel(lblMessage, "Änderungen speichern ?");
//        Tools.showSide(splitButtons, Tools.RIGHT_LOWER_SIDE, 500);
//    }
//
//    private void btnApplyActionPerformed(ActionEvent e) {
//        timelineMessage.cancel();
//        timelineMessage = null;
//        Tools.showSide(splitButtons, Tools.LEFT_UPPER_SIDE, 500);
//
//
//        if (laufendeOperation == LAUFENDE_OPERATION_EDITING) {
//            saveProductEdit();
//            Tools.showSide(splitButtonsLeft, Tools.LEFT_UPPER_SIDE, 500);
//        } else if (laufendeOperation == LAUFENDE_OPERATION_ZUSAMMENFASSUNG) {
//            mergeUs();
//        }
//        loadTable(lastCriteria);
//        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//        btnEdit.setEnabled(false);
//    }
//
//    private void btnCancelActionPerformed(ActionEvent e) {
//        timelineMessage.cancel();
//        timelineMessage = null;
//        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//        Tools.showSide(splitButtons, Tools.LEFT_UPPER_SIDE, 500);
//    }

    private void mergeUs() {
        int neu = tblProdukt.convertRowIndexToModel(target4Merge);
        Produkte neuesProdukt = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(neu);
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            for (int r = 0; r < mergeUs.length; r++) {
                int alt = tblProdukt.convertRowIndexToModel(mergeUs[r]);
                Produkte altesProdukt = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(alt);
                VorratTools.tauscheProdukt(em, altesProdukt, neuesProdukt);
                Main.debug("Lösche Produkt (wegen Merge): " + altesProdukt + " (" + altesProdukt.getId() + ")");
                em.remove(altesProdukt);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
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

            JMenuItem miEdit = new JMenu("Markierte Produkte bearbeiten");
            miEdit.setFont(new Font("sansserif", Font.PLAIN, 18));
            final int[] rows = tblProdukt.getSelectedRows();


            miEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Produkte> listProdukte = new ArrayList<Produkte>(rows.length);
                    for (int r = 0; r < rows.length; r++) {
                        final int finalR = r;
                        final int thisRow = tblProdukt.convertRowIndexToModel(rows[finalR]);
                        listProdukte.add(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow));
                    }
                    DlgProdukt dlg = new DlgProdukt(Main.mainframe, listProdukte);
                }
            });
            miEdit.setEnabled(rows.length > 0);
            menu.add(miEdit);


            JMenu miPopupMerge = new JMenu("Markierte Produkte zusammenfassen zu");
            miPopupMerge.setFont(new Font("sansserif", Font.PLAIN, 18));

            for (int r = 0; r < rows.length; r++) {
                final int finalR = r;
                final int thisRow = tblProdukt.convertRowIndexToModel(rows[finalR]);
                final Produkte produkte = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow);
                JMenuItem mi = new JMenuItem("[" + produkte.getId() + "] " + produkte.getBezeichnung());
                mi.setFont(new Font("sansserif", Font.PLAIN, 18));

                mi.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {

                        target4Merge = rows[finalR];
                        mergeUs = ArrayUtils.remove(rows, finalR);

                    }
                });

                miPopupMerge.add(mi);
            }


            menu.add(miPopupMerge);

            menu.show(tblProdukt, (int) p.getX(), (int) p.getY());
        }


    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspSearch = new JScrollPane();
        pnlSearch = new JXTaskPaneContainer();
        xTaskPane1 = new JXTaskPane();
        btnSearchAll = new JButton();
        xSearchField1 = new JXSearchField();
        pnlMain = new JPanel();
        jspProdukt = new JScrollPane();
        tblProdukt = new JTable();

        //======== this ========
        setVisible(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setClosable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "pref, default:grow",
                "default:grow"));

        //======== jspSearch ========
        {

            //======== pnlSearch ========
            {

                //======== xTaskPane1 ========
                {
                    xTaskPane1.setSpecial(true);
                    xTaskPane1.setTitle("Suchen");
                    xTaskPane1.setFont(new Font("sansserif", Font.BOLD, 18));
                    xTaskPane1.setLayout(new VerticalLayout(10));

                    //---- btnSearchAll ----
                    btnSearchAll.setText("Alle");
                    btnSearchAll.setFont(new Font("sansserif", Font.PLAIN, 18));
                    btnSearchAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSearchAllActionPerformed(e);
                        }
                    });
                    xTaskPane1.add(btnSearchAll);

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
                    xTaskPane1.add(xSearchField1);
                }
                pnlSearch.add(xTaskPane1);
            }
            jspSearch.setViewportView(pnlSearch);
        }
        contentPane.add(jspSearch, CC.xy(1, 1, CC.FILL, CC.FILL));

        //======== pnlMain ========
        {
            pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.X_AXIS));

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
            pnlMain.add(jspProdukt);
        }
        contentPane.add(pnlMain, CC.xy(2, 1, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void saveProductEdit() {

        int[] rows = tblProdukt.getSelectedRows();

        EntityManager em = Main.getEMF().createEntityManager();
        em.getTransaction().begin();

        try {

            em.getTransaction().begin();

            for (int r = 0; r < rows.length; r++) {

                int row = tblProdukt.convertRowIndexToModel(rows[r]);
                Produkte produkte = em.merge(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(row));

                if (!zwischenProdukt.getBezeichnung().isEmpty()) {
                    produkte.setBezeichnung(zwischenProdukt.getBezeichnung());
                }

                if (txtGTIN.isEnabled()) {
                    produkte.setGtin(zwischenProdukt.getGtin());
                }

                if (zwischenProdukt.getPackGroesse().compareTo(BigDecimal.ZERO) >= 0) {
                    produkte.setPackGroesse(zwischenProdukt.getPackGroesse());
                    VorratTools.setzePackungsgroesse(em, produkte);
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
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            Main.debug(e);
            em.getTransaction().rollback();
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
    private JScrollPane jspProdukt;
    private JTable tblProdukt;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
