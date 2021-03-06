/*
 * Created by JFormDesigner on Thu Aug 11 16:45:25 CEST 2011
 */

package desktop.products;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.*;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.ProdukteTableModel;
import tools.Const;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class FrmProdukte extends JFrame {

    JTree tree;
    RowFilter<ProdukteTableModel, Integer> warengruppeFilter;
    Warengruppe warengruppeFilterKriterium = null;

    //    private Pair<Integer, Object> criteria;
    RowFilter<ProdukteTableModel, Integer> ingTypeFilter;
    IngTypes ingTypeFilterKriterium = null;
    RowFilter<ProdukteTableModel, Integer> textFilter;
    String textKriterium = null;
    Stock foundStock = null;
    private JPopupMenu menu;
    private JidePopup popup;
    private JFrame thisComponent;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspSearch;
    private JXTaskPaneContainer pnlSearch;
    private JXTaskPane xTaskPane1;
    private JButton btnSearchAll;
    private JXSearchField xSearchField1;
    private JXTaskPane xTaskPane3;
    private JButton btnSearchEditProducts;
    private JButton btnNewIngType;
    private JPanel pnlMain;
    private JScrollPane jspProdukt;
    private JTable tblProdukt;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    private ProdukteTableModel ptm;
    private TableRowSorter<ProdukteTableModel> sorter;

    public FrmProdukte() {
        initComponents();
        thisComponent = this;
//        criteria = new Pair<Integer, Object>(Const.ALLE, null);
        createFilters();
        reload();

//        createTree();
        setTitle(Tools.getWindowTitle("Produkte-Verwaltung"));

        pack();
    }


    private void createFilters() {
        warengruppeFilter = new RowFilter<ProdukteTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends ProdukteTableModel, ? extends Integer> entry) {
                if (warengruppeFilterKriterium == null) return true;

                int row = entry.getIdentifier();
                Produkte produkt = entry.getModel().getProdukt(row);


                return produkt.getIngTypes().getWarengruppe().equals(warengruppeFilterKriterium);
            }
        };
        warengruppeFilterKriterium = null;

        ingTypeFilter = new RowFilter<ProdukteTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends ProdukteTableModel, ? extends Integer> entry) {
                if (ingTypeFilterKriterium == null) return true;

                int row = entry.getIdentifier();
                Produkte produkt = entry.getModel().getProdukt(row);


                return produkt.getIngTypes().equals(ingTypeFilterKriterium);
            }
        };
        ingTypeFilterKriterium = null;

        textFilter = new RowFilter<ProdukteTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends ProdukteTableModel, ? extends Integer> entry) {
                if (textKriterium == null || textKriterium.isEmpty()) return true;

                int row = entry.getIdentifier();
                Produkte produkt = entry.getModel().getProdukt(row);

                return (foundStock != null && foundStock.getProdukt().equals(produkt)) ||
                        produkt.getBezeichnung().toLowerCase().indexOf(textKriterium.trim()) >= 0 ||
                        Long.toString(produkt.getId()).equals(textKriterium.trim()) ||
                        Tools.catchNull(produkt.getGtin()).indexOf(textKriterium.trim()) >= 0 ||
                        produkt.getIngTypes().getBezeichnung().toLowerCase().indexOf(textKriterium.trim()) >= 0 ||
                        produkt.getIngTypes().getWarengruppe().getBezeichnung().toLowerCase().indexOf(textKriterium.trim()) >= 0;
            }
        };
        textKriterium = null;
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        textKriterium = xSearchField1.getText().toLowerCase();
        foundStock = StockTools.findByIDORScanner(textKriterium.trim());
        sorter.setRowFilter(textFilter);
        xSearchField1.selectAll();
    }

    public void reload() {

        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT p FROM Produkte p ORDER BY p.bezeichnung");
        List list = query.getResultList();
        em.close();

        ptm = new ProdukteTableModel(list, true);
        tblProdukt.setModel(ptm);

        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_INGTYPE).setCellRenderer(IngTypesTools.getTableCellRenderer());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_INGTYPE).setCellEditor(IngTypesTools.getTableCellEditor());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_WARENGRUPPE).setCellRenderer(WarengruppeTools.getTableCellRenderer());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_WARENGRUPPE).setCellEditor(WarengruppeTools.getTableCellEditor());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_LAGERART).setCellRenderer(IngTypesTools.getStorageRenderer());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_LAGERART).setCellEditor(IngTypesTools.getStorageEditor());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_EINHEIT).setCellRenderer(LagerTools.getEinheitTableCellRenderer());
        tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_EINHEIT).setCellEditor(LagerTools.getEinheitTableCellEditor());

        sorter = new TableRowSorter(ptm);
        sorter.setComparator(ProdukteTableModel.COL_INGTYPE, new Comparator<IngTypes>() {
            @Override
            public int compare(IngTypes o1, IngTypes o2) {
                return o1.compareTo(o2);
            }
        });
        sorter.setRowFilter(null);
        sorter.setSortsOnUpdates(true);
        tblProdukt.setRowSorter(sorter);

        tblProdukt.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        pnlMainComponentResized(null);

    }

    private void btnSearchAllActionPerformed(ActionEvent e) {
//        sorter.setRowFilter(null);
        reload();
//        criteria = new Pair<Integer, Object>(Const.ALLE, null);
//        reload();
    }

    private boolean mergeUs(ArrayList<Produkte> listProducts2Merge, Produkte target) {
//        int neu = tblProdukt.convertRowIndexToModel(target4Merge);
//        Produkte neuesProdukt = ((ProdukteTableModel) tblProdukt.getModel()).getProdukt(neu);

        boolean success = false;

        listProducts2Merge.remove(target);

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Produkte neuesProdukt = em.merge(target);
            em.lock(neuesProdukt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            for (Produkte p : listProducts2Merge) {
                Produkte altesProdukt = em.merge(p);

                for (Stock v : altesProdukt.getStockCollection()) {
                    Stock myStock = em.merge(v);
                    myStock.setProdukt(neuesProdukt);
                }
                altesProdukt.getStockCollection().clear();

                for (Allergene allergene : altesProdukt.getAllergenes()) {
                    neuesProdukt.getAllergenes().add(em.merge(allergene));
                }
                altesProdukt.getAllergenes().clear();

                for (Additives additives : altesProdukt.getAdditives()) {
                    neuesProdukt.getAdditives().add(em.merge(additives));
                }
                altesProdukt.getAdditives().clear();

                ptm.remove(altesProdukt);
                em.remove(altesProdukt);
            }
            em.getTransaction().commit();
            success = true;

            ptm.update(neuesProdukt);

        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
            reload();
        } catch (Exception e) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
        }
        return success;
    }

    private void tblProduktMousePressed(MouseEvent e) {


        if (ptm.getRowCount() == 0) {
            return;
        }


        Point p = e.getPoint();
        final int col = tblProdukt.columnAtPoint(p);
        final int row = tblProdukt.rowAtPoint(p);
        ListSelectionModel lsm = tblProdukt.getSelectionModel();


        //lsm.setSelectionInterval(row, row);
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();


        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        if (SwingUtilities.isLeftMouseButton(e) && col == ProdukteTableModel.COL_ALLADD && e.getClickCount() == 2) {
            final int thisRow = tblProdukt.convertRowIndexToModel(row);

            final PnlAssignAdditives dlgAssign = new PnlAssignAdditives(Main.getDesktop().getMenuweek(), ptm.getProdukt(thisRow).getAdditives(), ptm.getProdukt(thisRow).getAllergenes());
            dlgAssign.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (dlgAssign.getResponse() == JOptionPane.OK_OPTION) {
                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();

                            Produkte product = em.merge(ptm.getProdukt(thisRow));
                            em.lock(product, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                            product.getAllergenes().clear();
                            for (Allergene allergene : dlgAssign.getAssignedAllergenes()) {
                                product.getAllergenes().add(em.merge(allergene));
                            }

                            product.getAdditives().clear();
                            for (Additives additives : dlgAssign.getAssignedAdditives()) {
                                product.getAdditives().add(em.merge(additives));
                            }

                            em.getTransaction().commit();

                            ptm.update(product);
                        } catch (OptimisticLockException ole) {
                            em.getTransaction().rollback();
                            Main.warn(ole);
                        } catch (Exception exc) {
                            em.getTransaction().rollback();
                            Main.fatal(e);
                        } finally {
                            em.close();
                            popup = null;
                        }
                    }
                }
            });
            dlgAssign.setVisible(true);


        }

        if (SwingUtilities.isRightMouseButton(e)) {


            if (menu != null && menu.isVisible()) {
                menu.setVisible(false);
            }

            Tools.unregisterListeners(menu);
            menu = new JPopupMenu();


            JMenuItem miEdit = new JMenuItem("Markierte Produkte bearbeiten");
            miEdit.setFont(new Font("arial", Font.PLAIN, 18));
            final int[] rows = tblProdukt.getSelectedRows();
            final ArrayList<Produkte> listSelectedProducts = new ArrayList<Produkte>();
            for (int r = 0; r < rows.length; r++) {
                //                    final int finalR = r;
                int thisRow = tblProdukt.convertRowIndexToModel(rows[r]);
                listSelectedProducts.add(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow));
            }


            miEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    ArrayList<Produkte> listProdukte = new ArrayList<Produkte>(rows.length);
