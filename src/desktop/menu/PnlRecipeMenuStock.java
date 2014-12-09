/*
 * Created by JFormDesigner on Thu Dec 04 16:15:14 CET 2014
 */

package desktop.menu;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jdesktop.swingx.JXSearchField;
import tablemodels.IngType2recipeTableModel;
import tablemodels.StockTableModel3;
import tools.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlRecipeMenuStock extends PopupPanel {
    private final Recipes recipe;
    //    private final ArrayList<Stock> assigned, unassigned;
    private StockTableModel3 stmAss, stmUnass;
    private IngType2recipeTableModel it2rm;
    private JidePopup popup;
    private JPopupMenu menu;
    RowFilter<StockTableModel3, Integer> textFilter;
    private TableRowSorter<StockTableModel3> sorter;
    private int response;
    private java.util.List<ActionListener> listActions;
    private Warengruppe someDefaultCG;
    private ArrayList<IngTypes> listIngTypes;

    public void addActionListener(ActionListener al) {
        listActions.add(al);
    }

    public PnlRecipeMenuStock(Recipes recipe, ArrayList<Stock> assigned) {
        this.recipe = recipe;

        listActions = new ArrayList<ActionListener>();

        ArrayList<Stock> unassigned = new ArrayList<Stock>(Main.getStockList(false));
        unassigned.removeAll(assigned);

        it2rm = new IngType2recipeTableModel(recipe.getIngTypes2Recipes(), assigned);
        stmUnass = new StockTableModel3(unassigned);
        stmAss = new StockTableModel3(assigned);

        response = JOptionPane.CANCEL_OPTION;

        someDefaultCG = WarengruppeTools.getAll().get(0);

        listIngTypes = IngTypesTools.getAll();

        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblRecipe.setText(recipe.getTitle());
        lblRecipe.setToolTipText(recipe.getText());

        tblIngTypes.setModel(it2rm);
        tblUnassigned.setModel(stmUnass);
        tblAssigned.setModel(stmAss);

        createFilters();
        sorter = new TableRowSorter(stmUnass);
        sorter.setSortsOnUpdates(true);
        tblUnassigned.setRowSorter(sorter);
        sorter.setRowFilter(textFilter);

        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_INGTYPE).setCellRenderer(IngTypesTools.getTableCellRenderer());
        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_INGTYPE).setCellEditor(IngTypesTools.getTableCellEditor());

        stmAss.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                it2rm.update(stmAss.getData());
            }
        });

        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_LAGER).setCellRenderer(LagerTools.getTableCellRenderer());
        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_LAGER).setCellEditor(LagerTools.getTableCellEditor());

        tblIngTypes.getColumnModel().getColumn(IngType2recipeTableModel.COL_BEZEICHNUNG).setCellRenderer(IngTypesTools.getTableCellRenderer());
        tblIngTypes.getColumnModel().getColumn(IngType2recipeTableModel.COL_BEZEICHNUNG).setCellEditor(IngTypesTools.getTableCellEditor());

        tblUnassigned.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblAssigned.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

