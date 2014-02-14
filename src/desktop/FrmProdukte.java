/*
 * Created by JFormDesigner on Thu Aug 11 16:45:25 CEST 2011
 */

package desktop;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class FrmProdukte extends JInternalFrame {

    private Object[] spalten = new Object[]{"Produkt Nr.", "Bezeichnung", "Lagerart", "GTIN", "Packungsgröße", "Einheit", "Stoffart", "Warengruppe"};

    private JPopupMenu menu;

    private Pair<Integer, Object> criteria;

    private JComponent thisComponent;


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
            list = ProdukteTools.searchProdukte((Stoffart) criteria.getSecond());
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

        tblProdukt.setRowSorter(sorter);

        tblProdukt.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //pnlVorratComponentResized(null);

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
            em.lock(neuesProdukt, LockModeType.OPTIMISTIC);
            for (Produkte p : listProducts2Merge) {
                Produkte altesProdukt = em.merge(p);
                em.lock(altesProdukt, LockModeType.OPTIMISTIC);
                VorratTools.tauscheProdukt(em, altesProdukt, neuesProdukt);
                Main.debug("Lösche Produkt (wegen Merge): " + altesProdukt + " (" + altesProdukt.getId() + ")");
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

        if (e.isPopupTrigger()) {


            if (menu != null && menu.isVisible()) {
                menu.setVisible(false);
            }

            Tools.unregisterListeners(menu);
            menu = new JPopupMenu();

            JMenuItem miEdit = new JMenuItem("Markierte Produkte bearbeiten");
            miEdit.setFont(new Font("sansserif", Font.PLAIN, 18));
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


            if (listSelectedProducts.size() > 1) {
                JMenu miPopupMerge = new JMenu("Markierte Produkte zusammenfassen zu");
                miPopupMerge.setFont(new Font("sansserif", Font.PLAIN, 18));

                for (final Produkte thisProduct : listSelectedProducts) {
                    JMenuItem mi = new JMenuItem("[" + thisProduct.getId() + "] " + thisProduct.getBezeichnung());
                    mi.setFont(new Font("sansserif", Font.PLAIN, 18));

                    mi.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zusammenfassen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                mergeUs(listSelectedProducts, thisProduct);
                            }
                        }
                    });

                    miPopupMerge.add(mi);
                }

                menu.add(miPopupMerge);
            }


            JMenu menuPopupAssign = new JMenu("zuweisen zu");
            menuPopupAssign.setFont(new Font("sansserif", Font.PLAIN, 18));


            EntityManager em = Main.getEMF().createEntityManager();
            try {
                Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
                ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());


                for (Warengruppe warengruppe : listWarengruppen) {

                    JMenu menuWarengruppe = new JMenu(warengruppe.getBezeichnung());

                    ArrayList<Stoffart> listStoffarten = new ArrayList<Stoffart>(warengruppe.getStoffartCollection());
                    Collections.sort(listStoffarten);

                    for (final Stoffart stoffart : listStoffarten) {
                        JMenuItem miStoffart = new JMenuItem(stoffart.getBezeichnung());
                        miStoffart.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                    EntityManager em = Main.getEMF().createEntityManager();
                                    try {
                                        em.getTransaction().begin();
                                        for (Produkte p : listSelectedProducts) {
                                            Produkte myProdukt = em.merge(p);
                                            em.lock(myProdukt, LockModeType.OPTIMISTIC);
                                            myProdukt.setStoffart(stoffart);
                                        }
                                        em.getTransaction().commit();
                                    } catch (OptimisticLockException ole) {
                                        em.getTransaction().rollback();
                                    } catch (Exception exc) {
                                        em.getTransaction().rollback();
                                        Main.fatal(e);
                                    } finally {
                                        em.close();
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

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Warengruppen");
        final JTree tree = new JTree(root);
        tree.setOpaque(false);


        EntityManager em = Main.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
            ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());

            for (Warengruppe warengruppe : listWarengruppen) {

                DefaultMutableTreeNode nodeWG = new DefaultMutableTreeNode(warengruppe);
                root.add(nodeWG);

                ArrayList<Stoffart> listStoffarten = new ArrayList<Stoffart>(warengruppe.getStoffartCollection());
                Collections.sort(listStoffarten);

                for (Stoffart stoffart : listStoffarten) {
                    DefaultMutableTreeNode nodeSA = new DefaultMutableTreeNode(stoffart);
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
                    } else if (((DefaultMutableTreeNode) value).getUserObject() instanceof Stoffart) {

                        Stoffart stoffart = (Stoffart) ((DefaultMutableTreeNode) value).getUserObject();

                        text = stoffart.getBezeichnung() + " [" + StoffartTools.getNumOfProducts(stoffart) + "]";
                    }
                }


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

                    JMenuItem miEdit = new JMenuItem("Markiertes Objekt bearbeiten");
//                    miEdit.setFont(new Font("sansserif", Font.PLAIN, 18));

                    menu.add(miEdit);


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


                    if (lastComponent.getUserObject() instanceof Stoffart) {
                        criteria = new Pair<Integer, Object>(Const.STOFFART, lastComponent.getUserObject());
                        loadTable();
                    } else if (lastComponent.getUserObject() instanceof Warengruppe) {
                        criteria = new Pair<Integer, Object>(Const.WARENGRUPPE, lastComponent.getUserObject());
                        loadTable();
                    }

                }
            }
        });


        xTaskPane2.add(tree);

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspSearch = new JScrollPane();
        pnlSearch = new JXTaskPaneContainer();
        xTaskPane1 = new JXTaskPane();
        btnSearchAll = new JButton();
        xSearchField1 = new JXSearchField();
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
                "249dlu, default:grow",
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


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspSearch;
    private JXTaskPaneContainer pnlSearch;
    private JXTaskPane xTaskPane1;
    private JButton btnSearchAll;
    private JXSearchField xSearchField1;
    private JXTaskPane xTaskPane2;
    private JPanel pnlMain;
    private JScrollPane jspProdukt;
    private JTable tblProdukt;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
