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
import printer.Form;
import printer.Printer;
import tablemodels.VorratTableModel;
import threads.PrintProcessor;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
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

    private Printer pageprinter, etiprinter1, etiprinter2;
    private Form form1, form2;

    private JComponent thisComponent;

    private boolean initphase;

    private Pair<Integer, Object> suche = null;


    public FrmVorrat() {
        initphase = true;
        thisComponent = this;
        initComponents();
        myInit();
        pack();
        initphase = false;

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

//        String strQueryAddendum = "Alle";
//        if (btnAktiv.isSelected()) {
//            strQueryAddendum = "Aktiv";
//        } else if (btnInaktiv.isSelected()) {
//            strQueryAddendum = "Inaktiv";
//        }

        try {
            EntityManager em = Main.getEMF().createEntityManager();
            if (suche == null) {

                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " GROUP BY v");
                }

            } else if (suche.getFirst() == Const.DATUM) {
                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.eingang BETWEEN :eingang1 AND :eingang2 AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.eingang BETWEEN :eingang1 AND :eingang2  AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.eingang BETWEEN :eingang1 AND :eingang2 " +
                            " GROUP BY v");
                }

                Date eingang = (Date) suche.getSecond();
                query.setParameter("eingang1", new Date(Tools.startOfDay(eingang)));
                query.setParameter("eingang2", new Date(Tools.endOfDay(eingang)));
            } else if (suche.getFirst() == Const.PRODUKT) {
                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt = :produkt AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt = :produkt AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt = :produkt " +
                            " GROUP BY v");
                }
                Produkte produkt = (Produkte) suche.getSecond();
                query.setParameter("produkt", produkt);
            } else if (suche.getFirst() == Const.VORRAT) {
                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v = :vorrat AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v = :vorrat AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v = :vorrat " +
                            " GROUP BY v");
                }
                Vorrat vorrat = (Vorrat) suche.getSecond();
                query.setParameter("vorrat", vorrat);
            } else if (suche.getFirst() == Const.LAGER) {
                initphase = true;
                cmbWarengruppe.setSelectedIndex(0);
                cmbLieferant.setSelectedIndex(0);
                initphase = false;

                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                            " WHERE v.lager = :lager AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                            " WHERE v.lager = :lager AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                            " WHERE v.lager = :lager " +
                            " GROUP BY v");
                }

                Lager lager = (Lager) suche.getSecond();
                query.setParameter("lager", lager);
            } else if (suche.getFirst() == Const.WARENGRUPPE) {
                initphase = true;
                cmbLager.setSelectedIndex(0);
                cmbLieferant.setSelectedIndex(0);
                initphase = false;

                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt.ingTypes.warengruppe = :warengruppe AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt.ingTypes.warengruppe = :warengruppe AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt.ingTypes.warengruppe = :warengruppe " +
                            " GROUP BY v");
                }

                Warengruppe warengruppe = (Warengruppe) suche.getSecond();
                query.setParameter("warengruppe", warengruppe);
            } else if (suche.getFirst() == Const.LIEFERANT) {
                initphase = true;
                cmbLager.setSelectedIndex(0);
                cmbWarengruppe.setSelectedIndex(0);
                initphase = false;

                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.lieferant = :lieferant AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.lieferant = :lieferant AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.lieferant = :lieferant " +
                            " GROUP BY v");
                }

                Lieferanten lieferant = (Lieferanten) suche.getSecond();
                query.setParameter("lieferant", lieferant);
            } else if (suche.getFirst() == Const.PRODUKTNAME) {

                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt.bezeichnung LIKE :bezeichnung AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt.bezeichnung LIKE :bezeichnung AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                            " WHERE v.produkt.bezeichnung LIKE :bezeichnung " +
                            " GROUP BY v");
                }

                query.setParameter("bezeichnung", "%" + suche.getSecond().toString().trim() + "%");
            } else if (suche.getFirst() == Const.LAGERART) {

                if (btnAktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                            " WHERE v.lager.lagerart = :lagerart AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else if (btnInaktiv.isSelected()) {
                    query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                            " WHERE v.lager.lagerart = :lagerart AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                            " GROUP BY v");
                } else {
                    query = em.createQuery("SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                            " WHERE v.lager.lagerart = :lagerart " +
                            " GROUP BY v");
                }
                short lagerart = (Short) suche.getSecond();
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
        Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung");
        java.util.List warengruppe = query.getResultList();
        warengruppe.add(0, "<html><i>nach Warengruppe</i></html>");
        cmbWarengruppe.setModel(tools.Tools.newComboboxModel(warengruppe));
        em.close();
    }

    private void loadLager() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT l FROM Lager l ORDER BY l.bezeichnung");
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
        Query query = em.createQuery("SELECT l FROM Lieferanten l ORDER BY l.firma");
        java.util.List lieferanten = query.getResultList();
        lieferanten.add(0, "<html><i>nach Lieferanten</i></html>");
        cmbLieferant.setModel(tools.Tools.newComboboxModel(lieferanten));
        em.close();
    }

    private void pnlVorratComponentResized(ComponentEvent e) {

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


    private void thisComponentResized(ComponentEvent e) {

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

    private void cmbLieferantItemStateChanged(ItemEvent e) {
        if (initphase) return;
        tpZeit.setCollapsed(true);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            suche = new Pair<Integer, Object>(Const.LIEFERANT, cmbLieferant.getSelectedItem());
            loadVorratTable();
        }
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
            JMenuItem itemAusbuchen = new JMenuItem("Ausbuchen", Const.icon24stop);
            itemAusbuchen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(thisComponent, "Wirklich ?", "Ausbuchen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48stop) == JOptionPane.YES_OPTION) {
                        int[] rows = tblVorrat.getSelectedRows();
//                        VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();
                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();
                            for (int r = 0; r < rows.length; r++) {
                                int row = tblVorrat.convertRowIndexToModel(rows[r]);
                                Vorrat vorrat = em.merge(tm.getVorrat(row));
                                em.lock(vorrat, LockModeType.OPTIMISTIC);
                                VorratTools.ausbuchen(em, vorrat, "Abschlussbuchung");
                            }
                            em.getTransaction().commit();
                            loadVorratTable();
                        } catch (OptimisticLockException ole) {
                            Main.warn(ole);
                            em.getTransaction().rollback();
                        } catch (Exception e1) {
                            em.getTransaction().rollback();
                        } finally {
                            em.close();
                        }
                    }
                }
            });

            menu.add(itemAusbuchen);
            JMenuItem itemLoeschen = new JMenuItem("Löschen", Const.icon24remove);
            itemLoeschen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                        int[] rows = tblVorrat.getSelectedRows();