//        cmbStoffart.setModel(Tools.newComboboxModel(IngTypesTools.getAll()));



        thisComponentResized(null);
    }


    private void createFilters() {
//            warengruppeFilter = new RowFilter<ProdukteTableModel, Integer>() {
//                @Override
//                public boolean include(Entry<? extends ProdukteTableModel, ? extends Integer> entry) {
//                    if (warengruppeFilterKriterium == null) return true;
//
//                    int row = entry.getIdentifier();
//                    Produkte produkt = entry.getModel().getProdukt(row);
//
//
//                    return produkt.getIngTypes().getWarengruppe().equals(warengruppeFilterKriterium);
//                }
//            };
//            warengruppeFilterKriterium = null;
//
//            ingTypeFilter = new RowFilter<ProdukteTableModel, Integer>() {
//                @Override
//                public boolean include(Entry<? extends ProdukteTableModel, ? extends Integer> entry) {
//                    if (ingTypeFilterKriterium == null) return true;
//
//                    int row = entry.getIdentifier();
//                    Produkte produkt = entry.getModel().getProdukt(row);
//
//
//                    return produkt.getIngTypes().equals(ingTypeFilterKriterium);
//                }
//            };
//            ingTypeFilterKriterium = null;


        textFilter = new RowFilter<StockTableModel3, Integer>() {
            @Override
            public boolean include(Entry<? extends StockTableModel3, ? extends Integer> entry) {

                String textKriterium = searchUnAss.getText().trim().toLowerCase();
                if (textKriterium.isEmpty()) return true;

                int row = entry.getIdentifier();
                Stock stock = entry.getModel().getStock(row);

                return (stock.getProdukt().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0 ||
                        Long.toString(stock.getId()).equals(textKriterium) ||
                        Tools.catchNull(stock.getProdukt().getGtin()).indexOf(textKriterium) >= 0 ||
                        stock.getProdukt().getIngTypes().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0 ||
                        stock.getProdukt().getIngTypes().getWarengruppe().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0);


            }
        };

    }

    private void tblUnassignedMousePressed(MouseEvent e) {

        if (stmUnass.getRowCount() == 0) {
            return;
        }

        Point p = e.getPoint();
        final int col = tblUnassigned.columnAtPoint(p);
        final int row = tblUnassigned.rowAtPoint(p);
        ListSelectionModel lsm = tblUnassigned.getSelectionModel();

        if (lsm.isSelectionEmpty()) {
            lsm.setSelectionInterval(row, row);
        }

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


            JMenuItem miAssign = new JMenuItem("Markierte Vorräte zuordnen");
            miAssign.setFont(new Font("SansSerif", Font.PLAIN, 18));
            final int[] rows = tblUnassigned.getSelectedRows();
            final ArrayList<Stock> listSelected = new ArrayList<Stock>();
            for (int r = 0; r < rows.length; r++) {
                //                    final int finalR = r;
                int thisRow = tblUnassigned.convertRowIndexToModel(rows[r]);
                listSelected.add(stmUnass.getStock(thisRow));
            }


            miAssign.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stmAss.getData().addAll(listSelected);
                    stmAss.fireTableDataChanged();
                    Tools.packTable(tblAssigned, 0);

                    stmUnass.getData().removeAll(listSelected);
                    stmUnass.fireTableDataChanged();

//                    it2rm.update(stmAss.getData());

                    thisComponentResized(null);

                }
            });

            menu.add(miAssign);

            menu.show(tblUnassigned, (int) p.getX(), (int) p.getY());
        }


    }

    private void tblAssignedMousePressed(MouseEvent e) {


        if (stmAss.getRowCount() == 0) {
            return;
        }


        Point p = e.getPoint();
        final int col = tblAssigned.columnAtPoint(p);
        final int row = tblAssigned.rowAtPoint(p);
        ListSelectionModel lsm = tblAssigned.getSelectionModel();


        //lsm.setSelectionInterval(row, row);
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        if (SwingUtilities.isLeftMouseButton(e) && col == StockTableModel3.COL_ALLERGENES && e.getClickCount() == 2) {
            if (popup != null && popup.isVisible()) {
                popup.hidePopup();
            }

            Tools.unregisterListeners(popup);
            popup = new JidePopup();
            final int thisRow = tblAssigned.convertRowIndexToModel(row);
            final PnlAssign<Allergene> pnlAssign = new PnlAssign<Allergene>(stmAss.getStock(thisRow).getProdukt().getAllergenes(), AllergeneTools.getAll(), AllergeneTools.getListCellRenderer());

            popup.setTransient(true);
            popup.setOwner(tblAssigned);
            popup.getContentPane().add(pnlAssign);
            popup.setFocusable(true);
            popup.setDefaultFocusComponent(pnlAssign.getDefaultFocusComponent());

            popup.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                    if (pnlAssign.getAssigned() == null) return;

                    EntityManager em = Main.getEMF().createEntityManager();
                    try {
                        em.getTransaction().begin();

                        Produkte product = em.merge(stmAss.getStock(thisRow).getProdukt());
                        em.lock(product, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        product.getAllergenes().clear();
                        for (Allergene allergene : pnlAssign.getAssigned()) {
                            product.getAllergenes().add(em.merge(allergene));
                        }

                        em.getTransaction().commit();

                        stmAss.update(product);
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

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {

                }
            });

            SwingUtilities.convertPointToScreen(p, tblAssigned);

            popup.showPopup(p.x - (pnlAssign.getPreferredSize().width / 2), p.y + 10);

        }


        if (SwingUtilities.isRightMouseButton(e)) {


            if (menu != null && menu.isVisible()) {
                menu.setVisible(false);
            }

            Tools.unregisterListeners(menu);
            menu = new JPopupMenu();


            JMenuItem miUnassign = new JMenuItem("Markierte Vorräte entfernen");
            miUnassign.setFont(new Font("SansSerif", Font.PLAIN, 18));
            final int[] rows = tblAssigned.getSelectedRows();
            final ArrayList<Stock> listSelected = new ArrayList<Stock>();
            for (int r = 0; r < rows.length; r++) {
                //                    final int finalR = r;
                int thisRow = tblAssigned.convertRowIndexToModel(rows[r]);
                listSelected.add(stmAss.getStock(thisRow));
            }


            miUnassign.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stmAss.getData().removeAll(listSelected);
                    stmAss.fireTableDataChanged();

                    stmUnass.getData().addAll(listSelected);
                    stmUnass.fireTableDataChanged();

//                    it2rm.update(stmAss.getData());
//                    Tools.packTable(tblIngTypes, 0);
                }
            });

            menu.add(miUnassign);

            menu.show(tblAssigned, (int) p.getX(), (int) p.getY());
        }


