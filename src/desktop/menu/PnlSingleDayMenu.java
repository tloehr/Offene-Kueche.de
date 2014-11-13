package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JidePopupMenu;
import entity.Menu;
import entity.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import tools.Const;
import tools.GUITools;
import tools.PnlAssign;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tloehr on 14.10.14.
 */
public class PnlSingleDayMenu extends JPanel {

    private Closure changeAction;

    private final HashMap<LocalDate, String> holidays;
    private JLabel lblDate;
    private JTextField searcherWholeMenu;
    private JidePopup popupStocks;
    KeyboardFocusManager keyboardFocusManager;
    JButton btnStock;
    private JButton btnEmpty;
    private JButton btnRedToGreen;
    private Menuweek2Menu menuweek2Menu;

//
//    public void setMenu(Menuweek2Menu menuweek2Menu) {
//        this.menuweek2Menu = menuweek2Menu;
//
////        btnStock.setToolTipText(MenuTools.getStocksAsHTMLList(menuweek2Menu.getMenu()));
////        btnStock.setText(menuweek2Menu.getMenu().getStocks().isEmpty() ? "" : Integer.toString(menuweek2Menu.getMenu().getStocks().size()));
//        initPanel();
//    }

    public void setChangeAction(Closure changeAction) {
        this.changeAction = changeAction;
    }

    public PnlSingleDayMenu(Menuweek2Menu menuweek2Menu, HashMap<LocalDate, String> holidays) {
        super();
        this.menuweek2Menu = menuweek2Menu;
        this.holidays = holidays;


//        dlmM = new DefaultListModel<Menu>();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        initPanel();
    }

//    public LocalDate getDate() {
//        return new LocalDate(menuweek2Menu.getMenu().getDate());
//    }

    private void initPanel() {
        removeAll();

        final JPanel menuComplete = new JPanel();
        JPanel menuLine0 = new JPanel();
        JPanel menuLine1 = new JPanel();
        JPanel menuLine2 = new JPanel();
        JPanel menuLine3 = new JPanel();

        menuComplete.setLayout(new BoxLayout(menuComplete, BoxLayout.PAGE_AXIS));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM yyyy");

        lblDate = new JLabel();
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblDate.setText(sdf.format(menuweek2Menu.getDate()) +
                        (holidays.containsKey(new LocalDate(menuweek2Menu.getDate())) ? " (" + holidays.get(new LocalDate(menuweek2Menu.getDate())) + ")" : "") +
                        "#"+menuweek2Menu.getMenu().getId());
        
        lblDate.setForeground(new LocalDate(menuweek2Menu.getDate()).getDayOfWeek() == DateTimeConstants.SATURDAY || new LocalDate(menuweek2Menu.getDate()).getDayOfWeek() == DateTimeConstants.SUNDAY || holidays.containsKey(new LocalDate(menuweek2Menu.getDate())) ? Color.RED : Color.black);

        menuLine0.setLayout(new BoxLayout(menuLine0, BoxLayout.LINE_AXIS));
        menuLine1.setLayout(new BoxLayout(menuLine1, BoxLayout.LINE_AXIS));
        menuLine2.setLayout(new BoxLayout(menuLine2, BoxLayout.LINE_AXIS));
        menuLine3.setLayout(new BoxLayout(menuLine3, BoxLayout.LINE_AXIS));

        keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        menuLine0.add(lblDate);

        menuLine1.add(new MenuBlock(menuweek2Menu.getMenu().getStarter(), "Vorspeise", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setStarter(rce.getNewRecipe());
                changeAction.execute(menuweek2Menu);
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        }));

