/*
 * Created by JFormDesigner on Mon Sep 15 15:37:14 CEST 2014
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
import tablemodels.IngTypeTableModel;
import tools.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class FrmIngType extends JInternalFrame implements MyInternalFrames {

    private Pair<Integer, Object> criteria;
    private JPopupMenu menu;
    private JInternalFrame thisComponent;

    public FrmIngType() {
        initComponents();
        thisComponent = this;
        criteria = new Pair<Integer, Object>(Const.ALLE, null);
        loadTable();

        setTitle(Tools.getWindowTitle("Produkte-Verwaltung"));
        pack();
    }


    @Override
    public void reload() {
        loadTable();
    }

    private void loadTable() {

        java.util.List list = null;


        if (criteria.getFirst() == Const.ALLE) {
            EntityManager em = Main.getEMF().createEntityManager();
            Query query = em.createQuery("" +
                    " SELECT t FROM IngTypes t" +
                    " ORDER BY t.bezeichnung ");
            list = query.getResultList();
            em.close();
        } else if (criteria.getFirst() == Const.NAME_NR) {
//            list = ProdukteTools.searchProdukte(criteria.getSecond().toString());
        } else if (criteria.getFirst() == Const.STOFFART) {
//            list = ProdukteTools.getProdukte((Stoffart) criteria.getSecond());
        } else if (criteria.getFirst() == Const.WARENGRUPPE) {
//            list = ProdukteTools.searchProdukte((Warengruppe) criteria.getSecond());
        }

        tblTypes.setModel(new IngTypeTableModel(list));

//        tblTypes.getColumnModel().getColumn(IngTypeTableModel.COL_LAGERART).setCellRenderer(IngTypesTools.getStorageRenderer());
//        tblTypes.getColumnModel().getColumn(IngTypeTableModel.COL_LAGERART).setCellEditor(IngTypesTools.getStorageEditor());
        tblTypes.getColumnModel().getColumn(IngTypeTableModel.COL_WARENGRUPPE).setCellRenderer(WarengruppeTools.getTableCellRenderer());
        tblTypes.getColumnModel().getColumn(IngTypeTableModel.COL_WARENGRUPPE).setCellEditor(WarengruppeTools.getTableCellEditor());
        tblTypes.getColumnModel().getColumn(IngTypeTableModel.COL_EINHEIT).setCellRenderer(LagerTools.getEinheitTableCellRenderer());
        tblTypes.getColumnModel().getColumn(IngTypeTableModel.COL_EINHEIT).setCellEditor(LagerTools.getEinheitTableCellEditor());

        ((IngTypeTableModel) tblTypes.getModel()).setEditable(true);

//        TableRowSorter sorter = new TableRowSorter(tblProdukt.getModel());
        //        sorter.setComparator(ProdukteTableModel.COL_LAGERART, new Comparator<Short>() {
        //            public int compare(Short l1, Short l2) {
        //                return LagerTools.LAGERART[l1].compareTo(LagerTools.LAGERART[l2]);
        //            }
        //        });


        tblTypes.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        thisComponentResized(null);

    }

    private void btnSearchAllActionPerformed(ActionEvent e) {
        criteria = new Pair<Integer, Object>(Const.ALLE, null);
        reload();
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void thisComponentResized(ComponentEvent e) {
        Tools.packTable(tblTypes, 0);
    }

    private void tblTypesMousePressed(MouseEvent e) {
        final IngTypeTableModel tm = (IngTypeTableModel) tblTypes.getModel();
        if (tm.getRowCount() == 0) {
            return;
        }
        Point p = e.getPoint();
        final int col = tblTypes.columnAtPoint(p);
        final int row = tblTypes.rowAtPoint(p);
        ListSelectionModel lsm = tblTypes.getSelectionModel();

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

            if (tblTypes.getSelectedRows().length > 1) {
                JMenu miPopupMerge = new JMenu("Markierte Stoffarten zusammenfassen zu");
                miPopupMerge.setFont(new Font("arial", Font.PLAIN, 18));

                final ArrayList<IngTypes> listSelectedTypes = new ArrayList<IngTypes>();
                for (int thisRow : tblTypes.getSelectedRows()) {
                    listSelectedTypes.add(tm.getIngType(thisRow));
                }

                for (final IngTypes thisIngType : listSelectedTypes) {


                    JMenuItem mi = new JMenuItem("[" + thisIngType.getId() + "] " + thisIngType.getBezeichnung());
                    mi.setFont(new Font("arial", Font.PLAIN, 18));

                    mi.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zusammenfassen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
                                IngTypesTools.mergeUs(listSelectedTypes, thisIngType);
                                loadTable();
                            }
                        }
                    });

                    miPopupMerge.add(mi);
                }

                menu.add(miPopupMerge);
                menu.add(new JSeparator());
            }


//            if (tblTypes.getSelectedRows().length == 1) {
//
//                final IngTypes thisIngType = tm.getIngType(row);
//
//                JMenu miPopupProducts = new JMenu("Produkte anzeigen (" + thisIngType.getProdukteCollection().size() + ")");
//                miPopupProducts.setFont(new Font("arial", Font.PLAIN, 18));
//
//                final ArrayList<IngTypes> listSelectedTypes = new ArrayList<IngTypes>();
//                for (int thisRow : tblTypes.getSelectedRows()) {
//                    listSelectedTypes.add(tm.getIngType(thisRow));
//                }
//
//                final JList<Produkte> jListProdukte = new JList<Produkte>(thisIngType.getProdukteCollection().toArray(new Produkte[]{}));
//                jListProdukte.setCellRenderer(ProdukteTools.getListCellRenderer());
//                jListProdukte.setVisibleRowCount(30);
//                jListProdukte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//                jListProdukte.addListSelectionListener(new ListSelectionListener() {
//                    @Override
//                    public void valueChanged(ListSelectionEvent e) {
//                        if (e.getValueIsAdjusting()) return;
//                        ArrayList<Produkte> listProdukte = new ArrayList<Produkte>();
//                        listProdukte.add(jListProdukte.getSelectedValue());
//                        new DlgProdukt(Main.mainframe, listProdukte);
//                        loadTable();
//                    }
//                });
//                miPopupProducts.add(new JScrollPane(jListProdukte));
//
//                menu.add(miPopupProducts);
//                menu.add(new JSeparator());
//            }

            final JMenuItem miAllergics = new JMenuItem("Allergene zuordnen");
            miAllergics.setFont(new Font("arial", Font.PLAIN, 18));

            miAllergics.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Allergene> listAllergenes = new ArrayList<Allergene>();
                    for (int index : tblTypes.getSelectedRows()) {
                        for (Allergene allergene : tm.getIngType(index).getAllergenes()) {
                            if (!listAllergenes.contains(allergene)) {
                                listAllergenes.add(allergene);
                            }
                        }
                    }

//                    final JidePopup popup = new JidePopup();

                    final PnlAssign<Allergene> pnlAssign = new PnlAssign<Allergene>(listAllergenes, AllergeneTools.getAll(), AllergeneTools.getListCellRenderer());


                    int answer = JOptionPane.showInternalConfirmDialog(thisComponent, pnlAssign, "test", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (answer == JOptionPane.OK_OPTION) {
                        if (pnlAssign.getAssigned() == null) return;

                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();

                            for (int index : tblTypes.getSelectedRows()) {
                                IngTypes myIngType = em.merge(tm.getIngType(index));
                                em.lock(myIngType, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                myIngType.getAllergenes().clear();
                                for (Allergene allergene : pnlAssign.getAssigned()) {
                                    myIngType.getAllergenes().add(em.merge(allergene));
                                }
                            }

                            em.getTransaction().commit();
                        } catch (OptimisticLockException ole) {
                            Main.warn(ole);
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
            menu.add(miAllergics);


            final JMenuItem miAdditives = new JMenuItem("Zusatzstoffe zuordnen");
            miAdditives.setFont(new Font("arial", Font.PLAIN, 18));

            miAdditives.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Additives> listAdditives = new ArrayList<Additives>();
                    for (int index : tblTypes.getSelectedRows()) {
                        for (Additives additive : tm.getIngType(index).getAdditives()) {
                            if (!listAdditives.contains(additive)) {
                                listAdditives.add(additive);
                            }
                        }
                    }

                    final PnlAssign<Additives> pnlAssign = new PnlAssign<Additives>(listAdditives, AdditivesTools.getAll(), AdditivesTools.getListCellRenderer());

                    int answer = JOptionPane.showInternalConfirmDialog(thisComponent, pnlAssign, "test", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (answer == JOptionPane.OK_OPTION) {
                        if (pnlAssign.getAssigned() == null) return;

                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();

                            for (int index : tblTypes.getSelectedRows()) {
                                IngTypes myIngType = em.merge(tm.getIngType(index));
                                em.lock(myIngType, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                myIngType.getAdditives().clear();
                                for (Additives additives : pnlAssign.getAssigned()) {
                                    myIngType.getAdditives().add(em.merge(additives));
                                }
                            }

                            em.getTransaction().commit();
                        } catch (OptimisticLockException ole) {
                            Main.warn(ole);
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
            menu.add(miAdditives);


//
//            JMenuItem miDelete = new JMenuItem("löschen (inkl. Vorräte)");
//            miDelete.setFont(new Font("arial", Font.PLAIN, 18));
//
//            miDelete.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    EntityManager em1 = Main.getEMF().createEntityManager();
//                    try {
//                        em1.getTransaction().begin();
//                        for (Produkte p : listSelectedProducts) {
//                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                Produkte myProdukt = em1.merge(p);
//                                em1.remove(myProdukt);
//                            }
//                        }
//                        em1.getTransaction().commit();
//                    } catch (OptimisticLockException ole) {
//                        em1.getTransaction().rollback();
//                    } catch (Exception exc) {
//                        em1.getTransaction().rollback();
//                        Main.fatal(e);
//                    } finally {
//                        em1.close();
//                        loadTable();
//                    }
//                    loadTable();
//                }
//            });
//            miDelete.setEnabled(listSelectedProducts.size() > 0 && Main.getCurrentUser().isAdmin());
//            menu.add(miDelete);
//
//            if (listSelectedProducts.size() > 1) {
//                JMenu miPopupMerge = new JMenu("Markierte Produkte zusammenfassen zu");
//                miPopupMerge.setFont(new Font("arial", Font.PLAIN, 18));
//
//                for (final Produkte thisProduct : listSelectedProducts) {
//                    JMenuItem mi = new JMenuItem("[" + thisProduct.getId() + "] " + thisProduct.getBezeichnung());
//                    mi.setFont(new Font("arial", Font.PLAIN, 18));
//
//                    mi.addActionListener(new java.awt.event.ActionListener() {
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zusammenfassen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                mergeUs(listSelectedProducts, thisProduct);
//                            }
//                        }
//                    });
//
//                    miPopupMerge.add(mi);
//                }
//
//                menu.add(miPopupMerge);
//            }
//
//
//            JMenu menuLagerart = new JMenu("Lagerart setzen");
//            menuLagerart.setFont(new Font("arial", Font.PLAIN, 18));
//            for (final String lagerart : LagerTools.LAGERART) {
//
//                JMenuItem miLagerart = new JMenuItem(lagerart);
//                miLagerart.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        //                        if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                        EntityManager em1 = Main.getEMF().createEntityManager();
//                        try {
//                            em1.getTransaction().begin();
//                            for (Produkte p : listSelectedProducts) {
//                                Produkte myProdukt = em1.merge(p);
//                                em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//                                myProdukt.setLagerart((short) ArrayUtils.indexOf(LagerTools.LAGERART, lagerart));
//                            }
//                            em1.getTransaction().commit();
//                        } catch (OptimisticLockException ole) {
//                            em1.getTransaction().rollback();
//                        } catch (Exception exc) {
//                            em1.getTransaction().rollback();
//                            Main.fatal(e);
//                        } finally {
//                            em1.close();
//                            loadTable();
//                        }
//                        //                        }
//                    }
//                });
//                menuLagerart.add(miLagerart);
//
//
//            }
//            menu.add(menuLagerart);
//
//
//            JMenu menuEinheit = new JMenu("Einheit setzen");
//            menuEinheit.setFont(new Font("arial", Font.PLAIN, 18));
//            for (final String einheit : ProdukteTools.EINHEIT) {
//
//                JMenuItem miEinheit = new JMenuItem(einheit);
//                miEinheit.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        //                        if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                        EntityManager em1 = Main.getEMF().createEntityManager();
//                        try {
//                            em1.getTransaction().begin();
//                            for (Produkte p : listSelectedProducts) {
//                                Produkte myProdukt = em1.merge(p);
//                                em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//                                myProdukt.setEinheit((short) ArrayUtils.indexOf(ProdukteTools.EINHEIT, einheit));
//                            }
//                            em1.getTransaction().commit();
//                        } catch (OptimisticLockException ole) {
//                            em1.getTransaction().rollback();
//                        } catch (Exception exc) {
//                            em1.getTransaction().rollback();
//                            Main.fatal(e);
//                        } finally {
//                            em1.close();
//                            loadTable();
//                        }
//                        //                        }
//                    }
//                });
//                menuEinheit.add(miEinheit);
//            }
//            menu.add(menuEinheit);
//
//            JMenu menuPopupAssign = new JMenu("zuweisen zu Stoffart");
//            menuPopupAssign.setFont(new Font("arial", Font.PLAIN, 18));
//
//
//            EntityManager em = Main.getEMF().createEntityManager();
//            try {
//                Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
//                ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());
//
//
//                for (Warengruppe warengruppe : listWarengruppen) {
//
//                    JMenu menuWarengruppe = new JMenu(warengruppe.getBezeichnung());
//
//                    ArrayList<Stoffart> listStoffarten = new ArrayList<Stoffart>(warengruppe.getStoffartCollection());
//                    Collections.sort(listStoffarten);
//
//                    for (final Stoffart stoffart : listStoffarten) {
//                        JMenuItem miStoffart = new JMenuItem(stoffart.getBezeichnung());
//                        miStoffart.addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                    EntityManager em1 = Main.getEMF().createEntityManager();
//                                    try {
//                                        em1.getTransaction().begin();
//                                        for (Produkte p : listSelectedProducts) {
//                                            Produkte myProdukt = em1.merge(p);
//                                            em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//                                            myProdukt.setStoffart(em1.merge(stoffart));
//                                        }
//                                        em1.getTransaction().commit();
//                                    } catch (OptimisticLockException ole) {
//                                        em1.getTransaction().rollback();
//                                    } catch (Exception exc) {
//                                        em1.getTransaction().rollback();
//                                        Main.fatal(e);
//                                    } finally {
//                                        em1.close();
//                                        loadTable();
//                                    }
//                                }
//                            }
//                        });
//                        menuWarengruppe.add(miStoffart);
//                    }
//                    menuPopupAssign.add(menuWarengruppe);
//                }
//            } catch (Exception exc) {
//                Main.fatal(exc);
//            } finally {
//                em.close();
//            }
//
//            menu.add(menuPopupAssign);


            menu.show(tblTypes, (int) p.getX(), (int) p.getY());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspSearch = new JScrollPane();
        pnlSearch = new JXTaskPaneContainer();
        xTaskPane1 = new JXTaskPane();
        btnSearchAll = new JButton();
        xSearchField1 = new JXSearchField();
        xTaskPane2 = new JXTaskPane();
        xTaskPane3 = new JXTaskPane();
        scrollPane1 = new JScrollPane();
        tblTypes = new JTable();

        //======== this ========
        setVisible(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "default, $lcgap, default:grow",
                "default:grow, 2*($lgap, default)"));

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

                //======== xTaskPane3 ========
                {
                    xTaskPane3.setLayout(new VerticalLayout());
                }
                pnlSearch.add(xTaskPane3);
            }
            jspSearch.setViewportView(pnlSearch);
        }
        contentPane.add(jspSearch, CC.xywh(1, 1, 1, 5, CC.FILL, CC.FILL));

        //======== scrollPane1 ========
        {

            //---- tblTypes ----
            tblTypes.setFont(new Font("SansSerif", Font.PLAIN, 12));
            tblTypes.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblTypesMousePressed(e);
                }
            });
            scrollPane1.setViewportView(tblTypes);
        }
        contentPane.add(scrollPane1, CC.xywh(3, 1, 1, 5, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspSearch;
    private JXTaskPaneContainer pnlSearch;
    private JXTaskPane xTaskPane1;
    private JButton btnSearchAll;
    private JXSearchField xSearchField1;
    private JXTaskPane xTaskPane2;
    private JXTaskPane xTaskPane3;
    private JScrollPane scrollPane1;
    private JTable tblTypes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
