/*
 * Created by JFormDesigner on Fri Oct 31 16:19:42 CET 2014
 */

package desktop.products;

import Main.Main;
import beans.PrintListElement;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import org.jdesktop.swingx.JXSearchField;
import printer.Form;
import printer.Printer;
import threads.PrintProcessor;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class FrmReassignProduct extends JInternalFrame {

    private Stock stockSource, stockDestination;
    private Produkte productSource, productDestination;
    private ArrayList<Stock> listToChange;

    private Printer pageprinter, etiprinter1, etiprinter2;
    private Form form1, form2;
    private PrintProcessor pp;

    public FrmReassignProduct(PrintProcessor pp) {
        this.pp = pp;
        this.stockSource = null;
        this.productSource = null;
        this.stockDestination = null;
        this.productDestination = null;
        this.listToChange = null;

        pageprinter = Main.printers.getPrinters().get("pageprinter");
        etiprinter1 = Main.printers.getPrinters().get(Main.getProps().getProperty("etitype1"));
        etiprinter2 = Main.printers.getPrinters().get(Main.getProps().getProperty("etitype2"));

        form1 = etiprinter1.getForms().get(Main.getProps().getProperty("etiform1"));
        form2 = etiprinter2.getForms().get(Main.getProps().getProperty("etiform2"));

        initComponents();
        initPanel();
        pack();
    }

    private void initPanel() {
        if (!Main.getProps().containsKey("reassign1printer")) {
            Main.getProps().put("reassign1printer", "3");
        }

        btnPrt1.setSelected(Main.getProps().getProperty("reassign1printer").equals("0"));
        btnPrt2.setSelected(Main.getProps().getProperty("reassign1printer").equals("1"));
        btnPrtPage.setSelected(Main.getProps().getProperty("reassign1printer").equals("2"));
        btnPrtNone.setSelected(Main.getProps().getProperty("reassign1printer").equals("3"));


    }

    private void btnPrt1ItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "0");
        xSearchField1.requestFocus();
    }

    private void btnPrt2ItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "1");
        xSearchField1.requestFocus();
    }

    private void btnPrtPageItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "2");
        xSearchField1.requestFocus();
    }

    private void btnPrtNoneItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        Main.getProps().put("reassign1printer", "3");
        xSearchField1.requestFocus();
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        stockSource = StockTools.findByIDORScanner(xSearchField1.getText().trim());

        if (stockSource != null) {
            productSource = stockSource.getProdukt();
        } else {
            String gtin = ProdukteTools.getGTIN(xSearchField1.getText().trim());
            productSource = ProdukteTools.getProduct(gtin);

            if (productSource == null) {
                txtSource.setText("Hab nichts gefunden.");
                return;
            }

        }

        listToChange = StockTools.getActiveStocks(productSource);

        String text = "Produkt: [" + productSource.getId() + "] " + productSource.getBezeichnung() + (!productSource.isLoseWare() ? ", GTIN: " + productSource.getGtin() : "") + "\n";
        text += stockSource == null ? "" : "Angegebener Vorrat: " + stockSource.toString() + "\n";
        text += listToChange.isEmpty() ? "" : "Es gibt insgesamt " + listToChange.size() + " aktive Vorräte für dieses Produkt.";

        txtSource.setText(text);
        xSearchField1.requestFocus();

    }

    private void btnChangeSingleActionPerformed(ActionEvent e) {
        if (stockSource == null) {
            txtSource.setText("kein Vorrat angegeben. Geht nicht.");
            return;
        }

//        if (vorratSource.isAusgebucht()) {
//            txtSource.setText("Vorrat ist bereits ausgebucht. Geht nicht.");
//            return;
//        }

        if (productDestination == null) {
            txtDestination.setText("Kein Ziel-Produkt. Geht nicht.");
            return;
        }

        if (stockSource.getProdukt().equals(productDestination)) {
            txtDestination.setText("Ziel-Produkt und Quell-Produkt gleich. Lass den Quatsch!");
            return;
        }

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            Stock myStock = em.merge(stockSource);

            em.lock(myStock, LockModeType.OPTIMISTIC);
            myStock.setProdukt(productDestination);

            java.util.List<PrintListElement> printList = new ArrayList();
            if (btnPrt1.isSelected()) {
                printList.add(new PrintListElement(myStock, etiprinter1, form1, Main.getProps().getProperty("etiprinter1")));
            } else if (btnPrt1.isSelected()) {
                printList.add(new PrintListElement(myStock, etiprinter2, form2, Main.getProps().getProperty("etiprinter2")));
            } else if (btnPrtPage.isSelected()) {
                printList.add(new PrintListElement(myStock, pageprinter, null, Main.getProps().getProperty("pageprinter")));
            }

            em.getTransaction().commit();

            if (!btnPrtNone.isSelected()) {
                Collections.sort(printList); // Sortieren nach den PrimaryKeys
                pp.addPrintJobs(printList);
            }

        } catch (OptimisticLockException ole) {

            em.getTransaction().rollback();
            txtDestination.setText(ole.getMessage());

        } catch (Exception ex) {

            Main.fatal(ex);

        } finally {
            em.close();
        }

        txtSource.setText(null);
        txtDestination.setText(null);

        if (listToChange != null) {
            listToChange.clear();
        }

        stockSource = null;
        stockDestination = null;
        productDestination = null;
        productSource = null;

        xSearchField1.requestFocus();

    }

    private void xSearchField1FocusGained(FocusEvent e) {
        xSearchField1.selectAll();
    }

    private void btnChangeAllActionPerformed(ActionEvent e) {
        if (productSource == null || listToChange == null || listToChange.isEmpty()) {
            txtSource.setText("Kein Produkt or keine Vorräte. Geht nicht.");
            return;
        }

        if (productDestination == null) {
            txtDestination.setText("Kein Ziel-Produkt. Geht nicht.");
            return;
        }

        if (productSource.equals(productDestination)) {
            txtDestination.setText("Ziel-Produkt und Quell-Produkt gleich. Lass den Quatsch!");
            return;
        }

        int numOperations = 0;

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            Produkte myProductSource = em.merge(productSource);
            Produkte myProductDestination = em.merge(productDestination);

            em.lock(myProductSource, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(myProductDestination, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            java.util.List<PrintListElement> printList = new ArrayList();
            for (Stock stock : listToChange) {
                numOperations++;
                Stock myStock = em.merge(stock);
                em.lock(myStock, LockModeType.OPTIMISTIC);
                myStock.setProdukt(myProductDestination);

                myProductSource.getStockCollection().remove(myStock);
                myProductDestination.getStockCollection().add(myStock);

                if (btnPrt1.isSelected()) {
                    printList.add(new PrintListElement(myStock, etiprinter1, form1, Main.getProps().getProperty("etiprinter1")));
                } else if (btnPrt1.isSelected()) {
                    printList.add(new PrintListElement(myStock, etiprinter2, form2, Main.getProps().getProperty("etiprinter2")));
                } else if (btnPrtPage.isSelected()) {
                    printList.add(new PrintListElement(myStock, pageprinter, null, Main.getProps().getProperty("pageprinter")));
                }

            }

            em.getTransaction().commit();

            if (!btnPrtNone.isSelected()) {
                Collections.sort(printList); // Sortieren nach den PrimaryKeys
                pp.addPrintJobs(printList);
            }

        } catch (OptimisticLockException ole) {

        } catch (Exception ex) {

        } finally {
            em.close();
        }

        txtSource.setText(null);
        txtDestination.setText(numOperations + " Änderungen durchgeführt. ");

        if (listToChange != null) {
            listToChange.clear();
        }

        stockSource = null;
        stockDestination = null;
        productDestination = null;
        productSource = null;

        xSearchField1.requestFocus();
    }

    private void xSearchField2ActionPerformed(ActionEvent e) {
        stockDestination = StockTools.findByIDORScanner(xSearchField2.getText().trim());

        if (stockDestination != null) {
            productDestination = stockDestination.getProdukt();
        } else {
            String gtin = ProdukteTools.getGTIN(xSearchField2.getText().trim());
            productDestination = ProdukteTools.getProduct(gtin);


        }
//
//        String text = "Produkt: [" + productDestination.getId() + "] " + productDestination.getBezeichnung() + (!productDestination.isLoseWare() ? ", GTIN: " + productDestination.getGtin() : "") + "\n";
//        text += vorratDestination == null ? "" : "Angegebener Vorrat: " + vorratDestination.toString() + "\n";

        txtDestination.setText(getTextDestination());

        xSearchField2.requestFocus();
    }

    private void xSearchField2FocusGained(FocusEvent e) {
        xSearchField2.selectAll();
    }

    private void btnAddProductActionPerformed(ActionEvent e) {

        final DlgProdukt dlg = new DlgProdukt(Main.mainframe, new Produkte(IngTypesTools.getFirstType()));
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
                productDestination = dlg.getProduct();
                txtDestination.setText(getTextDestination());
                xSearchField2.requestFocus();
            }
        });
    }

    private String getTextDestination() {
        if (productDestination == null) {
            return "Hab nichts gefunden.";
        }
        String text = "Produkt: [" + productDestination.getId() + "] " + productDestination.getBezeichnung() + (!productDestination.isLoseWare() ? ", GTIN: " + productDestination.getGtin() : "") + "\n";
        text += stockDestination == null ? "" : "Angegebener Vorrat: " + stockDestination.toString() + "\n";
        return text;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        xSearchField1 = new JXSearchField();
        scrollPane1 = new JScrollPane();
        txtSource = new JTextArea();
        panel2 = new JPanel();
        label1 = new JLabel();
        hSpacer1 = new JPanel(null);
        xSearchField2 = new JXSearchField();
        hSpacer2 = new JPanel(null);
        btnAddProduct = new JButton();
        scrollPane2 = new JScrollPane();
        txtDestination = new JTextArea();
        btnChangeSingle = new JButton();
        btnChangeAll = new JButton();
        panel1 = new JPanel();
        btnPrt1 = new JToggleButton();
        btnPrt2 = new JToggleButton();
        btnPrtPage = new JToggleButton();
        btnPrtNone = new JToggleButton();

        //======== this ========
        setVisible(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "default, $lgap, fill:default:grow, $lgap, default, $lgap, fill:default:grow, 3*($lgap, default)"));

        //---- xSearchField1 ----
        xSearchField1.setFont(new Font("SansSerif", Font.PLAIN, 18));
        xSearchField1.setSearchMode(JXSearchField.SearchMode.REGULAR);
        xSearchField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xSearchField1ActionPerformed(e);
            }
        });
        xSearchField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                xSearchField1FocusGained(e);
            }
        });
        contentPane.add(xSearchField1, CC.xy(1, 1));

        //======== scrollPane1 ========
        {

            //---- txtSource ----
            txtSource.setText("text");
            txtSource.setFont(new Font("SansSerif", Font.PLAIN, 18));
            txtSource.setEditable(false);
            txtSource.setWrapStyleWord(true);
            txtSource.setLineWrap(true);
            scrollPane1.setViewportView(txtSource);
        }
        contentPane.add(scrollPane1, CC.xy(1, 3));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- label1 ----
            label1.setText("soll ge\u00e4ndert werden in");
            label1.setFont(new Font("SansSerif", Font.PLAIN, 18));
            panel2.add(label1);
            panel2.add(hSpacer1);

            //---- xSearchField2 ----
            xSearchField2.setSearchMode(JXSearchField.SearchMode.REGULAR);
            xSearchField2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            xSearchField2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xSearchField2ActionPerformed(e);
                }
            });
            xSearchField2.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    xSearchField2FocusGained(e);
                }
            });
            panel2.add(xSearchField2);
            panel2.add(hSpacer2);

            //---- btnAddProduct ----
            btnAddProduct.setText(null);
            btnAddProduct.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
            btnAddProduct.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddProductActionPerformed(e);
                }
            });
            panel2.add(btnAddProduct);
        }
        contentPane.add(panel2, CC.xy(1, 5));

        //======== scrollPane2 ========
        {

            //---- txtDestination ----
            txtDestination.setEditable(false);
            txtDestination.setWrapStyleWord(true);
            txtDestination.setLineWrap(true);
            txtDestination.setFont(new Font("SansSerif", Font.PLAIN, 18));
            scrollPane2.setViewportView(txtDestination);
        }
        contentPane.add(scrollPane2, CC.xy(1, 7));

        //---- btnChangeSingle ----
        btnChangeSingle.setText("Nur diesen einen \u00c4ndern");
        btnChangeSingle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btnChangeSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnChangeSingleActionPerformed(e);
            }
        });
        contentPane.add(btnChangeSingle, CC.xy(1, 9));

        //---- btnChangeAll ----
        btnChangeAll.setText("Alle \u00e4ndern, die da sind");
        btnChangeAll.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btnChangeAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnChangeAllActionPerformed(e);
            }
        });
        contentPane.add(btnChangeAll, CC.xy(1, 11));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnPrt1 ----
            btnPrt1.setText("Drucker 1");
            btnPrt1.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/labelprinter2.png")));
            btnPrt1.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrt1.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrt1ItemStateChanged(e);
                }
            });
            panel1.add(btnPrt1);

            //---- btnPrt2 ----
            btnPrt2.setText("Drucker 2");
            btnPrt2.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/labelprinter2.png")));
            btnPrt2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrt2.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrt2ItemStateChanged(e);
                }
            });
            panel1.add(btnPrt2);

            //---- btnPrtPage ----
            btnPrtPage.setText("Seitendrucker");
            btnPrtPage.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/printer.png")));
            btnPrtPage.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrtPage.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrtPageItemStateChanged(e);
                }
            });
            panel1.add(btnPrtPage);

            //---- btnPrtNone ----
            btnPrtNone.setText("Kein Druck");
            btnPrtNone.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/noprinter.png")));
            btnPrtNone.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnPrtNone.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrtNoneItemStateChanged(e);
                }
            });
            panel1.add(btnPrtNone);
        }
        contentPane.add(panel1, CC.xy(1, 13, CC.FILL, CC.DEFAULT));

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(btnPrt1);
        buttonGroup1.add(btnPrt2);
        buttonGroup1.add(btnPrtPage);
        buttonGroup1.add(btnPrtNone);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField xSearchField1;
    private JScrollPane scrollPane1;
    private JTextArea txtSource;
    private JPanel panel2;
    private JLabel label1;
    private JPanel hSpacer1;
    private JXSearchField xSearchField2;
    private JPanel hSpacer2;
    private JButton btnAddProduct;
    private JScrollPane scrollPane2;
    private JTextArea txtDestination;
    private JButton btnChangeSingle;
    private JButton btnChangeAll;
    private JPanel panel1;
    private JToggleButton btnPrt1;
    private JToggleButton btnPrt2;
    private JToggleButton btnPrtPage;
    private JToggleButton btnPrtNone;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
