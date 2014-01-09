/*
 * Created by JFormDesigner on Fri Jul 01 15:05:12 CEST 2011
 */

package touch;

import Main.Main;
import beans.Buchung;
import beans.PrintListElement;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallback;
import printer.Form;
import printer.Printer;
import printer.Printers;
import threads.PrintProcessor;
import tools.Const;
import tools.DlgException;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;


/**
 * @author Torsten Löhr
 */
public class PnlWareneingang extends DefaultTouchPanel {
    private int currentMode;
    private final int MODE_EDIT_PRODUCT = 1;
    private final int MODE_WARENEINGANG = 2;
    private final int MODE_VORRATREAKTIVIERUNG = 3;
    private Buchung aktuelleBuchung;
    private Produkte neuesProdukt;
    private Vorrat vorrat;
    private PrintProcessor pp;
    private boolean gtinOK, warengruppeEdit, stoffartEdit;

    private Printer pageprinter, etiprinter1, etiprinter2;
    private Form form1, form2;
    private double splitLiefADouble, splitLagerADouble;

    private DateFormat df, tf;

    //private boolean firstTime; // für Dinge die nur beim ersten mal initialisiert werden müssen.

    public PnlWareneingang(PrintProcessor pp) {
        this.pp = pp;
        //firstTime = true;
        initComponents();
        myInitComponents();

        pageprinter = Main.printers.getPrinters().get("pageprinter");
        etiprinter1 = Main.printers.getPrinters().get(Main.getProps().getProperty("etitype1"));
        etiprinter2 = Main.printers.getPrinters().get(Main.getProps().getProperty("etitype2"));

        form1 = etiprinter1.getForms().get(Main.getProps().getProperty("etiform1"));
        form2 = etiprinter2.getForms().get(Main.getProps().getProperty("etiform2"));

        //firstTime = false;
    }


    private void btnVerpackteWareItemStateChanged(ItemEvent e) {

        if (btnVerpackteWare.isSelected()) {
            Tools.showSide(splitNeuProd, 0.3d, 500);
            txtGTIN.requestFocus();
        } else {
            Tools.showSide(splitNeuProd, 0.08d, 500);
            txtProdBezeichnung.requestFocus();
            txtGTIN.setText("");
            neuesProdukt.setGtin(null);
            neuesProdukt.setPackGroesse(BigDecimal.ZERO);
        }

        setProduktEingabeButton();

    }

    private void txtGTINActionPerformed(ActionEvent e) {
        txtPackGroesse.requestFocus();
    }