//                        VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();
                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();
                            for (int r = 0; r < rows.length; r++) {
                                int row = tblVorrat.convertRowIndexToModel(rows[r]);
                                Vorrat vorrat = em.merge(tm.getVorrat(row));
                                em.lock(vorrat, LockModeType.OPTIMISTIC);
                                em.remove(vorrat);
                            }
                            em.getTransaction().commit();
                            loadVorratTable();
                        } catch (OptimisticLockException ole) {
                            Main.warn(ole);
                            em.getTransaction().rollback();
                        } catch (Exception e1) {
                            em.getTransaction().rollback();
                        } finally {
                            em.close();
                        }
                    }
                }
            });
            menu.add(itemLoeschen);
            menu.setEnabled(Main.getCurrentUser().isAdmin());


            JMenuItem itemZurueckBuchen = new JMenuItem("wieder einbuchen", Const.icon24undo);
            itemZurueckBuchen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(thisComponent, "Sicher ?", "wieder einbuchen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48undo) == JOptionPane.YES_OPTION) {
                        int[] rows = tblVorrat.getSelectedRows();
//                        VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();
                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();
                            for (int r = 0; r < rows.length; r++) {
                                int row = tblVorrat.convertRowIndexToModel(rows[r]);
                                Vorrat vorrat = em.merge(tm.getVorrat(row));
                                em.lock(vorrat, LockModeType.OPTIMISTIC);
                                VorratTools.reaktivieren(em, vorrat);
                            }
                            em.getTransaction().commit();
                            loadVorratTable();
                        } catch (OptimisticLockException ole) {
                            Main.warn(ole);
                            em.getTransaction().rollback();
                        } catch (Exception e1) {
                            em.getTransaction().rollback();
                        } finally {
                            em.close();
                        }
                    }
                }
            });
            menu.add(itemZurueckBuchen);

            menu.add(compFactory.createSeparator("Korrekturen", SwingConstants.CENTER));
            JMenuItem itemChangeProduct = new JMenuItem("Produkt neu zuweisen", Const.icon24undo);
            itemChangeProduct.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    int[] rows = tblVorrat.getSelectedRows();
                    //                        VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();
                    EntityManager em = Main.getEMF().createEntityManager();
                    try {
                        em.getTransaction().begin();
                        for (int r = 0; r < rows.length; r++) {
                            int row = tblVorrat.convertRowIndexToModel(rows[r]);
                            Vorrat vorrat = em.merge(tm.getVorrat(row));
                            em.lock(vorrat, LockModeType.OPTIMISTIC);
                            VorratTools.reaktivieren(em, vorrat);
                        }
                        em.getTransaction().commit();
                        loadVorratTable();
                    } catch (OptimisticLockException ole) {
                        Main.warn(ole);
                        em.getTransaction().rollback();
                    } catch (Exception e1) {
                        em.getTransaction().rollback();
                    } finally {
                        em.close();
                    }
                }

            });
            menu.add(itemChangeProduct);


            menu.add(compFactory.createSeparator("Zuweisen", SwingConstants.CENTER));
            JMenu menuLager = new JMenu("Lager");
            menuLager.setIcon(Const.icon24box);

            EntityManager em = Main.getEMF().createEntityManager();
            Query query = em.createQuery("SELECT l FROM Lager l ORDER BY l.bezeichnung");
            try {
                for (final Lager lager : new ArrayList<Lager>(query.getResultList())) {
                    JMenuItem mi = new JMenuItem(lager.getBezeichnung());
                    mi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (JOptionPane.showConfirmDialog(thisComponent, "Zuweisen ins Lager: " + lager.getBezeichnung() + " ?", "Lagerort ändern", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48box) == JOptionPane.YES_OPTION) {
                                int[] rows = tblVorrat.getSelectedRows();
//                                VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();
                                EntityManager em = Main.getEMF().createEntityManager();
                                try {
                                    em.getTransaction().begin();
                                    Lager myLager = em.merge(lager);
                                    for (int r = 0; r < rows.length; r++) {
                                        int row = tblVorrat.convertRowIndexToModel(rows[r]);
                                        Vorrat vorrat = em.merge(tm.getVorrat(row));
                                        em.lock(vorrat, LockModeType.OPTIMISTIC);
                                        vorrat.setLager(myLager);
                                    }
                                    em.getTransaction().commit();
                                    loadVorratTable();
                                } catch (OptimisticLockException ole) {
                                    Main.warn(ole);
                                    em.getTransaction().rollback();
                                } catch (Exception e1) {
                                    em.getTransaction().rollback();
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                    menuLager.add(mi);
                }
            } catch (Exception e) { // nicht gefunden
                Main.fatal(e);
            } finally {
                em.close();
            }
            menu.add(menuLager);


            JMenu menuLieferanten = new JMenu("Lieferanten");
            menuLieferanten.setIcon(Const.icon24truck);

            em = Main.getEMF().createEntityManager();
            query = em.createQuery("SELECT l FROM Lieferanten l ORDER BY l.firma");
            try {
                for (final Lieferanten lieferant : new ArrayList<Lieferanten>(query.getResultList())) {
                    JMenuItem mi = new JMenuItem(lieferant.getFirma() + Tools.catchNull(lieferant.getOrt(), ", ", ""));
                    mi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (JOptionPane.showConfirmDialog(thisComponent, "Erhalten von Lieferant: " + lieferant.getFirma() + " ?", "Lieferant ändern", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48box) == JOptionPane.YES_OPTION) {
                                int[] rows = tblVorrat.getSelectedRows();
//                                VorratTableModel tm = (VorratTableModel) tblVorrat.getModel();
                                EntityManager em = Main.getEMF().createEntityManager();
                                try {
                                    em.getTransaction().begin();
                                    Lieferanten myLieferant = em.merge(lieferant);
                                    for (int r = 0; r < rows.length; r++) {
                                        int row = tblVorrat.convertRowIndexToModel(rows[r]);
                                        Vorrat vorrat = em.merge(tm.getVorrat(row));
                                        em.lock(vorrat, LockModeType.OPTIMISTIC);
                                        vorrat.setLieferant(myLieferant);
                                    }
                                    em.getTransaction().commit();
                                    loadVorratTable();
                                } catch (OptimisticLockException ole) {
                                    Main.warn(ole);
                                    em.getTransaction().rollback();
                                } catch (Exception e1) {
                                    em.getTransaction().rollback();
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                    menuLieferanten.add(mi);
                }
            } catch (Exception e) { // nicht gefunden
                Main.fatal(e);
            } finally {
                em.close();
            }
            menu.add(menuLieferanten);

            menu.add(compFactory.createSeparator("Etiketten-Druck", SwingConstants.CENTER));

            // Printer
            JMenuItem itemPrinter1 = new JMenuItem("Drucker 1", Const.icon24labelPrinter2);
            itemPrinter1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    print(etiprinter1, form1, Main.getProps().getProperty("etiprinter1"));
                }
            });
            menu.add(itemPrinter1);

            JMenuItem itemPrinter2 = new JMenuItem("Drucker 2", Const.icon24labelPrinter2);
            itemPrinter2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    print(etiprinter2, form2, Main.getProps().getProperty("etiprinter2"));
                }
            });
            menu.add(itemPrinter2);

            JMenuItem itemPrinter3 = new JMenuItem("Seitendrucker", Const.icon24Pageprinter);
            itemPrinter3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    print(pageprinter, null, Main.getProps().getProperty("pageprinter"));
                }
            });
            menu.add(itemPrinter3);


            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }

    private void pnlWarenbestandComponentResized(ComponentEvent e) {
        // TODO add your code here
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
                        .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, 1315, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
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

        prepareSearchArea();
        loadVorratTable();
        // initBuchungenTable();


        //btnPrintLabels.setEnabled(Main.props.containsKey("labelPrinter") && Main.props.containsKey("receiptPrinter"));

        //tblBuchungen.setRowHeight(32);


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