        menuLine1.add(new MenuBlock(menuweek2Menu.getMenu().getMaincourse(), "Hauptgericht", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setMaincourse(rce.getNewRecipe());
                changeAction.execute(menuweek2Menu);
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        }));

        menuLine1.add(new MenuBlock(menuweek2Menu.getMenu().getSauce(), "Sauce", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSauce(rce.getNewRecipe());
                changeAction.execute(menuweek2Menu);
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        }));

        menuLine2.add(new MenuBlock(menuweek2Menu.getMenu().getSideveggie(), "Gemüse/Beilagen/Salat", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSideveggie(rce.getNewRecipe());
                changeAction.execute(menuweek2Menu);
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        }));

        menuLine2.add(new MenuBlock(menuweek2Menu.getMenu().getSidedish(), "Kartoffeln/Reis/Nudeln", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSidedish(rce.getNewRecipe());
                changeAction.execute(menuweek2Menu);
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        }));

        menuLine2.add(new MenuBlock(menuweek2Menu.getMenu().getDessert(), "Dessert", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setDessert(rce.getNewRecipe());
                changeAction.execute(menuweek2Menu);
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        }));

        searcherWholeMenu = new JTextField();
        searcherWholeMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());

        searcherWholeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (searcherWholeMenu.getText().trim().isEmpty()) {
                    menuweek2Menu.getMenu().setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                } else {
                    menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().trim());
                }
                changeAction.execute(menuweek2Menu.getMenu());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        });

        DefaultOverlayable ovrComment = new DefaultOverlayable(searcherWholeMenu);
        JLabel lblOverlay = new JLabel("Titel auf der Speisekarte");
        lblOverlay.setForeground(Const.deepskyblue);
        lblOverlay.setFont(new Font("SansSerif", Font.BOLD, 10));
        ovrComment.addOverlayComponent(lblOverlay, DefaultOverlayable.SOUTH_EAST);

        btnStock = new JButton(menuweek2Menu.getMenu().getStocks().isEmpty() ? "" : Integer.toString(menuweek2Menu.getMenu().getStocks().size()), Const.icon24box);
        btnStock.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnStock.setForeground(Const.mediumpurple4);
        btnStock.setToolTipText("<html>" + MenuTools.getStocksAsHTMLList(menuweek2Menu.getMenu()) + "</hml>");


        btnStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (popupStocks != null && popupStocks.isVisible()) {
                    popupStocks.hidePopup();
                }
                Tools.unregisterListeners(popupStocks);

                final PnlAssign<Stock> pnlAssign = new PnlAssign<Stock>(new ArrayList<Stock>(menuweek2Menu.getMenu().getStocks()), StockTools.getActiveStocks(), new DefaultListRenderer());
                popupStocks = GUITools.createPanelPopup(pnlAssign, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o == null) return;
                        if (CollectionUtils.isEqualCollection(pnlAssign.getAssigned(), menuweek2Menu.getMenu().getStocks())) return;

                        menuweek2Menu.getMenu().getStocks().clear();
                        for (Stock stock : pnlAssign.getAssigned()) {
                            menuweek2Menu.getMenu().getStocks().add(stock);
                        }
                        changeAction.execute(menuweek2Menu.getMenu());
                    }
                }, btnStock);


                GUITools.showPopup(popupStocks, SwingUtilities.CENTER);

            }
        });

        btnRedToGreen = new JButton(Const.icon24ledGreenOn4);
        btnRedToGreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeAction.execute(menuweek2Menu.getMenu());
            }
        });

        btnEmpty = new JButton(Const.icon24ledGreenOff4);
        btnEmpty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//
//
//                menuweek2Menu.getMenu().getStocks().clear();
//                menuweek2Menu.getMenu().setStarter(null);
//                menuweek2Menu.getMenu().setMaincourse(null);
//                menuweek2Menu.getMenu().setSauce(null);
//                menuweek2Menu.getMenu().setSideveggie(null);
//                menuweek2Menu.getMenu().setSidedish(null);
//                menuweek2Menu.getMenu().setDessert(null);
//                menuweek2Menu.getMenu().setText(null);
//                menuweek2Menu.setMenu(new Menu(menuweek2Menu));
                changeAction.execute(new Menu(menuweek2Menu));
            }
        });


        menuLine3.add(ovrComment);
        menuLine3.add(getMenuFindButton());
        menuLine3.add(btnStock);
        menuLine3.add(btnEmpty);
        menuLine3.add(btnRedToGreen);

        menuComplete.add(menuLine0);
        menuComplete.add(menuLine1);
        menuComplete.add(menuLine2);
        menuComplete.add(menuLine3);
        add(menuComplete);
    }

    private JButton getMenuFindButton() {
        final JButton btn = new JButton(Const.icon24find);
//
//        btn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                PnlSelect<Menu> pnlSelect = new PnlSelect<Menu>(MenuTools.getAllLike(searcherWholeMenu.getText().trim()), MenuTools.getListCellRenderer(), ListSelectionModel.SINGLE_SELECTION);
//                GUITools.showPopup(GUITools.createPanelPopup(pnlSelect, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        if (o == null) return;
//                        ArrayList<Menu> list = (ArrayList<Menu>) o;
//                        if (list.isEmpty()) return;
//
//                        menu = list.get(0);
//                        initPanel();
//                    }
//                }, btn), SwingUtilities.SOUTH);
//            }
//        });


        return btn;
    }


