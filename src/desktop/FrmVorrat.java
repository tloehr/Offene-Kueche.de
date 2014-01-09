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
import org.jdesktop.swingx.*;
import org.pushingpixels.trident.Timeline;
import printer.Form;
import printer.Printer;
import printer.TableModelHTMLConverter;
import tablemodels.BuchungenTableModel;
import tablemodels.VorratTableModel;
import threads.PrintProcessor;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * @author Torsten Löhr
 */
public class FrmVorrat extends javax.swing.JInternalFrame {

    private Object[] spaltenVorrat = new Object[]{"Vorrat Nr.", "Bezeichnung", "Lagerort", "Lieferant", "GTIN", "Eingangsmenge", "Restmenge", "Stoffart", "Warengruppe", "Eingang", "Anbruch", "Ausgang"};
    private Object[] spaltenBuchungen = new Object[]{"Datum", "Text", "Menge", "MitarbeiterIn"};
    private TableModelListener tml;
    private double splitMainDL, splitLeftButtonsMainDL, splitLBMLeftDL, splitVorratDL;
    private int vmode, bmode, laufendeVOperation;
    private Timeline textmessageTL;

    private Printer pageprinter, etiprinter1, etiprinter2;
    private Form form1, form2;

    private JComponent thisComponent;

    private boolean initphase;
    //private final Color searchColor = new Color(255, 0, 51);

    private ListSelectionListener vlsl, blsl;

    private Pair<Integer, Object> suche = null;

    private final int MODE_VORRAT_BROWSE = 0;
    private final int MODE_VORRAT_EDIT = 1;
    private final int MODE_VORRAT_OTHER_OPERATION = 2;

    private final int LAUFENDE_OPERATION_NICHTS = 0;
    private final int LAUFENDE_OPERATION_LOESCHEN = 1;
    private final int LAUFENDE_OPERATION_AUSBUCHEN = 2;
    private final int LAUFENDE_OPERATION_REAKTIVIERUNG = 3;
    private final int LAUFENDE_OPERATION_KORREKTUR = 4;

    private final int MODE_BUCHUNG_INVISIBLE = 0;
    private final int MODE_BUCHUNG_BROWSING = 1;

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

        // Alle evtl. laufenden Edits werden sofort gecancelt.
        if (vmode == MODE_VORRAT_EDIT) {
            btnEditVorrat.setSelected(false);
        }

        if (laufendeVOperation != LAUFENDE_OPERATION_NICHTS) {
            btnCancel1.doClick();
        }

        //splitMainDividerLocation = Tools.showSide(splitMain, Tools.LEFT_UPPER_SIDE, 400);
        Query query = null;

        Tools.resetBackground(pnlSuche);

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

                if (!initphase) {
                    tblVorrat.getSelectionModel().removeListSelectionListener(vlsl);
                }

                tblVorrat.setModel(new VorratTableModel(list, spaltenVorrat));
                tblVorrat.getSelectionModel().addListSelectionListener(vlsl);

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
                btnPrintEti1.setEnabled(false);
                btnPrintEti2.setEnabled(false);
                btnPageprinter.setEnabled(false);
                btnEditVorrat.setEnabled(false);