    private void txtGTINFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
        txtGTIN.selectAll();
    }

    private void txtPackGroesseCaretUpdate(CaretEvent e) {
        try {
            BigDecimal bd = new BigDecimal(txtPackGroesse.getText().replaceAll(",", "\\."));
            neuesProdukt.setPackGroesse(bd);
            //Main.Main.logger.debug("Double: " + dbl);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                error("Packungsgrößen müssen größer null sein", lblMessageLower);
            } else {
                fadeout(lblMessageLower);
            }
        } catch (NumberFormatException e1) {
            neuesProdukt.setPackGroesse(new BigDecimal(0d));
            error("Ungültiger Zahlenwert bei der Packungsgröße.", lblMessageLower);
        }
        setProduktEingabeButton();
    }

    private void txtPackGroesseFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
        //txtGTIN.selectAll();
    }

    private void btnNoPrinterItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1printer", "3");
    }

    private void thisComponentResized(ComponentEvent e) {
        setPanelMode(currentMode, 0);
    }

    private void btnNewProduktActionPerformed(ActionEvent e) {
        setPanelMode(MODE_EDIT_PRODUCT, 700);
        String suche = txtSearch.getText().trim();
        if (ProdukteTools.getGTIN(suche) != null) {
            btnVerpackteWare.setSelected(true);
            txtGTIN.setText(suche);
            txtProdBezeichnung.setText("");
        } else {
            txtProdBezeichnung.setText(suche);
            txtGTIN.setText("");
        }
        txtProdBezeichnung.requestFocus();

    }

    private void btnAcceptProdActionPerformed(ActionEvent e) {

        if (stoffartEdit) {
            btnApplyStoffart.doClick();
        }

        if (isProduktEingabeOK(true)) {
            EntityTools.persist(neuesProdukt);
            if (neuesProdukt.getGtin() != null) {
                txtSearch.setText(neuesProdukt.getGtin());
            } else {
                txtSearch.setText(neuesProdukt.getBezeichnung());
            }
            neuesProdukt = new Produkte();
            setPanelMode(MODE_WARENEINGANG, 700);
            txtSearchActionPerformed(null);
        }
    }

    private void btnAddLieferantActionPerformed(ActionEvent e) {
        splitLiefADouble = Tools.showSide(splitLieferant, Tools.RIGHT_LOWER_SIDE, 400);
        txtNewLieferant.setText("");
        txtNewLieferant.requestFocus();
    }

    private void btnCancelLieferantActionPerformed(ActionEvent e) {
        splitLiefADouble = Tools.showSide(splitLieferant, Tools.LEFT_UPPER_SIDE, 400);
    }

    private void btnAddLagerActionPerformed(ActionEvent e) {
        splitLagerADouble = Tools.showSide(splitLager, Tools.RIGHT_LOWER_SIDE, 400);
        txtNewLager.setText("");
        txtNewLager.requestFocus();
    }

    private void btnAddWarengruppeActionPerformed(ActionEvent e) {
        Tools.showSide(splitWarengruppe, Tools.RIGHT_LOWER_SIDE, 400);
        warengruppeEdit = true;
        txtNewWarengruppe.setText("");
        txtNewWarengruppe.requestFocus();
    }

    private void btnCancelLagerActionPerformed(ActionEvent e) {
        splitLagerADouble = Tools.showSide(splitLager, Tools.LEFT_UPPER_SIDE, 400);
    }

    private void btnCancelWarengruppeActionPerformed(ActionEvent e) {
        Tools.showSide(splitWarengruppe, Tools.LEFT_UPPER_SIDE, 400);
        warengruppeEdit = false;
        setWarengruppeEnabled(stoffartEdit);
    }

    private void btnCancelStoffartActionPerformed(ActionEvent e) {
        Tools.showSide(splitStoffart, Tools.LEFT_UPPER_SIDE, 400);
        stoffartEdit = false;
        if (splitWarengruppe.getDividerLocation() <= 10) {
            Tools.showSide(splitWarengruppe, Tools.LEFT_UPPER_SIDE, 400);
        }
        setWarengruppeEnabled(false);
    }

    private void btnAddStoffartActionPerformed(ActionEvent e) {
        Tools.showSide(splitStoffart, Tools.RIGHT_LOWER_SIDE, 400);
        txtNewStoffart.setText("");
        txtNewStoffart.requestFocus();
        stoffartEdit = true;
        setWarengruppeEnabled(true);
    }

    void txtFaktorSetEnabled(boolean enabled) {
        txtFaktor.setEnabled(enabled);
        btn5.setEnabled(enabled);
        btn6.setEnabled(enabled);
        btn10.setEnabled(enabled);
        btn12.setEnabled(enabled);
        btn20.setEnabled(enabled);
    }

    private void listProdukteValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {

            txtFaktorSetEnabled(!listProdukte.isSelectionEmpty());
            txtMenge.setEnabled(!listProdukte.isSelectionEmpty());

            if (!listProdukte.isSelectionEmpty()) {
                Produkte produkt = (Produkte) listProdukte.getSelectedValue();
                aktuelleBuchung.setProdukt(produkt);

                //lblProdukt.setText(produkt.getBezeichnung());
                info(produkt.getBezeichnung(), lblProdukt);

                lblProdInfo.setText(String.valueOf("ProdNr.: " + produkt.getId()) + ", " + (produkt.isLoseWare() ? "[lose Ware]" : produkt.getPackGroesse() + " " + ProdukteTools.EINHEIT[produkt.getEinheit()]));

                // Menge wird immer eingeschaltet. Bei Produkten mit einer GTIN kann man aber nicht mehr Menge einbuchen
                // als eine Packung hat, damit kann man Anbrüche einbuchen.
                lblEinheit.setText(Const.EINHEIT[produkt.getEinheit()]);
                // Faktor, wenn das Produkt verpackt ist.
                txtFaktorSetEnabled(produkt.getGtin() != null);
                if (produkt.getGtin() != null) {
                    txtMenge.setText(""); // produkt.getPackGroesse().toString()
                } else {
                    txtMenge.setText("1");
                }

            } else {
                lblProdukt.setIcon(null);
                lblProdukt.setText(null);
                lblProdInfo.setText(null);
            }

            setEinbuchenButton();

        }
    }

    private void txtFaktorFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void txtMengeFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void txtFaktorPropertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equalsIgnoreCase("enabled")) {
            txtFaktor.setText("1");
        }
    }

    private void cmbLagerItemStateChanged(ItemEvent e) {
        aktuelleBuchung.setLager((Lager) e.getItem());
        Main.getProps().put("touch1lager", new Integer(cmbLager.getSelectedIndex()).toString());
    }

    private void txtMengePropertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equalsIgnoreCase("enabled")) {
            if (!(Boolean) e.getNewValue()) { // true, wenn das Ding ENABLED wurde
                txtMenge.setText("0");
            } else {
                txtMenge.setText("1");
            }
        }
    }

    private void btnEinbuchenActionPerformed(ActionEvent e) {
        einbuchen();
        txtSearch.setText("");
        txtSearch.requestFocus();
    }

    private void einbuchen() {
        if (isEinbuchenOK(true)) {

            aktuelleBuchung.setPrinter(Printers.DRUCK_ETI1);

            if (aktuelleBuchung.getFaktor() < 1) {
                aktuelleBuchung.setFaktor(1);
            }

            if (aktuelleBuchung.getMenge() == null || aktuelleBuchung.getMenge().compareTo(BigDecimal.ZERO) <= 0) {
                aktuelleBuchung.setMenge(aktuelleBuchung.getProdukt().getPackGroesse());
            }

            EntityManager em = Main.getEMF().createEntityManager();
            em.getTransaction().begin();
            java.util.List<PrintListElement> printList = new ArrayList(aktuelleBuchung.getFaktor());

            try {

                // Für jedes "Paket" einen Vorrat anlegen und einbuchen.
                for (int i = 1; i <= aktuelleBuchung.getFaktor(); i++) {
                    Vorrat vorrat = new Vorrat(aktuelleBuchung.getProdukt(), (Lieferanten) cmbLieferant.getSelectedItem(), (Lager) cmbLager.getSelectedItem());
                    em.persist(vorrat);
                    Buchungen buchungen = new Buchungen(aktuelleBuchung.getMenge(), vorrat.getEingang());
                    buchungen.setText("Anfangsbestand");
                    buchungen.setStatus(BuchungenTools.BUCHEN_EINBUCHEN_ANFANGSBESTAND);
                    buchungen.setVorrat(vorrat);
                    buchungen.setMitarbeiter(Main.getCurrentUser());
                    em.persist(buchungen);

                    if (btnEtiketten1.isSelected()) {
                        printList.add(new PrintListElement(vorrat, etiprinter1, form1, Main.getProps().getProperty("etiprinter1")));
                    } else if (btnEtiketten2.isSelected()) {
                        printList.add(new PrintListElement(vorrat, etiprinter2, form2, Main.getProps().getProperty("etiprinter2")));
                    } else if (btnPagePrinter.isSelected()) {
                        printList.add(new PrintListElement(vorrat, pageprinter, null, Main.getProps().getProperty("pageprinter")));
                    }
                }

                em.getTransaction().commit();

                Tools.log(txtLog, "EINBUCHUNG " + aktuelleBuchung.getFaktor() + "x '" + aktuelleBuchung.getProdukt().getBezeichnung() + "' á " + aktuelleBuchung.getMenge() + " " + ProdukteTools.EINHEIT[aktuelleBuchung.getProdukt().getEinheit()]);

                if (!btnNoPrinter.isSelected()) {
                    Collections.sort(printList); // Sortieren nach den PrimaryKeys
                    pp.addPrintJobs(printList);
                }

            } catch (Exception e1) {
                Main.logger.fatal(e1.getMessage(), e1);
                em.getTransaction().rollback();
                Tools.log(txtLog, e1.getMessage());
            } finally {
                em.close();
            }

            aktuelleBuchung = new Buchung(aktuelleBuchung.getProdukt(), aktuelleBuchung.getFaktor(), aktuelleBuchung.getMenge(),
                    aktuelleBuchung.getPrinter(), (Lager) cmbLager.getSelectedItem());
        }
    }

    private void cmbLieferantFocusGained(FocusEvent e) {
        final Timeline timeline1 = new Timeline(e.getSource());
        timeline1.addPropertyToInterpolate("background", ((JComponent) e.getSource()).getBackground(), Color.red);
        timeline1.setDuration(140);
        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
    }

    private void cmbLagerFocusGained(FocusEvent e) {
        final Timeline timeline1 = new Timeline(e.getSource());
        timeline1.addPropertyToInterpolate("background", ((JComponent) e.getSource()).getBackground(), Color.red);
        timeline1.setDuration(140);
        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
    }

    private void txtSearchFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void btnApplyLieferantActionPerformed(ActionEvent e) {
        if (!txtNewLieferant.getText().isEmpty()) {
            Lieferanten lieferant = LieferantenTools.add(txtNewLieferant.getText());
            cmbLieferant.setModel(Tools.newComboboxModel("Lieferanten.findAllSorted"));
            cmbLieferant.setSelectedItem(lieferant);
        }
        btnCancelLieferantActionPerformed(e); // Klappt alles wieder ein.
    }

    private void txtNewLieferantCaretUpdate(CaretEvent e) {
        //btnApplyLieferant.setEnabled(!txtNewLieferant.getText().isEmpty());
    }

    private void txtNewLieferantFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void txtNewLagerCaretUpdate(CaretEvent e) {
        //btnApplyLager.setEnabled(!txtNewLager.getText().isEmpty());
    }

    private void txtNewLagerFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void btnApplyLagerActionPerformed(ActionEvent e) {
        if (!txtNewLager.getText().isEmpty()) {
            Lager lager = LagerTools.add(txtNewLager.getText());
            cmbLager.setModel(Tools.newComboboxModel("Lager.findAllSorted"));
            cmbLager.setSelectedItem(lager);
        }
        btnCancelLagerActionPerformed(e); // Klappt alles wieder ein.
    }

    private void cmbWarengruppeItemStateChanged(ItemEvent e) {

    }

    private void txtNewStoffartFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void txtNewWarengruppeFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void btnApplyStoffartActionPerformed(ActionEvent e) {
        if (warengruppeEdit) {
            btnApplyWarengruppe.doClick();
        }

        if (!txtNewStoffart.getText().isEmpty() && cmbWarengruppe.getSelectedItem() != null) {
            Stoffart stoffart = StoffartTools.add(txtNewStoffart.getText(), (short) cmbEinheit.getSelectedIndex(), (Warengruppe) cmbWarengruppe.getSelectedItem());
            StoffartTools.loadStoffarten(cmbStoffart);
            cmbStoffart.setSelectedItem(stoffart);
        }
        Tools.showSide(splitStoffart, Tools.LEFT_UPPER_SIDE, 400);
        stoffartEdit = false;
//        if (splitWarengruppe.getDividerLocation() <= 10) {
//            Tools.showSide(splitWarengruppe, Tools.LEFT_UPPER_SIDE, 400);
//            setWarengruppeEnabled(false);
//        }
    }

    private void cmbStoffartItemStateChanged(ItemEvent e) {
        neuesProdukt.setStoffart((Stoffart) cmbStoffart.getSelectedItem());
        cmbWarengruppe.setSelectedItem(neuesProdukt.getStoffart().getWarengruppe());
    }

    private void btnCancelNeuProdActionPerformed(ActionEvent e) {
        txtSearch.setText("");
        setPanelMode(MODE_WARENEINGANG, 700);
    }

    private void btnApplyWarengruppeActionPerformed(ActionEvent e) {
        if (!txtNewWarengruppe.getText().isEmpty()) {
            Warengruppe warengruppe = WarengruppeTools.add(txtNewWarengruppe.getText());
            loadWarengruppe();
            cmbWarengruppe.setSelectedItem(warengruppe);
        }
        Tools.showSide(splitWarengruppe, Tools.LEFT_UPPER_SIDE, 400);
        warengruppeEdit = false;
        setWarengruppeEnabled(false);
    }

    private void txtProdBezeichnungFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void txtProdBezeichnungCaretUpdate(CaretEvent e) {
        neuesProdukt.setBezeichnung(txtProdBezeichnung.getText().trim());
        setProduktEingabeButton();
    }

    private void cmbEinheitItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1einheit", new Integer(cmbEinheit.getSelectedIndex()).toString());
        neuesProdukt.setEinheit((short) cmbEinheit.getSelectedIndex());
        lblEinheit1.setText(cmbEinheit.getSelectedItem().toString());
    }

    private void cmbLagerartItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1lagerart", new Integer(cmbLagerart.getSelectedIndex()).toString());
        neuesProdukt.setLagerart((short) cmbLagerart.getSelectedIndex());
    }

    private void cmbLieferantItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1lieferant", new Integer(cmbLieferant.getSelectedIndex()).toString());
    }

    private void splitNeuProdComponentResized(ComponentEvent e) {
        if (btnVerpackteWare.isSelected()) {
            splitNeuProd.setDividerLocation(170);
        } else {
            splitNeuProd.setDividerLocation(60);
        }
    }

    private void txtMengeFocusLost(FocusEvent e) {
        txtMenge.setText(aktuelleBuchung.getMenge().toString());
    }

    private void txtMengeActionPerformed(ActionEvent e) {
        txtSearch.requestFocus();
    }

    private void txtSearchActionPerformed(ActionEvent e) {

        java.util.List<Produkte> produkte = ProdukteTools.searchProdukte(txtSearch.getText());
        listProdukte.setModel(Tools.newListModel(produkte));

        if (produkte == null || produkte.size() == 0) {
            vorrat = VorratTools.findByIDORScanner(txtSearch.getText());
            if (vorrat == null || !vorrat.isAusgebucht()) {
                setPanelMode(MODE_WARENEINGANG, 700);
                error("Kenn ich nicht", lblProdukt);
                aktuelleBuchung.setProdukt(null);
                setEinbuchenButton();
            } else {
                setPanelMode(MODE_VORRATREAKTIVIERUNG, 700);
            }
            txtSearch.selectAll();
            txtSearch.requestFocus();
        } else if (produkte.size() == 1) {
            setPanelMode(MODE_WARENEINGANG, 700);
            if (btnSofortBuchen.isSelected() && produkte.get(0).getGtin() != null) {
                aktuelleBuchung.setProdukt(produkte.get(0));
                aktuelleBuchung.setFaktor(1);
                aktuelleBuchung.setMenge(BigDecimal.ZERO);
                einbuchen();
                txtSearch.setText("");
                txtSearch.requestFocus();
            } else {
                listProdukte.setSelectedIndex(0);
                txtFaktor.requestFocus();
            }
        }


//        if (listProdukte.getModel().getSize() > 0) {
//            listProdukte.setSelectedIndex(0);
//            listProdukte.requestFocus();
//        }
    }

    private void txtFaktorActionPerformed(ActionEvent e) {
        txtMenge.requestFocus();
    }

    private void txtFaktorFocusLost(FocusEvent e) {
        try {
            aktuelleBuchung.setFaktor(Integer.parseInt(txtFaktor.getText()));

            int maxfaktor = Integer.parseInt(Main.getProps().getProperty("maxfaktor"));

            if (aktuelleBuchung.getFaktor() < 0 || aktuelleBuchung.getFaktor() > maxfaktor) {
                aktuelleBuchung.setFaktor(0);
            }
            if (aktuelleBuchung.getFaktor() > 1) {
                txtMenge.setText("");
            }

        } catch (NumberFormatException e1) {
            aktuelleBuchung.setFaktor(0);
        }
        setEinbuchenButton();
    }

    private void listProdukteKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtFaktor.isEnabled()) {
                txtFaktor.requestFocus();
            } else {
                txtMenge.requestFocus();
            }
        }
    }

    private void txtGTINFocusLost(FocusEvent e) {
        if (ProdukteTools.isGTIN(txtGTIN.getText())) {
            if (!ProdukteTools.isGTINinUse(txtGTIN.getText())) {
                gtinOK = false; // GTIN gibts schon
                neuesProdukt.setGtin(null);
                error("Strichcode wird schon verwendet.", lblMessageLower);
            } else {
                gtinOK = true;
                neuesProdukt.setGtin(txtGTIN.getText());
                fadeout(lblMessageLower);
            }
        } else {
            gtinOK = false;
            neuesProdukt.setGtin(null);
            fadeout(lblMessageLower);
        }
        setProduktEingabeButton();
    }

    private void btnEtiketten1ItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1printer", "0");
        txtSearch.requestFocus();
    }

    private void btnEtiketten2ItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1printer", "1");
        txtSearch.requestFocus();
    }

    private void btnPagePrinterItemStateChanged(ItemEvent e) {
        Main.getProps().put("touch1printer", "2");
        txtSearch.requestFocus();
    }

    private void txtFaktorCaretUpdate(CaretEvent e) {
        txtFaktorFocusLost(null);
    }

    private void txtGTINCaretUpdate(CaretEvent e) {

    }

    private void btnSofortBuchenItemStateChanged(ItemEvent e) {
        Tools.log(txtLog, btnSofortBuchen.isSelected() ? "Verpackte Ware wird direkt eingebucht, sobald alle Informationen vorliegen." : "Ware wird nur auf Knopfdruck eingebucht.");
        txtSearch.requestFocus();
    }

    private void btn20ActionPerformed(ActionEvent e) {
        txtFaktor.setText("20");
    }

    private void btn12ActionPerformed(ActionEvent e) {
        txtFaktor.setText("12");
    }

    private void btn10ActionPerformed(ActionEvent e) {
        txtFaktor.setText("10");
    }

    private void btn6ActionPerformed(ActionEvent e) {
        txtFaktor.setText("6");
    }

    private void btn5ActionPerformed(ActionEvent e) {
        txtFaktor.setText("5");
    }

    private void txtMengeCaretUpdate(CaretEvent e) {
        try {
            BigDecimal bd = new BigDecimal(txtMenge.getText().replaceAll(",", "\\."));
            Main.logger.debug("Double: " + bd);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                aktuelleBuchung.setMenge(BigDecimal.ZERO);
            } else {
                if (aktuelleBuchung.getProdukt() != null) {
                    if (aktuelleBuchung.getProdukt().getGtin() != null) {
                        // Es soll mehr ausgebucht werden als die Packung hergibt. Dann
                        // wird die Menge auf die Packungsgröße zurechtgestutzt.
                        if (aktuelleBuchung.getProdukt().getPackGroesse().compareTo(bd) < 0) {
                            aktuelleBuchung.setMenge(aktuelleBuchung.getProdukt().getPackGroesse());
                        } else {
                            // Ansonsten wird der Wert gesetzt.
                            aktuelleBuchung.setMenge(bd);
                        }
                    } else {
                        aktuelleBuchung.setMenge(bd);
                    }
                }

                if (txtFaktor.isEnabled()) {
                    txtFaktor.setText("1");
                }
            }
        } catch (NumberFormatException e1) {
            aktuelleBuchung.setMenge(BigDecimal.ZERO);
        }

        setEinbuchenButton();
    }

    private void splitLieferantComponentResized(ComponentEvent e) {
        Tools.showSide(splitLieferant, splitLiefADouble, 0);
    }

    private void splitLagerComponentResized(ComponentEvent e) {
        Tools.showSide(splitLager, splitLagerADouble, 0);
    }

    private void btnReaktivierenActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            VorratTools.reaktivieren(em, vorrat);
            em.getTransaction().commit();
            long id = vorrat.getId();
            setPanelMode(MODE_WARENEINGANG, 700);
            Tools.fadeinout(lblProdukt, "Vorrat ["+id+"] wieder eingebucht.");
        } catch (Exception e1) {
            new DlgException(e1);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitMain = new JSplitPane();
        pnlUpper = new JPanel();
        pnlUpperLeft = new JPanel();
        txtSearch = new JTextField();
        scrollPane1 = new JScrollPane();
        listProdukte = new JList();
        lblProdInfo = new JLabel();
        btnNewProdukt = new JButton();
        splitUpperRight = new JSplitPane();
        pnlUpperRight = new JPanel();
        lblProdukt = new JLabel();
        panel3 = new JPanel();
        txtFaktor = new JTextField();
        btn5 = new JButton();
        btn6 = new JButton();
        btn10 = new JButton();
        btn12 = new JButton();
        btn20 = new JButton();
        label2 = new JLabel();
        panel10 = new JPanel();
        txtMenge = new JTextField();
        lblEinheit = new JLabel();
        splitLieferant = new JSplitPane();
        panel4 = new JPanel();
        cmbLieferant = new JComboBox();
        btnAddLieferant = new JButton();
        panel5 = new JPanel();
        txtNewLieferant = new JTextField();
        btnApplyLieferant = new JButton();
        btnCancelLieferant = new JButton();
        splitLager = new JSplitPane();
        panel6 = new JPanel();
        cmbLager = new JComboBox();
        btnAddLager = new JButton();
        panel7 = new JPanel();
        txtNewLager = new JTextField();
        btnApplyLager = new JButton();
        btnCancelLager = new JButton();
        scrollPane2 = new JScrollPane();
        txtLog = new JTextArea();
        panel11 = new JPanel();
        btnEtiketten1 = new JToggleButton();
        btnEtiketten2 = new JToggleButton();
        btnPagePrinter = new JToggleButton();
        btnNoPrinter = new JToggleButton();
        btnEinbuchen = new JButton();
        btnSofortBuchen = new JToggleButton();
        panel14 = new JPanel();
        label1 = new JLabel();
        lblVorrat = new JLabel();
        label4 = new JLabel();
        label3 = new JLabel();
        label5 = new JLabel();
        lblEinDatum = new JLabel();
        lblEinMA = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        lblAusDatum = new JLabel();
        lblAusMA = new JLabel();
        btnReaktivieren = new JButton();
        pnlLower = new JPanel();
        panel1 = new JPanel();
        lbl1 = new JLabel();
        txtProdBezeichnung = new JTextField();
        cmbEinheit = new JComboBox();
        cmbLagerart = new JComboBox();
        panel2 = new JPanel();
        splitNeuProd = new JSplitPane();
        pnlVerpackt = new JPanel();
        btnVerpackteWare = new JToggleButton();
        lblStrichcode = new JLabel();
        txtGTIN = new JTextField();
        txtPackGroesse = new JTextField();
        lbl4 = new JLabel();
        lblEinheit1 = new JLabel();
        pnlProduktLower = new JPanel();
        splitStoffart = new JSplitPane();
        panel12 = new JPanel();
        cmbStoffart = new JComboBox();
        btnAddStoffart = new JButton();
        panel13 = new JPanel();
        txtNewStoffart = new JTextField();
        btnApplyStoffart = new JButton();
        btnCancelStoffart = new JButton();
        btnAcceptProd = new JButton();
        btnCancelNeuProd = new JButton();
        splitWarengruppe = new JSplitPane();
        panel8 = new JPanel();
        cmbWarengruppe = new JComboBox();
        btnAddWarengruppe = new JButton();
        panel9 = new JPanel();
        txtNewWarengruppe = new JTextField();
        btnApplyWarengruppe = new JButton();
        btnCancelWarengruppe = new JButton();
        lblMessageLower = new JLabel();

        //======== this ========
        setMinimumSize(new Dimension(500, 300));
        setPreferredSize(new Dimension(500, 300));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== splitMain ========
        {
            splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitMain.setDividerLocation(500);
            splitMain.setEnabled(false);
            splitMain.setMinimumSize(new Dimension(500, 400));
            splitMain.setPreferredSize(new Dimension(500, 400));

            //======== pnlUpper ========
            {
                pnlUpper.setLayout(new FormLayout(
                    "default, $lcgap, default:grow",
                    "fill:default:grow"));

                //======== pnlUpperLeft ========
                {
                    pnlUpperLeft.setBorder(new EtchedBorder());
                    pnlUpperLeft.setLayout(new FormLayout(
                        "$lcgap, default, $rgap, $glue",
                        "$rgap, $lgap, fill:default, $lgap, fill:default:grow, $lgap, default, $lgap, fill:default, $lgap, $nlgap"));

                    //---- txtSearch ----
                    txtSearch.setFont(new Font("sansserif", Font.PLAIN, 24));
                    txtSearch.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtSearchFocusGained(e);
                        }
                    });
                    txtSearch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtSearchActionPerformed(e);
                        }
                    });
                    pnlUpperLeft.add(txtSearch, CC.xy(2, 3));

                    //======== scrollPane1 ========
                    {

                        //---- listProdukte ----
                        listProdukte.setFont(new Font("sansserif", Font.PLAIN, 24));
                        listProdukte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        listProdukte.addListSelectionListener(new ListSelectionListener() {
                            @Override
                            public void valueChanged(ListSelectionEvent e) {
                                listProdukteValueChanged(e);
                            }
                        });
                        listProdukte.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                listProdukteKeyPressed(e);
                            }
                        });
                        scrollPane1.setViewportView(listProdukte);
                    }
                    pnlUpperLeft.add(scrollPane1, CC.xy(2, 5));

                    //---- lblProdInfo ----
                    lblProdInfo.setFont(new Font("sansserif", Font.PLAIN, 20));
                    pnlUpperLeft.add(lblProdInfo, CC.xy(2, 7));

                    //---- btnNewProdukt ----
                    btnNewProdukt.setText("Neues Produkt");
                    btnNewProdukt.setFont(new Font("sansserif", Font.PLAIN, 24));
                    btnNewProdukt.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                    btnNewProdukt.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnNewProduktActionPerformed(e);
                        }
                    });
                    pnlUpperLeft.add(btnNewProdukt, CC.xy(2, 9));
                }
                pnlUpper.add(pnlUpperLeft, CC.xy(1, 1));

                //======== splitUpperRight ========
                {
                    splitUpperRight.setDividerSize(0);
                    splitUpperRight.setDividerLocation(500);

                    //======== pnlUpperRight ========
                    {
                        pnlUpperRight.setLayout(new FormLayout(
                            "$rgap, 2*($lcgap, default:grow), $lcgap, default, $lcgap, $rgap",
                            "$rgap, $lgap, 20dlu, 4*($lgap, fill:default), $lgap, fill:default:grow, 2*($lgap, fill:default)"));
                        ((FormLayout)pnlUpperRight.getLayout()).setColumnGroups(new int[][] {{3, 5}});

                        //---- lblProdukt ----
                        lblProdukt.setText(" ");
                        lblProdukt.setFont(new Font("sansserif", Font.BOLD, 24));
                        lblProdukt.setHorizontalAlignment(SwingConstants.CENTER);
                        lblProdukt.setBackground(new Color(204, 204, 255));
                        lblProdukt.setOpaque(true);
                        pnlUpperRight.add(lblProdukt, CC.xywh(3, 3, 5, 1));

                        //======== panel3 ========
                        {
                            panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                            //---- txtFaktor ----
                            txtFaktor.setFont(new Font("sansserif", Font.PLAIN, 24));
                            txtFaktor.setToolTipText("Anzahl bei verpackter Ware");
                            txtFaktor.addFocusListener(new FocusAdapter() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                    txtFaktorFocusGained(e);
                                }
                                @Override
                                public void focusLost(FocusEvent e) {
                                    txtFaktorFocusLost(e);
                                }
                            });
                            txtFaktor.addPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent e) {
                                    txtFaktorPropertyChange(e);
                                }
                            });
                            txtFaktor.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    txtFaktorActionPerformed(e);
                                }
                            });
                            txtFaktor.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtFaktorCaretUpdate(e);
                                }
                            });
                            panel3.add(txtFaktor);

                            //---- btn5 ----
                            btn5.setText("5");
                            btn5.setFont(new Font("sansserif", Font.BOLD, 20));
                            btn5.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btn5ActionPerformed(e);
                                }
                            });
                            panel3.add(btn5);

                            //---- btn6 ----
                            btn6.setText("6");
                            btn6.setFont(new Font("sansserif", Font.BOLD, 20));
                            btn6.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btn6ActionPerformed(e);
                                }
                            });
                            panel3.add(btn6);

                            //---- btn10 ----
                            btn10.setText("10");
                            btn10.setFont(new Font("sansserif", Font.BOLD, 20));
                            btn10.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btn10ActionPerformed(e);
                                }
                            });
                            panel3.add(btn10);

                            //---- btn12 ----
                            btn12.setText("12");
                            btn12.setFont(new Font("sansserif", Font.BOLD, 20));
                            btn12.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btn12ActionPerformed(e);
                                }
                            });
                            panel3.add(btn12);

                            //---- btn20 ----
                            btn20.setText("20");
                            btn20.setFont(new Font("sansserif", Font.BOLD, 20));
                            btn20.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btn20ActionPerformed(e);
                                }
                            });
                            panel3.add(btn20);

                            //---- label2 ----
                            label2.setText("x");
                            label2.setFont(new Font("sansserif", Font.PLAIN, 24));
                            panel3.add(label2);
                        }
                        pnlUpperRight.add(panel3, CC.xywh(3, 5, 5, 1));

                        //======== panel10 ========
                        {
                            panel10.setLayout(new BoxLayout(panel10, BoxLayout.X_AXIS));

                            //---- txtMenge ----
                            txtMenge.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
                            txtMenge.setToolTipText("Menge bei looser Ware");
                            txtMenge.addFocusListener(new FocusAdapter() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                    txtMengeFocusGained(e);
                                }
                                @Override
                                public void focusLost(FocusEvent e) {
                                    txtMengeFocusLost(e);
                                }
                            });
                            txtMenge.addPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent e) {
                                    txtMengePropertyChange(e);
                                }
                            });
                            txtMenge.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    txtMengeActionPerformed(e);
                                }
                            });
                            txtMenge.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtMengeCaretUpdate(e);
                                }
                            });
                            panel10.add(txtMenge);

                            //---- lblEinheit ----
                            lblEinheit.setText(" ");
                            lblEinheit.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
                            panel10.add(lblEinheit);
                        }
                        pnlUpperRight.add(panel10, CC.xywh(3, 7, 5, 1));

                        //======== splitLieferant ========
                        {
                            splitLieferant.setEnabled(false);
                            splitLieferant.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    splitLieferantComponentResized(e);
                                }
                            });

                            //======== panel4 ========
                            {
                                panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));

                                //---- cmbLieferant ----
                                cmbLieferant.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                cmbLieferant.setModel(new DefaultComboBoxModel(new String[] {
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 3",
                                    "item 1",
                                    "item 2",
                                    "item 1",
                                    "item 1",
                                    "item 2",
                                    "item 3"
                                }));
                                cmbLieferant.addFocusListener(new FocusAdapter() {
                                    @Override
                                    public void focusGained(FocusEvent e) {
                                        cmbLieferantFocusGained(e);
                                    }
                                });
                                cmbLieferant.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        cmbLieferantItemStateChanged(e);
                                    }
                                });
                                panel4.add(cmbLieferant);

                                //---- btnAddLieferant ----
                                btnAddLieferant.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                btnAddLieferant.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                                btnAddLieferant.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnAddLieferantActionPerformed(e);
                                    }
                                });
                                panel4.add(btnAddLieferant);
                            }
                            splitLieferant.setLeftComponent(panel4);

                            //======== panel5 ========
                            {
                                panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));

                                //---- txtNewLieferant ----
                                txtNewLieferant.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                txtNewLieferant.setText(" ");
                                txtNewLieferant.addCaretListener(new CaretListener() {
                                    @Override
                                    public void caretUpdate(CaretEvent e) {
                                        txtNewLieferantCaretUpdate(e);
                                    }
                                });
                                txtNewLieferant.addFocusListener(new FocusAdapter() {
                                    @Override
                                    public void focusGained(FocusEvent e) {
                                        txtNewLieferantFocusGained(e);
                                    }
                                });
                                txtNewLieferant.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnApplyLieferantActionPerformed(e);
                                    }
                                });
                                panel5.add(txtNewLieferant);

                                //---- btnApplyLieferant ----
                                btnApplyLieferant.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                btnApplyLieferant.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                                btnApplyLieferant.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnApplyLieferantActionPerformed(e);
                                    }
                                });
                                panel5.add(btnApplyLieferant);

                                //---- btnCancelLieferant ----
                                btnCancelLieferant.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                btnCancelLieferant.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                                btnCancelLieferant.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnCancelLieferantActionPerformed(e);
                                    }
                                });
                                panel5.add(btnCancelLieferant);
                            }
                            splitLieferant.setRightComponent(panel5);
                        }
                        pnlUpperRight.add(splitLieferant, CC.xywh(3, 9, 5, 1));

                        //======== splitLager ========
                        {
                            splitLager.setEnabled(false);
                            splitLager.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    splitLagerComponentResized(e);
                                }
                            });

                            //======== panel6 ========
                            {
                                panel6.setLayout(new BoxLayout(panel6, BoxLayout.X_AXIS));

                                //---- cmbLager ----
                                cmbLager.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                cmbLager.setModel(new DefaultComboBoxModel(new String[] {
                                    "item 1",
                                    "item 2",
                                    "item 3"
                                }));
                                cmbLager.addFocusListener(new FocusAdapter() {
                                    @Override
                                    public void focusGained(FocusEvent e) {
                                        cmbLagerFocusGained(e);
                                    }
                                });
                                cmbLager.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        cmbLagerItemStateChanged(e);
                                    }
                                });
                                panel6.add(cmbLager);

                                //---- btnAddLager ----
                                btnAddLager.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                btnAddLager.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                                btnAddLager.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnAddLagerActionPerformed(e);
                                    }
                                });
                                panel6.add(btnAddLager);
                            }
                            splitLager.setLeftComponent(panel6);

                            //======== panel7 ========
                            {
                                panel7.setLayout(new BoxLayout(panel7, BoxLayout.X_AXIS));

                                //---- txtNewLager ----
                                txtNewLager.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                txtNewLager.setText(" ");
                                txtNewLager.addCaretListener(new CaretListener() {
                                    @Override
                                    public void caretUpdate(CaretEvent e) {
                                        txtNewLagerCaretUpdate(e);
                                    }
                                });
                                txtNewLager.addFocusListener(new FocusAdapter() {
                                    @Override
                                    public void focusGained(FocusEvent e) {
                                        txtNewLagerFocusGained(e);
                                    }
                                });
                                txtNewLager.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnApplyLagerActionPerformed(e);
                                    }
                                });
                                panel7.add(txtNewLager);

                                //---- btnApplyLager ----
                                btnApplyLager.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                btnApplyLager.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                                btnApplyLager.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnApplyLagerActionPerformed(e);
                                    }
                                });
                                panel7.add(btnApplyLager);

                                //---- btnCancelLager ----
                                btnCancelLager.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                btnCancelLager.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                                btnCancelLager.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnCancelLagerActionPerformed(e);
                                    }
                                });
                                panel7.add(btnCancelLager);
                            }
                            splitLager.setRightComponent(panel7);
                        }
                        pnlUpperRight.add(splitLager, CC.xywh(3, 11, 5, 1));

                        //======== scrollPane2 ========
                        {

                            //---- txtLog ----
                            txtLog.setForeground(Color.black);
                            txtLog.setFont(new Font("sansserif", Font.PLAIN, 18));
                            txtLog.setEditable(false);
                            txtLog.setBackground(Color.lightGray);
                            txtLog.setPreferredSize(new Dimension(12, 70));
                            scrollPane2.setViewportView(txtLog);
                        }
                        pnlUpperRight.add(scrollPane2, CC.xywh(3, 13, 5, 1));

                        //======== panel11 ========
                        {
                            panel11.setLayout(new GridLayout());

                            //---- btnEtiketten1 ----
                            btnEtiketten1.setText("1");
                            btnEtiketten1.setFont(new Font("sansserif", Font.BOLD, 18));
                            btnEtiketten1.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/labelprinter2.png")));
                            btnEtiketten1.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnEtiketten1ItemStateChanged(e);
                                }
                            });
                            panel11.add(btnEtiketten1);

                            //---- btnEtiketten2 ----
                            btnEtiketten2.setText("2");
                            btnEtiketten2.setFont(new Font("sansserif", Font.BOLD, 18));
                            btnEtiketten2.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/labelprinter2.png")));
                            btnEtiketten2.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnEtiketten2ItemStateChanged(e);
                                }
                            });
                            panel11.add(btnEtiketten2);

                            //---- btnPagePrinter ----
                            btnPagePrinter.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/printer.png")));
                            btnPagePrinter.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnPagePrinterItemStateChanged(e);
                                }
                            });
                            panel11.add(btnPagePrinter);

                            //---- btnNoPrinter ----
                            btnNoPrinter.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/noprinter.png")));
                            btnNoPrinter.setSelected(true);
                            btnNoPrinter.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnNoPrinterItemStateChanged(e);
                                }
                            });
                            panel11.add(btnNoPrinter);
                        }
                        pnlUpperRight.add(panel11, CC.xywh(3, 15, 5, 1));

                        //---- btnEinbuchen ----
                        btnEinbuchen.setText("Einbuchen");
                        btnEinbuchen.setFont(new Font("sansserif", Font.PLAIN, 24));
                        btnEinbuchen.setBackground(new Color(255, 102, 102));
                        btnEinbuchen.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnEinbuchenActionPerformed(e);
                            }
                        });
                        pnlUpperRight.add(btnEinbuchen, CC.xywh(3, 17, 3, 1));

                        //---- btnSofortBuchen ----
                        btnSofortBuchen.setToolTipText("Sofort Buchen wenn m\u00f6glich");
                        btnSofortBuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/agt_member.png")));
                        btnSofortBuchen.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnSofortBuchenItemStateChanged(e);
                            }
                        });
                        pnlUpperRight.add(btnSofortBuchen, CC.xy(7, 17));
                    }
                    splitUpperRight.setLeftComponent(pnlUpperRight);

                    //======== panel14 ========
                    {
                        panel14.setLayout(new FormLayout(
                            "default, 2*($lcgap, default:grow), $lcgap, default",
                            "$rgap, 2*($lgap, default), 12dlu, 2*(default, $lgap), default, 12dlu, 3*(default, $lgap), fill:default:grow, $lgap, default"));

                        //---- label1 ----
                        label1.setText("Ausgebuchter Vorrat");
                        label1.setFont(new Font("sansserif", Font.BOLD, 24));
                        label1.setHorizontalAlignment(SwingConstants.CENTER);
                        label1.setBackground(Color.orange);
                        label1.setOpaque(true);
                        panel14.add(label1, CC.xywh(3, 3, 3, 1));

                        //---- lblVorrat ----
                        lblVorrat.setText("text");
                        lblVorrat.setFont(new Font("sansserif", Font.PLAIN, 24));
                        lblVorrat.setBackground(new Color(204, 204, 255));
                        lblVorrat.setOpaque(true);
                        lblVorrat.setHorizontalAlignment(SwingConstants.CENTER);
                        panel14.add(lblVorrat, CC.xywh(3, 5, 3, 1));

                        //---- label4 ----
                        label4.setText("Eingebucht:");
                        label4.setBackground(Color.pink);
                        label4.setOpaque(true);
                        label4.setFont(new Font("sansserif", Font.PLAIN, 24));
                        panel14.add(label4, CC.xywh(3, 7, 3, 1));

                        //---- label3 ----
                        label3.setText("Am / um");
                        label3.setFont(new Font("sansserif", Font.PLAIN, 24));
                        panel14.add(label3, CC.xy(3, 9));

                        //---- label5 ----
                        label5.setText("Von");
                        label5.setFont(new Font("sansserif", Font.PLAIN, 24));
                        panel14.add(label5, CC.xy(5, 9));

                        //---- lblEinDatum ----
                        lblEinDatum.setText("text");
                        lblEinDatum.setFont(new Font("sansserif", Font.BOLD, 24));
                        panel14.add(lblEinDatum, CC.xy(3, 11));

                        //---- lblEinMA ----
                        lblEinMA.setText("text");
                        lblEinMA.setFont(new Font("sansserif", Font.BOLD, 24));
                        panel14.add(lblEinMA, CC.xy(5, 11));

                        //---- label6 ----
                        label6.setText("Ausgebucht:");
                        label6.setBackground(Color.pink);
                        label6.setOpaque(true);
                        label6.setFont(new Font("sansserif", Font.PLAIN, 24));
                        panel14.add(label6, CC.xywh(3, 13, 3, 1));

                        //---- label7 ----
                        label7.setText("Am / um");
                        label7.setFont(new Font("sansserif", Font.PLAIN, 24));
                        panel14.add(label7, CC.xy(3, 15));

                        //---- label8 ----
                        label8.setText("Von");
                        label8.setFont(new Font("sansserif", Font.PLAIN, 24));
                        panel14.add(label8, CC.xy(5, 15));

                        //---- lblAusDatum ----
                        lblAusDatum.setText("text");
                        lblAusDatum.setFont(new Font("sansserif", Font.BOLD, 24));
                        panel14.add(lblAusDatum, CC.xy(3, 17));

                        //---- lblAusMA ----
                        lblAusMA.setText("text");
                        lblAusMA.setFont(new Font("sansserif", Font.BOLD, 24));
                        panel14.add(lblAusMA, CC.xy(5, 17));

                        //---- btnReaktivieren ----
                        btnReaktivieren.setText("Wieder einbuchen");
                        btnReaktivieren.setFont(new Font("sansserif", Font.PLAIN, 24));
                        btnReaktivieren.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnReaktivierenActionPerformed(e);
                            }
                        });
                        panel14.add(btnReaktivieren, CC.xywh(3, 21, 3, 1));
                    }
                    splitUpperRight.setRightComponent(panel14);
                }
                pnlUpper.add(splitUpperRight, CC.xy(3, 1));
            }
            splitMain.setTopComponent(pnlUpper);

            //======== pnlLower ========
            {
                pnlLower.setLayout(new BoxLayout(pnlLower, BoxLayout.PAGE_AXIS));

                //======== panel1 ========
                {

                    //---- lbl1 ----
                    lbl1.setLabelFor(txtProdBezeichnung);
                    lbl1.setText("Produkt");
                    lbl1.setFont(new Font("sansserif", Font.PLAIN, 24));

                    //---- txtProdBezeichnung ----
                    txtProdBezeichnung.setFont(new Font("sansserif", Font.PLAIN, 24));
                    txtProdBezeichnung.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtProdBezeichnungFocusGained(e);
                        }
                    });
                    txtProdBezeichnung.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            txtProdBezeichnungCaretUpdate(e);
                        }
                    });

                    //---- cmbEinheit ----
                    cmbEinheit.setFont(new Font("sansserif", Font.PLAIN, 24));
                    cmbEinheit.setPreferredSize(new Dimension(70, 45));
                    cmbEinheit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbEinheitItemStateChanged(e);
                        }
                    });

                    //---- cmbLagerart ----
                    cmbLagerart.setFont(new Font("sansserif", Font.PLAIN, 24));
                    cmbLagerart.setPreferredSize(new Dimension(70, 45));
                    cmbLagerart.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbLagerartItemStateChanged(e);
                        }
                    });

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addGroup(GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                                        .addComponent(cmbEinheit, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmbLagerart, GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
                                    .addGroup(GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                                        .addComponent(lbl1)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProdBezeichnung, GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE)))
                                .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(lbl1))
                                    .addComponent(txtProdBezeichnung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(cmbLagerart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbEinheit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                    );
                }
                pnlLower.add(panel1);

                //======== panel2 ========
                {
                    panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                    //======== splitNeuProd ========
                    {
                        splitNeuProd.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        splitNeuProd.setDividerLocation(250);
                        splitNeuProd.setMinimumSize(new Dimension(358, 100));
                        splitNeuProd.setPreferredSize(new Dimension(954, 100));
                        splitNeuProd.setDividerSize(0);
                        splitNeuProd.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                splitNeuProdComponentResized(e);
                            }
                        });

                        //======== pnlVerpackt ========
                        {
                            pnlVerpackt.setMinimumSize(new Dimension(230, 50));
                            pnlVerpackt.setPreferredSize(new Dimension(952, 50));

                            //---- btnVerpackteWare ----
                            btnVerpackteWare.setText("Verpackte Ware");
                            btnVerpackteWare.setFont(new Font("sansserif", Font.PLAIN, 24));
                            btnVerpackteWare.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ark.png")));
                            btnVerpackteWare.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    btnVerpackteWareItemStateChanged(e);
                                }
                            });

                            //---- lblStrichcode ----
                            lblStrichcode.setLabelFor(txtGTIN);
                            lblStrichcode.setText("Strichcode");
                            lblStrichcode.setFont(new Font("sansserif", Font.PLAIN, 24));

                            //---- txtGTIN ----
                            txtGTIN.setFont(new Font("sansserif", Font.PLAIN, 24));
                            txtGTIN.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    txtGTINActionPerformed(e);
                                }
                            });
                            txtGTIN.addFocusListener(new FocusAdapter() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                    txtGTINFocusGained(e);
                                }
                                @Override
                                public void focusLost(FocusEvent e) {
                                    txtGTINFocusLost(e);
                                }
                            });
                            txtGTIN.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtGTINCaretUpdate(e);
                                }
                            });

                            //---- txtPackGroesse ----
                            txtPackGroesse.setFont(new Font("sansserif", Font.PLAIN, 24));
                            txtPackGroesse.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtPackGroesseCaretUpdate(e);
                                }
                            });
                            txtPackGroesse.addFocusListener(new FocusAdapter() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                    txtPackGroesseFocusGained(e);
                                }
                            });

                            //---- lbl4 ----
                            lbl4.setLabelFor(txtPackGroesse);
                            lbl4.setText("Packungsgr\u00f6\u00dfe");
                            lbl4.setFont(new Font("sansserif", Font.PLAIN, 24));

                            //---- lblEinheit1 ----
                            lblEinheit1.setText("text");
                            lblEinheit1.setFont(new Font("sansserif", Font.PLAIN, 24));

                            GroupLayout pnlVerpacktLayout = new GroupLayout(pnlVerpackt);
                            pnlVerpackt.setLayout(pnlVerpacktLayout);
                            pnlVerpacktLayout.setHorizontalGroup(
                                pnlVerpacktLayout.createParallelGroup()
                                    .addGroup(pnlVerpacktLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(pnlVerpacktLayout.createParallelGroup()
                                            .addGroup(pnlVerpacktLayout.createSequentialGroup()
                                                .addComponent(btnVerpackteWare, GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
                                                .addContainerGap())
                                            .addGroup(pnlVerpacktLayout.createSequentialGroup()
                                                .addGroup(pnlVerpacktLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(lblStrichcode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(lbl4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(pnlVerpacktLayout.createParallelGroup()
                                                    .addGroup(pnlVerpacktLayout.createSequentialGroup()
                                                        .addComponent(txtPackGroesse, GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(lblEinheit1, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(txtGTIN, GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE))
                                                .addGap(12, 12, 12))))
                            );
                            pnlVerpacktLayout.setVerticalGroup(
                                pnlVerpacktLayout.createParallelGroup()
                                    .addGroup(pnlVerpacktLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(btnVerpackteWare)
                                        .addGap(18, 18, 18)
                                        .addGroup(pnlVerpacktLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblStrichcode, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtGTIN, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(20, 20, 20)
                                        .addGroup(pnlVerpacktLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lbl4, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPackGroesse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblEinheit1, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)))
                            );
                        }
                        splitNeuProd.setTopComponent(pnlVerpackt);

                        //======== pnlProduktLower ========
                        {
                            pnlProduktLower.setPreferredSize(new Dimension(952, 182));

                            //======== splitStoffart ========
                            {
                                splitStoffart.setEnabled(false);

                                //======== panel12 ========
                                {
                                    panel12.setLayout(new BoxLayout(panel12, BoxLayout.X_AXIS));

                                    //---- cmbStoffart ----
                                    cmbStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    cmbStoffart.setModel(new DefaultComboBoxModel(new String[] {
                                        "item 1",
                                        "item 2",
                                        "item 3"
                                    }));
                                    cmbStoffart.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent e) {
                                            cmbStoffartItemStateChanged(e);
                                        }
                                    });
                                    panel12.add(cmbStoffart);

                                    //---- btnAddStoffart ----
                                    btnAddStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    btnAddStoffart.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                                    btnAddStoffart.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnAddStoffartActionPerformed(e);
                                        }
                                    });
                                    panel12.add(btnAddStoffart);
                                }
                                splitStoffart.setLeftComponent(panel12);

                                //======== panel13 ========
                                {
                                    panel13.setLayout(new BoxLayout(panel13, BoxLayout.X_AXIS));

                                    //---- txtNewStoffart ----
                                    txtNewStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    txtNewStoffart.setText(" ");
                                    txtNewStoffart.addFocusListener(new FocusAdapter() {
                                        @Override
                                        public void focusGained(FocusEvent e) {
                                            txtNewStoffartFocusGained(e);
                                        }
                                    });
                                    panel13.add(txtNewStoffart);

                                    //---- btnApplyStoffart ----
                                    btnApplyStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    btnApplyStoffart.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                                    btnApplyStoffart.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnApplyStoffartActionPerformed(e);
                                        }
                                    });
                                    panel13.add(btnApplyStoffart);

                                    //---- btnCancelStoffart ----
                                    btnCancelStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    btnCancelStoffart.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                                    btnCancelStoffart.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnCancelStoffartActionPerformed(e);
                                        }
                                    });
                                    panel13.add(btnCancelStoffart);
                                }
                                splitStoffart.setRightComponent(panel13);
                            }

                            //---- btnAcceptProd ----
                            btnAcceptProd.setText("Produkt hinzuf\u00fcgen");
                            btnAcceptProd.setFont(new Font("sansserif", Font.PLAIN, 24));
                            btnAcceptProd.setBackground(new Color(255, 102, 102));
                            btnAcceptProd.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnAcceptProdActionPerformed(e);
                                }
                            });

                            //---- btnCancelNeuProd ----
                            btnCancelNeuProd.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                            btnCancelNeuProd.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnCancelNeuProdActionPerformed(e);
                                }
                            });

                            //======== splitWarengruppe ========
                            {
                                splitWarengruppe.setEnabled(false);

                                //======== panel8 ========
                                {
                                    panel8.setLayout(new BoxLayout(panel8, BoxLayout.X_AXIS));

                                    //---- cmbWarengruppe ----
                                    cmbWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    cmbWarengruppe.setModel(new DefaultComboBoxModel(new String[] {
                                        "item 1",
                                        "item 2",
                                        "item 3"
                                    }));
                                    cmbWarengruppe.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent e) {
                                            cmbWarengruppeItemStateChanged(e);
                                        }
                                    });
                                    panel8.add(cmbWarengruppe);

                                    //---- btnAddWarengruppe ----
                                    btnAddWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    btnAddWarengruppe.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                                    btnAddWarengruppe.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnAddWarengruppeActionPerformed(e);
                                        }
                                    });
                                    panel8.add(btnAddWarengruppe);
                                }
                                splitWarengruppe.setLeftComponent(panel8);

                                //======== panel9 ========
                                {
                                    panel9.setLayout(new BoxLayout(panel9, BoxLayout.X_AXIS));

                                    //---- txtNewWarengruppe ----
                                    txtNewWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    txtNewWarengruppe.setText(" ");
                                    txtNewWarengruppe.addFocusListener(new FocusAdapter() {
                                        @Override
                                        public void focusGained(FocusEvent e) {
                                            txtNewWarengruppeFocusGained(e);
                                        }
                                    });
                                    panel9.add(txtNewWarengruppe);

                                    //---- btnApplyWarengruppe ----
                                    btnApplyWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    btnApplyWarengruppe.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                                    btnApplyWarengruppe.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnApplyWarengruppeActionPerformed(e);
                                        }
                                    });
                                    panel9.add(btnApplyWarengruppe);

                                    //---- btnCancelWarengruppe ----
                                    btnCancelWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                                    btnCancelWarengruppe.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                                    btnCancelWarengruppe.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnCancelWarengruppeActionPerformed(e);
                                        }
                                    });
                                    panel9.add(btnCancelWarengruppe);
                                }
                                splitWarengruppe.setRightComponent(panel9);
                            }

                            //---- lblMessageLower ----
                            lblMessageLower.setFont(new Font("sansserif", Font.BOLD, 24));
                            lblMessageLower.setHorizontalAlignment(SwingConstants.CENTER);

                            GroupLayout pnlProduktLowerLayout = new GroupLayout(pnlProduktLower);
                            pnlProduktLower.setLayout(pnlProduktLowerLayout);
                            pnlProduktLowerLayout.setHorizontalGroup(
                                pnlProduktLowerLayout.createParallelGroup()
                                    .addGroup(pnlProduktLowerLayout.createSequentialGroup()
                                        .addGroup(pnlProduktLowerLayout.createParallelGroup()
                                            .addGroup(pnlProduktLowerLayout.createSequentialGroup()
                                                .addComponent(btnAcceptProd, GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancelNeuProd, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(pnlProduktLowerLayout.createSequentialGroup()
                                                .addGap(2, 2, 2)
                                                .addGroup(pnlProduktLowerLayout.createParallelGroup()
                                                    .addComponent(splitWarengruppe, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1017, Short.MAX_VALUE)
                                                    .addComponent(splitStoffart, GroupLayout.DEFAULT_SIZE, 1017, Short.MAX_VALUE)))
                                            .addGroup(pnlProduktLowerLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(lblMessageLower, GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)))
                                        .addContainerGap())
                            );
                            pnlProduktLowerLayout.setVerticalGroup(
                                pnlProduktLowerLayout.createParallelGroup()
                                    .addGroup(pnlProduktLowerLayout.createSequentialGroup()
                                        .addComponent(splitStoffart, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(splitWarengruppe, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblMessageLower, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(pnlProduktLowerLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnCancelNeuProd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnAcceptProd))
                                        .addContainerGap())
                            );
                            pnlProduktLowerLayout.linkSize(SwingConstants.VERTICAL, new Component[] {btnAcceptProd, btnCancelNeuProd});
                        }
                        splitNeuProd.setBottomComponent(pnlProduktLower);
                    }
                    panel2.add(splitNeuProd);
                }
                pnlLower.add(panel2);
            }
            splitMain.setBottomComponent(pnlLower);
        }
        add(splitMain);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(btnEtiketten1);
        buttonGroup1.add(btnEtiketten2);
        buttonGroup1.add(btnPagePrinter);
        buttonGroup1.add(btnNoPrinter);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitMain;
    private JPanel pnlUpper;
    private JPanel pnlUpperLeft;
    private JTextField txtSearch;
    private JScrollPane scrollPane1;
    private JList listProdukte;
    private JLabel lblProdInfo;
    private JButton btnNewProdukt;
    private JSplitPane splitUpperRight;
    private JPanel pnlUpperRight;
    private JLabel lblProdukt;
    private JPanel panel3;
    private JTextField txtFaktor;
    private JButton btn5;
    private JButton btn6;
    private JButton btn10;
    private JButton btn12;
    private JButton btn20;
    private JLabel label2;
    private JPanel panel10;
    private JTextField txtMenge;
    private JLabel lblEinheit;
    private JSplitPane splitLieferant;
    private JPanel panel4;
    private JComboBox cmbLieferant;
    private JButton btnAddLieferant;
    private JPanel panel5;
    private JTextField txtNewLieferant;
    private JButton btnApplyLieferant;
    private JButton btnCancelLieferant;
    private JSplitPane splitLager;
    private JPanel panel6;
    private JComboBox cmbLager;
    private JButton btnAddLager;
    private JPanel panel7;
    private JTextField txtNewLager;
    private JButton btnApplyLager;
    private JButton btnCancelLager;
    private JScrollPane scrollPane2;
    private JTextArea txtLog;
    private JPanel panel11;
    private JToggleButton btnEtiketten1;
    private JToggleButton btnEtiketten2;
    private JToggleButton btnPagePrinter;
    private JToggleButton btnNoPrinter;
    private JButton btnEinbuchen;
    private JToggleButton btnSofortBuchen;
    private JPanel panel14;
    private JLabel label1;
    private JLabel lblVorrat;
    private JLabel label4;
    private JLabel label3;
    private JLabel label5;
    private JLabel lblEinDatum;
    private JLabel lblEinMA;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel lblAusDatum;
    private JLabel lblAusMA;
    private JButton btnReaktivieren;
    private JPanel pnlLower;
    private JPanel panel1;
    private JLabel lbl1;
    private JTextField txtProdBezeichnung;
    private JComboBox cmbEinheit;
    private JComboBox cmbLagerart;
    private JPanel panel2;
    private JSplitPane splitNeuProd;
    private JPanel pnlVerpackt;
    private JToggleButton btnVerpackteWare;
    private JLabel lblStrichcode;
    private JTextField txtGTIN;
    private JTextField txtPackGroesse;
    private JLabel lbl4;
    private JLabel lblEinheit1;
    private JPanel pnlProduktLower;
    private JSplitPane splitStoffart;
    private JPanel panel12;
    private JComboBox cmbStoffart;
    private JButton btnAddStoffart;
    private JPanel panel13;
    private JTextField txtNewStoffart;
    private JButton btnApplyStoffart;
    private JButton btnCancelStoffart;
    private JButton btnAcceptProd;
    private JButton btnCancelNeuProd;
    private JSplitPane splitWarengruppe;
    private JPanel panel8;
    private JComboBox cmbWarengruppe;
    private JButton btnAddWarengruppe;
    private JPanel panel9;
    private JTextField txtNewWarengruppe;
    private JButton btnApplyWarengruppe;
    private JButton btnCancelWarengruppe;
    private JLabel lblMessageLower;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    // Vorbereitungen, die ich neben dem Designer noch treffen muss.
    private void myInitComponents() {
        aktuelleBuchung = new Buchung();

        df = DateFormat.getDateInstance();
        tf = DateFormat.getTimeInstance();

        listProdukte.setModel(Tools.newListModel(null));
        cmbLieferant.setModel(Tools.newComboboxModel("Lieferanten.findAllSorted"));
        cmbLager.setModel(tools.Tools.newComboboxModel("Lager.findAllSorted"));
        cmbLager.setSelectedIndex(Integer.parseInt(Main.getProps().getProperty("touch1lager")));
        cmbLieferant.setSelectedIndex(Integer.parseInt(Main.getProps().getProperty("touch1lieferant")));

        splitLagerADouble = 1.0d;
        splitLiefADouble = 1.0d;

        setPanelMode(MODE_WARENEINGANG, 0);

        if (!Main.getProps().containsKey("touch1printer")) {
            Main.getProps().put("touch1printer", "3");
        }

        btnEtiketten1.setSelected(Main.getProps().getProperty("touch1printer").equals("0"));
        btnEtiketten2.setSelected(Main.getProps().getProperty("touch1printer").equals("1"));
        btnPagePrinter.setSelected(Main.getProps().getProperty("touch1printer").equals("2"));
        btnNoPrinter.setSelected(Main.getProps().getProperty("touch1printer").equals("3"));

    }