//        if (SwingUtilities.isLeftMouseButton(e) && col == ProdukteTableModel.COL_ADDITIVES && e.getClickCount() == 2) {
//            if (popup != null && popup.isVisible()) {
//                popup.hidePopup();
//            }
//
//            Tools.unregisterListeners(popup);
//            popup = new JidePopup();
//            final int thisRow = tblProdukt.convertRowIndexToModel(row);
//            final PnlAssign<Additives> pnlAssign = new PnlAssign<Additives>(ptm.getProdukt(thisRow).getAdditives(), AdditivesTools.getAll(), AdditivesTools.getListCellRenderer());
//
//            popup.setMovable(false);
//            popup.setTransient(true);
//            popup.setOwner(tblProdukt);
//            popup.getContentPane().add(pnlAssign);
//            popup.setFocusable(true);
//            popup.setDefaultFocusComponent(pnlAssign.getDefaultFocusComponent());
//
//            popup.addPopupMenuListener(new PopupMenuListener() {
//                @Override
//                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//
//                }
//
//                @Override
//                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//
//                    if (pnlAssign.getAssigned() == null) return;
//
//                    EntityManager em = Main.getEMF().createEntityManager();
//                    try {
//                        em.getTransaction().begin();
//
//                        Produkte product = em.merge(ptm.getProdukt(thisRow));
//                        em.lock(product, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                        product.getAdditives().clear();
//                        for (Additives additive : pnlAssign.getAssigned()) {
//                            product.getAdditives().add(em.merge(additive));
//                        }
//
//                        em.getTransaction().commit();
//
//                        ptm.update(product);
//                    } catch (OptimisticLockException ole) {
//                        em.getTransaction().rollback();
//                        Main.warn(ole);
//                    } catch (Exception exc) {
//                        em.getTransaction().rollback();
//                        Main.fatal(e);
//                    } finally {
//                        em.close();
//                        popup = null;
//                    }
//                }
//
//                @Override
//                public void popupMenuCanceled(PopupMenuEvent e) {
//
//                }
//            });
//
//            SwingUtilities.convertPointToScreen(p, tblProdukt);
//
//            popup.showPopup(p.x - (pnlAssign.getPreferredSize().width / 2), p.y + 10);
//
//        }


