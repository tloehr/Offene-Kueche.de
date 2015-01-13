/*
 * Created by JFormDesigner on Thu Dec 04 16:15:14 CET 2014
 */

package desktop.menu;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import desktop.products.PnlAssignAdditives;
import entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import tablemodels.StockTableModel3;
import tools.Const;
import tools.Pair;
import tools.PopupPanel;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlRecipeMenuStock extends PopupPanel {
    private final Recipes recipe;
    //    private final ArrayList<Stock> assigned, unassigned;
    private StockTableModel3 stmAss, stmUnass;
//    private IngType2recipeTableModel it2rm;


    private DefaultTreeModel treemodel;

    private JidePopup popup;
    private JPopupMenu menu;
    private RowFilter<StockTableModel3, Integer> textFilter;
    private TableRowSorter<StockTableModel3> sorter;
    private java.util.List<ActionListener> listActions;
    private Warengruppe someDefaultCG;
    private ArrayList<IngTypes> listIngTypes;
    private ArrayList<Recipes> listRecipes;

    public void addActionListener(ActionListener al) {
        listActions.add(al);
    }

    public PnlRecipeMenuStock(Recipes recipe, ArrayList<Stock> assigned) {
        this.recipe = recipe;

        listActions = new ArrayList<ActionListener>();

        ArrayList<Stock> unassigned = new ArrayList<Stock>();
        unassigned.removeAll(assigned);

//        it2rm = new IngType2recipeTableModel(recipe.getIngTypes2Recipes(), assigned);

        createFirstTreeModel();

        stmUnass = new StockTableModel3(unassigned);
        stmAss = new StockTableModel3(assigned);

        someDefaultCG = WarengruppeTools.getAll().get(0);

        listIngTypes = IngTypesTools.getAll();
        listRecipes = RecipeTools.getAll();
        listRecipes.remove(recipe);
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblRecipe.setText(recipe.getTitle());
        lblRecipe.setToolTipText(recipe.getText());

        treeIngredients.setModel(treemodel);

        tblUnassigned.setModel(stmUnass);
        tblAssigned.setModel(stmAss);

//        createFilters();
        sorter = new TableRowSorter(stmUnass);
        sorter.setSortsOnUpdates(true);
        tblUnassigned.setRowSorter(sorter);
//        sorter.setRowFilter(textFilter);

        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_INGTYPE).setCellRenderer(IngTypesTools.getTableCellRenderer());
        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_INGTYPE).setCellEditor(IngTypesTools.getTableCellEditor());

        stmAss.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
//                it2rm.update(stmAss.getData());
            }
        });

        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_LAGER).setCellRenderer(LagerTools.getTableCellRenderer());
        tblAssigned.getColumnModel().getColumn(StockTableModel3.COL_LAGER).setCellEditor(LagerTools.getTableCellEditor());


        treeIngredients.setCellRenderer(getTreeCellRenderer());
        treeIngredients.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                ArrayList<IngTypes> listIngTypes = new ArrayList<IngTypes>();
                for (TreePath path : treeIngredients.getSelectionPaths()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                    if (node.getUserObject() instanceof Ingtypes2Recipes) {
                        if (!listIngTypes.contains(((Ingtypes2Recipes) node.getUserObject()).getIngType())) {
                            listIngTypes.add(((Ingtypes2Recipes) node.getUserObject()).getIngType());
                        }
                    }
                }

                stmUnass.getData().clear();
                if (!listIngTypes.isEmpty()) {
                    stmUnass.getData().addAll(StockTools.getActiveStocks(listIngTypes));
                }
                stmUnass.fireTableDataChanged();
            }
        });

