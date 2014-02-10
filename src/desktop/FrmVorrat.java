/*
 * Created by JFormDesigner on Wed Feb 09 16:13:45 CET 2011
 */

package desktop;


import Main.Main;
import beans.PrintListElement;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.*;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.pushingpixels.trident.Timeline;
import printer.Form;
import printer.Printer;
import tablemodels.VorratTableModel;
import threads.PrintProcessor;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * @author Torsten Löhr
 */
public class FrmVorrat extends javax.swing.JInternalFrame {
    private JPopupMenu menu;
    private Object[] spaltenVorrat = new Object[]{"Vorrat Nr.", "Bezeichnung", "Lagerort", "Lieferant", "GTIN", "Eingangsmenge", "Restmenge", "Stoffart", "Warengruppe", "Eingang", "Anbruch", "Ausgang"};
    private Object[] spaltenBuchungen = new Object[]{"Datum", "Text", "Menge", "MitarbeiterIn"};
    private TableModelListener tml;
    //    private double splitMainDL, splitLeftButtonsMainDL, splitLBMLeftDL, splitVorratDL;
//    private int vmode, bmode, laufendeVOperation;
    private Timeline textmessageTL;

    private Printer pageprinter, etiprinter1, etiprinter2;
    private Form form1, form2;

    private JComponent thisComponent;

    private boolean initphase;
    //private final Color searchColor = new Color(255, 0, 51);

//    private ListSelectionListener vlsl, blsl;

    private Pair<Integer, Object> suche = null;

//    private final int MODE_VORRAT_BROWSE = 0;
//    private final int MODE_VORRAT_EDIT = 1;
//    private final int MODE_VORRAT_OTHER_OPERATION = 2;
//
//    private final int LAUFENDE_OPERATION_NICHTS = 0;
//    private final int LAUFENDE_OPERATION_LOESCHEN = 1;
//    private final int LAUFENDE_OPERATION_AUSBUCHEN = 2;
//    private final int LAUFENDE_OPERATION_REAKTIVIERUNG = 3;
//    private final int LAUFENDE_OPERATION_KORREKTUR = 4;
//
//    private final int MODE_BUCHUNG_INVISIBLE = 0;
//    private final int MODE_BUCHUNG_BROWSING = 1;

    public FrmVorrat() {
        initphase = true;
        thisComponent = this;
        initComponents();
        myInit();
        pack();
        initphase = false;
        thisComponentResized(null);
    }

    private void loadVorratTable() {

//        // Alle evtl. laufenden Edits werden sofort gecancelt.
//        if (vmode == MODE_VORRAT_EDIT) {
//            btnEditVorrat.setSelected(false);
//        }
//
//        if (laufendeVOperation != LAUFENDE_OPERATION_NICHTS) {
//            btnCancel1.doClick();
//        }

        //splitMainDividerLocation = Tools.showSide(splitMain, Tools.LEFT_UPPER_SIDE, 400);
        Query query = null;

//        Tools.resetBackground(pnlSuche);

        String strQueryAddendum = "Alle";
        if (btnAktiv.isSelected()) {
            strQueryAddendum = "Aktiv";
        } else if (btnInaktiv.isSelected()) {
            strQueryAddendum = "Inaktiv";
        }

        try {
            EntityManager em = Main.getEMF().createEntityManager();
            if (suche == null) {
                query = em.createNamedQuery("Buchungen.findSUMByAlle" + strQueryAddendum);
            } else if (suche.getFirst() == Const.DATUM) {
                Date eingang = (Date) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByVorratDatum" + strQueryAddendum);
                query.setParameter("eingang1", new Date(Tools.startOfDay(eingang)));
                query.setParameter("eingang2", new Date(Tools.endOfDay(eingang)));
            } else if (suche.getFirst() == Const.PRODUKT) {
                Produkte produkt = (Produkte) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByProdukt" + strQueryAddendum);
                query.setParameter("produkt", produkt);
            } else if (suche.getFirst() == Const.VORRAT) {
                Vorrat vorrat = (Vorrat) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByVorrat" + strQueryAddendum);
                query.setParameter("vorrat", vorrat);
            } else if (suche.getFirst() == Const.LAGER) {
                initphase = true;
                cmbWarengruppe.setSelectedIndex(0);
                cmbLieferant.setSelectedIndex(0);
                initphase = false;
                Lager lager = (Lager) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByLager" + strQueryAddendum);
                query.setParameter("lager", lager);
            } else if (suche.getFirst() == Const.WARENGRUPPE) {
                initphase = true;
                cmbLager.setSelectedIndex(0);
                cmbLieferant.setSelectedIndex(0);
                initphase = false;
                Warengruppe warengruppe = (Warengruppe) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByWarengruppe" + strQueryAddendum);
                query.setParameter("warengruppe", warengruppe);
            } else if (suche.getFirst() == Const.LIEFERANT) {
                initphase = true;
                cmbLager.setSelectedIndex(0);
                cmbWarengruppe.setSelectedIndex(0);
                initphase = false;
                Lieferanten lieferant = (Lieferanten) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByLieferant" + strQueryAddendum);
                query.setParameter("lieferant", lieferant);
            } else if (suche.getFirst() == Const.PRODUKTNAME) {
                query = em.createNamedQuery("Buchungen.findSUMByProduktBezeichnung" + strQueryAddendum);
                query.setParameter("bezeichnung", "%" + suche.getSecond().toString().trim() + "%");
            } else if (suche.getFirst() == Const.LAGERART) {
                short lagerart = (Short) suche.getSecond();
                query = em.createNamedQuery("Buchungen.findSUMByLagerart" + strQueryAddendum);
                query.setParameter("lagerart", lagerart);
            }


            if (query != null) {

                List list = query.getResultList();

//                if (!initphase) {
//                    tblVorrat.getSelectionModel().removeListSelectionListener(vlsl);
//                }

                tblVorrat.setModel(new VorratTableModel(list, spaltenVorrat));
//                tblVorrat.getSelectionModel().addListSelectionListener(vlsl);

                TableRowSorter sorter = new TableRowSorter(tblVorrat.getModel());
                sorter.setComparator(VorratTableModel.COL_LAGER, new Comparator<Lager>() {
                    public int compare(Lager l1, Lager l2) {
                        return l1.toString().compareToIgnoreCase(l2.toString());
                    }
                });

                tblVorrat.setRowSorter(sorter);

                tblVorrat.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                pnlVorratComponentResized(null);

                //btnDeleteVorrat.setEnabled(false);
                //btnAusbuchen.setEnabled(false);
//                btnPrintEti1.setEnabled(false);
//                btnPrintEti2.setEnabled(false);
//                btnPageprinter.setEnabled(false);
//                btnEditVorrat.setEnabled(false);
//
//                btnShowRightSide.setEnabled(false);


            }
        } catch (Exception e) { // nicht gefunden
            Main.logger.fatal(e.getMessage(), e);
            //e.printStackTrace();
        }
    }