//    private void searchMenus(String searchPattern) {
//        dlmM.clear();
//
//        EntityManager em = Main.getEMF().createEntityManager();
//
//        Query query = em.createQuery("SELECT r FROM Menu r WHERE (r.text LIKE :pattern) ORDER BY r.text ");
//        query.setParameter("pattern", "%" + searchPattern + "%");
//
//        ArrayList<Menu> listMenus = new ArrayList<Menu>(query.getResultList());
//        em.close();
//
//        for (Menu menu : listMenus) {
//            dlmM.addElement(menuweek2Menu.getMenu());
//        }
//    }


    private class MenuBlock extends JPanel {

        private final RecipeChangeListener rcl;
        private Recipes recipe;
        private final JTextField searcher;
        private JidePopup popup;
        private DefaultListModel<Recipes> dlm;
        private JButton btnMenu;
        private boolean initPhase;
        private JList<Recipes> jList;
        private JScrollPane scrl;

        MenuBlock(final Recipes recipeIn, String overlay, RecipeChangeListener rclIn) {
            super();
            this.rcl = rclIn;
            initPhase = true;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            dlm = new DefaultListModel<Recipes>();

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            revalidate();
                            repaint();
                        }
                    });
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                    super.componentMoved(e);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            revalidate();
                            repaint();
                        }
                    });
                }
            });

            searcher = new JTextField(12);
            searcher.setFocusable(true);
            DefaultOverlayable ovrComment = new DefaultOverlayable(searcher);
            JLabel lblOverlay = new JLabel(overlay);
            lblOverlay.setForeground(Const.deepskyblue);
            lblOverlay.setFont(new Font("SansSerif", Font.BOLD, 10));
            ovrComment.addOverlayComponent(lblOverlay, DefaultOverlayable.SOUTH_EAST);

            searcher.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                        cancel();
                    if (e.getKeyCode() == KeyEvent.VK_DOWN)
                        goDownInList();
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                        goUpInList();

                }
            });
            searcher.setFont(new Font("SansSerif", Font.PLAIN, 18));

            searcher.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    searcher.selectAll();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    if (popup != null) {
                        popup.hidePopup();
                        popup = null;
                    }
                    createRecipeIfNecessary(searcher.getText());
                }
            });

            searcher.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    caretListener(e);
                }
            });

            searcher.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (jList != null && jList.getSelectedValue() != null) {
                        setRecipe(jList.getSelectedValue());
                    } else {
                        createRecipeIfNecessary(searcher.getText());
                    }
                    keyboardFocusManager.focusNextComponent();
                }
            });

            btnMenu = new JButton();
            btnMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopupMenu jMenu = new JidePopupMenu();
                    JMenuItem miOn = new JMenuItem("Speichern", Const.icon24ledGreenOn);
                    JMenuItem miOff = new JMenuItem("Leer", Const.icon24ledGreenOff);
                    jMenu.add(miOn);
                    jMenu.add(miOff);

                    miOn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setRecipe(recipe);
                            if (recipe != null && recipe.getId() == 0) {
                                rcl.recipeChanged(new RecipeChangeEvent(searcher, recipe));

                                // refresh the entity object that has been persisted in the parent class
                                EntityManager em = Main.getEMF().createEntityManager();
                                Query refreshQuery = em.createQuery("SELECT r FROM Recipes r WHERE r.title = :title");
                                refreshQuery.setParameter("title", recipe.getTitle());
                                recipe = (Recipes) refreshQuery.getResultList().get(0);
                                em.close();

                                setAccepted();
                            }
                        }
                    });

                    miOff.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setRecipe(null);
                        }
                    });

                    jMenu.show(btnMenu, 0, btnMenu.getPreferredSize().height);
                }
            });

            setRecipe(recipeIn);

            add(ovrComment);
            add(btnMenu);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    revalidate();
                    repaint();
                }
            });

            initPhase = false;
        }

        private void goDownInList() {
            if (popup != null && popup.isShowing() && !dlm.isEmpty()) {
                int index = Math.min(jList.getModel().getSize() - 1, jList.getSelectedIndex() + 1);
                jList.setSelectedIndex(index);
                jList.ensureIndexIsVisible(index);
            }
        }

        private void goUpInList() {
            if (popup != null && popup.isShowing() && !dlm.isEmpty()) {
                int index = Math.max(jList.getSelectedIndex() - 1, 0);
                jList.setSelectedIndex(index);
                jList.ensureIndexIsVisible(index);
            }
        }

        void setAccepted() {
            if (recipe == null) {
                btnMenu.setIcon(Const.icon24ledGreenOff);
            } else if (recipe.getId() == 0) {
                btnMenu.setIcon(Const.icon24ledRedOn);
            } else {
                btnMenu.setIcon(Const.icon24ledGreenOn);
            }
//            searcher.setEditable(recipe == null || recipe.getId() == 0);
        }

        void setRecipe(Recipes recipe) {
            this.recipe = recipe;
            searcher.setText(recipe == null ? "" : recipe.getTitle());
            searcher.setToolTipText(recipe == null ? "" : recipe.getText());

            if (!initPhase && ((recipe != null && recipe.getId() != 0) || recipe == null)) {
                rcl.recipeChanged(new RecipeChangeEvent(searcher, recipe));
            }

            setAccepted();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    revalidate();
                    repaint();
                }
            });
        }

        void cancel() {
            searcher.setText(recipe == null ? "" : recipe.getTitle());
        }

        private void createRecipeIfNecessary(String title) {
            if (title.trim().isEmpty()) return;
            // Recipes thisRecipe = findInDLM(title.trim());
            Recipes thisRecipe = null;

            // refresh the entity object that has been persisted in the parent class
            EntityManager em = Main.getEMF().createEntityManager();
            Query refreshQuery = em.createQuery("SELECT r FROM Recipes r WHERE r.title = :title");
            refreshQuery.setParameter("title", title);

            ArrayList<Recipes> list = new ArrayList(refreshQuery.getResultList());
            if (list.size() == 1) {
                thisRecipe = (Recipes) refreshQuery.getResultList().get(0);
            } else {
                thisRecipe = new Recipes(title.trim());
            }

            setRecipe(thisRecipe);

        }

        void caretListener(CaretEvent cae) {
            if (!searcher.hasFocus()) return;

            if (searcher.getText().trim().isEmpty() && popup != null && popup.isShowing()) {
                popup.hidePopup();
                popup = null;
                return;
            }

            if (cae.getDot() <= 0) {
                if (popup != null && popup.isShowing()) {
                    popup.hidePopup();
                    popup = null;
                }
                return;
            }

            if (recipe != null && recipe.getTitle().equals(searcher.getText())) {
                if (popup != null && popup.isShowing()) {
                    popup.hidePopup();
                    popup = null;
                }
                return;
            }

            if (popup == null) {
                popup = new JidePopup();
                popup.setOwner(searcher);
                popup.setMovable(false);

                jList = new JList(dlm);
                jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                jList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            setRecipe(jList.getSelectedValue());
                            if (popup != null && popup.isShowing()) {
                                popup.hidePopup();
                            }
                        }
                        super.mouseClicked(e);
                    }
                });

                scrl = new JScrollPane(jList);

                popup.setContentPane(scrl);
                popup.removeExcludedComponent(searcher);

                GUITools.showPopup(popup, SwingConstants.SOUTH);
            }

            searchRecipes(searcher.getText().trim());

            if (dlm.isEmpty() && popup.isShowing()) {
                popup.hidePopup();

            }

            if (!dlm.isEmpty() && popup != null && !popup.isShowing()) {
                popup.setOwner(searcher);
                GUITools.showPopup(popup, SwingConstants.SOUTH);
            }


        }

        private Recipes findInDLM(String searchPattern) {
            if (dlm.isEmpty()) return null;

            Recipes found = null;

            for (Recipes r : Collections.list(dlm.elements())) {
                if (r.getTitle().equals(searchPattern)) {
                    found = r;
                    break;
                }
            }

            return found;


        }


        private void searchRecipes(String searchPattern) {
            dlm.clear();

            EntityManager em = Main.getEMF().createEntityManager();

            Query query = em.createQuery("SELECT r FROM Recipes r WHERE (r.text LIKE :pattern) OR (r.title LIKE :pattern) ORDER BY r.title ");
            query.setParameter("pattern", "%" + searchPattern + "%");

            ArrayList<Recipes> listRecipes = new ArrayList<Recipes>(query.getResultList());
            em.close();

            for (Recipes recipes : listRecipes) {
                dlm.addElement(recipes);
            }
        }

    }

    private interface RecipeChangeListener extends EventListener {
        void recipeChanged(RecipeChangeEvent rce);
    }

    private class RecipeChangeEvent extends EventObject {

        private final Recipes newRecipe;

        /**
         * Creates a new CaretEvent object.
         *
         * @param source the object responsible for the event
         */
        public RecipeChangeEvent(Object source, Recipes newRecipe) {
            super(source);
            this.newRecipe = newRecipe;
        }

        public boolean isRecipeDeleted() {
            return newRecipe == null;
        }

        public Recipes getNewRecipe() {
            return newRecipe;
        }
    }


}