//    private void setPanelModel(int mode){
//
//    }

    // Eigene Hilfsmethoden
    private void setPanelMode(int mode, int speed) {
        boolean refresh = currentMode == mode;
        currentMode = mode;
        warengruppeEdit = false;
        stoffartEdit = false;
        switch (mode) {
            case MODE_WARENEINGANG: {

                Tools.showSide(splitMain, Tools.LEFT_UPPER_SIDE, speed);
                Tools.showSide(splitUpperRight, Tools.LEFT_UPPER_SIDE, speed);

                if (!refresh) {

                    vorrat = null;

                    lblVorrat.setText("");
                    lblEinMA.setText("");
                    lblAusMA.setText("");
                    lblEinDatum.setText("");
                    lblAusDatum.setText("");

                    btnNewProdukt.setEnabled(true);
                    btnVerpackteWare.setSelected(false);
                    txtSearch.requestFocus();
                }


//        splitNeuProd.setDividerLocation(0);
                // Mit Produkteingabe 180
                // ohne 55
                //currentMode = MODE_WARENEINGANG;
                break;
            }
            case MODE_EDIT_PRODUCT: {
                // 1. splitMain so setzen, dass nur der obere Teil zu sehen ist.

                splitWarengruppe.setDividerLocation(1.0d);
                splitStoffart.setDividerLocation(1.0d);
                splitNeuProd.setDividerLocation(0.0d);

                Tools.showSide(splitUpperRight, Tools.LEFT_UPPER_SIDE, speed);
                Tools.showSide(splitMain, Tools.RIGHT_LOWER_SIDE, speed);




                if (!refresh) {
                    vorrat = null;
                    neuesProdukt = new Produkte();
                    cmbLagerart.setModel(new DefaultComboBoxModel(LagerTools.LAGERART));
                    cmbEinheit.setModel(new DefaultComboBoxModel(ProdukteTools.EINHEIT));

                    cmbLagerart.setSelectedIndex(Integer.parseInt(Main.getProps().getProperty("touch1lagerart")));
                    cmbEinheit.setSelectedIndex(Integer.parseInt(Main.getProps().getProperty("touch1einheit")));
                    lblEinheit1.setText(cmbEinheit.getSelectedItem().toString());
                    //btnPrinter.setSelected(Main.getProps().getProperty("touch1printlabel").equalsIgnoreCase("true"));

                    neuesProdukt.setLagerart((short) cmbLagerart.getSelectedIndex());
                    neuesProdukt.setEinheit((short) cmbEinheit.getSelectedIndex());

                    setWarengruppeEnabled(false);
                    loadWarengruppe();
                    StoffartTools.loadStoffarten(cmbStoffart);
                    cmbStoffart.setSelectedIndex(0);
                    cmbWarengruppe.setSelectedItem(((Stoffart) cmbStoffart.getSelectedItem()).getWarengruppe());

                    neuesProdukt.setStoffart((Stoffart) cmbStoffart.getSelectedItem());

                    neuesProdukt.setPackGroesse(BigDecimal.ZERO);

                    txtGTIN.setText("");
                }
                break;
            }
            case MODE_VORRATREAKTIVIERUNG: {
                Tools.showSide(splitUpperRight, Tools.RIGHT_LOWER_SIDE, speed);
                Tools.showSide(splitMain, Tools.LEFT_UPPER_SIDE, speed);

                if (!refresh) {
                    btnNewProdukt.setEnabled(false);


                    lblVorrat.setText("[" + vorrat.getId() + "] " + vorrat.getProdukt().getBezeichnung());

                    lblEinDatum.setText(df.format(vorrat.getEingang()) + ", " + tf.format(vorrat.getEingang()));
                    lblAusDatum.setText(df.format(vorrat.getAusgang()) + ", " + tf.format(vorrat.getAusgang()));

                    lblEinMA.setText(MitarbeiterTools.getUserString(VorratTools.getEingangMA(vorrat)));
                    lblAusMA.setText(MitarbeiterTools.getUserString(VorratTools.getAusgangMA(vorrat)));
                }
                break;
            }
            default: {

            }
        }
    }

    private void setEinbuchenButton() {
        btnEinbuchen.setBackground(isEinbuchenOK(false) ? new Color(102, 255, 102) : new Color(255, 102, 102));
    }

    private boolean isEinbuchenOK(boolean mitGrund) {
        // Wann kann ich hinzufügen ?
        //
        // Die aktuelle Buchung muss ein Produkt haben
        boolean produktLiegtVor = aktuelleBuchung.getProdukt() != null;
        boolean wareUnverpackt = produktLiegtVor && aktuelleBuchung.getProdukt().getGtin() == null;

        // Es muss ein Faktor bei verpackter Ware vorliegen
        boolean faktorOK = wareUnverpackt || aktuelleBuchung.getFaktor() > 0;

        // oder eine Menge bei unverpackter Ware.
        boolean mengeOK = !wareUnverpackt || aktuelleBuchung.getMenge().compareTo(BigDecimal.ZERO) > 0;

        boolean ok = produktLiegtVor && faktorOK && mengeOK;

        if (!ok && mitGrund) {
            String grund = "Sie können nicht einbuchen weil:\n";
            if (!produktLiegtVor) {
                grund += "Sie noch kein Produkt ausgewählt haben.\n";
            }
            if (!faktorOK) {
                grund += "Sie keine korrekten Buchungsfaktor eingegen haben.\n";
            }
            if (!mengeOK) {
                grund += "Sie keine korrekte Buchungsmenge eingegen haben.\n";
            }
            Tools.log(txtLog, grund);
        }

        // ODER es kann nicht mehr an Menge eingebucht werden als in die Packung reingeht.
        //boolean mengePasstInPackung = produktLiegtVor && aktuelleBuchung.getMenge().compareTo(aktuelleBuchung.getProdukt().getPackGroesse()) <= 0;

        return ok;
        //btnEinbuchen.setBackground(ok ? new Color(102, 255, 102) : new Color(255, 102, 102));
    }

    private void setProduktEingabeButton() {
        btnAcceptProd.setBackground(isProduktEingabeOK(false) ? new Color(102, 255, 102) : new Color(255, 102, 102));
    }

    private boolean isProduktEingabeOK(boolean mitGrund) {
        // Wann nehme ich ein neues Produkt an ?

        // Wenn es eine Bezeichnung hat.
        boolean bezeichnungEingegeben = neuesProdukt.getBezeichnung() != null && !neuesProdukt.getBezeichnung().isEmpty();
        // Entweder wenn es sich nicht um verpackte Ware handelt oder (wenn doch), ein gültige GTIN eingegeben wurde
        // und eine Packungsgröße größer 0.
        boolean unverpackteWare = !btnVerpackteWare.isSelected();
        boolean verpackteWareOK = (gtinOK && neuesProdukt.getPackGroesse().compareTo(BigDecimal.ZERO) > 0);
        boolean stoffartOK = cmbStoffart.getSelectedIndex() >= 0;

        boolean ok = bezeichnungEingegeben
                && (unverpackteWare || verpackteWareOK)
                && stoffartOK;

        if (!ok && mitGrund) {
            String grund = "Sie können das Produkt nicht speichern, weil:\n";
            if (!bezeichnungEingegeben) {
                grund += "Sie keine Bezeichnung eingegeben haben.\n";
            }
            if (!(unverpackteWare || verpackteWareOK)) {
                grund += "Sie keine korrekte Packungsangaben gemaht haben.\n";
            }
            if (!stoffartOK) {
                grund += "Sie keine korrekte Angaben über die Stoffart gemacht haben.\n";
            }
            error(grund, lblMessageLower);
        }

        return ok;
    }

    private void setWarengruppeEnabled(boolean enable) {
        cmbWarengruppe.setEnabled(enable);
        btnAddWarengruppe.setEnabled(enable);
        txtNewWarengruppe.setEnabled(enable);
        btnApplyWarengruppe.setEnabled(enable);
        btnCancelWarengruppe.setEnabled(enable);
    }

    @Override
    public void startAction() {
        txtSearch.requestFocus();
        txtComponentFocusGained(new FocusEvent(txtSearch, 0));
    }

    private void loadWarengruppe() {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createNamedQuery("Warengruppe.findAllSorted");
        try {
            java.util.List warengruppe = query.getResultList();
            cmbWarengruppe.setModel(tools.Tools.newComboboxModel(warengruppe));
        } catch (Exception e) { // nicht gefunden
            //
        } finally {
            em.close();
        }
    }