    private void initBuchungenTable() {
//        if (btnShowRightSide.isSelected() && tblVorrat.getSelectedRow() >= 0) {
//
////            // erst alte Eingabe rückgängig machen
////            if (btnCancelBuchung.isEnabled()) {
////                btnCancelBuchung.doClick();
////            }
//
//            final Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
//            tblBuchungen.setModel(new BuchungenTableModel((List) vorrat.getBuchungenCollection(), spaltenBuchungen));
//            //tblBuchungen.getColumnModel().getColumn(BuchungenTableModel.COL_DATUM).getCellEditor().getTableCellEditorComponent(tblVorrat, ui, isSelected, WIDTH, WIDTH);
//
//            tblBuchungen.setRowSorter(null);
//            // Damit die Anzeige auf der anderen Seite aktualisiert wird.
//            if (tml != null) {
//                tblBuchungen.getModel().removeTableModelListener(tml);
//            }
//            tml = new TableModelListener() {
//
//                public void tableChanged(TableModelEvent e) {
//                    // Summe in der Vorrat Tabelle korrigieren.
//                    EntityManager em = Main.getEMF().createEntityManager();
//                    javax.persistence.Query query = em.createNamedQuery("Vorrat.Buchungen.summeBestand");
//                    query.setParameter("vorrat", vorrat);
//                    ((Object[]) ((VorratTableModel) tblVorrat.getModel()).getData().get(tblVorrat.getSelectedRow()))[1] = (BigDecimal) query.getSingleResult();
//                    ((VorratTableModel) tblVorrat.getModel()).fireTableCellUpdated(tblVorrat.getSelectedRow(), VorratTableModel.COL_RESTMENGE);
//                    em.close();
//                }
//            };
//
//            tblBuchungen.getModel().addTableModelListener(tml);
//            //pnlBuchungenComponentResized(null);
//
//        } else {
//            tblBuchungen.setModel(new DefaultTableModel());
//        }


    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {

        }
        super.setVisible(aFlag);
    }

    private void loadWarengruppe() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Warengruppe.findAllSorted");
        java.util.List warengruppe = query.getResultList();
        warengruppe.add(0, "<html><i>nach Warengruppe</i></html>");
        cmbWarengruppe.setModel(tools.Tools.newComboboxModel(warengruppe));
        em.close();
    }

    private void loadLager() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Lager.findAllSorted");
        java.util.List lager = query.getResultList();
        lager.add(0, "<html><i>nach Lager</i></html>");
        cmbLager.setModel(tools.Tools.newComboboxModel(lager));
        em.close();
    }

    private void loadLagerart() {
        DefaultComboBoxModel dcmb = new DefaultComboBoxModel(LagerTools.LAGERART);
        dcmb.insertElementAt("<html><i>nach Lagerart</i></html>", 0);
        cmbLagerart.setModel(dcmb);
        cmbLagerart.setSelectedIndex(0);
    }

    private void loadLieferanten() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Lieferanten.findAllSorted");
        java.util.List lieferanten = query.getResultList();
        lieferanten.add(0, "<html><i>nach Lieferanten</i></html>");
        cmbLieferant.setModel(tools.Tools.newComboboxModel(lieferanten));
        em.close();
    }

    private void pnlVorratComponentResized(ComponentEvent e) {
//        Dimension dim = pnlVorrat.getSize();
//        int[] width = new int[]{70, 0, 215, 215, 100, 60, 60, 110, 110, 90, 90, 90};
//        int textWidth = dim.width - Tools.sum(width) - 10;
//        if (textWidth > 0) {
//            width[VorratTableModel.COL_BEZEICHNUNG] = textWidth;
//            Tools.setTableColumnPreferredWidth(tblVorrat, width);
//        }
        Tools.packTable(tblVorrat, 0);
    }

    private void print(Printer printer, Form form, String printername) {

        java.util.List<PrintListElement> printList = new ArrayList(tblVorrat.getRowCount());
        PrintProcessor pp = ((FrmDesktop) Main.mainframe).getPrintProcessor();

        int[] rows = tblVorrat.getSelectedRows();

        for (int r = 0; r < rows.length; r++) {
            int row = tblVorrat.convertRowIndexToModel(rows[r]);
            Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(row);
            printList.add(new PrintListElement(vorrat, printer, form, printername));
        }

        Collections.sort(printList); // Sortieren nach den PrimaryKeys
        pp.addPrintJobs(printList);

    }