//        tblIngTypes.getColumnModel().getColumn(IngType2recipeTableModel.COL_BEZEICHNUNG).setCellRenderer(IngTypesTools.getTableCellRenderer());
//        tblIngTypes.getColumnModel().getColumn(IngType2recipeTableModel.COL_BEZEICHNUNG).setCellEditor(IngTypesTools.getTableCellEditor());

        tblUnassigned.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblAssigned.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        tblUnassigned.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {


                    final int[] rows = tblUnassigned.getSelectedRows();
                    final ArrayList<Stock> listSelected = new ArrayList<Stock>();
                    for (int r = 0; r < rows.length; r++) {
                        //                    final int finalR = r;
                        int thisRow = tblUnassigned.convertRowIndexToModel(rows[r]);
                        listSelected.add(stmUnass.getStock(thisRow));
                    }

                    stmAss.getData().addAll(listSelected);
                    stmAss.fireTableDataChanged();
                    Tools.packTable(tblAssigned, 0);

                    stmUnass.getData().removeAll(listSelected);
                    stmUnass.fireTableDataChanged();

                    thisComponentResized(null);
                }
            }
        });

        tblAssigned.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    final int col = tblAssigned.columnAtPoint(p);
                    if (col == StockTableModel3.COL_ALLADD) return;

                    final int[] rows = tblAssigned.getSelectedRows();
                    final ArrayList<Stock> listSelected = new ArrayList<Stock>();
                    for (int r = 0; r < rows.length; r++) {
                        //                    final int finalR = r;
                        int thisRow = tblAssigned.convertRowIndexToModel(rows[r]);
                        listSelected.add(stmAss.getStock(thisRow));
                    }

                    stmAss.getData().removeAll(listSelected);
                    stmAss.fireTableDataChanged();

                    stmUnass.getData().addAll(listSelected);
                    stmUnass.fireTableDataChanged();
                }
            }
        });

        cmbIngTypeOrRecipe.setRenderer(new DefaultListRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel comp = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                comp.setIcon(Const.icon16info);
                if (value instanceof Recipes) {
                    comp.setIcon(Const.icon16recipe);
                } else if (value instanceof IngTypes) {
                    comp.setIcon(Const.icon16ingtype);
                }
                return comp;
            }
        });

        thisComponentResized(null);
    }


