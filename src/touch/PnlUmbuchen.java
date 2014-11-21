/*
 * Created by JFormDesigner on Tue Aug 23 14:47:53 CEST 2011
 */

package touch;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import org.pushingpixels.trident.Timeline;
import printer.Printers;
import tablemodels.StockTableModel2;
import tablerenderer.UmbuchenRenderer;
import threads.SoundProcessor;
import tools.Const;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlUmbuchen extends DefaultTouchPanel {
    Lager ziel;
    Lieferanten lieferant;
    Stock stock;
    private final int MODULENUMBER = 3;
    private SoundProcessor sp;
    private Object[] spaltenVorrat = new Object[]{"Vorrat Nr.", "Bezeichnung", "Menge", "Status"};
    private boolean txtSearchChecked;

    private Timeline timeline;


    public PnlUmbuchen(SoundProcessor sp) {
        initComponents();
        this.sp = sp;
        myInit();
    }

    @Override
    public void startAction() {
        txtSearch.requestFocus();
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        stock = StockTools.findByIDORScanner(txtSearch.getText());
        if (stock != null) {
            if (stock.isAusgebucht() && !cbZombieRevive.isSelected()) {
                Tools.log(txtLog, stock.getId(), stock.getProdukt().getBezeichnung(), "Dieser Vorrat wurde bereits ausgebucht.");
                Tools.fadeout(lblProdukt);
                sp.error();
                stock = null;
            } else {
                if (btnSofortUmbuchen.isSelected()) {
                    umbuchen();
                    Tools.fadeinout(lblProdukt, "[" + stock.getId() + "] " + stock.getProdukt().getBezeichnung());
                    stock = null;
                } else {
                    Tools.fadein(lblProdukt, "[" + stock.getId() + "] " + stock.getProdukt().getBezeichnung());
                }
            }
        } else {
            Tools.log(txtLog, 0, txtSearch.getText(), "Unbekannte Vorrat Nummer.");
            Tools.fadeout(lblProdukt);
            sp.warning();
        }
        btnUmbuchen.setEnabled(stock != null);
        txtSearch.selectAll();
        txtSearch.requestFocus();
        txtSearchChecked = true;
    }

    private void umbuchen() {

        EntityManager em = Main.getEMF().createEntityManager();
        try {

            em.getTransaction().begin();

            Stock myStock = em.merge(stock);

            if (myStock.isAusgebucht()) {
                myStock.setAusgang(Const.DATE_BIS_AUF_WEITERES);
                myStock.setAnbruch(Const.DATE_BIS_AUF_WEITERES);


                Query query = em.createQuery("DELETE FROM Buchungen b WHERE b.stock = :vorrat AND b.status <> :butnotstatus");
                query.setParameter("vorrat", myStock);
                query.setParameter("butnotstatus", BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);

                query.executeUpdate();

//                Collection<Buchungen> buchungen = myVorrat.getBuchungenCollection();
//                for (Buchungen b : buchungen) {
//                    Buchungen buchung = em.merge(b);
//                    if (buchung.getStatus() != BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
//                        em.remove(buchung);
//                    }
//                }
//                for (Buchungen b : buchungen) {
//                    if (b.getStatus() != BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND) {
//                        myVorrat.getBuchungenCollection().remove(b);
//                    }
//                }
            }

            myStock.setLager(em.merge(ziel));
            if (lieferant != null) {
                myStock.setLieferant(em.merge(lieferant));
            }
//            EntityTools.merge(vorrat);
            em.getTransaction().commit();

            stock = myStock;

            Tools.log(txtLog, myStock.getId(), myStock.getProdukt().getBezeichnung(), "umgebucht");
            if (tblVorrat.getModel() instanceof StockTableModel2) {
                int row = ((StockTableModel2) tblVorrat.getModel()).addVorrat(stock); // diese Methode fügt den Vorrat nur dann hinzu, wenn nötig.
                Tools.scrollCellToVisible(tblVorrat, row, 1);
                tblVorrat.getSelectionModel().setSelectionInterval(row, row);
            }
            sp.bell();

        } catch (Exception ee) {
            em.getTransaction().rollback();
            ee.printStackTrace();
        } finally {
            em.close();
        }

    }

    private void btnSofortUmbuchenItemStateChanged(ItemEvent e) {
        Tools.log(txtLog, btnSofortUmbuchen.isSelected() ? "Vorrat wird direkt umgebucht." : "Vorrat wird nur auf Knopfdruck umgebucht.");
        txtSearch.requestFocus();
    }

    private void cmbLagerItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {

            vorratsListeInsLogbuch();

            ziel = (Lager) cmbLager.getSelectedItem();
            Tools.log(txtLog, "=================================================================");
            Tools.log(txtLog, "Ab jetzt wird ins Lager: \"" + ziel.getBezeichnung() + "\" umgebucht.");
            Tools.log(txtLog, "=================================================================");
            Main.getProps().put("touch" + MODULENUMBER + "lager", new Integer(cmbLager.getSelectedIndex()).toString());
            loadVorratTable();
            txtSearch.requestFocus();
        }
    }

    private void loadVorratTable() {

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.stock v " +
                    // die 0 ist ein kleiner Kniff und wird für das Umbuchen gebraucht.
                    " WHERE v.lager = :lager AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                    " GROUP BY v");

            query.setParameter("lager", (Lager) cmbLager.getSelectedItem());

            java.util.List list = query.getResultList();

            tblVorrat.setModel(new StockTableModel2(list, spaltenVorrat));
            Tools.packTable(tblVorrat, 0);

            tblVorrat.getColumnModel().getColumn(StockTableModel2.COL_VORRAT_ID).setCellRenderer(new UmbuchenRenderer());
            tblVorrat.getColumnModel().getColumn(StockTableModel2.COL_BEZEICHNUNG).setCellRenderer(new UmbuchenRenderer());
            tblVorrat.getColumnModel().getColumn(StockTableModel2.COL_MENGE).setCellRenderer(new UmbuchenRenderer());
            tblVorrat.getColumnModel().getColumn(StockTableModel2.COL_ICON).setCellRenderer(new UmbuchenRenderer());

        } catch (Exception e) { // nicht gefunden
            Main.logger.fatal(e.getMessage(), e);
            //e.printStackTrace();
        } finally {
            em.close();
        }

    }

    private void btnUmbuchenActionPerformed(ActionEvent e) {
        if (!txtSearchChecked) {
            txtSearchActionPerformed(null);
        }
        umbuchen();
        Tools.fadeout(lblProdukt);
        txtSearch.requestFocus();
    }

    private void txtSearchFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void btnClearActionPerformed(ActionEvent e) {
        txtLog.setText("");
    }

    private void btnPrintActionPerformed(ActionEvent e) {
        if (!txtLog.getText().trim().isEmpty()) {
            Printers.print(this, "<span id=\"fonttext\">" + Tools.replace(txtLog.getText(), "\n", "<br/>") + "</span>", true);
        }
    }


    private void btnToUnbekanntActionPerformed(ActionEvent e) {
        Lager unbekannt = LagerTools.getUnbekannt();
        StockTableModel2 model = (StockTableModel2) tblVorrat.getModel();
        Tools.log(txtLog, "Folgende Vorräte wurden auf Unbekannt umgebucht:");
        Tools.log(txtLog, "================================================");

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            for (int row = 0; row < model.getRowCount(); row++) {
                // Model Row Index Umwandlung ist hier unnötig. Markierungen bleiben unberücksichtigt.
                if (model.getStatus(row) == StockTableModel2.STATUS_FRAGLICH) {
                    Stock stock = em.merge(model.getVorrat(row));
                    em.lock(stock, LockModeType.OPTIMISTIC);
                    Tools.log(txtLog, stock.getId(), stock.getProdukt().getBezeichnung(), "");
                    stock.setLager(unbekannt);
                }
            }
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception ex) {
            em.getTransaction().rollback();
            Main.fatal(ex);
        } finally {
            em.close();
            loadVorratTable();
        }
        Tools.log(txtLog, "================================================");
    }

    private void vorratsListeInsLogbuch() {
        if (tblVorrat.getModel().getRowCount() > 0) {
            StockTableModel2 model = (StockTableModel2) tblVorrat.getModel();
            Tools.log(txtLog, "Die Vorratsliste enhielt folgende Einträge:");
            Tools.log(txtLog, "===========================================");
            for (int row = 0; row < model.getRowCount(); row++) {
                int r = tblVorrat.convertRowIndexToModel(row);
                Stock stock = model.getVorrat(r);
                String status = "";
                if (model.getStatus(r) == StockTableModel2.STATUS_FRAGLICH) {
                    status = "ungeprüft";
                } else if (model.getStatus(r) == StockTableModel2.STATUS_NEU) {
                    status = "   neu   ";
                } else if (model.getStatus(r) == StockTableModel2.STATUS_OK) {
                    status = "    ok   ";
                } else {
                    status = "?";
                }
                Tools.log(txtLog, stock.getId(), status, stock.getProdukt().getBezeichnung());
            }
            Tools.log(txtLog, "================================================");
        }
    }

    private void txtSearchFocusLost(FocusEvent e) {

    }

    private void txtSearchCaretUpdate(CaretEvent e) {
        txtSearchChecked = false;
    }


    private void loadLieferant() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT l FROM Lieferanten l ORDER BY l.firma");
        try {
            java.util.List lieferant = query.getResultList();
            lieferant.add(0, "<html><i>Lieferant nicht &auml;ndern</i></html>");
            cmbLieferant.setModel(tools.Tools.newComboboxModel(new ArrayList<Lieferanten>(lieferant)));
        } catch (Exception e) { // nicht gefunden
            //
        } finally {
            em.close();
        }
    }

    private void cmbLieferantItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && cmbLieferant.getSelectedIndex() > 0) {

            lieferant = (Lieferanten) cmbLieferant.getSelectedItem();
            Tools.log(txtLog, "=================================================================");
            Tools.log(txtLog, "Ab jetzt wird auf Lieferant: \"" + lieferant.getFirma() + "\" umgebucht.");
            Tools.log(txtLog, "=================================================================");
            loadVorratTable();
            txtSearch.requestFocus();
        } else {
            lieferant = null;
        }
    }