//    private void btnSaveBuchungActionPerformed(ActionEvent e) {
//        if (initphase) return;
//        ((BuchungenTableModel) tblBuchungen.getModel()).saveRow(tblBuchungen.getRowCount() - 1);
//        btnAddBuchung.setEnabled(true);
//        btnSaveBuchung.setEnabled(false);
//        btnCancelBuchung.setEnabled(false);
//    }
//
//    private void btnCancelBuchungActionPerformed(ActionEvent e) {
//        if (initphase) return;
//        ((BuchungenTableModel) tblBuchungen.getModel()).cancelNewRow();
//        btnAddBuchung.setEnabled(true);
//        btnSaveBuchung.setEnabled(false);
//        btnCancelBuchung.setEnabled(false);
//    }
//
//    private void btnGesamtBestandActionPerformed(ActionEvent e) {
//        pnlZeit.setCollapsed(true);
//        pnlSpezial.setCollapsed(true);
//
//        splitMain.setDividerLocation(0);
//        //Tools.showSide(splitMain, splitMainDividerLocation, 3000);
//    }

    private void thisComponentResized(ComponentEvent e) {

    }

    private void pnlZeitPropertyChange(PropertyChangeEvent e) {
        Main.debug(e.getPropertyName());
    }

    private void jdcEinbuchenPropertyChange(PropertyChangeEvent e) {
        Main.debug(jdcEinbuchen.getDate());
        initphase = true;
        cmbLager.setSelectedIndex(0);
        cmbLieferant.setSelectedIndex(0);
        cmbWarengruppe.setSelectedIndex(0);
        txtSuchen.setText("");
        tpSpezial.setCollapsed(true);
        initphase = false;
        suche = new Pair<Integer, Object>(Const.DATUM, jdcEinbuchen.getDate());
        loadVorratTable();
    }

    private void cmbWarengruppeItemStateChanged(ItemEvent e) {
        if (initphase) return;
        tpZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.WARENGRUPPE, cmbWarengruppe.getSelectedItem());
            loadVorratTable();
        }
    }

    private void cmbLagerItemStateChanged(ItemEvent e) {
        if (initphase) return;
        tpZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LAGER, cmbLager.getSelectedItem());
            loadVorratTable();
        }
    }

//    private void btnShowRightSideItemStateChanged(ItemEvent e) {
//        btnShowRightSide.setToolTipText(btnShowRightSide.isSelected() ? "Buchungen ausblenden" : "Buchungen einblenden");
//        splitMainDL = btnShowRightSide.isSelected() ? 0.65d : 1.0d;
//        Tools.showSide(splitMain, splitMainDL, 400);
//        initBuchungenTable();
//    }

    private void cmbLieferantItemStateChanged(ItemEvent e) {
        if (initphase) return;
        tpZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LIEFERANT, cmbLieferant.getSelectedItem());
            loadVorratTable();
        }
    }