//        if (SwingUtilities.isRightMouseButton(e)) {
//
//
//            if (menu != null && menu.isVisible()) {
//                menu.setVisible(false);
//            }
//
//            Tools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//
//            JMenuItem miEdit = new JMenuItem("Markierte Produkte bearbeiten");
//            miEdit.setFont(new Font("arial", Font.PLAIN, 18));
//            final int[] rows = tblProdukt.getSelectedRows();
//            final ArrayList<Produkte> listSelectedProducts = new ArrayList<Produkte>();
//            for (int r = 0; r < rows.length; r++) {
//                //                    final int finalR = r;
//                int thisRow = tblProdukt.convertRowIndexToModel(rows[r]);
//                listSelectedProducts.add(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow));
//            }
//
//
//            miEdit.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    //                    ArrayList<Produkte> listProdukte = new ArrayList<Produkte>(rows.length);
//                    //                    for (int r = 0; r < rows.length; r++) {
//                    //                        final int finalR = r;
//                    //                        final int thisRow = tblProdukt.convertRowIndexToModel(rows[finalR]);
//                    //                        listProdukte.add(((ProdukteTableModel) tblProdukt.getModel()).getProdukt(thisRow));
//                    //                    }
//                    final DlgProdukt dlg = new DlgProdukt(Main.mainframe, listSelectedProducts.get(0));
//                    dlg.addComponentListener(new ComponentAdapter() {
//                        @Override
//                        public void componentHidden(ComponentEvent e) {
//                            super.componentHidden(e);
//
//                            ptm.update(dlg.getProduct());
//                        }
//                    });
//
//                }
//            });
//            miEdit.setEnabled(listSelectedProducts.size() == 1);
//            menu.add(miEdit);
//
//            JMenuItem miInfo = new JMenuItem("Vorrat Info");
//            miInfo.setFont(new Font("arial", Font.PLAIN, 18));
//
//            miInfo.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    String ids = "";
//                    BigDecimal menge = BigDecimal.ZERO;
//                    int active = 0;
//                    for (Stock stock : listSelectedProducts.get(0).getStockCollection()) {
//                        if (!stock.isAusgebucht()) {
//                            active++;
//                            ids += stock.getId() + ", " + DateFormat.getDateInstance(DateFormat.SHORT).format(stock.getEingang()) + (active % 6 == 0 ? "\n" : "; ");
//                            menge = menge.add(StockTools.getSumme(stock));
//                        }
//                    }
//
//                    String text = "Produkt ID: " + listSelectedProducts.get(0).getId() + "\n";
//                    text += listSelectedProducts.get(0).getStockCollection().size() + " Vorräte insgesamt.\n";
//                    text += "Davon " + active + " noch nicht verbraucht.\n";
//                    if (active > 0) {
//                        text += "Noch vorhandene Gesamtmenge: " + menge.setScale(2, RoundingMode.HALF_UP).toString() + " " + IngTypesTools.EINHEIT[listSelectedProducts.get(0).getIngTypes().getEinheit()] + "\n";
//                        text += "Active IDs: " + ids;
//                    }
//
//                    JOptionPane.showInternalMessageDialog(thisComponent, text, "Vorrat Info", JOptionPane.INFORMATION_MESSAGE);
//
//                    Main.debug(text);
//
//                }
//            });
//            miInfo.setEnabled(listSelectedProducts.size() == 1);
//            menu.add(miInfo);
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
//                            if (JOptionPane.showConfirmDialog(thisComponent, "Das Produkt " + p.getId() + " hat " + p.getStockCollection().size() + " Vorräte.\nWirklich löschen ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//                                Produkte myProdukt = em1.merge(p);
//                                em1.remove(myProdukt);
//                                ptm.remove(myProdukt);
//                            }
//                        }
//                        em1.getTransaction().commit();
//                    } catch (OptimisticLockException ole) {
//
//                        em1.getTransaction().rollback();
//                        Main.warn(ole);
//                    } catch (Exception exc) {
//                        em1.getTransaction().rollback();
//                        Main.fatal(e);
//                    } finally {
//                        em1.close();
//                        //                        reload();
//                    }
//                    //                    reload();
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
//                                //                                reload();
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
//            //            JMenu menuLagerart = new JMenu("Lagerart setzen");
//            //            menuLagerart.setFont(new Font("arial", Font.PLAIN, 18));
//            //            for (final String lagerart : LagerTools.LAGERART) {
//            //
//            //                JMenuItem miLagerart = new JMenuItem(lagerart);
//            //                miLagerart.addActionListener(new ActionListener() {
//            //                    @Override
//            //                    public void actionPerformed(ActionEvent e) {
//            ////                        if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//            //                        EntityManager em1 = Main.getEMF().createEntityManager();
//            //                        try {
//            //                            em1.getTransaction().begin();
//            //                            for (Produkte p : listSelectedProducts) {
//            //                                Produkte myProdukt = em1.merge(p);
//            //                                em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//            //                                myProdukt.getIngTypes().setLagerart((short) ArrayUtils.indexOf(LagerTools.LAGERART, lagerart));
//            //                            }
//            //                            em1.getTransaction().commit();
//            //                            ptm.update(listSelectedProducts);
//            //                        } catch (OptimisticLockException ole) {
//            //                            Main.warn(ole);
//            //                            em1.getTransaction().rollback();
//            //                        } catch (Exception exc) {
//            //                            em1.getTransaction().rollback();
//            //                            Main.fatal(e);
//            //                        } finally {
//            //                            em1.close();
//            ////                            reload();
//            //                        }
//            ////                        }
//            //                    }
//            //                });
//            //                menuLagerart.add(miLagerart);
//            //
//            //
//            //            }
//            //            menu.add(menuLagerart);
//            //
//            //
//            //            JMenu menuEinheit = new JMenu("Einheit setzen");
//            //            menuEinheit.setFont(new Font("arial", Font.PLAIN, 18));
//            //            for (final String einheit : IngTypesTools.EINHEIT) {
//            //
//            //                JMenuItem miEinheit = new JMenuItem(einheit);
//            //                miEinheit.addActionListener(new ActionListener() {
//            //                    @Override
//            //                    public void actionPerformed(ActionEvent e) {
//            ////                        if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//            //                        EntityManager em1 = Main.getEMF().createEntityManager();
//            //                        try {
//            //                            em1.getTransaction().begin();
//            //                            for (Produkte p : listSelectedProducts) {
//            //                                Produkte myProdukt = em1.merge(p);
//            //                                em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//            //                                myProdukt.getIngTypes().setEinheit((short) ArrayUtils.indexOf(IngTypesTools.EINHEIT, einheit));
//            //                            }
//            //                            em1.getTransaction().commit();
//            //                            ptm.update(listSelectedProducts);
//            //                        } catch (OptimisticLockException ole) {
//            //                            Main.warn(ole);
//            //                            em1.getTransaction().rollback();
//            //                        } catch (Exception exc) {
//            //                            em1.getTransaction().rollback();
//            //                            Main.fatal(e);
//            //                        } finally {
//            //                            em1.close();
//            ////                            reload();
//            //                        }
//            ////                        }
//            //                    }
//            //                });
//            //                menuEinheit.add(miEinheit);
//            //            }
//            //            menu.add(menuEinheit);
//            //
//            //            JMenu menuPopupAssign = new JMenu("zuweisen zu Stoffart");
//            //            menuPopupAssign.setFont(new Font("arial", Font.PLAIN, 18));
//            //
//            //
//            //            EntityManager em = Main.getEMF().createEntityManager();
//            //            try {
//            //                Query query = em.createQuery("SELECT w FROM Warengruppe w ORDER BY w.bezeichnung ");
//            //                ArrayList<Warengruppe> listWarengruppen = new ArrayList<Warengruppe>(query.getResultList());
//            //
//            //
//            //                for (Warengruppe warengruppe : listWarengruppen) {
//            //
//            //                    JMenu menuWarengruppe = new JMenu(warengruppe.getBezeichnung());
//            //
//            //                    ArrayList<IngTypes> listStoffarten = new ArrayList<IngTypes>(warengruppe.getIngTypesCollection());
//            //                    Collections.sort(listStoffarten);
//            //
//            //                    for (final IngTypes ingTypes : listStoffarten) {
//            //                        JMenuItem miStoffart = new JMenuItem(ingTypes.getBezeichnung());
//            //                        miStoffart.addActionListener(new ActionListener() {
//            //                            @Override
//            //                            public void actionPerformed(ActionEvent e) {
//            //                                if (JOptionPane.showConfirmDialog(thisComponent, "Echt jetzt ?", "Zuweisen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48remove) == JOptionPane.YES_OPTION) {
//            //                                    EntityManager em1 = Main.getEMF().createEntityManager();
//            //                                    try {
//            //                                        em1.getTransaction().begin();
//            //                                        for (Produkte p : listSelectedProducts) {
//            //                                            Produkte myProdukt = em1.merge(p);
//            //                                            em1.lock(myProdukt, LockModeType.OPTIMISTIC);
//            //                                            myProdukt.setIngTypes(em1.merge(ingTypes));
//            //                                        }
//            //                                        em1.getTransaction().commit();
//            //                                        ptm.update(listSelectedProducts);
//            //                                    } catch (OptimisticLockException ole) {
//            //                                        Main.warn(ole);
//            //                                        em1.getTransaction().rollback();
//            //                                    } catch (Exception exc) {
//            //                                        em1.getTransaction().rollback();
//            //                                        Main.fatal(e);
//            //                                    } finally {
//            //                                        em1.close();
//            ////                                        reload();
//            //                                    }
//            //                                }
//            //                            }
//            //                        });
//            //                        menuWarengruppe.add(miStoffart);
//            //                    }
//            //                    menuPopupAssign.add(menuWarengruppe);
//            //                }
//            //            } catch (Exception exc) {
//            //                Main.fatal(exc.getMessage());
//            //            } finally {
//            //                em.close();
//            //            }
//            //
//            //            menu.add(menuPopupAssign);
//
//
//            menu.show(tblProdukt, (int) p.getX(), (int) p.getY());
//        }


    }