//    private void btnApplyActionPerformed(ActionEvent e) {
//
//        if (laufendeVOperation == LAUFENDE_OPERATION_BUCHEN_AUF_UNBEKANNT) {
//            Lager unbekannt = LagerTools.getUnbekannt();
//            VorratTableModel2 model = (VorratTableModel2) tblVorrat.getModel();
//            Tools.log(txtLog, "Folgende Vorräte wurden auf Unbekannt umgebucht:");
//            Tools.log(txtLog, "================================================");
//            for (int row = 0; row < model.getRowCount(); row++) {
//                // Model Row Index Umwandlung ist hier unnötig. Markierungen bleiben unberücksichtigt.
//                if (model.getStatus(row) == VorratTableModel2.STATUS_FRAGLICH) {
//                    Vorrat vorrat = model.getVorrat(row);
//                    Tools.log(txtLog, vorrat.getId(), vorrat.getProdukt().getBezeichnung(), "");
//                    vorrat.setLager(unbekannt);
//                    EntityTools.merge(vorrat);
//                }
//            }
//            Tools.log(txtLog, "================================================");
//        } else {
//            EntityManager em = Main.getEMF().createEntityManager();
//            try {
//                em.getTransaction().begin();
//                int[] rows = tblVorrat.getSelectedRows();
//
//                for (int r = 0; r < rows.length; r++) {
//                    // Diese Zeile ist sehr wichtig, da sie die Auswahl in der Tabelle bzgl. einer Umsortierung berücksichtigt.
//                    int row = tblVorrat.convertRowIndexToModel(rows[r]);
//                    Vorrat vorrat = ((VorratTableModel2) tblVorrat.getModel()).getVorrat(row);
//                    if (laufendeVOperation == LAUFENDE_OPERATION_LOESCHEN) {
//                        Main.logger.info("DELETE STOCK: " + vorrat.toString());
//                        Tools.log(txtLog, vorrat.getId(), vorrat.getProdukt().getBezeichnung(), "GELÖSCHT");
//                        EntityTools.delete(vorrat);
//                    } else if (laufendeVOperation == LAUFENDE_OPERATION_AUSBUCHEN) {
//                        Main.logger.info("AUSBUCHEN STOCK: " + vorrat.toString());
//                        Tools.log(txtLog, vorrat.getId(), vorrat.getProdukt().getBezeichnung(), "AUSGEBUCHT");
//                        VorratTools.ausbuchen(vorrat, "Abschlussbuchung");
//                    }
//                }
//                em.getTransaction().commit();
//            } catch (Exception e1) {
//                em.getTransaction().rollback();
//            } finally {
//                em.close();
//            }
//        }
//        timeline.cancel();
//        splitLeftRightDouble = Tools.showSide(splitLeftRight, Tools.LEFT_UPPER_SIDE, 400);
//
//        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;
//        loadVorratTable();
//    }

    private void btnAusbuchenActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            int[] rows = tblVorrat.getSelectedRows();

            for (int r = 0; r < rows.length; r++) {
                // Diese Zeile ist sehr wichtig, da sie die Auswahl in der Tabelle bzgl. einer Umsortierung berücksichtigt.
                int row = tblVorrat.convertRowIndexToModel(rows[r]);
                Stock stock = ((StockTableModel2) tblVorrat.getModel()).getVorrat(row);

                Main.logger.info("AUSBUCHEN STOCK: " + stock.toString());
                Tools.log(txtLog, stock.getId(), stock.getProdukt().getBezeichnung(), "AUSGEBUCHT");
                StockTools.ausbuchen(stock, "Abschlussbuchung");
            }
            em.getTransaction().commit();
        } catch (Exception e1) {
            em.getTransaction().rollback();
        } finally {
            em.close();
            loadVorratTable();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        defaultTouchPanel1 = new DefaultTouchPanel();
        txtSearch = new JTextField();
        lblProdukt = new JLabel();
        cmbLieferant = new JComboBox();
        cmbLager = new JComboBox();
        pnlLog = new JPanel();
        logScrollPane = new JScrollPane();
        txtLog = new JTextArea();
        label1 = new JLabel();
        pnlVorraete = new JPanel();
        title1 = new JLabel();
        scrollPane2 = new JScrollPane();
        tblVorrat = new JTable();
        cbZombieRevive = new JToggleButton();
        panel2 = new JPanel();
        btnClear = new JButton();
        btnPrint = new JButton();
        btnToUnbekannt = new JButton();
        btnAusbuchen = new JButton();
        btnUmbuchen = new JButton();
        btnSofortUmbuchen = new JToggleButton();

        //======== this ========
        setLayout(new CardLayout());

        //======== defaultTouchPanel1 ========
        {
            defaultTouchPanel1.setLayout(new FormLayout(
                "276dlu, $lcgap, pref:grow, $lcgap, center:default",
                "4*(30dlu, $lgap), fill:default:grow, $lgap, default"));

            //---- txtSearch ----
            txtSearch.setFont(new Font("sansserif", Font.BOLD, 24));
            txtSearch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtSearchActionPerformed(e);
                }
            });
            txtSearch.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtSearchFocusGained(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    txtSearchFocusLost(e);
                }
            });
            txtSearch.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtSearchCaretUpdate(e);
                }
            });
            defaultTouchPanel1.add(txtSearch, CC.xywh(1, 1, 5, 1, CC.DEFAULT, CC.FILL));

            //---- lblProdukt ----
            lblProdukt.setText(" ");
            lblProdukt.setFont(new Font("sansserif", Font.BOLD, 24));
            lblProdukt.setHorizontalAlignment(SwingConstants.CENTER);
            lblProdukt.setBackground(new Color(204, 204, 255));
            lblProdukt.setOpaque(true);
            defaultTouchPanel1.add(lblProdukt, CC.xywh(1, 3, 5, 1, CC.DEFAULT, CC.FILL));

            //---- cmbLieferant ----
            cmbLieferant.setFont(new Font("sansserif", Font.PLAIN, 18));
            cmbLieferant.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbLieferantItemStateChanged(e);
                }
            });
            defaultTouchPanel1.add(cmbLieferant, CC.xywh(1, 5, 5, 1, CC.DEFAULT, CC.FILL));

            //---- cmbLager ----
            cmbLager.setFont(new Font("sansserif", Font.PLAIN, 18));
            cmbLager.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbLagerItemStateChanged(e);
                }
            });
            defaultTouchPanel1.add(cmbLager, CC.xywh(1, 7, 5, 1, CC.DEFAULT, CC.FILL));

            //======== pnlLog ========
            {
                pnlLog.setLayout(new BorderLayout());

                //======== logScrollPane ========
                {
                    logScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                    //---- txtLog ----
                    txtLog.setBackground(Color.lightGray);
                    txtLog.setFont(new Font("sansserif", Font.PLAIN, 18));
                    txtLog.setEditable(false);
                    txtLog.setWrapStyleWord(true);
                    txtLog.setLineWrap(true);
                    logScrollPane.setViewportView(txtLog);
                }
                pnlLog.add(logScrollPane, BorderLayout.CENTER);

                //---- label1 ----
                label1.setText("Logbuch");
                label1.setBackground(new Color(51, 51, 255));
                label1.setForeground(Color.yellow);
                label1.setOpaque(true);
                label1.setFont(new Font("sansserif", Font.BOLD, 24));
                label1.setHorizontalAlignment(SwingConstants.CENTER);
                pnlLog.add(label1, BorderLayout.NORTH);
            }
            defaultTouchPanel1.add(pnlLog, CC.xy(1, 9));

            //======== pnlVorraete ========
            {
                pnlVorraete.setLayout(new BorderLayout());

                //---- title1 ----
                title1.setFont(new Font("sansserif", Font.BOLD, 24));
                title1.setText("Vorr\u00e4te zur \u00dcberpr\u00fcfung");
                title1.setBackground(new Color(51, 255, 51));
                title1.setOpaque(true);
                title1.setHorizontalAlignment(SwingConstants.CENTER);
                pnlVorraete.add(title1, BorderLayout.NORTH);

                //======== scrollPane2 ========
                {

                    //---- tblVorrat ----
                    tblVorrat.setFont(new Font("sansserif", Font.PLAIN, 18));
                    tblVorrat.setAutoCreateRowSorter(true);
                    tblVorrat.setRowHeight(20);
                    scrollPane2.setViewportView(tblVorrat);
                }
                pnlVorraete.add(scrollPane2, BorderLayout.CENTER);

                //---- cbZombieRevive ----
                cbZombieRevive.setText("Wieder einbuchen wenn n\u00f6tig");
                cbZombieRevive.setFont(new Font("Arial", Font.PLAIN, 26));
                cbZombieRevive.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cb-off.png")));
                cbZombieRevive.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cb-on.png")));
                cbZombieRevive.setPressedIcon(null);
                pnlVorraete.add(cbZombieRevive, BorderLayout.SOUTH);
            }
            defaultTouchPanel1.add(pnlVorraete, CC.xy(3, 9));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

                //---- btnClear ----
                btnClear.setToolTipText("Logbuch l\u00f6schen");
                btnClear.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/editclear.png")));
                btnClear.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnClearActionPerformed(e);
                    }
                });
                panel2.add(btnClear);

                //---- btnPrint ----
                btnPrint.setToolTipText("Logbuch drucken");
                btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/printer.png")));
                btnPrint.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPrintActionPerformed(e);
                    }
                });
                panel2.add(btnPrint);

                //---- btnToUnbekannt ----
                btnToUnbekannt.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/db_status.png")));
                btnToUnbekannt.setToolTipText("Fragliche Vorr\u00e4te auf Unbekannt buchen");
                btnToUnbekannt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnToUnbekanntActionPerformed(e);
                    }
                });
                panel2.add(btnToUnbekannt);

                //---- btnAusbuchen ----
                btnAusbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/games.png")));
                btnAusbuchen.setToolTipText("Markierte Vorr\u00e4te ausbuchen");
                btnAusbuchen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAusbuchenActionPerformed(e);
                    }
                });
                panel2.add(btnAusbuchen);
            }
            defaultTouchPanel1.add(panel2, CC.xy(5, 9));

            //---- btnUmbuchen ----
            btnUmbuchen.setText("Umbuchen");
            btnUmbuchen.setFont(new Font("sansserif", Font.BOLD, 24));
            btnUmbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/apply.png")));
            btnUmbuchen.setEnabled(false);
            btnUmbuchen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnUmbuchenActionPerformed(e);
                }
            });
            defaultTouchPanel1.add(btnUmbuchen, CC.xywh(1, 11, 3, 1));

            //---- btnSofortUmbuchen ----
            btnSofortUmbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/agt_member.png")));
            btnSofortUmbuchen.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnSofortUmbuchenItemStateChanged(e);
                }
            });
            defaultTouchPanel1.add(btnSofortUmbuchen, CC.xy(5, 11));
        }
        add(defaultTouchPanel1, "card1");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    private void myInit() {
        cmbLager.setModel(tools.Tools.newComboboxModel(LagerTools.getAll()));
        cmbLager.setSelectedIndex(Integer.parseInt(Main.getProps().getProperty("touch" + MODULENUMBER + "lager")));
        loadLieferant();
        cmbLieferant.setSelectedIndex(0);
        ziel = (Lager) cmbLager.getSelectedItem();
        lieferant = null;
        txtSearch.requestFocus();
        btnSofortUmbuchen.setSelected(true);
        cbZombieRevive.setSelected(true);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private DefaultTouchPanel defaultTouchPanel1;
    private JTextField txtSearch;
    private JLabel lblProdukt;
    private JComboBox cmbLieferant;
    private JComboBox cmbLager;
    private JPanel pnlLog;
    private JScrollPane logScrollPane;
    private JTextArea txtLog;
    private JLabel label1;
    private JPanel pnlVorraete;
    private JLabel title1;
    private JScrollPane scrollPane2;
    private JTable tblVorrat;
    private JToggleButton cbZombieRevive;
    private JPanel panel2;
    private JButton btnClear;
    private JButton btnPrint;
    private JButton btnToUnbekannt;
    private JButton btnAusbuchen;
    private JButton btnUmbuchen;
    private JToggleButton btnSofortUmbuchen;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