//    private void splitLeftButtonsMainComponentResized(ComponentEvent e) {
//        splitLeftButtonsMain.setDividerLocation(splitLeftButtonsMainDL);
//    }
//
//    private void splitLBMLeftComponentResized(ComponentEvent e) {
//        splitLBMLeft.setDividerLocation(splitLBMLeftDL);
//    }
//
//    private void splitMainComponentResized(ComponentEvent e) {
//        splitMain.setDividerLocation(splitMainDL);
//    }
//
//    private void splitVorratComponentResized(ComponentEvent e) {
//        splitVorrat.setDividerLocation(splitVorratDL);
//    }
//
//    private void btnEditVorratItemStateChanged(ItemEvent e) {
//        if (btnEditVorrat.isSelected()) {
//            vmode = MODE_VORRAT_EDIT;
//            splitVorratDL = Tools.showSide(splitVorrat, 0.75d, 400);
//            splitLBMLeftDL = Tools.showSide(splitLBMLeft, Tools.RIGHT_LOWER_SIDE, 400);
//
//            if (tblVorrat.getSelectedRowCount() == 1) {
//                Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
//                cmbVEditLager.setSelectedItem(vorrat.getLager());
//                cmbVEditLieferant.setSelectedItem(vorrat.getLieferant());
//            } else {
//                cmbVEditLager.setSelectedIndex(0);
//                cmbVEditLieferant.setSelectedIndex(0);
//            }
//
//        } else {
//            vmode = MODE_VORRAT_BROWSE;
//            splitVorratDL = Tools.showSide(splitVorrat, Tools.LEFT_UPPER_SIDE, 400);
//            splitLBMLeftDL = Tools.showSide(splitLBMLeft, Tools.LEFT_UPPER_SIDE, 400);
//        }
//    }
//
//    private void btnSaveVEditActionPerformed(ActionEvent e) {
//        int[] rows = tblVorrat.getSelectedRows();
//
//        for (int r = 0; r < rows.length; r++) {
//            int row = tblVorrat.convertRowIndexToModel(rows[r]);
//            Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(row);
//            if (cmbVEditLager.getSelectedIndex() > 0) {
//                vorrat.setLager((Lager) cmbVEditLager.getSelectedItem());
//            }
//            if (cmbVEditLieferant.getSelectedIndex() > 0) {
//                vorrat.setLieferant((Lieferanten) cmbVEditLieferant.getSelectedItem());
//            }
//            EntityTools.merge(vorrat);
//        }
//
//        btnEditVorrat.setSelected(false);
//        loadVorratTable();
//    }
//
//    private void btnCancelVEditActionPerformed(ActionEvent e) {
//        btnEditVorrat.setSelected(false);
//    }

    private void pnlWarenbestandComponentResized(ComponentEvent e) {

    }

//    private void btnDeleteVorratActionPerformed(ActionEvent e) {
//        laufendeVOperation = LAUFENDE_OPERATION_LOESCHEN;
//        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
//        showVEditMessage("markierte Vorräte wirklich löschen ?");
//    }
//
//    private void btnCancel1ActionPerformed(ActionEvent e) {
//        textmessageTL.cancel();
//        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;
//        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.LEFT_UPPER_SIDE, 400);
//    }

//    private void btnApply1ActionPerformed(ActionEvent e) {
//        int[] rows = tblVorrat.getSelectedRows();
//
//        EntityManager em = Main.getEMF().createEntityManager();
//        try {
//            em.getTransaction().begin();
//            for (int r = 0; r < rows.length; r++) {
//                // Diese Zeile ist sehr wichtig, da sie die Auswahl in der Tabelle bzgl. einer Umsortierung berücksichtigt.
//                int row = tblVorrat.convertRowIndexToModel(rows[r]);
//
//                Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(row);
//                if (laufendeVOperation == LAUFENDE_OPERATION_LOESCHEN) {
//                    Main.logger.info("DELETE VORRAT: " + vorrat.toString());
//                    EntityTools.delete(vorrat);
//                } else if (laufendeVOperation == LAUFENDE_OPERATION_AUSBUCHEN) {
//                    Main.logger.info("AUSBUCHEN VORRAT: " + vorrat.toString());
//                    VorratTools.ausbuchen(vorrat, "Abschlussbuchung");
//                } else if (laufendeVOperation == LAUFENDE_OPERATION_REAKTIVIERUNG) {
//                    Main.logger.info("ZURÜCK BUCHEN VORRAT: " + vorrat.toString());
//                    VorratTools.reaktivieren(em, vorrat);
//                } else if (laufendeVOperation == LAUFENDE_OPERATION_KORREKTUR) {
//                    Main.logger.info("ANFANGSBESTÄNDE KORRIGIEREN: " + vorrat.toString());
//                    VorratTools.korregiereAnfangsbestand(em, vorrat);
//                } else {
//
//                }
//            }
//            em.getTransaction().commit();
//        } catch (Exception e1) {
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//        }
//        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;
//        textmessageTL.cancel();
//        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.LEFT_UPPER_SIDE, 400);
//
//        loadVorratTable();
//    }

