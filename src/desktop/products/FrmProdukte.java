/*
 * Created by JFormDesigner on Thu Aug 11 16:45:25 CEST 2011
 */

package desktop.products;

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
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class FrmProdukte extends JInternalFrame {

    JTree tree;
    private Object[] spalten = new Object[]{"Produkt Nr.", "Bezeichnung", "Lagerart", "GTIN", "Packungsgröße", "Einheit", "Stoffart", "Warengruppe"};
    private JPopupMenu menu;

    private Pair<Integer, Object> criteria;

    private JComponent thisComponent;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspSearch;
    private JXTaskPaneContainer pnlSearch;
    private JXTaskPane xTaskPane1;
    private JButton btnSearchAll;
    private JXSearchField xSearchField1;
    private JXTaskPane xTaskPane3;
    private JButton btnSearchEditProducts;
    private JXTaskPane xTaskPane2;
    private JPanel pnlMain;
    private JScrollPane jspProdukt;

    //    boolean isOnlySameClassesAreSelected() {
//        boolean sameClasses = true;
//        Class prevClass = tree.getSelectionPaths()[0].getLastPathComponent().getClass();
//        for (TreePath path : tree.getSelectionPaths()) {
//            Class thisClass = path.getLastPathComponent().getClass();
//            if (!thisClass.equals(prevClass)) {
//                sameClasses = false;
//                break;
//            }
//            prevClass = thisClass;
//        }
//        return sameClasses;
//    }
    private JTable tblProdukt;

    public FrmProdukte() {
        initComponents();
        thisComponent = this;
        criteria = new Pair<Integer, Object>(Const.ALLE, null);
        loadTable();
        createTree();
        setTitle(Tools.getWindowTitle("Produkte-Verwaltung"));
        pack();
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        criteria = new Pair<Integer, Object>(Const.NAME_NR, xSearchField1.getText());
        loadTable();
    }

    private void loadTable() {

        List list = null;

        if (criteria.getFirst() == Const.ALLE) {
            EntityManager em = Main.getEMF().createEntityManager();
            Query query = em.createNamedQuery("Produkte.findAllSorted");
            list = query.getResultList();
            em.close();
        } else if (criteria.getFirst() == Const.NAME_NR) {
            list = ProdukteTools.searchProdukte(criteria.getSecond().toString());
        } else if (criteria.getFirst() == Const.STOFFART) {
            list = ProdukteTools.getProdukte((IngTypes) criteria.getSecond());
        } else if (criteria.getFirst() == Const.WARENGRUPPE) {
            list = ProdukteTools.searchProdukte((Warengruppe) criteria.getSecond());
        }

        tblProdukt.setModel(new ProdukteTableModel(list, spalten));

        TableRowSorter sorter = new TableRowSorter(tblProdukt.getModel());
//        sorter.setComparator(ProdukteTableModel.COL_LAGERART, new Comparator<Short>() {
//            public int compare(Short l1, Short l2) {
//                return LagerTools.LAGERART[l1].compareTo(LagerTools.LAGERART[l2]);
//            }
//        });
//
//        sorter.addRowSorterListener(new RowSorterListener() {
//            @Override
//            public void sorterChanged(RowSorterEvent e) {
//
//            }
//        });

        tblProdukt.setRowSorter(sorter);

        tblProdukt.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        pnlMainComponentResized(null);

    }

    private void btnSearchAllActionPerformed(ActionEvent e) {
        criteria = new Pair<Integer, Object>(Const.ALLE, null);
        loadTable();
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
                em.lock(altesProdukt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                for (Vorrat v : altesProdukt.getVorratCollection()) {
                    Vorrat myVorrat = em.merge(v);
                    myVorrat.setProdukt(neuesProdukt);
                }
                altesProdukt.getVorratCollection().clear();

                for (Allergene allergene : altesProdukt.getAllergenes()) {
                    neuesProdukt.getAllergenes().add(em.merge(allergene));
                }
                altesProdukt.getAllergenes().clear();

                for (Additives additives : altesProdukt.getAdditives()) {
                    neuesProdukt.getAdditives().add(em.merge(additives));
                }
                altesProdukt.getAdditives().clear();

                em.remove(altesProdukt);
            }
            em.getTransaction().commit();
            success = true;


        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
        } catch (Exception e) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
        }
        return success;
    }

    private void tblProduktMousePressed(MouseEvent e) {

        final ProdukteTableModel tm = (ProdukteTableModel) tblProdukt.getModel();
        if (tm.getRowCount() == 0) {
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
                    ArrayList<Produkte> listProdukte = new ArrayList<Produkte>(rows.length);
                    for (int r = 0; r < rows.length; r++) {
                        final int finalR = r;
                        final int thisRow = tblProdukt.convertRowIndexToModel(rows[finalR]);
                        listProdukte.add(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow));
                    }
                    new DlgProdukt(Main.mainframe, listProdukte);
                    loadTable();
                }
            });
            miEdit.setEnabled(rows.length > 0);
            menu.add(miEdit);

            JMenuItem miInfo = new JMenuItem("Vorrat Info");
            miInfo.setFont(new Font("arial", Font.PLAIN, 18));

            miInfo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String ids = "";
                    BigDecimal menge = BigDecimal.ZERO;
                    int active = 0;
                    for (Vorrat vorrat : listSelectedProducts.get(0).getVorratCollection()) {
                        if (!vorrat.isAusgebucht()) {
                            active++;
                            ids += vorrat.getId() + ", " + DateFormat.getDateInstance(DateFormat.SHORT).format(vorrat.getEingang()) + (active % 6 == 0 ? "\n" : "; ");
                            menge = menge.add(VorratTools.getSumme(vorrat));
                        }
                    }

                    String text = "Produkt ID: " + listSelectedProducts.get(0).getId() + "\n";
                    text += listSelectedProducts.get(0).getVorratCollection().size() + " Vorräte insgesamt.\n";
                    text += "Davon " + active + " noch nicht verbraucht.\n";
                    if (active > 0) {
                        text += "Noch vorhandene Gesamtmenge: " + menge.setScale(2, RoundingMode.HALF_UP).toString() + " " + IngTypesTools.EINHEIT[listSelectedProducts.get(0).getIngTypes().getEinheit()] + "\n";
                        text += "Active IDs: " + ids;
                    }

                    JOptionPane.showInternalMessageDialog(thisComponent, text, "Vorrat Info", JOptionPane.INFORMATION_MESSAGE);

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
                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                Produkte myProdukt = em1.merge(p);
                                em1.remove(myProdukt);
                            }
                        }
                        em1.getTransaction().commit();
                    } catch (OptimisticLockException ole) {
                        em1.getTransaction().rollback();
                    } catch (Exception exc) {
                        em1.getTransaction().rollback();
                        Main.fatal(e);
                    } finally {
                        em1.close();
                        loadTable();
                    }
                    loadTable();
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
                                loadTable();
                            }
                        }
                    });

                    miPopupMerge.add(mi);
                }

                menu.add(miPopupMerge);
            }


            JMenu menuLagerart = new JMenu("Lagerart setzen");
            menuLagerart.setFont(new Font("arial", Font.PLAIN, 18));
            for (final String lagerart : LagerTools.LAGERART) {

                JMenuItem miLagerart = new JMenuItem(lagerart);
                miLagerart.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                        EntityManager em1 = Main.getEMF().createEntityManager();
                        try {
                            em1.getTransaction().begin();
                            for (Produkte p : listSelectedProducts) {
                                Produkte myProdukt = em1.merge(p);
                                em1.lock(myProdukt, LockModeType.OPTIMISTIC);
                                myProdukt.getIngTypes().setLagerart((short) ArrayUtils.indexOf(LagerTools.LAGERART, lagerart));
                            }
                            em1.getTransaction().commit();
                        } catch (OptimisticLockException ole) {
                            em1.getTransaction().rollback();
                        } catch (Exception exc) {
                            em1.getTransaction().rollback();
                            Main.fatal(e);
                        } finally {
                            em1.close();
                            loadTable();
                        }
//                        }
                    }
                });
                menuLagerart.add(miLagerart);


            }
            menu.add(menuLagerart);


            JMenu menuEinheit = new JMenu("Einheit setzen");
            menuEinheit.setFont(new Font("arial", Font.PLAIN, 18));
            for (final String einheit : IngTypesTools.EINHEIT) {

                JMenuItem miEinheit = new JMenuItem(einheit);
                miEinheit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                        EntityManager em1 = Main.getEMF().createEntityManager();
                        try {
                            em1.getTransaction().begin();
                            for (Produkte p : listSelectedProducts) {
                                Produkte myProdukt = em1.merge(p);
                                em1.lock(myProdukt, LockModeType.OPTIMISTIC);
                                myProdukt.getIngTypes().setEinheit((short) ArrayUtils.indexOf(IngTypesTools.EINHEIT, einheit));
                            }
                            em1.getTransaction().commit();
                        } catch (OptimisticLockException ole) {
                            em1.getTransaction().rollback();
                        } catch (Exception exc) {
                            em1.getTransaction().rollback();
                            Main.fatal(e);
                        } finally {
                            em1.close();
                            loadTable();
                        }
//                        }
                    }
                });
                menuEinheit.add(miEinheit);
            }
            menu.add(menuEinheit);

            JMenu menuPopupAssign = new JMenu("zuweisen zu Stoffart");
            menuPopupAssign.setFont(new Font("arial", Font.PLAIN, 18));


            EntityManager em = Main.getEMF().createEntityManager();
            try {
                Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
                ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());


                for (Warengruppe warengruppe : listWarengruppen) {

                    JMenu menuWarengruppe = new JMenu(warengruppe.getBezeichnung());

                    ArrayList<IngTypes> listStoffarten = new ArrayList<IngTypes>(warengruppe.getIngTypesCollection());
                    Collections.sort(listStoffarten);

                    for (final IngTypes ingTypes : listStoffarten) {
                        JMenuItem miStoffart = new JMenuItem(ingTypes.getBezeichnung());
                        miStoffart.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                    EntityManager em1 = Main.getEMF().createEntityManager();
                                    try {
                                        em1.getTransaction().begin();
                                        for (Produkte p : listSelectedProducts) {
                                            Produkte myProdukt = em1.merge(p);
                                            em1.lock(myProdukt, LockModeType.OPTIMISTIC);
                                            myProdukt.setIngTypes(em1.merge(ingTypes));
                                        }
                                        em1.getTransaction().commit();
                                    } catch (OptimisticLockException ole) {
                                        em1.getTransaction().rollback();
                                    } catch (Exception exc) {
                                        em1.getTransaction().rollback();
                                        Main.fatal(e);
                                    } finally {
                                        em1.close();
                                        loadTable();
                                    }
                                }
                            }
                        });
                        menuWarengruppe.add(miStoffart);
                    }
                    menuPopupAssign.add(menuWarengruppe);
                }
            } catch (Exception exc) {
                Main.fatal(exc);
            } finally {
                em.close();
            }

            menu.add(menuPopupAssign);


            menu.show(tblProdukt, (int) p.getX(), (int) p.getY());
        }


    }

    private void createTree() {
        xTaskPane2.removeAll();

        String expansion = null;
        if (tree != null) {
            expansion = Tools.getExpansionState(tree, 0);
            tree.removeAll();
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Warengruppen");
        tree = new JTree(root);
//        tree.setOpaque(false);


        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
            ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());

            for (Warengruppe warengruppe : listWarengruppen) {

                DefaultMutableTreeNode nodeWG = new DefaultMutableTreeNode(warengruppe);
                root.add(nodeWG);

                ArrayList<IngTypes> listStoffarten = new ArrayList<IngTypes>(warengruppe.getIngTypesCollection());
                Collections.sort(listStoffarten);

                for (IngTypes ingTypes : listStoffarten) {
                    DefaultMutableTreeNode nodeSA = new DefaultMutableTreeNode(ingTypes);
                    nodeWG.add(nodeSA);
                }
            }
        } catch (Exception e) {
            Main.fatal(e);
        } finally {
            em.close();
        }

        tree.setCellRenderer(new TreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

//                Main.debug(value.getClass().getName());

                String text = value.toString();

                if (value instanceof DefaultMutableTreeNode) {
                    if (((DefaultMutableTreeNode) value).getUserObject() instanceof Warengruppe) {

                        Warengruppe warengruppe = (Warengruppe) ((DefaultMutableTreeNode) value).getUserObject();

                        text = warengruppe.getBezeichnung() + " [" + WarengruppeTools.getNumOfProducts(warengruppe) + "]";
                    } else if (((DefaultMutableTreeNode) value).getUserObject() instanceof IngTypes) {

                        IngTypes ingTypes = (IngTypes) ((DefaultMutableTreeNode) value).getUserObject();

                        text = ingTypes.getBezeichnung() + " [" + IngTypesTools.getNumOfProducts(ingTypes) + "]";
                    }
                }

                setFont(new Font("arial", Font.PLAIN, 16));
//                setOpaque(false);

                return new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row, hasFocus);
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                Point p = e.getPoint();
                final int row = tree.getClosestRowForLocation(p.x, p.y);