                btnShowRightSide.setEnabled(false);


            }
        } catch (Exception e) { // nicht gefunden
            Main.logger.fatal(e.getMessage(), e);
            //e.printStackTrace();
        }
    }

    private void initBuchungenTable() {
        if (btnShowRightSide.isSelected() && tblVorrat.getSelectedRow() >= 0) {

//            // erst alte Eingabe rückgängig machen
//            if (btnCancelBuchung.isEnabled()) {
//                btnCancelBuchung.doClick();
//            }

            final Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
            tblBuchungen.setModel(new BuchungenTableModel((List) vorrat.getBuchungenCollection(), spaltenBuchungen));
            //tblBuchungen.getColumnModel().getColumn(BuchungenTableModel.COL_DATUM).getCellEditor().getTableCellEditorComponent(tblVorrat, ui, isSelected, WIDTH, WIDTH);

            tblBuchungen.setRowSorter(null);
            // Damit die Anzeige auf der anderen Seite aktualisiert wird.
            if (tml != null) {
                tblBuchungen.getModel().removeTableModelListener(tml);
            }
            tml = new TableModelListener() {

                public void tableChanged(TableModelEvent e) {
                    // Summe in der Vorrat Tabelle korrigieren.
                    EntityManager em = Main.getEMF().createEntityManager();
                    javax.persistence.Query query = em.createNamedQuery("Vorrat.Buchungen.summeBestand");
                    query.setParameter("vorrat", vorrat);
                    ((Object[]) ((VorratTableModel) tblVorrat.getModel()).getData().get(tblVorrat.getSelectedRow()))[1] = (BigDecimal) query.getSingleResult();
                    ((VorratTableModel) tblVorrat.getModel()).fireTableCellUpdated(tblVorrat.getSelectedRow(), VorratTableModel.COL_RESTMENGE);
                    em.close();
                }
            };

            tblBuchungen.getModel().addTableModelListener(tml);
            //pnlBuchungenComponentResized(null);

        } else {
            tblBuchungen.setModel(new DefaultTableModel());
        }


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

    private void pnlBuchungenComponentResized(ComponentEvent e) {
        if (tblBuchungen.getRowCount() > 0) {
            Dimension dim = pnlBuchungen.getSize();
            int[] width = new int[]{86, 0, 70, 80};
            int textWidth = dim.width - Tools.sum(width) - 10;
            if (textWidth > 0) {
                width[1] = textWidth;
                Tools.setTableColumnPreferredWidth(tblBuchungen, width);
            }
        }
    }

    private void tblBuchungenPropertyChange(PropertyChangeEvent e) {
//        if (e.getPropertyName().equalsIgnoreCase("model")) {
//            btnAddBuchung.setEnabled(((TableModel) e.getNewValue()).getRowCount() > 0);
//        }
    }

    private void btnAddBuchungActionPerformed(ActionEvent e) {
        if (initphase) return;
        ((BuchungenTableModel) tblBuchungen.getModel()).addEmptyRow();
        btnAddBuchung.setEnabled(false);
        btnSaveBuchung.setEnabled(true);
        btnCancelBuchung.setEnabled(true);
        tblBuchungen.editCellAt(tblBuchungen.getRowCount() - 1, 0);
        Component editor = tblBuchungen.getEditorComponent();
        ((JTextComponent) editor).selectAll();
    }

    private void btnPrintEti1ActionPerformed(ActionEvent e) {
        print(etiprinter1, form1, Main.getProps().getProperty("etiprinter1"));

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

    private void btnSaveBuchungActionPerformed(ActionEvent e) {
        if (initphase) return;
        ((BuchungenTableModel) tblBuchungen.getModel()).saveRow(tblBuchungen.getRowCount() - 1);
        btnAddBuchung.setEnabled(true);
        btnSaveBuchung.setEnabled(false);
        btnCancelBuchung.setEnabled(false);
    }

    private void btnCancelBuchungActionPerformed(ActionEvent e) {
        if (initphase) return;
        ((BuchungenTableModel) tblBuchungen.getModel()).cancelNewRow();
        btnAddBuchung.setEnabled(true);
        btnSaveBuchung.setEnabled(false);
        btnCancelBuchung.setEnabled(false);
    }

    private void btnGesamtBestandActionPerformed(ActionEvent e) {
        pnlZeit.setCollapsed(true);
        pnlSpezial.setCollapsed(true);

        splitMain.setDividerLocation(0);
        //Tools.showSide(splitMain, splitMainDividerLocation, 3000);
    }

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
        pnlSpezial.setCollapsed(true);
        initphase = false;
        suche = new Pair<Integer, Object>(Const.DATUM, jdcEinbuchen.getDate());
        loadVorratTable();
    }

    private void cmbWarengruppeItemStateChanged(ItemEvent e) {
        if (initphase) return;
        pnlZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.WARENGRUPPE, cmbWarengruppe.getSelectedItem());
            loadVorratTable();
        }
    }

    private void cmbLagerItemStateChanged(ItemEvent e) {
        if (initphase) return;
        pnlZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LAGER, cmbLager.getSelectedItem());
            loadVorratTable();
        }
    }

    private void btnShowRightSideItemStateChanged(ItemEvent e) {
        btnShowRightSide.setToolTipText(btnShowRightSide.isSelected() ? "Buchungen ausblenden" : "Buchungen einblenden");
        splitMainDL = btnShowRightSide.isSelected() ? 0.65d : 1.0d;
        Tools.showSide(splitMain, splitMainDL, 400);
        initBuchungenTable();
    }

    private void cmbLieferantItemStateChanged(ItemEvent e) {
        if (initphase) return;
        pnlZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LIEFERANT, cmbLieferant.getSelectedItem());
            loadVorratTable();
        }
    }

    private void splitLeftButtonsMainComponentResized(ComponentEvent e) {
        splitLeftButtonsMain.setDividerLocation(splitLeftButtonsMainDL);
    }

    private void splitLBMLeftComponentResized(ComponentEvent e) {
        splitLBMLeft.setDividerLocation(splitLBMLeftDL);
    }

    private void splitMainComponentResized(ComponentEvent e) {
        splitMain.setDividerLocation(splitMainDL);
    }

    private void splitVorratComponentResized(ComponentEvent e) {
        splitVorrat.setDividerLocation(splitVorratDL);
    }

    private void btnEditVorratItemStateChanged(ItemEvent e) {
        if (btnEditVorrat.isSelected()) {
            vmode = MODE_VORRAT_EDIT;
            splitVorratDL = Tools.showSide(splitVorrat, 0.75d, 400);
            splitLBMLeftDL = Tools.showSide(splitLBMLeft, Tools.RIGHT_LOWER_SIDE, 400);

            if (tblVorrat.getSelectedRowCount() == 1) {
                Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
                cmbVEditLager.setSelectedItem(vorrat.getLager());
                cmbVEditLieferant.setSelectedItem(vorrat.getLieferant());
            } else {
                cmbVEditLager.setSelectedIndex(0);
                cmbVEditLieferant.setSelectedIndex(0);
            }

        } else {
            vmode = MODE_VORRAT_BROWSE;
            splitVorratDL = Tools.showSide(splitVorrat, Tools.LEFT_UPPER_SIDE, 400);
            splitLBMLeftDL = Tools.showSide(splitLBMLeft, Tools.LEFT_UPPER_SIDE, 400);
        }
    }

    private void btnSaveVEditActionPerformed(ActionEvent e) {
        int[] rows = tblVorrat.getSelectedRows();

        for (int r = 0; r < rows.length; r++) {
            int row = tblVorrat.convertRowIndexToModel(rows[r]);
            Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(row);
            if (cmbVEditLager.getSelectedIndex() > 0) {
                vorrat.setLager((Lager) cmbVEditLager.getSelectedItem());
            }
            if (cmbVEditLieferant.getSelectedIndex() > 0) {
                vorrat.setLieferant((Lieferanten) cmbVEditLieferant.getSelectedItem());
            }
            EntityTools.merge(vorrat);
        }

        btnEditVorrat.setSelected(false);
        loadVorratTable();
    }

    private void btnCancelVEditActionPerformed(ActionEvent e) {
        btnEditVorrat.setSelected(false);
    }

    private void pnlWarenbestandComponentResized(ComponentEvent e) {

    }

    private void btnDeleteVorratActionPerformed(ActionEvent e) {
        laufendeVOperation = LAUFENDE_OPERATION_LOESCHEN;
        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
        showVEditMessage("markierte Vorräte wirklich löschen ?");
    }

    private void btnCancel1ActionPerformed(ActionEvent e) {
        textmessageTL.cancel();
        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;
        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.LEFT_UPPER_SIDE, 400);
    }

    private void btnApply1ActionPerformed(ActionEvent e) {
        int[] rows = tblVorrat.getSelectedRows();

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            for (int r = 0; r < rows.length; r++) {
                // Diese Zeile ist sehr wichtig, da sie die Auswahl in der Tabelle bzgl. einer Umsortierung berücksichtigt.
                int row = tblVorrat.convertRowIndexToModel(rows[r]);

                Vorrat vorrat = ((VorratTableModel) tblVorrat.getModel()).getVorrat(row);
                if (laufendeVOperation == LAUFENDE_OPERATION_LOESCHEN) {
                    Main.logger.info("DELETE VORRAT: " + vorrat.toString());
                    EntityTools.delete(vorrat);
                } else if (laufendeVOperation == LAUFENDE_OPERATION_AUSBUCHEN) {
                    Main.logger.info("AUSBUCHEN VORRAT: " + vorrat.toString());
                    VorratTools.ausbuchen(vorrat, "Abschlussbuchung");
                } else if (laufendeVOperation == LAUFENDE_OPERATION_REAKTIVIERUNG) {
                    Main.logger.info("ZURÜCK BUCHEN VORRAT: " + vorrat.toString());
                    VorratTools.reaktivieren(em, vorrat);
                } else if (laufendeVOperation == LAUFENDE_OPERATION_KORREKTUR) {
                    Main.logger.info("ANFANGSBESTÄNDE KORRIGIEREN: " + vorrat.toString());
                    VorratTools.korregiereAnfangsbestand(em, vorrat);
                } else {

                }
            }
            em.getTransaction().commit();
        } catch (Exception e1) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;
        textmessageTL.cancel();
        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.LEFT_UPPER_SIDE, 400);

        loadVorratTable();
    }

    private void btnAusbuchenActionPerformed(ActionEvent e) {
        laufendeVOperation = LAUFENDE_OPERATION_AUSBUCHEN;
        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
        showVEditMessage("markierte Vorräte wirklich ausbuchen ?");
    }

    private void btnPrintEti2ActionPerformed(ActionEvent e) {
        print(etiprinter2, form2, Main.getProps().getProperty("etiprinter2"));
    }

    private void btnPageprinterActionPerformed(ActionEvent e) {
        print(pageprinter, null, Main.getProps().getProperty("pageprinter"));
    }

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
        pnlZeit.setCollapsed(true);
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

    private void btnReopenActionPerformed(ActionEvent e) {
        if (laufendeVOperation != LAUFENDE_OPERATION_NICHTS || !btnInaktiv.isSelected()) return;
        laufendeVOperation = LAUFENDE_OPERATION_REAKTIVIERUNG;
        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
        showVEditMessage("markierte Vorräte wirklich zurückbuchen ?");
    }

    private void btnAnfangKorrigierenActionPerformed(ActionEvent e) {
        if (laufendeVOperation != LAUFENDE_OPERATION_NICHTS) return;
        laufendeVOperation = LAUFENDE_OPERATION_KORREKTUR;
        splitLeftButtonsMainDL = Tools.showSide(splitLeftButtonsMain, Tools.RIGHT_LOWER_SIDE, 400);
        showVEditMessage("markierte Vorräte wirklich korrigieren ?");
    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {
        if (initphase) return;
        pnlZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LAGERART, (short) cmbLagerart.getSelectedIndex());
            loadVorratTable();
        }
    }

    private void btnSumItemStateChanged(ItemEvent e) {
        btnSum.setText(btnSum.isSelected() ? "Restsummen anzeigen" : "Summe bei Einbuchung");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        pnlMain = new JPanel();
        pnlSuche = new JPanel();
        jspSuche = new JScrollPane();
        tskSuche = new JXTaskPaneContainer();
        separator1 = compFactory.createSeparator("Suchen", SwingConstants.CENTER);
        pnlGesamt = new JXTaskPane();
        btnAktiv = new JToggleButton();
        btnInaktiv = new JToggleButton();
        btnGesamt = new JToggleButton();
        pnlZeit = new JXTaskPane();
        jdcEinbuchen = new JDateChooser();
        pnlSpezial = new JXTaskPane();
        cmbWarengruppe = new JComboBox();
        cmbLager = new JComboBox();
        cmbLagerart = new JComboBox();
        cmbLieferant = new JComboBox();
        txtSuchen = new JXSearchField();
        btnSum = new JToggleButton();
        btnNoFilter = new JButton();
        separator2 = compFactory.createSeparator("Befehle", SwingConstants.CENTER);
        pnlMisc = new JXTaskPane();
        splitMain = new JSplitPane();
        pnlWarenbestand = new JPanel();
        lblWarenbestand = new JLabel();
        splitVorrat = new JSplitPane();
        pnlVorrat = new JScrollPane();
        tblVorrat = new JTable();
        panel1 = new JPanel();
        cmbVEditLager = new JComboBox();
        cmbVEditLieferant = new JComboBox();
        btnDeleteVorrat = new JButton();
        btnAusbuchen = new JButton();
        btnReopen = new JButton();
        btnAnfangKorrigieren = new JButton();
        splitLeftButtonsMain = new JSplitPane();
        splitLBMLeft = new JSplitPane();
        pnlLBVorgangOps = new JPanel();
        btnEditVorrat = new JToggleButton();
        btnPrintEti1 = new JButton();
        btnPrintEti2 = new JButton();
        btnPageprinter = new JButton();
        hSpacer1 = new JPanel(null);
        btnShowRightSide = new JToggleButton();
        pnlLBMLower = new JPanel();
        btnSaveVEdit = new JButton();
        btnCancelVEdit = new JButton();
        pnlLBMYesNo = new JPanel();
        btnApply1 = new JXButton();
        hSpacer2 = new JPanel(null);
        lblApply1 = new JLabel();
        hSpacer3 = new JPanel(null);
        btnCancel1 = new JButton();
        pnlEinzelbuchung = new JPanel();
        jLabel2 = new JLabel();
        pnlBuchungen = new JScrollPane();
        tblBuchungen = new JTable();
        pnlRightButtons = new JPanel();
        btnAddBuchung = new JButton();
        btnSaveBuchung = new JButton();
        btnCancelBuchung = new JButton();

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

            //======== pnlSuche ========
            {

                //======== jspSuche ========
                {

                    //======== tskSuche ========
                    {
                        tskSuche.setPaintBorderInsets(false);
                        tskSuche.setOpaque(false);
                        tskSuche.add(separator1);

                        //======== pnlGesamt ========
                        {
                            pnlGesamt.setTitle("Vorratsauswahl");
                            Container pnlGesamtContentPane = pnlGesamt.getContentPane();
                            pnlGesamtContentPane.setLayout(new VerticalLayout(10));

                            //---- btnAktiv ----
                            btnAktiv.setText("Aktive");
                            btnAktiv.setSelected(true);
                            btnAktiv.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnAuswahlItemStateChanged(e);
                                }
                            });
                            pnlGesamtContentPane.add(btnAktiv);

                            //---- btnInaktiv ----
                            btnInaktiv.setText("Ausgebuchte ");
                            btnInaktiv.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnAuswahlItemStateChanged(e);
                                }
                            });
                            pnlGesamtContentPane.add(btnInaktiv);

                            //---- btnGesamt ----
                            btnGesamt.setText("Alle");
                            btnGesamt.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnAuswahlItemStateChanged(e);
                                }
                            });
                            pnlGesamtContentPane.add(btnGesamt);
                        }
                        tskSuche.add(pnlGesamt);

                        //======== pnlZeit ========
                        {
                            pnlZeit.setTitle("nach Einbuchungszeit");
                            pnlZeit.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent e) {
                                    pnlZeitPropertyChange(e);
                                }
                            });
                            Container pnlZeitContentPane = pnlZeit.getContentPane();
                            pnlZeitContentPane.setLayout(new VerticalLayout(10));

                            //---- jdcEinbuchen ----
                            jdcEinbuchen.setToolTipText("nach Einbuchungsdatum");
                            jdcEinbuchen.addPropertyChangeListener("date", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent e) {
                                    jdcEinbuchenPropertyChange(e);
                                }
                            });
                            pnlZeitContentPane.add(jdcEinbuchen);
                        }
                        tskSuche.add(pnlZeit);

                        //======== pnlSpezial ========
                        {
                            pnlSpezial.setTitle("nach anderen Kriterien");
                            pnlSpezial.setCollapsed(true);
                            Container pnlSpezialContentPane = pnlSpezial.getContentPane();
                            pnlSpezialContentPane.setLayout(new VerticalLayout(10));

                            //---- cmbWarengruppe ----
                            cmbWarengruppe.setModel(new DefaultComboBoxModel(new String[] {
                                "nach Warengruppe",
                                "Gefl\u00fcgel"
                            }));
                            cmbWarengruppe.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    cmbWarengruppeItemStateChanged(e);
                                }
                            });
                            pnlSpezialContentPane.add(cmbWarengruppe);

                            //---- cmbLager ----
                            cmbLager.setModel(new DefaultComboBoxModel(new String[] {
                                "<html><i>nach Lager</i></html>",
                                "Lager 1"
                            }));
                            cmbLager.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    cmbLagerItemStateChanged(e);
                                }
                            });
                            pnlSpezialContentPane.add(cmbLager);

                            //---- cmbLagerart ----
                            cmbLagerart.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    cmbLagerartItemStateChanged(e);
                                }
                            });
                            pnlSpezialContentPane.add(cmbLagerart);

                            //---- cmbLieferant ----
                            cmbLieferant.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    cmbLieferantItemStateChanged(e);
                                }
                            });
                            pnlSpezialContentPane.add(cmbLieferant);

                            //---- txtSuchen ----
                            txtSuchen.setToolTipText("Suchen1");
                            txtSuchen.setPrompt("nach Text, GTIN, Bestand");
                            txtSuchen.setFocusBehavior(org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
                            txtSuchen.setSearchMode(org.jdesktop.swingx.JXSearchField.SearchMode.REGULAR);
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
                            pnlSpezialContentPane.add(txtSuchen);

                            //---- btnSum ----
                            btnSum.setText("Restsummen anzeigen");
                            btnSum.setSelected(true);
                            btnSum.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnSumItemStateChanged(e);
                                }
                            });
                            pnlSpezialContentPane.add(btnSum);

                            //---- btnNoFilter ----
                            btnNoFilter.setText("ohne Einschr\u00e4nkung");
                            btnNoFilter.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnNoFilterActionPerformed(e);
                                }
                            });
                            pnlSpezialContentPane.add(btnNoFilter);
                        }
                        tskSuche.add(pnlSpezial);
                        tskSuche.add(separator2);

                        //======== pnlMisc ========
                        {
                            pnlMisc.setSpecial(true);
                            pnlMisc.setTitle("Verschiedenes");
                            Container pnlMiscContentPane = pnlMisc.getContentPane();
                            pnlMiscContentPane.setLayout(new BoxLayout(pnlMiscContentPane, BoxLayout.X_AXIS));
                        }
                        tskSuche.add(pnlMisc);
                    }
                    jspSuche.setViewportView(tskSuche);
                }

                GroupLayout pnlSucheLayout = new GroupLayout(pnlSuche);
                pnlSuche.setLayout(pnlSucheLayout);
                pnlSucheLayout.setHorizontalGroup(
                    pnlSucheLayout.createParallelGroup()
                        .addComponent(jspSuche, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                );
                pnlSucheLayout.setVerticalGroup(
                    pnlSucheLayout.createParallelGroup()
                        .addComponent(jspSuche, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                );
            }

            //======== splitMain ========
            {
                splitMain.setDividerLocation(700);
                splitMain.setEnabled(false);
                splitMain.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        splitMainComponentResized(e);
                    }
                });

                //======== pnlWarenbestand ========
                {
                    pnlWarenbestand.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            pnlWarenbestandComponentResized(e);
                        }
                    });

                    //---- lblWarenbestand ----
                    lblWarenbestand.setBackground(Color.blue);
                    lblWarenbestand.setFont(new Font("Lucida Grande", Font.BOLD, 18));
                    lblWarenbestand.setForeground(Color.white);
                    lblWarenbestand.setHorizontalAlignment(SwingConstants.CENTER);
                    lblWarenbestand.setText("Warenbestand");
                    lblWarenbestand.setOpaque(true);

                    //======== splitVorrat ========
                    {
                        splitVorrat.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        splitVorrat.setEnabled(false);
                        splitVorrat.setDividerSize(1);
                        splitVorrat.setDividerLocation(250);
                        splitVorrat.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                splitVorratComponentResized(e);
                            }
                        });

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
                                new Object[][] {
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                                },
                                new String[] {
                                    "Title 1", "Title 2", "Title 3", "Title 4"
                                }
                            ));
                            tblVorrat.setAutoCreateRowSorter(true);
                            tblVorrat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            tblVorrat.setRowHeight(20);
                            pnlVorrat.setViewportView(tblVorrat);
                        }
                        splitVorrat.setTopComponent(pnlVorrat);

                        //======== panel1 ========
                        {
                            panel1.setPreferredSize(new Dimension(70, 80));
                            panel1.setLayout(new FormLayout(
                                "$rgap, 2*(default:grow, $lcgap), $rgap",
                                "2*($rgap, fill:20dlu), $lgap, 20dlu"));
                            ((FormLayout)panel1.getLayout()).setColumnGroups(new int[][] {{2, 4}});

                            //---- cmbVEditLager ----
                            cmbVEditLager.setFont(new Font("sansserif", Font.PLAIN, 18));
                            panel1.add(cmbVEditLager, CC.xy(2, 2));

                            //---- cmbVEditLieferant ----
                            cmbVEditLieferant.setFont(new Font("sansserif", Font.PLAIN, 18));
                            panel1.add(cmbVEditLieferant, CC.xy(4, 2));

                            //---- btnDeleteVorrat ----
                            btnDeleteVorrat.setIcon(null);
                            btnDeleteVorrat.setEnabled(false);
                            btnDeleteVorrat.setText("l\u00f6schen");
                            btnDeleteVorrat.setFont(new Font("sansserif", Font.PLAIN, 18));
                            btnDeleteVorrat.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnDeleteVorratActionPerformed(e);
                                }
                            });
                            panel1.add(btnDeleteVorrat, CC.xy(2, 4));

                            //---- btnAusbuchen ----
                            btnAusbuchen.setIcon(null);
                            btnAusbuchen.setText("ausbuchen");
                            btnAusbuchen.setFont(new Font("sansserif", Font.PLAIN, 18));
                            btnAusbuchen.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnAusbuchenActionPerformed(e);
                                }
                            });
                            panel1.add(btnAusbuchen, CC.xy(4, 4));

                            //---- btnReopen ----
                            btnReopen.setText("wieder \u00f6ffnen");
                            btnReopen.setFont(new Font("sansserif", Font.PLAIN, 18));
                            btnReopen.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnReopenActionPerformed(e);
                                }
                            });
                            panel1.add(btnReopen, CC.xy(2, 6));

                            //---- btnAnfangKorrigieren ----
                            btnAnfangKorrigieren.setText("Anfangsbest\u00e4nde korrigieren");
                            btnAnfangKorrigieren.setFont(new Font("sansserif", Font.PLAIN, 18));
                            btnAnfangKorrigieren.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnAnfangKorrigierenActionPerformed(e);
                                }
                            });
                            panel1.add(btnAnfangKorrigieren, CC.xy(4, 6));
                        }
                        splitVorrat.setBottomComponent(panel1);
                    }

                    //======== splitLeftButtonsMain ========
                    {
                        splitLeftButtonsMain.setPreferredSize(new Dimension(491, 45));
                        splitLeftButtonsMain.setEnabled(false);
                        splitLeftButtonsMain.setDividerSize(1);
                        splitLeftButtonsMain.setDividerLocation(400);
                        splitLeftButtonsMain.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                splitLeftButtonsMainComponentResized(e);
                            }
                        });

                        //======== splitLBMLeft ========
                        {
                            splitLBMLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
                            splitLBMLeft.setDividerSize(1);
                            splitLBMLeft.setEnabled(false);
                            splitLBMLeft.setDividerLocation(40);
                            splitLBMLeft.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    splitLBMLeftComponentResized(e);
                                }
                            });

                            //======== pnlLBVorgangOps ========
                            {
                                pnlLBVorgangOps.setMinimumSize(new Dimension(0, 0));
                                pnlLBVorgangOps.setLayout(new BoxLayout(pnlLBVorgangOps, BoxLayout.X_AXIS));

                                //---- btnEditVorrat ----
                                btnEditVorrat.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit.png")));
                                btnEditVorrat.setEnabled(false);
                                btnEditVorrat.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        btnEditVorratItemStateChanged(e);
                                    }
                                });
                                pnlLBVorgangOps.add(btnEditVorrat);

                                //---- btnPrintEti1 ----
                                btnPrintEti1.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/labelprinter2.png")));
                                btnPrintEti1.setToolTipText("Drucken auf Etikett 1");
                                btnPrintEti1.setEnabled(false);
                                btnPrintEti1.setText("1");
                                btnPrintEti1.setFont(new Font("sansserif", Font.BOLD, 18));
                                btnPrintEti1.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnPrintEti1ActionPerformed(e);
                                    }
                                });
                                pnlLBVorgangOps.add(btnPrintEti1);

                                //---- btnPrintEti2 ----
                                btnPrintEti2.setText("2");
                                btnPrintEti2.setFont(new Font("sansserif", Font.BOLD, 18));
                                btnPrintEti2.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/labelprinter2.png")));
                                btnPrintEti2.setToolTipText("Drucken auf Etikett 2");
                                btnPrintEti2.setEnabled(false);
                                btnPrintEti2.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnPrintEti2ActionPerformed(e);
                                    }
                                });
                                pnlLBVorgangOps.add(btnPrintEti2);

                                //---- btnPageprinter ----
                                btnPageprinter.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/printer.png")));
                                btnPageprinter.setToolTipText("DIN A4 Vorratszettel drucken");
                                btnPageprinter.setEnabled(false);
                                btnPageprinter.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnPageprinterActionPerformed(e);
                                    }
                                });
                                pnlLBVorgangOps.add(btnPageprinter);
                                pnlLBVorgangOps.add(hSpacer1);

                                //---- btnShowRightSide ----
                                btnShowRightSide.setToolTipText("Buchungen einblenden");
                                btnShowRightSide.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/2leftarrow.png")));
                                btnShowRightSide.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/24x24/2rightarrow.png")));
                                btnShowRightSide.setRolloverEnabled(false);
                                btnShowRightSide.setEnabled(false);
                                btnShowRightSide.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        btnShowRightSideItemStateChanged(e);
                                    }
                                });
                                pnlLBVorgangOps.add(btnShowRightSide);
                            }
                            splitLBMLeft.setTopComponent(pnlLBVorgangOps);

                            //======== pnlLBMLower ========
                            {
                                pnlLBMLower.setLayout(new BoxLayout(pnlLBMLower, BoxLayout.X_AXIS));

                                //---- btnSaveVEdit ----
                                btnSaveVEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/filesave3.png")));
                                btnSaveVEdit.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnSaveVEditActionPerformed(e);
                                    }
                                });
                                pnlLBMLower.add(btnSaveVEdit);

                                //---- btnCancelVEdit ----
                                btnCancelVEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cancel.png")));
                                btnCancelVEdit.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnCancelVEditActionPerformed(e);
                                    }
                                });
                                pnlLBMLower.add(btnCancelVEdit);
                            }
                            splitLBMLeft.setBottomComponent(pnlLBMLower);
                        }
                        splitLeftButtonsMain.setLeftComponent(splitLBMLeft);

                        //======== pnlLBMYesNo ========
                        {
                            pnlLBMYesNo.setLayout(new BoxLayout(pnlLBMYesNo, BoxLayout.X_AXIS));

                            //---- btnApply1 ----
                            btnApply1.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/ok.png")));
                            btnApply1.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnApply1ActionPerformed(e);
                                }
                            });
                            pnlLBMYesNo.add(btnApply1);
                            pnlLBMYesNo.add(hSpacer2);

                            //---- lblApply1 ----
                            lblApply1.setText("text");
                            lblApply1.setFont(new Font("sansserif", Font.PLAIN, 26));
                            pnlLBMYesNo.add(lblApply1);
                            pnlLBMYesNo.add(hSpacer3);

                            //---- btnCancel1 ----
                            btnCancel1.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cancel.png")));
                            btnCancel1.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnCancel1ActionPerformed(e);
                                }
                            });
                            pnlLBMYesNo.add(btnCancel1);
                        }
                        splitLeftButtonsMain.setRightComponent(pnlLBMYesNo);
                    }

                    GroupLayout pnlWarenbestandLayout = new GroupLayout(pnlWarenbestand);
                    pnlWarenbestand.setLayout(pnlWarenbestandLayout);
                    pnlWarenbestandLayout.setHorizontalGroup(
                        pnlWarenbestandLayout.createParallelGroup()
                            .addComponent(lblWarenbestand, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                            .addComponent(splitVorrat, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                            .addComponent(splitLeftButtonsMain, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                    );
                    pnlWarenbestandLayout.setVerticalGroup(
                        pnlWarenbestandLayout.createParallelGroup()
                            .addGroup(pnlWarenbestandLayout.createSequentialGroup()
                                .addComponent(lblWarenbestand)
                                .addComponent(splitVorrat)
                                .addComponent(splitLeftButtonsMain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                    );
                }
                splitMain.setLeftComponent(pnlWarenbestand);

                //======== pnlEinzelbuchung ========
                {

                    //---- jLabel2 ----
                    jLabel2.setBackground(Color.green);
                    jLabel2.setFont(new Font("Lucida Grande", Font.BOLD, 18));
                    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel2.setText("Einzelbuchungen");
                    jLabel2.setOpaque(true);

                    //======== pnlBuchungen ========
                    {
                        pnlBuchungen.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                pnlBuchungenComponentResized(e);
                            }
                        });

                        //---- tblBuchungen ----
                        tblBuchungen.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                            },
                            new String[] {
                                "Title 1", "Title 2", "Title 3", "Title 4"
                            }
                        ));
                        tblBuchungen.setAutoCreateRowSorter(true);
                        tblBuchungen.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        tblBuchungen.addPropertyChangeListener(new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent e) {
                                tblBuchungenPropertyChange(e);
                            }
                        });
                        pnlBuchungen.setViewportView(tblBuchungen);
                    }

                    //======== pnlRightButtons ========
                    {
                        pnlRightButtons.setMinimumSize(new Dimension(0, 0));
                        pnlRightButtons.setLayout(new BoxLayout(pnlRightButtons, BoxLayout.X_AXIS));

                        //---- btnAddBuchung ----
                        btnAddBuchung.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
                        btnAddBuchung.setEnabled(false);
                        btnAddBuchung.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddBuchungActionPerformed(e);
                            }
                        });
                        pnlRightButtons.add(btnAddBuchung);

                        //---- btnSaveBuchung ----
                        btnSaveBuchung.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/ok.png")));
                        btnSaveBuchung.setEnabled(false);
                        btnSaveBuchung.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnSaveBuchungActionPerformed(e);
                            }
                        });
                        pnlRightButtons.add(btnSaveBuchung);

                        //---- btnCancelBuchung ----
                        btnCancelBuchung.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cancel.png")));
                        btnCancelBuchung.setEnabled(false);
                        btnCancelBuchung.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnCancelBuchungActionPerformed(e);
                            }
                        });
                        pnlRightButtons.add(btnCancelBuchung);
                    }

                    GroupLayout pnlEinzelbuchungLayout = new GroupLayout(pnlEinzelbuchung);
                    pnlEinzelbuchung.setLayout(pnlEinzelbuchungLayout);
                    pnlEinzelbuchungLayout.setHorizontalGroup(
                        pnlEinzelbuchungLayout.createParallelGroup()
                            .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(pnlBuchungen, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(pnlRightButtons, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    );
                    pnlEinzelbuchungLayout.setVerticalGroup(
                        pnlEinzelbuchungLayout.createParallelGroup()
                            .addGroup(pnlEinzelbuchungLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlBuchungen, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlRightButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    );
                }
                splitMain.setRightComponent(pnlEinzelbuchung);
            }

            GroupLayout pnlMainLayout = new GroupLayout(pnlMain);
            pnlMain.setLayout(pnlMainLayout);
            pnlMainLayout.setHorizontalGroup(
                pnlMainLayout.createParallelGroup()
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(pnlSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(splitMain, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE))
            );
            pnlMainLayout.setVerticalGroup(
                pnlMainLayout.createParallelGroup()
                    .addComponent(pnlSuche, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(splitMain, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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


        pnlMisc.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Liste drucken");
                putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/24x24/printer.png")));
                putValue(Action.SHORT_DESCRIPTION, "Druckt eine Gesamtliste der markierten VorrÃ¤te");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Main.printers.print(thisComponent, TableModelHTMLConverter.convert(tblVorrat), false);
            }
        });

    }

    private void loadTimeSearches() {
        jdcEinbuchen.setDate(new Date());
        final SimpleDateFormat dfShort = new SimpleDateFormat("EEEE");
        final DateFormat dfLong = DateFormat.getDateInstance(DateFormat.SHORT);

        for (int days = 0; days < 7; days++) {

            final int d = days;
            //pnlMyVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/")));

            final Date day = Tools.addDate(new Date(), -days);

            pnlZeit.add(new AbstractAction() {
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

        laufendeVOperation = LAUFENDE_OPERATION_NICHTS;

        vmode = MODE_VORRAT_BROWSE;
        bmode = MODE_BUCHUNG_INVISIBLE;

        vlsl = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // wenn nur eine Zeile gewählt wurde,
                    // dann kann die rechte Seite mit den Einzelbuchungen angezeigt werden.

                    int rowcount = tblVorrat.getSelectedRowCount();

                    if (btnEditVorrat.isSelected()) {
                        btnCancelVEdit.doClick();
                    }

                    // btnDeleteVorrat.setEnabled(rowcount > 0);
                    //btnAusbuchen.setEnabled(rowcount > 0);
                    btnPrintEti1.setEnabled(rowcount > 0);
                    btnPrintEti2.setEnabled(rowcount > 0);
                    btnPageprinter.setEnabled(rowcount > 0);
                    btnEditVorrat.setEnabled(rowcount > 0);

//                    if (rowcount <= 1 && btnShowRightSide.isSelected()) {
//                        btnShowRightSide.setSelected(false);
//                    }

                    //btnShowRightSide.setEnabled(false);
                    //btnShowRightSide.setEnabled(rowcount == 1);

                }
            }
        };

        prepareSearchArea();
        loadVorratTable();
        initBuchungenTable();

        loadCMB4Edit();

        setDividerTo(1.0d);
        splitLBMLeftDL = 1.0d;
        splitLeftButtonsMainDL = 1.0d;
        splitVorratDL = 1.0d;

        //btnPrintLabels.setEnabled(Main.props.containsKey("labelPrinter") && Main.props.containsKey("receiptPrinter"));

        //tblBuchungen.setRowHeight(32);


    }

    private void loadCMB4Edit() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Lager.findAllSorted");
        try {
            java.util.List lager = query.getResultList();
            lager.add(0, "<html><i>ändern Lager</i></html>");
            cmbVEditLager.setModel(tools.Tools.newComboboxModel(lager));
        } catch (Exception e) { // nicht gefunden
            //
        } finally {
            em.close();
        }
        em = Main.getEMF().createEntityManager();
        query = em.createNamedQuery("Lieferanten.findAllSorted");
        try {
            java.util.List lieferant = query.getResultList();
            lieferant.add(0, "<html><i>ändern Lieferant</i></html>");
            cmbVEditLieferant.setModel(tools.Tools.newComboboxModel(lieferant));
        } catch (Exception e) { // nicht gefunden
            //
        } finally {
            em.close();
        }
    }

    private void setDividerTo(double d) {
        splitMainDL = d;
        splitMain.setDividerLocation(splitMainDL);
    }

    private void showVEditMessage(String text) {
        lblApply1.setText(text);
        textmessageTL = new Timeline(lblApply1);
        textmessageTL.addPropertyToInterpolate("foreground", lblApply1.getForeground(), Color.red);
        textmessageTL.setDuration(600);
        textmessageTL.playLoop(Timeline.RepeatBehavior.REVERSE);

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlMain;
    private JPanel pnlSuche;
    private JScrollPane jspSuche;
    private JXTaskPaneContainer tskSuche;
    private JComponent separator1;
    private JXTaskPane pnlGesamt;
    private JToggleButton btnAktiv;
    private JToggleButton btnInaktiv;
    private JToggleButton btnGesamt;
    private JXTaskPane pnlZeit;
    private JDateChooser jdcEinbuchen;
    private JXTaskPane pnlSpezial;
    private JComboBox cmbWarengruppe;
    private JComboBox cmbLager;
    private JComboBox cmbLagerart;
    private JComboBox cmbLieferant;
    private JXSearchField txtSuchen;
    private JToggleButton btnSum;
    private JButton btnNoFilter;
    private JComponent separator2;
    private JXTaskPane pnlMisc;
    private JSplitPane splitMain;
    private JPanel pnlWarenbestand;
    private JLabel lblWarenbestand;
    private JSplitPane splitVorrat;
    private JScrollPane pnlVorrat;
    private JTable tblVorrat;
    private JPanel panel1;
    private JComboBox cmbVEditLager;
    private JComboBox cmbVEditLieferant;
    private JButton btnDeleteVorrat;
    private JButton btnAusbuchen;
    private JButton btnReopen;
    private JButton btnAnfangKorrigieren;
    private JSplitPane splitLeftButtonsMain;
    private JSplitPane splitLBMLeft;
    private JPanel pnlLBVorgangOps;
    private JToggleButton btnEditVorrat;
    private JButton btnPrintEti1;
    private JButton btnPrintEti2;
    private JButton btnPageprinter;
    private JPanel hSpacer1;
    private JToggleButton btnShowRightSide;
    private JPanel pnlLBMLower;
    private JButton btnSaveVEdit;
    private JButton btnCancelVEdit;
    private JPanel pnlLBMYesNo;
    private JXButton btnApply1;
    private JPanel hSpacer2;
    private JLabel lblApply1;
    private JPanel hSpacer3;
    private JButton btnCancel1;
    private JPanel pnlEinzelbuchung;
    private JLabel jLabel2;
    private JScrollPane pnlBuchungen;
    private JTable tblBuchungen;
    private JPanel pnlRightButtons;
    private JButton btnAddBuchung;
    private JButton btnSaveBuchung;
    private JButton btnCancelBuchung;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