//    private void btnAusbuchenActionPerformed(ActionEvent e) {
//        laufendeVOperation = LAUFENDE_OPERATION_AUSBUCHEN;
//        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
//        showVEditMessage("markierte Vorräte wirklich ausbuchen ?");
//    }
//
//    private void btnPrintEti2ActionPerformed(ActionEvent e) {
//        print(etiprinter2, form2, Main.getProps().getProperty("etiprinter2"));
//    }
//
//    private void btnPageprinterActionPerformed(ActionEvent e) {
//        print(pageprinter, null, Main.getProps().getProperty("pageprinter"));
//    }

    private void txtSuchenActionPerformed(ActionEvent e) {
        suche = null;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            String suchtext = txtSuchen.getText().trim();
            if (!suchtext.isEmpty()) {
                // Was genau wird gesucht ?

                if (suchtext.matches("^" + ProdukteTools.IN_STORE_PREFIX + "\\d{11}$")) { // VorratID in EAN 13 Kodiert mit in-store Präfix (z.B. 20)
                    // Ausschneiden der VorID aus dem EAN Code. IN-STORE-PREFIX und die Prüfsummenziffer weg.
                    Vorrat vorrat = em.find(Vorrat.class, Long.parseLong(suchtext.substring(2, 12)));
                    if (vorrat != null) {
                        suche = new Pair<Integer, Object>(Const.VORRAT, vorrat);
                    }
                } else if (ProdukteTools.isGTIN(suchtext)) {  // GTIN13
                    java.util.List<Produkte> list = ProdukteTools.searchProdukte(suchtext);
                    // Da GTINs eindeutig sind, kann die Liste immer nur einen Eintrag haben.
                    if (!list.isEmpty()) {
                        suche = new Pair<Integer, Object>(Const.PRODUKT, list.get(0));
                    }
                } else if (suchtext.matches("^\\d+")) { // Nur Ziffern, dann kann das nur eine VorratID von Hand
                    Vorrat vorrat = em.find(Vorrat.class, Long.parseLong(suchtext));
                    if (vorrat != null) {
                        suche = new Pair<Integer, Object>(Const.VORRAT, vorrat);
                    }
                } else { // Produktbezeichnung
                    if (!suchtext.isEmpty()) {
                        suche = new Pair<Integer, Object>(Const.PRODUKTNAME, suchtext);
                    }
                }
            }
        } catch (Exception e1) {
            suche = null;
        } finally {
            em.close();
        }
        loadVorratTable();
    }

    private void btnNoFilterActionPerformed(ActionEvent e) {
        initphase = true;
        cmbLager.setSelectedIndex(0);
        cmbLagerart.setSelectedIndex(0);
        cmbLieferant.setSelectedIndex(0);
        cmbWarengruppe.setSelectedIndex(0);
        txtSuchen.setText("");
        tpZeit.setCollapsed(true);
        suche = null;
        initphase = false;
        loadVorratTable();
    }

    private void txtSuchenCaretUpdate(CaretEvent e) {
        initphase = true;
        cmbLager.setSelectedIndex(0);
        cmbLieferant.setSelectedIndex(0);
        cmbWarengruppe.setSelectedIndex(0);
        initphase = false;
    }

    private void btnAuswahlItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            loadVorratTable();
        }
    }

//    private void btnReopenActionPerformed(ActionEvent e) {
//        if (laufendeVOperation != LAUFENDE_OPERATION_NICHTS || !btnInaktiv.isSelected()) return;
//        laufendeVOperation = LAUFENDE_OPERATION_REAKTIVIERUNG;
//        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
//        showVEditMessage("markierte Vorräte wirklich zurückbuchen ?");
//    }
//
//    private void btnAnfangKorrigierenActionPerformed(ActionEvent e) {
//        if (laufendeVOperation != LAUFENDE_OPERATION_NICHTS) return;
//        laufendeVOperation = LAUFENDE_OPERATION_KORREKTUR;
//        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
//        showVEditMessage("markierte Vorräte wirklich korrigieren ?");
//    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {
        if (initphase) return;
        tpZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LAGERART, (short) cmbLagerart.getSelectedIndex());
            loadVorratTable();
        }
    }

    private void btnSumItemStateChanged(ItemEvent e) {
        btnSum.setText(btnSum.isSelected() ? "Restsummen anzeigen" : "Summe bei Einbuchung");
    }

    private void tblVorratMousePressed(MouseEvent evt) {
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblVorrat.getSelectionModel();
        Point p2 = evt.getPoint();
        SwingUtilities.convertPointToScreen(p2, tblVorrat);
        final Point screenposition = p2;

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tblVorrat.rowAtPoint(p);
        final int col = tblVorrat.columnAtPoint(p);
        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        final VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();

        if (SwingUtilities.isRightMouseButton(evt)) {
            DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
            Tools.unregisterListeners(menu);
            menu = new JPopupMenu();

            menu.add(compFactory.createSeparator("Operationen", SwingConstants.CENTER));
            JMenuItem itemAusbuchen = new JMenuItem("Ausbuchen");
            menu.add(itemAusbuchen);
            JMenuItem itemLoeschen = new JMenuItem("Löschen");
            menu.add(itemLoeschen);
            JMenuItem itemWiederOeffnen = new JMenuItem("wieder öffnen");
            menu.add(itemWiederOeffnen);
            JMenuItem itemAnfangKorrektur = new JMenuItem("Anfangsbestände korrigieren");
            menu.add(itemAnfangKorrektur);


            menu.add(compFactory.createSeparator("Zuordnungen", SwingConstants.CENTER));
            JMenu lager = new JMenu("Lager");
            lager.add(new JMenuItem("Kühlhaus"));
            lager.add(new JMenuItem("Truhe 1"));

            menu.add(lager);


            JMenu lieferanten = new JMenu("Lieferanten");
            lieferanten.add(new JMenuItem("Sohnius"));
            lieferanten.add(new JMenuItem("Aldi"));
            menu.add(lieferanten);

            menu.add(compFactory.createSeparator("Etiketten-Druck", SwingConstants.CENTER));

            // Printer
            JMenuItem itemPrinter1 = new JMenuItem("Drucker 1", Const.icon22labelPrinter2);
            itemPrinter1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    print(etiprinter1, form1, Main.getProps().getProperty("etiprinter1"));
                }
            });
            menu.add(itemPrinter1);

            JMenuItem itemPrinter2 = new JMenuItem("Drucker 2", Const.icon22labelPrinter2);
            itemPrinter2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    print(etiprinter2, form2, Main.getProps().getProperty("etiprinter2"));
                }
            });
            menu.add(itemPrinter2);

            JMenuItem itemPrinter3 = new JMenuItem("Seitendrucker", Const.icon22Pageprinter);
            itemPrinter3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    print(pageprinter, null, Main.getProps().getProperty("pageprinter"));
                }
            });
            menu.add(itemPrinter3);