//    public ArrayList<Stock> getAssigned() {
//        return assigned;
//    }

    private void thisComponentResized(ComponentEvent e) {

        int[] widths = new int[]{60, 343, 80, 56, 72, 87, 99, 259, 173, 116};

        for (int col = 0; col < widths.length; col++) {
            tblUnassigned.getColumnModel().getColumn(col).setPreferredWidth(widths[col]);
            tblAssigned.getColumnModel().getColumn(col).setPreferredWidth(widths[col]);
        }

//        Tools.packTable(tblIngTypes, 0);

    }

    private void cmbIngTypesItemStateChanged(ItemEvent e) {

    }

    private void tblIngTypesMousePressed(MouseEvent e) {
        if (it2rm.getRowCount() == 0) {
            return;
        }

        Point p = e.getPoint();
        final int col = tblIngTypes.columnAtPoint(p);
        final int row = tblIngTypes.rowAtPoint(p);
        ListSelectionModel lsm = tblIngTypes.getSelectionModel();

        if (lsm.isSelectionEmpty()) {
            lsm.setSelectionInterval(row, row);
        }

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


            JMenuItem miAssign = new JMenuItem("Markierte Stoffart entfernen");
            miAssign.setFont(new Font("SansSerif", Font.PLAIN, 18));
            final int[] rows = tblIngTypes.getSelectedRows();
            final ArrayList<Ingtypes2Recipes> listSelected = new ArrayList<Ingtypes2Recipes>();
            for (int r = 0; r < rows.length; r++) {
                //                    final int finalR = r;
                int thisRow = tblIngTypes.convertRowIndexToModel(rows[r]);
                listSelected.add(it2rm.getData().get(thisRow));
            }


            miAssign.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    for (Ingtypes2Recipes ingtypes2Recipe : listSelected) {
                        it2rm.remove(ingtypes2Recipe);
                        recipe.getIngTypes2Recipes().remove(ingtypes2Recipe);
                    }

                }
            });

            menu.add(miAssign);

            menu.show(tblIngTypes, (int) p.getX(), (int) p.getY());
        }
    }