//    private void createFilters() {
//
//        textFilter = new RowFilter<StockTableModel3, Integer>() {
//            @Override
//            public boolean include(Entry<? extends StockTableModel3, ? extends Integer> entry) {
//                int row = entry.getIdentifier();
//                Stock stock = entry.getModel().getStock(row);
//
//                if (!tbOldStocks.isSelected() && stock.isAusgebucht()) return false;
//
//                if (!treeIngredients.getSelectionModel().isSelectionEmpty() && !treeIngredients.getSelectionModel().getLeadSelectionPath().getLastPathComponent().equals(treeIngredients.getModel().getRoot())) {
//                    for (TreePath path : treeIngredients.getSelectionModel().getSelectionPaths()) {
//                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//                        if (node.getUserObject() instanceof Recipes) {
//                            if (!RecipeTools.contains((Recipes) node.getUserObject(), stock.getProdukt().getIngTypes())) {
//                                return false;
//                            }
//                        } else if (node.getUserObject() instanceof Ingtypes2Recipes) {
//                            if (!stock.getProdukt().getIngTypes().equals(((Ingtypes2Recipes) node.getUserObject()).getIngType())) {
//                                return false;
//                            }
//                        }
//                    }
//                }
//
//                String textKriterium = searchUnAss.getText().trim().toLowerCase();
//                if (textKriterium.isEmpty()) return true;
//
//                return (stock.getProdukt().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0 ||
//                        Long.toString(stock.getId()).equals(textKriterium) ||
//                        Tools.catchNull(stock.getProdukt().getGtin()).indexOf(textKriterium) >= 0 ||
//                        stock.getProdukt().getIngTypes().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0 ||
//                        stock.getProdukt().getIngTypes().getWarengruppe().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0);
//
//
//            }
//        };
//
//    }

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

        if (SwingUtilities.isLeftMouseButton(e) && col == StockTableModel3.COL_ALLADD && e.getClickCount() == 2) {
//            if (popup != null && popup.isVisible()) {
//                popup.hidePopup();
//            }
//
//            Tools.unregisterListeners(popup);
//            popup = new JidePopup();
            final int thisRow = tblAssigned.convertRowIndexToModel(row);


            final PnlAssignAdditives dlgAssign = new PnlAssignAdditives(Main.getDesktop().getMenuweek(), stmAss.getStock(thisRow).getProdukt().getAdditives(), stmAss.getStock(thisRow).getProdukt().getAllergenes());
            dlgAssign.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (dlgAssign.getResponse() == JOptionPane.OK_OPTION) {
                        EntityManager em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();

                            Produkte product = em.merge(stmAss.getStock(thisRow).getProdukt());
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


    }

//    public ArrayList<Stock> getAssigned() {
//        return assigned;
//    }

    private void thisComponentResized(ComponentEvent e) {

        int[] widths = new int[]{60, 150, 80, 100, 87, 99, 259, 173, 116};

        for (int col = 0; col < widths.length; col++) {
            tblUnassigned.getColumnModel().getColumn(col).setPreferredWidth(widths[col]);
            tblAssigned.getColumnModel().getColumn(col).setPreferredWidth(widths[col]);
        }

//        Tools.packTable(tblIngTypes, 0);

    }

    private void tblIngTypesMousePressed(MouseEvent e) {


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
        ArrayList listAllToSearchIn = new ArrayList();

        if (txtSearchNewIngType.getText().trim().isEmpty()) {

            listAllToSearchIn.addAll(listIngTypes);
            listAllToSearchIn.addAll(listRecipes);

            cmbIngTypeOrRecipe.setModel(Tools.newComboboxModel(listAllToSearchIn));
            btnAddNew.setIcon(Const.icon24upArrow);

            if (!treeIngredients.getSelectionModel().isSelectionEmpty()) {
                treeIngredients.getSelectionModel().clearSelection();
            }

            return;
        }
        final String searchText = txtSearchNewIngType.getText().trim().toLowerCase();

        listAllToSearchIn.addAll(listIngTypes);
        listAllToSearchIn.addAll(listRecipes);

        CollectionUtils.filter(listAllToSearchIn, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                if (o instanceof Recipes) {
                    return ((Recipes) o).getTitle().trim().toLowerCase().indexOf(searchText) > -1;
                } else {
                    return ((IngTypes) o).getBezeichnung().trim().toLowerCase().indexOf(searchText) > -1;
                }
            }
        });

        btnAddNew.setIcon(containsOnlyRecipesOrIsEmpty(listAllToSearchIn) ? Const.icon24add : Const.icon24upArrow);
        if (containsOnlyRecipesOrIsEmpty(listAllToSearchIn)) {
            cmbIngTypeOrRecipe.setModel(new DefaultComboBoxModel(new IngTypes[]{new IngTypes(txtSearchNewIngType.getText().trim(), someDefaultCG)}));
        } else {
            cmbIngTypeOrRecipe.setModel(Tools.newComboboxModel(listAllToSearchIn));
        }
    }


    boolean containsOnlyRecipesOrIsEmpty(ArrayList list) {
        boolean positive = list.isEmpty();
        for (Object o : list) {
            positive = o instanceof Recipes;
            if (!positive) break;
        }
        return positive;
    }


    void createFirstTreeModel() {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(recipe);

        for (Recipes r : recipe.getSubrecipes()) {
//            DefaultMutableTreeNode recipeRoot = new DefaultMutableTreeNode(r);
//            root.add(recipeRoot);
//
//            for (Ingtypes2Recipes it2r : r.getIngTypes2Recipes()) {
//                recipeRoot.add(new DefaultMutableTreeNode(it2r));
//            }

            root.add(getSubtree(r));
        }

        for (Ingtypes2Recipes it2r : recipe.getIngTypes2Recipes()) {
            root.add(new DefaultMutableTreeNode(it2r));
        }

        treemodel = new DefaultTreeModel(root);
    }


    DefaultMutableTreeNode getSubtree(Recipes recipe) {
        DefaultMutableTreeNode recipeRoot = new DefaultMutableTreeNode(recipe);


        for (Ingtypes2Recipes it2r : recipe.getIngTypes2Recipes()) {
            recipeRoot.add(new DefaultMutableTreeNode(it2r));
        }

        return recipeRoot;
    }


    private void btnAddNewActionPerformed(ActionEvent e) {

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treemodel.getRoot();

        if (cmbIngTypeOrRecipe.getSelectedItem() instanceof Recipes) { //&& !((Recipes) cmbIngTypeOrRecipe.getSelectedItem()).getIngTypes2Recipes().isEmpty()) {

            Recipes newSubRecipe = (Recipes) cmbIngTypeOrRecipe.getSelectedItem();

            if (!recipe.getSubrecipes().contains(newSubRecipe)) {
//                recipe.getSubrecipes().add(newSubRecipe);
                treemodel.insertNodeInto(getSubtree(newSubRecipe), root, root.getChildCount());
                treemodel.reload();
            }

        } else if (cmbIngTypeOrRecipe.getSelectedItem() instanceof IngTypes) {

            IngTypes newIngType = (IngTypes) cmbIngTypeOrRecipe.getSelectedItem();


            if (!RecipeTools.contains(recipe, newIngType)) {
                Ingtypes2Recipes newIngType2Recipe = new Ingtypes2Recipes(recipe, newIngType);
                newIngType2Recipe.setAmount(BigDecimal.ZERO);
//                recipe.getIngTypes2Recipes().add(newIngType2Recipe);
                treemodel.insertNodeInto(new DefaultMutableTreeNode(newIngType2Recipe), root, root.getChildCount());
                treemodel.reload();
            }

        }

//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                scrollPane4.revalidate();
//                scrollPane4.repaint();
//            }
//        });

    }

    private void searchUnAssActionPerformed(ActionEvent e) {
        stmUnass.getData().clear();
        if (!searchUnAss.getText().isEmpty()) {
            stmUnass.getData().addAll(StockTools.getActiveStocks(searchUnAss.getText().trim()));
        }
        stmUnass.fireTableDataChanged();
    }

    private void tbOldStocksItemStateChanged(ItemEvent e) {
        searchUnAssActionPerformed(null);
    }


    private TreeCellRenderer getTreeCellRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                if (node.getUserObject() instanceof Recipes) {
                    JLabel comp = (JLabel) super.getTreeCellRendererComponent(tree, ((Recipes) node.getUserObject()).getTitle(), sel, expanded, leaf, row, hasFocus);
                    comp.setIcon(node.isRoot() ? Const.icon24recipeBlue : Const.icon24recipe);
                    if (node.isRoot()) {
                        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                    } else {
                        comp.setFont(comp.getFont().deriveFont(Font.PLAIN));
                    }
                    return comp;
                } else if (node.getUserObject() instanceof Ingtypes2Recipes) {
                    JLabel comp = (JLabel) super.getTreeCellRendererComponent(tree, ((Ingtypes2Recipes) node.getUserObject()).getIngType().getBezeichnung(), sel, expanded, leaf, row, hasFocus);
                    comp.setIcon(Const.icon24ingtype);
                    return comp;
                } else {
                    return super.getTreeCellRendererComponent(tree, ":-( " + node.getUserObject(), sel, expanded, leaf, row, hasFocus);
                }
            }
        };
    }

    private void treeIngredientsMousePressed(MouseEvent e) {
        if (treemodel.getChildCount(treemodel.getRoot()) == 0) {
            return;
        }

        Point p = e.getPoint();
//        final int col = t.columnAtPoint(p);
        final int row = treeIngredients.getRowForLocation(p.x, p.y);

        final TreeSelectionModel tsm = treeIngredients.getSelectionModel();

        if (tsm.isSelectionEmpty()) {
            tsm.setSelectionPath(treeIngredients.getPathForLocation(p.x, p.y));
        }

//        boolean singleRowSelected = tsm.getSelectionPaths().length == 1;


        //        if (SwingUtilities.isLeftMouseButton(e)) {
        //            sorter.setRowFilter(ingTypeFilter);
        //            stmUnass.fireTableDataChanged();
        //        }

        if (SwingUtilities.isRightMouseButton(e)) {
            if (menu != null && menu.isVisible()) {
                menu.setVisible(false);
            }

            Tools.unregisterListeners(menu);
            menu = new JPopupMenu();


            JMenuItem miAssign = new JMenuItem("Markierte Einträge entfernen");
            miAssign.setFont(new Font("SansSerif", Font.PLAIN, 18));


            miAssign.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (TreePath path : tsm.getSelectionPaths()) {
                        if (path.getPath().length == 2) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            treemodel.removeNodeFromParent(node);
                            if (node.getUserObject() instanceof Ingtypes2Recipes) {
                                recipe.getIngTypes2Recipes().remove(node.getUserObject());
                            } else if (node.getUserObject() instanceof Recipes) {
                                recipe.getSubrecipes().remove(node.getUserObject());
                            }
                        }
                    }
                }
            });

            menu.add(miAssign);

            menu.show(treeIngredients, (int) p.getX(), (int) p.getY());
        }
    }

    private void btnUnselectActionPerformed(ActionEvent e) {
        treeIngredients.getSelectionModel().clearSelection();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblRecipe = new JLabel();
        searchUnAss = new JXSearchField();
        scrollPane4 = new JScrollPane();
        treeIngredients = new JTree();
        scrollPane2 = new JScrollPane();
        tblAssigned = new JTable();
        scrollPane3 = new JScrollPane();
        tblUnassigned = new JTable();
        btnUnselect = new JButton();
        tbOldStocks = new JToggleButton();
        txtSearchNewIngType = new JXSearchField();
        btnAddNew = new JButton();
        cmbIngTypeOrRecipe = new JComboBox();
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
                "3*(default, $lcgap), 2*(default:grow, $lcgap), default",
                "default, $lgap, fill:default:grow, $lgap, default, $lgap, fill:pref, 3*($lgap, default)"));

        //---- lblRecipe ----
        lblRecipe.setText("text");
        lblRecipe.setFont(new Font("SansSerif", Font.PLAIN, 22));
        add(lblRecipe, CC.xywh(3, 1, 5, 1));

        //---- searchUnAss ----
        searchUnAss.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchUnAss.setText(" ");
        searchUnAss.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchUnAssActionPerformed(e);
            }
        });
        add(searchUnAss, CC.xy(9, 1));

        //======== scrollPane4 ========
        {

            //---- treeIngredients ----
            treeIngredients.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    treeIngredientsMousePressed(e);
                }
            });
            scrollPane4.setViewportView(treeIngredients);
        }
        add(scrollPane4, CC.xywh(3, 3, 3, 1));

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
        add(scrollPane2, CC.xywh(7, 3, 1, 7));

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
        add(scrollPane3, CC.xywh(9, 3, 1, 5));

        //---- btnUnselect ----
        btnUnselect.setText("unselect");
        btnUnselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnUnselectActionPerformed(e);
            }
        });
        add(btnUnselect, CC.xywh(3, 5, 3, 1));

        //---- tbOldStocks ----
        tbOldStocks.setText("Auch alte Vorr\u00e4te");
        tbOldStocks.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tbOldStocksItemStateChanged(e);
            }
        });
        add(tbOldStocks, CC.xywh(9, 8, 1, 2));

        //---- txtSearchNewIngType ----
        txtSearchNewIngType.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtSearchNewIngType.setText(" ");
        txtSearchNewIngType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchNewIngTypeActionPerformed(e);
            }
        });
        add(txtSearchNewIngType, CC.xy(3, 7));

        //---- btnAddNew ----
        btnAddNew.setText(null);
        btnAddNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddNewActionPerformed(e);
            }
        });
        add(btnAddNew, CC.xywh(5, 7, 1, 3));

        //---- cmbIngTypeOrRecipe ----
        cmbIngTypeOrRecipe.setFont(new Font("SansSerif", Font.BOLD, 14));
        cmbIngTypeOrRecipe.setModel(new DefaultComboBoxModel(new String[]{
                "item 1",
                "item 2",
                "item 3"
        }));
        add(cmbIngTypeOrRecipe, CC.xy(3, 9));

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
        add(panel2, CC.xy(9, 11, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public Object getResult() {

        ArrayList result = new ArrayList();
        result.add(stmAss.getData());

        return new Pair<java.util.List<Stock>, DefaultTreeModel>(stmAss.getData(), treemodel);
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
    private JScrollPane scrollPane4;
    private JTree treeIngredients;
    private JScrollPane scrollPane2;
    private JTable tblAssigned;
    private JScrollPane scrollPane3;
    private JTable tblUnassigned;
    private JButton btnUnselect;
    private JToggleButton tbOldStocks;
    private JXSearchField txtSearchNewIngType;
    private JButton btnAddNew;
    private JComboBox cmbIngTypeOrRecipe;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnOk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