//            if (col == TMSYSFiles.COL_DESCRIPTION && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
//
//                final JMenuItem itemPopupEdit = new JMenuItem(OPDE.lang.getString("misc.commands.edit"), SYSConst.icon22edit3);
//                itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                        final JidePopup popup = new JidePopup();
//                        popup.setMovable(false);
//                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                        final JComponent editor = new JTextArea(sysfile.getBeschreibung(), 10, 40);
//                        ((JTextArea) editor).setLineWrap(true);
//                        ((JTextArea) editor).setWrapStyleWord(true);
//                        ((JTextArea) editor).setEditable(true);
//
//                        popup.getContentPane().add(new JScrollPane(editor));
//                        final JButton saveButton = new JButton(SYSConst.icon22apply);
//                        saveButton.addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent actionEvent) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    popup.hidePopup();
//                                    SYSFiles mySysfile = em.merge(sysfile);
//                                    mySysfile.setBeschreibung(((JTextArea) editor).getText().trim());
//                                    em.getTransaction().commit();
//                                    tm.setSYSFile(row, mySysfile);
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//
//                            }
//                        });
//
//                        saveButton.setHorizontalAlignment(SwingConstants.RIGHT);
//                        JPanel pnl = new JPanel(new BorderLayout(10, 10));
//                        JScrollPane pnlEditor = new JScrollPane(editor);
//
//                        pnl.add(pnlEditor, BorderLayout.CENTER);
//                        JPanel buttonPanel = new JPanel();
//                        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
//                        buttonPanel.add(saveButton);
//                        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
//                        pnl.add(buttonPanel, BorderLayout.SOUTH);
//
//                        popup.setOwner(tblFiles);
//                        popup.removeExcludedComponent(tblFiles);
//                        popup.getContentPane().add(pnl);
//                        popup.setDefaultFocusComponent(editor);
//
//                        popup.showPopup(screenposition.x, screenposition.y);
//
//                    }
//                });
//                menu.add(itemPopupEdit);
//            }