//    private void loadStoffarten() {
//        Query query = em.createNamedQuery("Stoffart.findAllSorted");
//        try {
//            java.util.List stoffarten = query.getResultList();
//            cmbStoffart.setModel(tools.Tools.newComboboxModel(stoffarten));
//        } catch (Exception e) { // nicht gefunden
//            //
//        }
//    }


    private void success(String message, JLabel lbl) {
        lbl.setText(message);
        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/apply.png")));
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), Color.green);
        timeline1.setDuration(300);
        final JLabel lbl1 = lbl;
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    fadeout(lbl1);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
    }

    private void error(String message, JLabel lbl) {
        lbl.setText(message);
        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/stop.png")));
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), Color.red);
        timeline1.setDuration(300);
        final JLabel lbl1 = lbl;
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    fadeout(lbl1);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.playLoop(4, Timeline.RepeatBehavior.REVERSE);
    }

    private void fadeout(JLabel lbl) {
        //lbl.setIcon(null);
        final JLabel lbl1 = lbl;
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), lbl.getBackground());
        timeline1.setDuration(700);
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    lbl1.setText("");
                    lbl1.setForeground(Color.black);
                    lbl1.setIcon(null);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.play();
    }

    private void info(String message, JLabel lbl) {
        lbl.setText(message);
        lbl.setForeground(Color.black);
        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/info.png")));
    }

}