//                    for (int r = 0; r < rows.length; r++) {
//                        final int finalR = r;
//                        final int thisRow = tblProdukt.convertRowIndexToModel(rows[finalR]);
//                        listProdukte.add(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow));
//                    }
                    final DlgProdukt dlg = new DlgProdukt(Main.mainframe, listSelectedProducts.get(0));
                    dlg.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentHidden(ComponentEvent e) {
                            super.componentHidden(e);

                            ptm.update(dlg.getProduct());
                        }
                    });

                }
            });
            miEdit.setEnabled(listSelectedProducts.size() == 1);
            menu.add(miEdit);

            JMenuItem miInfo = new JMenuItem("Vorrat Info");
            miInfo.setFont(new Font("arial", Font.PLAIN, 18));

            miInfo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String ids = "";
                    BigDecimal menge = BigDecimal.ZERO;
                    int active = 0;
                    for (Stock stock : listSelectedProducts.get(0).getStockCollection()) {
                        if (!stock.isAusgebucht()) {
                            active++;
                            ids += stock.getId() + ", " + DateFormat.getDateInstance(DateFormat.SHORT).format(stock.getEingang()) + (active % 6 == 0 ? "\n" : "; ");
                            menge = menge.add(StockTools.getSumme(stock));
                        }
                    }

                    String text = "Produkt ID: " + listSelectedProducts.get(0).getId() + "\n";
                    text += listSelectedProducts.get(0).getStockCollection().size() + " Vorräte insgesamt.\n";
                    text += "Davon " + active + " noch nicht verbraucht.\n";
                    if (active > 0) {
                        text += "Noch vorhandene Gesamtmenge: " + menge.setScale(2, RoundingMode.HALF_UP).toString() + " " + IngTypesTools.EINHEIT[listSelectedProducts.get(0).getIngTypes().getEinheit()] + "\n";
                        text += "Active IDs: " + ids;
                    }

                    JOptionPane.showMessageDialog(thisComponent, text, "Vorrat Info", JOptionPane.INFORMATION_MESSAGE);

                    Main.debug(text);

                }
            });
            miInfo.setEnabled(listSelectedProducts.size() == 1);
            menu.add(miInfo);

            JMenuItem miDelete = new JMenuItem("löschen (inkl. Vorräte)");
            miDelete.setFont(new Font("arial", Font.PLAIN, 18));

            miDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EntityManager em1 = Main.getEMF().createEntityManager();
                    try {
                        em1.getTransaction().begin();
                        for (Produkte p : listSelectedProducts) {
                            if (JOptionPane.showConfirmDialog(thisComponent, "Das Produkt " + p.getId() + " hat " + p.getStockCollection().size() + " Vorräte.\nWirklich löschen ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                Produkte myProdukt = em1.merge(p);
                                em1.remove(myProdukt);
                                ptm.remove(myProdukt);
                            }
                        }
                        em1.getTransaction().commit();
                    } catch (OptimisticLockException ole) {

                        em1.getTransaction().rollback();
                        Main.warn(ole);
                    } catch (Exception exc) {
                        em1.getTransaction().rollback();
                        Main.fatal(e);
                    } finally {
                        em1.close();
//                        reload();
                    }
//                    reload();
                }
            });
            miDelete.setEnabled(listSelectedProducts.size() > 0 && Main.getCurrentUser().isAdmin());
            menu.add(miDelete);

            if (listSelectedProducts.size() > 1) {
                JMenu miPopupMerge = new JMenu("Markierte Produkte zusammenfassen zu");
                miPopupMerge.setFont(new Font("arial", Font.PLAIN, 18));

                for (final Produkte thisProduct : listSelectedProducts) {
                    JMenuItem mi = new JMenuItem("[" + thisProduct.getId() + "] " + thisProduct.getBezeichnung());
                    mi.setFont(new Font("arial", Font.PLAIN, 18));

                    mi.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zusammenfassen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                mergeUs(listSelectedProducts, thisProduct);
//                                reload();
                            }
                        }
                    });

                    miPopupMerge.add(mi);
                }

                menu.add(miPopupMerge);
            }

            menu.show(tblProdukt, (int) p.getX(), (int) p.getY());
        }


    }