//            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
//                JMenuItem itemPopupDelete = new JMenuItem(OPDE.lang.getString("misc.commands.delete"), SYSConst.icon22delete);
//                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                        new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><b>" + sysfile.getFilename() + "</b><br/>" + OPDE.lang.getString("misc.questions.delete2"), new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o.equals(JOptionPane.YES_OPTION)) {
//                                    SYSFilesTools.deleteFile(sysfile);
//                                    reloadTable();
//                                }
//                            }
//                        });
//
//                    }
//                });
//                menu.add(itemPopupDelete);
//                itemPopupDelete.setEnabled(singleRowSelected);
//            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        pnlMain = new JPanel();
        jspSearch = new JScrollPane();
        tpcSearch = new JXTaskPaneContainer();
        separator1 = compFactory.createSeparator("Suchen", SwingConstants.CENTER);
        tpGesamt = new JXTaskPane();
        btnAktiv = new JToggleButton();
        btnInaktiv = new JToggleButton();
        btnGesamt = new JToggleButton();
        tpZeit = new JXTaskPane();
        jdcEinbuchen = new JDateChooser();
        tpSpezial = new JXTaskPane();
        cmbWarengruppe = new JComboBox();
        cmbLager = new JComboBox();
        cmbLagerart = new JComboBox();
        cmbLieferant = new JComboBox();
        txtSuchen = new JXSearchField();
        btnSum = new JToggleButton();
        btnNoFilter = new JButton();
        pnlWarenbestand = new JPanel();
        lblWarenbestand = new JLabel();
        pnlVorrat = new JScrollPane();
        tblVorrat = new JTable();

        //======== this ========
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setVisible(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        Container contentPane = getContentPane();

        //======== pnlMain ========
        {
            pnlMain.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
            pnlMain.setLayout(new FormLayout(
                    "default, $lcgap, default:grow",
                    "fill:default:grow"));

            //======== jspSearch ========
            {

                //======== tpcSearch ========
                {
                    tpcSearch.add(separator1);

                    //======== tpGesamt ========
                    {
                        tpGesamt.setTitle("Vorratauswahl");
                        tpGesamt.setLayout(new VerticalLayout(10));

                        //---- btnAktiv ----
                        btnAktiv.setText("Aktive");
                        btnAktiv.setSelected(true);
                        btnAktiv.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnAuswahlItemStateChanged(e);
                            }
                        });
                        tpGesamt.add(btnAktiv);

                        //---- btnInaktiv ----
                        btnInaktiv.setText("Ausgebuchte ");
                        btnInaktiv.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnAuswahlItemStateChanged(e);
                            }
                        });
                        tpGesamt.add(btnInaktiv);

                        //---- btnGesamt ----
                        btnGesamt.setText("Alle");
                        btnGesamt.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnAuswahlItemStateChanged(e);
                            }
                        });
                        tpGesamt.add(btnGesamt);
                    }
                    tpcSearch.add(tpGesamt);

                    //======== tpZeit ========
                    {
                        tpZeit.setTitle("Einbuchung");
                        tpZeit.setLayout(new VerticalLayout(10));

                        //---- jdcEinbuchen ----
                        jdcEinbuchen.setToolTipText("nach Einbuchungsdatum");
                        jdcEinbuchen.addPropertyChangeListener("date", new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent e) {
                                jdcEinbuchenPropertyChange(e);
                            }
                        });
                        tpZeit.add(jdcEinbuchen);
                    }
                    tpcSearch.add(tpZeit);

                    //======== tpSpezial ========
                    {
                        tpSpezial.setTitle("andere Kriterien");
                        tpSpezial.setLayout(new VerticalLayout(10));

                        //---- cmbWarengruppe ----
                        cmbWarengruppe.setModel(new DefaultComboBoxModel(new String[]{
                                "nach Warengruppe",
                                "Gefl\u00fcgel"
                        }));
                        cmbWarengruppe.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbWarengruppeItemStateChanged(e);
                            }
                        });
                        tpSpezial.add(cmbWarengruppe);

                        //---- cmbLager ----
                        cmbLager.setModel(new DefaultComboBoxModel(new String[]{
                                "<html><i>nach Lager</i></html>",
                                "Lager 1"
                        }));
                        cmbLager.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbLagerItemStateChanged(e);
                            }
                        });
                        tpSpezial.add(cmbLager);

                        //---- cmbLagerart ----
                        cmbLagerart.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbLagerartItemStateChanged(e);
                            }
                        });
                        tpSpezial.add(cmbLagerart);

                        //---- cmbLieferant ----
                        cmbLieferant.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbLieferantItemStateChanged(e);
                            }
                        });
                        tpSpezial.add(cmbLieferant);

                        //---- txtSuchen ----
                        txtSuchen.setToolTipText("Suchen1");
                        txtSuchen.setPrompt("nach Text, GTIN, Bestand");
                        txtSuchen.setFocusBehavior(PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
                        txtSuchen.setSearchMode(JXSearchField.SearchMode.REGULAR);
                        txtSuchen.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                txtSuchenActionPerformed(e);
                            }
                        });
                        txtSuchen.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtSuchenCaretUpdate(e);
                            }
                        });
                        tpSpezial.add(txtSuchen);

                        //---- btnSum ----
                        btnSum.setText("Restsummen anzeigen");
                        btnSum.setSelected(true);
                        btnSum.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnSumItemStateChanged(e);
                            }
                        });
                        tpSpezial.add(btnSum);

                        //---- btnNoFilter ----
                        btnNoFilter.setText("ohne Einschr\u00e4nkung");
                        btnNoFilter.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnNoFilterActionPerformed(e);
                            }
                        });
                        tpSpezial.add(btnNoFilter);
                    }
                    tpcSearch.add(tpSpezial);
                }
                jspSearch.setViewportView(tpcSearch);
            }
            pnlMain.add(jspSearch, CC.xy(1, 1));

            //======== pnlWarenbestand ========
            {
                pnlWarenbestand.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        pnlWarenbestandComponentResized(e);
                    }
                });
                pnlWarenbestand.setLayout(new FormLayout(
                        "left:default:grow",
                        "fill:default, fill:default:grow"));

                //---- lblWarenbestand ----
                lblWarenbestand.setBackground(Color.blue);
                lblWarenbestand.setFont(new Font("Lucida Grande", Font.BOLD, 18));
                lblWarenbestand.setForeground(Color.white);
                lblWarenbestand.setHorizontalAlignment(SwingConstants.CENTER);
                lblWarenbestand.setText("Warenbestand");
                lblWarenbestand.setOpaque(true);
                pnlWarenbestand.add(lblWarenbestand, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

                //======== pnlVorrat ========
                {
                    pnlVorrat.setPreferredSize(new Dimension(454, 500));
                    pnlVorrat.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            pnlVorratComponentResized(e);
                        }
                    });

                    //---- tblVorrat ----
                    tblVorrat.setModel(new DefaultTableModel(
                            new Object[][]{
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                            },
                            new String[]{
                                    "Title 1", "Title 2", "Title 3", "Title 4"
                            }
                    ));
                    tblVorrat.setAutoCreateRowSorter(true);
                    tblVorrat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    tblVorrat.setRowHeight(20);
                    tblVorrat.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tblVorratMousePressed(e);
                        }
                    });
                    pnlVorrat.setViewportView(tblVorrat);
                }
                pnlWarenbestand.add(pnlVorrat, CC.xy(1, 2, CC.FILL, CC.FILL));
            }
            pnlMain.add(pnlWarenbestand, CC.xy(3, 1));
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, 1313, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
        );

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(btnAktiv);
        buttonGroup1.add(btnInaktiv);
        buttonGroup1.add(btnGesamt);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void prepareSearchArea() {
        loadWarengruppe();
        loadLager();
        loadLagerart();
        loadLieferanten();
        loadTimeSearches();


//        pnlMisc.add(new AbstractAction() {
//            {
//                putValue(Action.NAME, "Liste drucken");
//                putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/24x24/printer.png")));
//                putValue(Action.SHORT_DESCRIPTION, "Druckt eine Gesamtliste der markierten VorrÃ¤te");
//            }
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Main.printers.print(thisComponent, TableModelHTMLConverter.convert(tblVorrat), false);
//            }
//        });

    }

    private void loadTimeSearches() {
        jdcEinbuchen.setDate(new Date());
        final SimpleDateFormat dfShort = new SimpleDateFormat("EEEE");
        final DateFormat dfLong = DateFormat.getDateInstance(DateFormat.SHORT);

        for (int days = 0; days < 7; days++) {

            final int d = days;
            //pnlMyVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/")));

            final Date day = Tools.addDate(new Date(), -days);

            tpZeit.add(new AbstractAction() {
                {
                    String dayname = "";
                    if (d == 0) {
                        dayname = "Heute";
                    } else if (d == 1) {
                        dayname = "Gestern";
                    } else {
                        dayname = dfShort.format(day);
                    }

                    putValue(Action.NAME, dayname);
                    putValue(Action.SHORT_DESCRIPTION, dfLong.format(day));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    jdcEinbuchen.setDate(day);
                }
            });


        }
    }

    private void myInit() {

        setTitle(tools.Tools.getWindowTitle("Vorräte"));

        pageprinter = Main.printers.getPrinters().get("pageprinter");
        etiprinter1 = Main.printers.getPrinters().get(Main.getProps().getProperty("etitype1"));
        etiprinter2 = Main.printers.getPrinters().get(Main.getProps().getProperty("etitype2"));

        form1 = etiprinter1.getForms().get(Main.getProps().getProperty("etiform1"));
        form2 = etiprinter2.getForms().get(Main.getProps().getProperty("etiform2"));

//        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;
//
//        vmode = MODE_VORRAT_BROWSE;
//        bmode = MODE_BUCHUNG_INVISIBLE;
//
//        vlsl = new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!e.getValueIsAdjusting()) {
//                    // wenn nur eine Zeile gewählt wurde,
//                    // dann kann die rechte Seite mit den Einzelbuchungen angezeigt werden.
//
//                    int rowcount = tblVorrat.getSelectedRowCount();
//
//                    if (btnEditVorrat.isSelected()) {
//                        btnCancelVEdit.doClick();
//                    }
//
//                    // btnDeleteVorrat.setEnabled(rowcount > 0);
//                    //btnAusbuchen.setEnabled(rowcount > 0);
//                    btnPrintEti1.setEnabled(rowcount > 0);
//                    btnPrintEti2.setEnabled(rowcount > 0);
//                    btnPageprinter.setEnabled(rowcount > 0);
//                    btnEditVorrat.setEnabled(rowcount > 0);
//
////                    if (rowcount <= 1 && btnShowRightSide.isSelected()) {
////                        btnShowRightSide.setSelected(false);
////                    }
//
//                    //btnShowRightSide.setEnabled(false);
//                    //btnShowRightSide.setEnabled(rowcount == 1);
//
//                }
//            }
//        };

        prepareSearchArea();
        loadVorratTable();
        initBuchungenTable();

        loadCMB4Edit();


        //btnPrintLabels.setEnabled(Main.props.containsKey("labelPrinter") && Main.props.containsKey("receiptPrinter"));

        //tblBuchungen.setRowHeight(32);


    }

    private void loadCMB4Edit() {
//        EntityManager em = Main.getEMF().createEntityManager();
//        Query query = em.createNamedQuery("Lager.findAllSorted");
//        try {
//            java.util.List lager = query.getResultList();
//            lager.add(0, "<html><i>ändern Lager</i></html>");
//            cmbVEditLager.setModel(tools.Tools.newComboboxModel(lager));
//        } catch (Exception e) { // nicht gefunden
//            //
//        } finally {
//            em.close();
//        }
//        em = Main.getEMF().createEntityManager();
//        query = em.createNamedQuery("Lieferanten.findAllSorted");
//        try {
//            java.util.List lieferant = query.getResultList();
//            lieferant.add(0, "<html><i>ändern Lieferant</i></html>");
//            cmbVEditLieferant.setModel(tools.Tools.newComboboxModel(lieferant));
//        } catch (Exception e) { // nicht gefunden
//            //
//        } finally {
//            em.close();
//        }
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlMain;
    private JScrollPane jspSearch;
    private JXTaskPaneContainer tpcSearch;
    private JComponent separator1;
    private JXTaskPane tpGesamt;
    private JToggleButton btnAktiv;
    private JToggleButton btnInaktiv;
    private JToggleButton btnGesamt;
    private JXTaskPane tpZeit;
    private JDateChooser jdcEinbuchen;
    private JXTaskPane tpSpezial;
    private JComboBox cmbWarengruppe;
    private JComboBox cmbLager;
    private JComboBox cmbLagerart;
    private JComboBox cmbLieferant;
    private JXSearchField txtSuchen;
    private JToggleButton btnSum;
    private JButton btnNoFilter;
    private JPanel pnlWarenbestand;
    private JLabel lblWarenbestand;
    private JScrollPane pnlVorrat;
    private JTable tblVorrat;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