//    private void cmbIngTypesActionPerformed(ActionEvent e) {
//
//        IngTypes ingType = (IngTypes) cmbIngTypes.getSelectedItem();
//
//        if (!it2rm.contains(ingType)) {
//            Ingtypes2Recipes ingtypes2Recipes = new Ingtypes2Recipes(recipe, ingType);
//
//            //                it2rm.getData().add(ingtypes2Recipes);
//            recipe.getIngTypes2Recipes().add(ingtypes2Recipes);
//
//            it2rm.fireTableDataChanged();
//
//            //                it2rm.add(ingtypes2Recipes);
//        }
//
//
//    }

    private void btnAddITActionPerformed(ActionEvent e) {


        it2rm.add((IngTypes) cmbStoffart.getSelectedItem(), recipe);
        it2rm.fireTableDataChanged();
    }


    private void cmbStoffartItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }


    private void btnOkActionPerformed(ActionEvent e) {
        actionPerformed("OK");
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        actionPerformed("CANCEL");
    }

    private void actionPerformed(String command) {
        for (ActionListener al : listActions) {
            al.actionPerformed(new ActionEvent(this, 0, command));
        }
    }

    private void txtSearchNewIngTypeActionPerformed(ActionEvent e) {
        if (txtSearchNewIngType.getText().trim().isEmpty()) {
            cmbStoffart.setModel(Tools.newComboboxModel(listIngTypes));
            btnAddNew.setIcon(Const.icon24upArrow);
            return;
        }
        final String searchText = txtSearchNewIngType.getText().trim().toLowerCase();

        ArrayList<IngTypes> myListIT = new ArrayList<IngTypes>(listIngTypes);
        CollectionUtils.filter(myListIT, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return ((IngTypes) o).getBezeichnung().toLowerCase().indexOf(searchText) > -1;
            }
        });

        btnAddNew.setIcon(myListIT.isEmpty() ? Const.icon24add : Const.icon24upArrow);
        if (myListIT.isEmpty()) {
            cmbStoffart.setModel(new DefaultComboBoxModel<IngTypes>(new IngTypes[]{new IngTypes(txtSearchNewIngType.getText().trim(), someDefaultCG)}));
        } else {
            cmbStoffart.setModel(Tools.newComboboxModel(IngTypesTools.getAll(txtSearchNewIngType.getText())));
        }
    }

    private void btnAddNewActionPerformed(ActionEvent e) {

//        if (cmbStoffart.getModel().getSize() == 0){
//
//        } else {
//
//        }

        it2rm.add((IngTypes) cmbStoffart.getSelectedItem(), recipe);
        it2rm.fireTableDataChanged();
    }

    private void searchUnAssActionPerformed(ActionEvent e) {
        stmUnass.fireTableDataChanged();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblRecipe = new JLabel();
        searchUnAss = new JXSearchField();
        scrollPane2 = new JScrollPane();
        tblAssigned = new JTable();
        scrollPane3 = new JScrollPane();
        tblUnassigned = new JTable();
        scrollPane4 = new JScrollPane();
        tblIngTypes = new JTable();
        txtSearchNewIngType = new JXSearchField();
        btnAddNew = new JButton();
        cmbStoffart = new JComboBox();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnOk = new JButton();

        //======== this ========
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new FormLayout(
            "default:grow, $lcgap, default, 2*($lcgap, default:grow)",
            "2*(default, $lgap), fill:default:grow, $lgap, fill:pref, 3*($lgap, default)"));

        //---- lblRecipe ----
        lblRecipe.setText("text");
        lblRecipe.setFont(new Font("SansSerif", Font.PLAIN, 22));
        add(lblRecipe, CC.xywh(1, 1, 5, 1));

        //---- searchUnAss ----
        searchUnAss.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchUnAss.setText(" ");
        searchUnAss.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchUnAssActionPerformed(e);
            }
        });
        add(searchUnAss, CC.xy(7, 1));

        //======== scrollPane2 ========
        {

            //---- tblAssigned ----
            tblAssigned.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tblAssigned.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblAssignedMousePressed(e);
                }
            });
            scrollPane2.setViewportView(tblAssigned);
        }
        add(scrollPane2, CC.xywh(5, 3, 1, 3));

        //======== scrollPane3 ========
        {

            //---- tblUnassigned ----
            tblUnassigned.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tblUnassigned.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblUnassignedMousePressed(e);
                }
            });
            scrollPane3.setViewportView(tblUnassigned);
        }
        add(scrollPane3, CC.xywh(7, 3, 1, 3));

        //======== scrollPane4 ========
        {

            //---- tblIngTypes ----
            tblIngTypes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tblIngTypes.setFont(new Font("Dialog", Font.BOLD, 12));
            tblIngTypes.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblIngTypesMousePressed(e);
                }
            });
            scrollPane4.setViewportView(tblIngTypes);
        }
        add(scrollPane4, CC.xywh(1, 3, 3, 3));

        //---- txtSearchNewIngType ----
        txtSearchNewIngType.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtSearchNewIngType.setText(" ");
        txtSearchNewIngType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchNewIngTypeActionPerformed(e);
            }
        });
        add(txtSearchNewIngType, CC.xy(1, 7));

        //---- btnAddNew ----
        btnAddNew.setText(null);
        btnAddNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddNewActionPerformed(e);
            }
        });
        add(btnAddNew, CC.xywh(3, 7, 1, 3));

        //---- cmbStoffart ----
        cmbStoffart.setFont(new Font("SansSerif", Font.BOLD, 14));
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
        add(cmbStoffart, CC.xy(1, 9));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setText("Cancel");
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel2.add(btnCancel);

            //---- btnOk ----
            btnOk.setText("OK");
            btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnOkActionPerformed(e);
                }
            });
            panel2.add(btnOk);
        }
        add(panel2, CC.xy(7, 11, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public Object getResult() {
        return new Pair<java.util.List<Stock>, java.util.List<Ingtypes2Recipes>>(stmAss.getData(), it2rm.getData());
    }

    @Override
    public void setStartFocus() {

    }

    @Override
    public boolean isSaveOK() {
        return true;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblRecipe;
    private JXSearchField searchUnAss;
    private JScrollPane scrollPane2;
    private JTable tblAssigned;
    private JScrollPane scrollPane3;
    private JTable tblUnassigned;
    private JScrollPane scrollPane4;
    private JTable tblIngTypes;
    private JXSearchField txtSearchNewIngType;
    private JButton btnAddNew;
    private JComboBox cmbStoffart;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnOk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