//                final int row = tblProdukt.rowAtPoint(p);
                TreeSelectionModel tsm = tree.getSelectionModel();

                //lsm.setSelectionInterval(row, row);
                boolean singleRowSelected = tsm.getMaxSelectionRow() == tsm.getMinSelectionRow();

                if (singleRowSelected) {
                    tsm.setSelectionPath(tree.getPathForRow(row));
                }

                if (e.isPopupTrigger()) {
                    if (menu != null && menu.isVisible()) {
                        menu.setVisible(false);
                    }

                    Tools.unregisterListeners(menu);
                    menu = new JPopupMenu();


                    JMenuItem miNewWarengruppe = new JMenuItem("Neue Warengruppe erstellen");
                    miNewWarengruppe.setFont(new Font("arial", Font.PLAIN, 18));
                    miNewWarengruppe.setEnabled(singleRowSelected);
                    miNewWarengruppe.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String newText = JOptionPane.showInputDialog(thisComponent, "Warengruppe", "Neu erstellen", JOptionPane.OK_CANCEL_OPTION);
                            if (newText != null && !newText.trim().isEmpty()) {
                                EntityManager em = Main.getEMF().createEntityManager();
                                try {
                                    em.getTransaction().begin();
                                    Warengruppe myWarengruppe = em.merge(new Warengruppe(newText));
                                    em.getTransaction().commit();
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                } catch (Exception exc) {
                                    em.getTransaction().rollback();
                                    Main.fatal(e);
                                } finally {
                                    em.close();
                                    createTree();
                                }
                            }
                        }
                    });
                    miNewWarengruppe.setEnabled(singleRowSelected && ((DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent()).getUserObject() instanceof Warengruppe);
                    menu.add(miNewWarengruppe);

                    JMenuItem miNewStoffart = new JMenuItem("Neue Stoffart erstellen");
                    miNewStoffart.setFont(new Font("arial", Font.PLAIN, 18));
                    miNewStoffart.setEnabled(singleRowSelected);
                    miNewStoffart.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String newText = JOptionPane.showInputDialog(thisComponent, "Stoffart", "Neu erstellen", JOptionPane.OK_CANCEL_OPTION);
                            if (newText != null && !newText.trim().isEmpty()) {
                                EntityManager em = Main.getEMF().createEntityManager();
                                try {
                                    em.getTransaction().begin();
                                    DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();
                                    Warengruppe myWarengruppe = em.merge((Warengruppe) thisNode.getUserObject());
                                    IngTypes myIngTypes = em.merge(new IngTypes(newText, myWarengruppe));
                                    em.getTransaction().commit();
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                } catch (Exception exc) {
                                    em.getTransaction().rollback();
                                    Main.fatal(e);
                                } finally {
                                    em.close();
                                    createTree();
                                }
                            }
                        }
                    });
                    miNewStoffart.setEnabled(singleRowSelected && ((DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent()).getUserObject() instanceof Warengruppe);
                    menu.add(miNewStoffart);

                    JMenuItem miRename = new JMenuItem("Markiertes Objekt umbennen");
                    miRename.setFont(new Font("arial", Font.PLAIN, 18));
                    miRename.setEnabled(singleRowSelected);
                    miRename.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

                            String text = "";
                            if (thisNode.getUserObject() instanceof Warengruppe) {
                                text = ((Warengruppe) thisNode.getUserObject()).getBezeichnung();

                            } else if (thisNode.getUserObject() instanceof IngTypes) {
                                text = ((IngTypes) thisNode.getUserObject()).getBezeichnung();
                            }

                            String newText = JOptionPane.showInputDialog(thisComponent, "Bezeichnung", text);
                            if (newText != null && !newText.trim().isEmpty()) {
                                EntityManager em = Main.getEMF().createEntityManager();
                                try {
                                    em.getTransaction().begin();

                                    if (thisNode.getUserObject() instanceof IngTypes) {
                                        IngTypes myIngTypes = em.merge((IngTypes) thisNode.getUserObject());
                                        em.lock(myIngTypes, LockModeType.OPTIMISTIC);
                                        myIngTypes.setBezeichnung(newText);
                                    } else if (thisNode.getUserObject() instanceof Warengruppe) {
                                        Warengruppe myWarengruppe = em.merge((Warengruppe) thisNode.getUserObject());
                                        em.lock(myWarengruppe, LockModeType.OPTIMISTIC);
                                        myWarengruppe.setBezeichnung(newText);
                                    }

                                    em.getTransaction().commit();
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                } catch (Exception exc) {
                                    em.getTransaction().rollback();
                                    Main.fatal(e);
                                } finally {
                                    em.close();
                                    createTree();
                                }
                            }

                        }
                    });
                    menu.add(miRename);

                    if (isOnlyStoffartSelected()) {

                        JMenuItem miDelete = new JMenuItem("Markierte Stoffgruppe löschen, wenn leer");
                        miDelete.setFont(new Font("arial", Font.PLAIN, 18));
//                        miDelete.setEnabled(StoffartTools.getNumOfProducts(stoffart) == 0);
                        miDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                    EntityManager em = Main.getEMF().createEntityManager();
                                    try {
                                        em.getTransaction().begin();

                                        for (TreePath path : tree.getSelectionPaths()) {
                                            DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                                            IngTypes myIngTypes = em.merge((IngTypes) thisNode.getUserObject());
                                            if (myIngTypes.getProdukteCollection().isEmpty()) {
                                                em.remove(myIngTypes);
                                            }
                                        }

                                        em.getTransaction().commit();
                                    } catch (OptimisticLockException ole) {
                                        em.getTransaction().rollback();
                                    } catch (Exception exc) {
                                        em.getTransaction().rollback();
                                        Main.fatal(e);
                                    } finally {
                                        em.close();
                                        createTree();
                                    }
                                }
                            }
                        });
                        menu.add(miDelete);
                    }

                    if (isOnlyStoffartSelected() && singleRowSelected) {
                        final IngTypes ingTypes = (IngTypes) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
                        JMenu menuDeleteReplace = new JMenu("Markierte Stoffgruppe löschen, Produkte zuweisen zu");
                        menuDeleteReplace.setFont(new Font("arial", Font.PLAIN, 18));
                        menuDeleteReplace.setEnabled(IngTypesTools.getNumOfProducts(ingTypes) > 0);

                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
                            ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());


                            for (Warengruppe warengruppe : listWarengruppen) {

                                JMenu menuWarengruppe = new JMenu(warengruppe.getBezeichnung());

                                ArrayList<IngTypes> listStoffarten = new ArrayList<IngTypes>(warengruppe.getIngTypesCollection());
                                Collections.sort(listStoffarten);

                                for (final IngTypes s : listStoffarten) {
                                    JMenuItem miStoffart = new JMenuItem(s.getBezeichnung());
                                    miStoffart.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                                EntityManager em1 = Main.getEMF().createEntityManager();
                                                try {
                                                    em1.getTransaction().begin();
                                                    IngTypes myIngTypes = em1.merge(s);
                                                    IngTypes ingTypes2delete = em1.merge(ingTypes);
                                                    for (Produkte p : ingTypes2delete.getProdukteCollection()) {
                                                        Produkte myProdukt = em1.merge(p);
                                                        em1.lock(myProdukt, LockModeType.OPTIMISTIC);
                                                        myProdukt.setIngTypes(myIngTypes);
                                                    }
                                                    ingTypes2delete.getProdukteCollection().clear();
                                                    em1.remove(ingTypes2delete);
                                                    em1.getTransaction().commit();
                                                } catch (OptimisticLockException ole) {
                                                    em1.getTransaction().rollback();
                                                } catch (Exception exc) {
                                                    em1.getTransaction().rollback();
                                                    Main.fatal(e);
                                                } finally {
                                                    em1.close();
                                                    createTree();
                                                }
                                            }
                                        }
                                    });
                                    menuWarengruppe.add(miStoffart);
                                }
                                menuDeleteReplace.add(menuWarengruppe);
                            }
                        } catch (Exception exc) {
                            Main.fatal(exc);
                        } finally {
                            em.close();
                        }
                        menu.add(menuDeleteReplace);

                    }


                    if (isOnlyWarengruppeSelected() && singleRowSelected) {

                        final Warengruppe warengruppe = (Warengruppe) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();

                        JMenuItem miDelete = new JMenuItem("Markierte Warengruppe löschen");
                        miDelete.setFont(new Font("arial", Font.PLAIN, 18));
                        miDelete.setEnabled(WarengruppeTools.getNumOfProducts(warengruppe) == 0);
                        miDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                    EntityManager em = Main.getEMF().createEntityManager();
                                    try {
                                        em.getTransaction().begin();

                                        Warengruppe myWarengruppe = em.merge(warengruppe);
                                        em.remove(myWarengruppe);

                                        em.getTransaction().commit();
                                    } catch (OptimisticLockException ole) {
                                        em.getTransaction().rollback();
                                    } catch (Exception exc) {
                                        em.getTransaction().rollback();
                                        Main.fatal(e);
                                    } finally {
                                        em.close();
                                        createTree();
                                    }
                                }
                            }
                        });
                        menu.add(miDelete);
                    }

                    if (isOnlyStoffartSelected()) {

                        JMenu menuAssign = new JMenu("Stoffgruppe[n] zuordnen zu");
                        menuAssign.setFont(new Font("arial", Font.PLAIN, 18));


                        DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();

                        if (isAllHaveTheSameWarengruppe()) {
                            EntityManager em = Main.getEMF().createEntityManager();
                            try {
                                Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
                                ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());
                                for (final Warengruppe warengruppe : listWarengruppen) {
                                    JMenuItem miWarengruppe = new JMenuItem(warengruppe.getBezeichnung());
                                    miWarengruppe.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                                EntityManager em = Main.getEMF().createEntityManager();
                                                try {
                                                    em.getTransaction().begin();
                                                    for (TreePath path : tree.getSelectionPaths()) {
                                                        DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                                                        IngTypes myIngTypes = em.merge((IngTypes) thisNode.getUserObject());
                                                        em.lock(myIngTypes, LockModeType.OPTIMISTIC);
                                                        myIngTypes.setWarengruppe(em.merge(warengruppe));
                                                    }
                                                    em.getTransaction().commit();
                                                } catch (OptimisticLockException ole) {
                                                    em.getTransaction().rollback();
                                                } catch (Exception exc) {
                                                    em.getTransaction().rollback();
                                                    Main.fatal(e);
                                                } finally {
                                                    em.close();
                                                    createTree();
                                                }
                                            }
                                        }
                                    });
                                    menuAssign.add(miWarengruppe);
                                }
                            } catch (Exception exc) {
                                Main.fatal(exc);
                            } finally {
                                em.close();
                            }

                            menu.add(menuAssign);
                        }
                    }
                    menu.show(tree, (int) p.getX(), (int) p.getY());
                }


            }
        });


        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                JTree myTree = (JTree) e.getSource();