//    private void createTree() {
//        xTaskPane2.removeAll();
//
//        String expansion = null;
//        if (tree != null) {
//            expansion = Tools.getExpansionState(tree, 0);
//            tree.removeAll();
//        }
//
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Warengruppen");
//        tree = new JTree(root);
////        tree.setOpaque(false);
//
//
//        EntityManager em = Main.getEMF().createEntityManager();
//        try {
//            Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
//            ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());
//
//            for (Warengruppe warengruppe : listWarengruppen) {
//
//                DefaultMutableTreeNode nodeWG = new DefaultMutableTreeNode(warengruppe);
//                root.add(nodeWG);
//
//                ArrayList<IngTypes> listStoffarten = new ArrayList<IngTypes>(warengruppe.getIngTypesCollection());
//                Collections.sort(listStoffarten);
//
//                for (IngTypes ingTypes : listStoffarten) {
//                    DefaultMutableTreeNode nodeSA = new DefaultMutableTreeNode(ingTypes);
//                    nodeWG.add(nodeSA);
//                }
//            }
//        } catch (Exception e) {
//            Main.fatal(e);
//        } finally {
//            em.close();
//        }
//
//        tree.setCellRenderer(new TreeCellRenderer() {
//            @Override
//            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//
////                Main.debug(value.getClass().getName());
//
//                String text = value.toString();
//
//                if (value instanceof DefaultMutableTreeNode) {
//                    if (((DefaultMutableTreeNode) value).getUserObject() instanceof Warengruppe) {
//
//                        Warengruppe warengruppe = (Warengruppe) ((DefaultMutableTreeNode) value).getUserObject();
//
//                        text = warengruppe.getBezeichnung() + " [" + WarengruppeTools.getNumOfProducts(warengruppe) + "]";
//                    } else if (((DefaultMutableTreeNode) value).getUserObject() instanceof IngTypes) {
//
//                        IngTypes ingTypes = (IngTypes) ((DefaultMutableTreeNode) value).getUserObject();
//
//                        text = ingTypes.getBezeichnung() + " [" + IngTypesTools.getNumOfProducts(ingTypes) + "]";
//                    }
//                }
//
//                setFont(new Font("arial", Font.PLAIN, 16));
////                setOpaque(false);
//
//                return new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row, hasFocus);
//            }
//        });
//
//        tree.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
//
//                Point p = e.getPoint();
//                final int row = tree.getClosestRowForLocation(p.x, p.y);
////                final int row = tblProdukt.rowAtPoint(p);
//                TreeSelectionModel tsm = tree.getSelectionModel();
//
//                //lsm.setSelectionInterval(row, row);
//                boolean singleRowSelected = tsm.getMaxSelectionRow() == tsm.getMinSelectionRow();
//
//                if (singleRowSelected) {
//                    tsm.setSelectionPath(tree.getPathForRow(row));
//                }
//
//                if (e.isPopupTrigger()) {
//                    if (menu != null && menu.isVisible()) {
//                        menu.setVisible(false);
//                    }
//
//                    Tools.unregisterListeners(menu);
//                    menu = new JPopupMenu();
//
//
//                    JMenuItem miNewWarengruppe = new JMenuItem("Neue Warengruppe erstellen");
//                    miNewWarengruppe.setFont(new Font("arial", Font.PLAIN, 18));
//                    miNewWarengruppe.setEnabled(singleRowSelected);
//                    miNewWarengruppe.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            String newText = JOptionPane.showInputDialog(thisComponent, "Warengruppe", "Neu erstellen", JOptionPane.OK_CANCEL_OPTION);
//                            if (newText != null && !newText.trim().isEmpty()) {
//                                EntityManager em = Main.getEMF().createEntityManager();
//                                try {
//                                    em.getTransaction().begin();
//                                    Warengruppe myWarengruppe = em.merge(new Warengruppe(newText));
//                                    em.getTransaction().commit();
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    Main.warn(ole);
//                                } catch (Exception exc) {
//                                    em.getTransaction().rollback();
//                                    Main.fatal(e);
//                                } finally {
//                                    em.close();
//                                    createTree();
//                                }
//                            }
//                        }
//                    });
//                    miNewWarengruppe.setEnabled(singleRowSelected && ((DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent()).getUserObject() instanceof Warengruppe);
//                    menu.add(miNewWarengruppe);
//
//                    JMenuItem miNewStoffart = new JMenuItem("Neue Stoffart erstellen");
//                    miNewStoffart.setFont(new Font("arial", Font.PLAIN, 18));
//                    miNewStoffart.setEnabled(singleRowSelected);
//                    miNewStoffart.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            String newText = JOptionPane.showInputDialog(thisComponent, "Stoffart", "Neu erstellen", JOptionPane.OK_CANCEL_OPTION);
//                            if (newText != null && !newText.trim().isEmpty()) {
//                                EntityManager em = Main.getEMF().createEntityManager();
//                                try {
//                                    em.getTransaction().begin();
//                                    DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();
//                                    Warengruppe myWarengruppe = em.merge((Warengruppe) thisNode.getUserObject());
//                                    IngTypes myIngTypes = em.merge(new IngTypes(newText, myWarengruppe));
//                                    em.getTransaction().commit();
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    Main.warn(ole);
//                                } catch (Exception exc) {
//                                    em.getTransaction().rollback();
//                                    Main.fatal(e);
//                                } finally {
//                                    em.close();
//                                    createTree();
//                                }
//                            }
//                        }
//                    });
//                    miNewStoffart.setEnabled(singleRowSelected && ((DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent()).getUserObject() instanceof Warengruppe);
//                    menu.add(miNewStoffart);
//
//                    JMenuItem miRename = new JMenuItem("Markiertes Objekt umbennen");
//                    miRename.setFont(new Font("arial", Font.PLAIN, 18));
//                    miRename.setEnabled(singleRowSelected);
//                    miRename.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
//
//                            String text = "";
//                            if (thisNode.getUserObject() instanceof Warengruppe) {
//                                text = ((Warengruppe) thisNode.getUserObject()).getBezeichnung();
//
//                            } else if (thisNode.getUserObject() instanceof IngTypes) {
//                                text = ((IngTypes) thisNode.getUserObject()).getBezeichnung();
//                            }
//
//                            String newText = JOptionPane.showInputDialog(thisComponent, "Bezeichnung", text);
//                            if (newText != null && !newText.trim().isEmpty()) {
//                                EntityManager em = Main.getEMF().createEntityManager();
//                                try {
//                                    em.getTransaction().begin();
//
//                                    if (thisNode.getUserObject() instanceof IngTypes) {
//                                        IngTypes myIngTypes = em.merge((IngTypes) thisNode.getUserObject());
//                                        em.lock(myIngTypes, LockModeType.OPTIMISTIC);
//                                        myIngTypes.setBezeichnung(newText);
//                                    } else if (thisNode.getUserObject() instanceof Warengruppe) {
//                                        Warengruppe myWarengruppe = em.merge((Warengruppe) thisNode.getUserObject());
//                                        em.lock(myWarengruppe, LockModeType.OPTIMISTIC);
//                                        myWarengruppe.setBezeichnung(newText);
//                                    }
//
//                                    em.getTransaction().commit();
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    Main.warn(ole);
//                                } catch (Exception exc) {
//                                    em.getTransaction().rollback();
//                                    Main.fatal(e);
//                                } finally {
//                                    em.close();
//                                    createTree();
//                                }
//                            }
//
//                        }
//                    });
//                    menu.add(miRename);
//
//                    if (isOnlyStoffartSelected()) {
//
//                        JMenuItem miDelete = new JMenuItem("Markierte Stoffgruppe löschen, wenn leer");
//                        miDelete.setFont(new Font("arial", Font.PLAIN, 18));
////                        miDelete.setEnabled(StoffartTools.getNumOfProducts(stoffart) == 0);
//                        miDelete.addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                    EntityManager em = Main.getEMF().createEntityManager();
//                                    try {
//                                        em.getTransaction().begin();
//
//                                        for (TreePath path : tree.getSelectionPaths()) {
//                                            DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) path.getLastPathComponent();
//                                            IngTypes myIngTypes = em.merge((IngTypes) thisNode.getUserObject());
//                                            if (myIngTypes.getProdukteCollection().isEmpty()) {
//                                                em.remove(myIngTypes);
//                                            }
//                                        }
//
//                                        em.getTransaction().commit();
//                                    } catch (OptimisticLockException ole) {
//                                        em.getTransaction().rollback();
//                                        Main.warn(ole);
//                                    } catch (Exception exc) {
//                                        em.getTransaction().rollback();
//                                        Main.fatal(e);
//                                    } finally {
//                                        em.close();
//                                        createTree();
//                                    }
//                                }
//                            }
//                        });
//                        menu.add(miDelete);
//                    }
//
//                    if (isOnlyStoffartSelected() && singleRowSelected) {
//                        final IngTypes ingTypes = (IngTypes) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
//                        JMenu menuDeleteReplace = new JMenu("Markierte Stoffgruppe löschen, Produkte zuweisen zu");
//                        menuDeleteReplace.setFont(new Font("arial", Font.PLAIN, 18));
//                        menuDeleteReplace.setEnabled(IngTypesTools.getNumOfProducts(ingTypes) > 0);
//
//                        EntityManager em = Main.getEMF().createEntityManager();
//                        try {
//                            Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
//                            ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());
//
//
//                            for (Warengruppe warengruppe : listWarengruppen) {
//
//                                JMenu menuWarengruppe = new JMenu(warengruppe.getBezeichnung());
//
//                                ArrayList<IngTypes> listStoffarten = new ArrayList<IngTypes>(warengruppe.getIngTypesCollection());
//                                Collections.sort(listStoffarten);
//
//                                for (final IngTypes s : listStoffarten) {
//                                    JMenuItem miStoffart = new JMenuItem(s.getBezeichnung());
//                                    miStoffart.addActionListener(new ActionListener() {
//                                        @Override
//                                        public void actionPerformed(ActionEvent e) {
//                                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                                EntityManager em1 = Main.getEMF().createEntityManager();
//                                                try {
//                                                    em1.getTransaction().begin();
//                                                    IngTypes myIngTypes = em1.merge(s);
//                                                    IngTypes ingTypes2delete = em1.merge(ingTypes);
//                                                    for (Produkte p : ingTypes2delete.getProdukteCollection()) {
//                                                        Produkte myProdukt = em1.merge(p);
//                                                        em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//                                                        myProdukt.setIngTypes(myIngTypes);
//                                                    }
//                                                    ingTypes2delete.getProdukteCollection().clear();
//                                                    em1.remove(ingTypes2delete);
//                                                    em1.getTransaction().commit();
//                                                } catch (OptimisticLockException ole) {
//                                                    em1.getTransaction().rollback();
//                                                    Main.warn(ole);
//                                                } catch (Exception exc) {
//                                                    em1.getTransaction().rollback();
//                                                    Main.fatal(e);
//                                                } finally {
//                                                    em1.close();
//                                                    createTree();
//                                                }
//                                            }
//                                        }
//                                    });
//                                    menuWarengruppe.add(miStoffart);
//                                }
//                                menuDeleteReplace.add(menuWarengruppe);
//                            }
//                        } catch (Exception exc) {
//                            Main.fatal(exc.getMessage());
//                        } finally {
//                            em.close();
//                        }
//                        menu.add(menuDeleteReplace);
//
//                    }
//
//
//                    if (isOnlyWarengruppeSelected() && singleRowSelected) {
//
//                        final Warengruppe warengruppe = (Warengruppe) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
//
//                        JMenuItem miDelete = new JMenuItem("Markierte Warengruppe löschen");
//                        miDelete.setFont(new Font("arial", Font.PLAIN, 18));
//                        miDelete.setEnabled(WarengruppeTools.getNumOfProducts(warengruppe) == 0);
//                        miDelete.addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                    EntityManager em = Main.getEMF().createEntityManager();
//                                    try {
//                                        em.getTransaction().begin();
//
//                                        Warengruppe myWarengruppe = em.merge(warengruppe);
//                                        em.remove(myWarengruppe);
//
//                                        em.getTransaction().commit();
//                                    } catch (OptimisticLockException ole) {
//                                        em.getTransaction().rollback();
//                                        Main.warn(ole);
//                                    } catch (Exception exc) {
//                                        em.getTransaction().rollback();
//                                        Main.fatal(e);
//                                    } finally {
//                                        em.close();
//                                        createTree();
//                                    }
//                                }
//                            }
//                        });
//                        menu.add(miDelete);
//                    }
//
//                    if (isOnlyStoffartSelected()) {
//
//                        JMenu menuAssign = new JMenu("Stoffgruppe[n] zuordnen zu");
//                        menuAssign.setFont(new Font("arial", Font.PLAIN, 18));
//
//
//                        DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();
//
//                        if (isAllHaveTheSameWarengruppe()) {
//                            EntityManager em = Main.getEMF().createEntityManager();
//                            try {
//                                Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
//                                ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());
//                                for (final Warengruppe warengruppe : listWarengruppen) {
//                                    JMenuItem miWarengruppe = new JMenuItem(warengruppe.getBezeichnung());
//                                    miWarengruppe.addActionListener(new ActionListener() {
//                                        @Override
//                                        public void actionPerformed(ActionEvent e) {
//                                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                                EntityManager em = Main.getEMF().createEntityManager();
//                                                try {
//                                                    em.getTransaction().begin();
//                                                    for (TreePath path : tree.getSelectionPaths()) {
//                                                        DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) path.getLastPathComponent();
//                                                        IngTypes myIngTypes = em.merge((IngTypes) thisNode.getUserObject());
//                                                        em.lock(myIngTypes, LockModeType.OPTIMISTIC);
//                                                        myIngTypes.setWarengruppe(em.merge(warengruppe));
//                                                    }
//                                                    em.getTransaction().commit();
//                                                } catch (OptimisticLockException ole) {
//                                                    em.getTransaction().rollback();
//                                                    Main.warn(ole);
//                                                } catch (Exception exc) {
//                                                    em.getTransaction().rollback();
//                                                    Main.fatal(e);
//                                                } finally {
//                                                    em.close();
//                                                    createTree();
//                                                }
//                                            }
//                                        }
//                                    });
//                                    menuAssign.add(miWarengruppe);
//                                }
//                            } catch (Exception exc) {
//                                Main.fatal(exc.getMessage());
//                            } finally {
//                                em.close();
//                            }
//
//                            menu.add(menuAssign);
//                        }
//                    }
//                    menu.show(tree, (int) p.getX(), (int) p.getY());
//                }
//
//
//            }
//        });
//
//
//        tree.addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//
//                JTree myTree = (JTree) e.getSource();
//
////
////                if (myTree.getSelectionPaths().length != 1) {
////                    tblProdukt.setModel(new DefaultTableModel());
////                    return;
////                }
//
//                TreePath path = e.getNewLeadSelectionPath();
//
//                if (path != null) {
//                    DefaultMutableTreeNode lastComponent = (DefaultMutableTreeNode) path.getLastPathComponent();
//
//
//                    if (lastComponent.getUserObject() instanceof IngTypes) {
//                        ingTypeFilterKriterium = (IngTypes) lastComponent.getUserObject();
//                        sorter.setRowFilter(ingTypeFilter);
//                    } else if (lastComponent.getUserObject() instanceof Warengruppe) {
//                        warengruppeFilterKriterium = (Warengruppe) lastComponent.getUserObject();
//                        sorter.setRowFilter(warengruppeFilter);
////                        criteria = new Pair<Integer, Object>(Const.WARENGRUPPE, lastComponent.getUserObject());
////                        reload();
//                    }
//
//                }
//            }
//        });
//
//
//        if (expansion != null) {
//            Tools.restoreExpansionState(tree, 0, expansion);
//        }
//
//        xTaskPane2.add(tree);
//
//    }

    boolean isOnlyStoffartSelected() {
        boolean onlyStoffarten = true;
        for (TreePath path : tree.getSelectionPaths()) {
            DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (!(thisNode.getUserObject() instanceof IngTypes)) {
                onlyStoffarten = false;
                break;
            }
        }
        return onlyStoffarten;
    }

    boolean isAllHaveTheSameWarengruppe() {
        if (!isOnlyStoffartSelected()) {
            return false;
        }
        boolean sameWarengruppe = true;
        Warengruppe warengruppe = ((IngTypes) ((DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent()).getUserObject()).getWarengruppe();
        for (TreePath path : tree.getSelectionPaths()) {
            IngTypes thisIngTypes = (IngTypes) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            if (!warengruppe.equals(thisIngTypes.getWarengruppe())) {
                sameWarengruppe = false;
                break;
            }
        }
        return sameWarengruppe;
    }

    boolean isOnlyWarengruppeSelected() {
        boolean onlyWarengruppen = true;
        for (TreePath path : tree.getSelectionPaths()) {
            DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (!(thisNode.getUserObject() instanceof Warengruppe)) {
                onlyWarengruppen = false;
                break;
            }
        }
        return onlyWarengruppen;
    }

    private void pnlMainComponentResized(ComponentEvent e) {
//        Tools.packTable(tblProdukt, 0);
        int[] widths = new int[]{69, 592, 145, 89, 134, 386, 50, 171, 260};

        for (int col = 0; col < widths.length; col++) {
            tblProdukt.getColumnModel().getColumn(col).setPreferredWidth(widths[col]);

        }
    }

    private void btnSearchEditProductsActionPerformed(ActionEvent e) {
//        ArrayList<Produkte> list = new ArrayList<Produkte>();
//        list.add(new Produkte(IngTypesTools.getFirstType()));

        final DlgProdukt dlg = new DlgProdukt(Main.mainframe, new Produkte(IngTypesTools.getFirstType()));
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
                ptm.update(dlg.getProduct());
            }
        });
    }

    private void xSearchField1FocusGained(FocusEvent e) {
        xSearchField1.selectAll();
    }

    private void btnNewIngTypeActionPerformed(ActionEvent e) {
        String input = JOptionPane.showInputDialog(thisComponent, "", "Neue Stoffart eingeben", JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        input = input.trim();

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            IngTypes newIngType = em.merge(new IngTypes(input, WarengruppeTools.getAll().get(0)));
            em.getTransaction().commit();

            tblProdukt.getColumnModel().getColumn(ProdukteTableModel.COL_INGTYPE).setCellEditor(IngTypesTools.getTableCellEditor());

        } catch (OptimisticLockException ole) {
            Main.warn(ole);
            em.getTransaction().rollback();
        } catch (javax.persistence.RollbackException rbe) {
            Main.warn(rbe);
            em.getTransaction().rollback();
        } catch (Exception exc) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspSearch = new JScrollPane();
        pnlSearch = new JXTaskPaneContainer();
        xTaskPane1 = new JXTaskPane();
        btnSearchAll = new JButton();
        xSearchField1 = new JXSearchField();
        xTaskPane3 = new JXTaskPane();
        btnSearchEditProducts = new JButton();
        btnNewIngType = new JButton();
        pnlMain = new JPanel();
        jspProdukt = new JScrollPane();
        tblProdukt = new JTable();

        //======== this ========
        setVisible(true);
        setResizable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "134dlu, default:grow",
                "default:grow"));

        //======== jspSearch ========
        {

            //======== pnlSearch ========
            {

                //======== xTaskPane1 ========
                {
                    xTaskPane1.setTitle("Suchen");
                    xTaskPane1.setFont(new Font("sansserif", Font.BOLD, 18));
                    xTaskPane1.setOpaque(false);
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
                    xSearchField1.setSearchMode(JXSearchField.SearchMode.REGULAR);
                    xSearchField1.setInstantSearchDelay(5000);
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
                    xTaskPane1.add(xSearchField1);
                }
                pnlSearch.add(xTaskPane1);

                //======== xTaskPane3 ========
                {
                    xTaskPane3.setTitle("Funktionen");
                    xTaskPane3.setFont(new Font("Dialog", Font.BOLD, 18));
                    xTaskPane3.setOpaque(false);
                    xTaskPane3.setSpecial(true);
                    xTaskPane3.setLayout(new VerticalLayout(10));

                    //---- btnSearchEditProducts ----
                    btnSearchEditProducts.setText("Produkte einzeln bearbeiten");
                    btnSearchEditProducts.setEnabled(false);
                    btnSearchEditProducts.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSearchEditProductsActionPerformed(e);
                        }
                    });
                    xTaskPane3.add(btnSearchEditProducts);

                    //---- btnNewIngType ----
                    btnNewIngType.setText("Neue Stoffart");
                    btnNewIngType.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnNewIngTypeActionPerformed(e);
                        }
                    });
                    xTaskPane3.add(btnNewIngType);
                }
                pnlSearch.add(xTaskPane3);
            }
            jspSearch.setViewportView(pnlSearch);
        }
        contentPane.add(jspSearch, CC.xy(1, 1, CC.FILL, CC.FILL));

        //======== pnlMain ========
        {
            pnlMain.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pnlMainComponentResized(e);
                }
            });
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
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

}