//
//                if (myTree.getSelectionPaths().length != 1) {
//                    tblProdukt.setModel(new DefaultTableModel());
//                    return;
//                }

                TreePath path = e.getNewLeadSelectionPath();

                if (path != null) {
                    DefaultMutableTreeNode lastComponent = (DefaultMutableTreeNode) path.getLastPathComponent();


                    if (lastComponent.getUserObject() instanceof IngTypes) {
                        criteria = new Pair<Integer, Object>(Const.STOFFART, lastComponent.getUserObject());
                        loadTable();
                    } else if (lastComponent.getUserObject() instanceof Warengruppe) {
                        criteria = new Pair<Integer, Object>(Const.WARENGRUPPE, lastComponent.getUserObject());
                        loadTable();
                    }

                }
            }
        });


        if (expansion != null) {
            Tools.restoreExpansionState(tree, 0, expansion);
        }

        xTaskPane2.add(tree);

    }

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
        Tools.packTable(tblProdukt, 0);
    }

    private void btnSearchEditProductsActionPerformed(ActionEvent e) {
        DlgProdukt dlg = new DlgProdukt(Main.mainframe, null);
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
                loadTable();
            }
        });
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
        xTaskPane2 = new JXTaskPane();
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
                    xSearchField1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            xSearchField1ActionPerformed(e);
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
                    btnSearchEditProducts.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSearchEditProductsActionPerformed(e);
                        }
                    });
                    xTaskPane3.add(btnSearchEditProducts);
                }
                pnlSearch.add(xTaskPane3);

                //======== xTaskPane2 ========
                {
                    xTaskPane2.setTitle("Warengruppen");
                    xTaskPane2.setFont(new Font("sansserif", Font.BOLD, 18));
                    xTaskPane2.setCollapsed(true);
                    xTaskPane2.setLayout(new VerticalLayout(10));
                }
                pnlSearch.add(xTaskPane2);
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
