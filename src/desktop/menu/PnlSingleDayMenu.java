package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.OverlayableUtils;
import entity.Menu;
import entity.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import tools.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
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

//    private Closure changeAction;

    private final HashMap<LocalDate, String> holidays;
    private final PSDChangeListener psdChangeListener;

    private JTextField searcherWholeMenu;
    private JidePopup popupStocks;
    KeyboardFocusManager keyboardFocusManager;
    JButton btnStock;
    private JButton btnEmpty;
    private JButton btnRedToGreen;
    private Menuweek2Menu menuweek2Menu;
//    private Menu menu;

//
//    public void setMenu(Menuweek2Menu menuweek2Menu) {
//        this.menuweek2Menu = menuweek2Menu;
//
////        btnStock.setToolTipText(MenuTools.getStocksAsHTMLList(menuweek2Menu.getMenu()));
////        btnStock.setText(menuweek2Menu.getMenu().getStocks().isEmpty() ? "" : Integer.toString(menuweek2Menu.getMenu().getStocks().size()));
//        initPanel();
//    }


    public void setMenuweek2Menu(Menuweek2Menu menuweek2Menu) {
        this.menuweek2Menu = menuweek2Menu;
        initPanel();
    }

    public PnlSingleDayMenu(Menuweek2Menu menuweek2Menu, HashMap<LocalDate, String> holidays, PSDChangeListener psdChangeListener) {
        super();
        this.menuweek2Menu = menuweek2Menu;
        this.holidays = holidays;
        this.psdChangeListener = psdChangeListener;


//        dlmM = new DefaultListModel<Menu>();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        initPanel();
    }


    private Menuweek2Menu replaceMenu(Menu replaceBy) {
        Menuweek2Menu myMenuweek2Menu = null;
        EntityManager em = Main.getEMF().createEntityManager();
        try {

            em.getTransaction().begin();
            myMenuweek2Menu = em.merge(menuweek2Menu);

            Menu oldMenu = em.merge(menuweek2Menu.getMenu());
            em.lock(oldMenu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            oldMenu.getMenu2menuweeks().remove(myMenuweek2Menu);

            if (oldMenu.getMenu2menuweeks().isEmpty()) { // to prevent orphan menus
                em.remove(oldMenu);
            }

            Menu replacement = em.merge(replaceBy);
            myMenuweek2Menu.setMenu(replacement);
            replacement.getMenu2menuweeks().add(myMenuweek2Menu);

            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception exc) {
            Main.error(exc.getMessage());
            em.getTransaction().rollback();
            Main.fatal(exc.getMessage());
        } finally {
            em.close();
            //                    notifyCaller();
        }
        return myMenuweek2Menu;
    }


    private Menuweek2Menu mergeChanges(Menu menu1) {
        Menuweek2Menu myMenuweek2Menu = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Menu editedMenu = null;
        try {
            em.getTransaction().begin();

            editedMenu = em.merge(menu1);
            myMenuweek2Menu = em.merge(menuweek2Menu);

            em.lock(editedMenu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            myMenuweek2Menu.setMenu(editedMenu);
            editedMenu.getMenu2menuweeks().remove(menuweek2Menu);
            editedMenu.getMenu2menuweeks().add(myMenuweek2Menu);

            em.getTransaction().commit();

        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception exc) {
            Main.error(exc.getMessage());
            em.getTransaction().rollback();
            Main.fatal(exc.getMessage());
        } finally {
            em.close();
            psdChangeListener.menuEdited(new PSDChangeEvent(this, editedMenu, myMenuweek2Menu));
        }
        return myMenuweek2Menu;
    }

//    public LocalDate getDate() {
//        return new LocalDate(menuweek2Menu.getMenu().getDate());
//    }

    private void initPanel() {
        removeAll();

        final JPanel menuComplete = new JPanel();
        JPanel menuLine0 = new JPanel();
        final JPanel menuLine1 = new JPanel();
        final JPanel menuLine2 = new JPanel();
        JPanel menuLine3 = new JPanel();

        menuComplete.setLayout(new BoxLayout(menuComplete, BoxLayout.PAGE_AXIS));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM yyyy");

        JLabel lblDate = new JLabel();
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblDate.setText(sdf.format(menuweek2Menu.getDate()) +
                (holidays.containsKey(new LocalDate(menuweek2Menu.getDate())) ? " (" + holidays.get(new LocalDate(menuweek2Menu.getDate())) + ")" : ""));

        lblDate.setForeground(new LocalDate(menuweek2Menu.getDate()).getDayOfWeek() == DateTimeConstants.SATURDAY || new LocalDate(menuweek2Menu.getDate()).getDayOfWeek() == DateTimeConstants.SUNDAY || holidays.containsKey(new LocalDate(menuweek2Menu.getDate())) ? Color.RED : Color.black);
        lblDate.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel lblID = new JLabel();
        lblID.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblID.setText("#" + menuweek2Menu.getMenu().getId());

        menuLine0.setLayout(new BorderLayout());
        menuLine1.setLayout(new BoxLayout(menuLine1, BoxLayout.LINE_AXIS));
        menuLine2.setLayout(new BoxLayout(menuLine2, BoxLayout.LINE_AXIS));
        menuLine3.setLayout(new BoxLayout(menuLine3, BoxLayout.LINE_AXIS));

        keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        menuLine0.add(BorderLayout.CENTER, lblDate);
        menuLine0.add(BorderLayout.EAST, lblID);

        menuLine1.add(new MenuBlock(menuweek2Menu.getMenu().getStarter(), "Vorspeise", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setStarter(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
            }
        }));

        menuLine1.add(new MenuBlock(menuweek2Menu.getMenu().getMaincourse(), "Hauptgericht", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setMaincourse(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
            }
        }));

        menuLine1.add(new MenuBlock(menuweek2Menu.getMenu().getSauce(), "Sauce", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSauce(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
            }
        }));

        menuLine2.add(new MenuBlock(menuweek2Menu.getMenu().getSideveggie(), "Gemüse/Beilagen/Salat", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSideveggie(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
            }
        }));

        menuLine2.add(new MenuBlock(menuweek2Menu.getMenu().getSidedish(), "Kartoffeln/Reis/Nudeln", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSidedish(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
            }
        }));

        menuLine2.add(new MenuBlock(menuweek2Menu.getMenu().getDessert(), "Dessert", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setDessert(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());

            }
        }));

        searcherWholeMenu = new JTextField() {
            @Override
            public void repaint(long tm, int x, int y, int width, int height) {
                super.repaint(tm, x, y, width, height);
                OverlayableUtils.repaintOverlayable(this);
            }
        };
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
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());
            }
        });

        DefaultOverlayable ovrComment = new DefaultOverlayable(searcherWholeMenu);
        JLabel lblOverlay = new JLabel("Titel auf der Speisekarte");
        lblOverlay.setForeground(Const.deepskyblue);
        lblOverlay.setFont(new Font("SansSerif", Font.BOLD, 10));
        ovrComment.addOverlayComponent(lblOverlay, DefaultOverlayable.SOUTH_EAST);


        btnStock = new JButton() {
            @Override
            public void repaint(long tm, int x, int y, int width, int height) {
                super.repaint(tm, x, y, width, height);
                OverlayableUtils.repaintOverlayable(this);
            }
        };
        btnStock.setText(menuweek2Menu.getMenu().getStocks().isEmpty() ? "leer" : Integer.toString(menuweek2Menu.getMenu().getStocks().size()) + " Prod. zugeordnet");
//        btnStock.setIcon(menuweek2Menu.getMenu().getStocks().isEmpty() ? Const.icon24blackBadge : Const.icon24whiteBadge);
        btnStock.setFont(new Font("SansSerif", Font.BOLD, 18));
//        btnStock.setForeground(Color.ORANGE);
//        btnStock.setHorizontalTextPosition(SwingConstants.CENTER);
//        btnStock.setVerticalTextPosition(SwingConstants.CENTER);
        btnStock.setToolTipText(MenuTools.getStocksAsHTMLList(menuweek2Menu.getMenu()));

        btnStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (popupStocks != null && popupStocks.isVisible()) {
                    popupStocks.hidePopup();
                }
                Tools.unregisterListeners(popupStocks);

                final PnlAssign<Stock> pnlAssign = new PnlAssign<Stock>(new ArrayList<Stock>(menuweek2Menu.getMenu().getStocks()), Main.getStockList(e.getModifiers() == InputEvent.CTRL_MASK), new DefaultListRenderer());
                pnlAssign.setVisibleRowCount(30);
                popupStocks = GUITools.createPanelPopup(pnlAssign, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o == null) return;
                        if (CollectionUtils.isEqualCollection(pnlAssign.getAssigned(), menuweek2Menu.getMenu().getStocks()))
                            return;
                        menuweek2Menu.getMenu().getStocks().clear();
                        for (Stock stock : pnlAssign.getAssigned()) {
                            menuweek2Menu.getMenu().getStocks().add(stock);
                        }
                        menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                        btnStock.setToolTipText(MenuTools.getStocksAsHTMLList(menuweek2Menu.getMenu()));
                        btnStock.setText(menuweek2Menu.getMenu().getStocks().isEmpty() ? "" : Integer.toString(menuweek2Menu.getMenu().getStocks().size()));
                        btnStock.setIcon(menuweek2Menu.getMenu().getStocks().isEmpty() ? Const.icon24blackBadge : Const.icon24whiteBadge);
                    }
                }, btnStock);

                GUITools.showPopup(popupStocks, SwingUtilities.CENTER);
            }
        });


//        DefaultOverlayable ovrBadge = new DefaultOverlayable(btnStock);
//        JLabel lblBadge = new JLabel(menuweek2Menu.getMenu().getStocks().isEmpty() ? "" : Integer.toString(menuweek2Menu.getMenu().getStocks().size()), Const.icon24ledRedOn, SwingConstants.CENTER);
//        lblBadge.setHorizontalTextPosition(SwingConstants.CENTER);
//        lblBadge.setVerticalTextPosition(SwingConstants.CENTER);
//        lblBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
//        ovrBadge.addOverlayComponent(lblBadge, DefaultOverlayable.NORTH_EAST);


        btnRedToGreen = new JButton(Const.icon24ledGreenOn4);
        btnRedToGreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component comp : menuLine1.getComponents()) {
                    if (comp instanceof MenuBlock) {
                        ((MenuBlock) comp).menuitemSave();
                    }
                }
                for (Component comp : menuLine2.getComponents()) {
                    if (comp instanceof MenuBlock) {
                        ((MenuBlock) comp).menuitemSave();
                    }
                }
            }
        });

        btnEmpty = new JButton(Const.icon24ledGreenOff4);
        btnEmpty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuweek2Menu = replaceMenu(new Menu());
                initPanel();
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

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String txtSearch = searcherWholeMenu.getText().trim();

                try {
                    long menuid = Long.parseLong(txtSearch);
                    EntityManager em = Main.getEMF().createEntityManager();
                    Menu menu = em.find(Menu.class, menuid);
                    em.close();

                    if (menu == null) {
                        throw new NumberFormatException("PK not found");
                    }
                    menuweek2Menu = replaceMenu(menu);
                    initPanel();

                } catch (NumberFormatException nfe) {
                    PnlSelect<Menuweek2Menu> pnlSelect = new PnlSelect<Menuweek2Menu>(Menuweek2MenuTools.getAllLike(searcherWholeMenu.getText().trim()), Menuweek2MenuTools.getListCellRenderer(), ListSelectionModel.SINGLE_SELECTION);
                    GUITools.showPopup(GUITools.createPanelPopup(pnlSelect, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o == null) return;
                            ArrayList<Menuweek2Menu> list = (ArrayList<Menuweek2Menu>) o;
                            if (list.isEmpty()) return;
                            menuweek2Menu = replaceMenu(list.get(0).getMenu().clone());
                            initPanel();
                        }
                    }, btn), SwingUtilities.SOUTH);
                }


            }
        });

        return btn;
    }


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

//            addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    super.componentResized(e);
//                    SwingUtilities.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            revalidate();
//                            repaint();
//                        }
//                    });
//                }
//
//                @Override
//                public void componentMoved(ComponentEvent e) {
//                    super.componentMoved(e);
//                    SwingUtilities.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            revalidate();
//                            repaint();
//                        }
//                    });
//                }
//            });

            searcher = new JTextField(12) {
                @Override
                public void repaint(long tm, int x, int y, int width, int height) {
                    super.repaint(tm, x, y, width, height);
                    OverlayableUtils.repaintOverlayable(this);
                }
            };
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
//                    createRecipeIfNecessary(searcher.getText());
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
                    miOn.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    JMenuItem miOff = new JMenuItem("Leer", Const.icon24ledGreenOff);
                    miOff.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    jMenu.add(miOn);
                    jMenu.add(miOff);

                    miOn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            menuitemSave();
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

        public void menuitemSave() {
            if (recipe != null && recipe.getId() > 0) return;

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
            Recipes thisRecipe;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PnlSingleDayMenu that = (PnlSingleDayMenu) o;

        if (btnEmpty != null ? !btnEmpty.equals(that.btnEmpty) : that.btnEmpty != null) return false;
        if (btnRedToGreen != null ? !btnRedToGreen.equals(that.btnRedToGreen) : that.btnRedToGreen != null)
            return false;
        if (btnStock != null ? !btnStock.equals(that.btnStock) : that.btnStock != null) return false;

        if (holidays != null ? !holidays.equals(that.holidays) : that.holidays != null) return false;
        if (keyboardFocusManager != null ? !keyboardFocusManager.equals(that.keyboardFocusManager) : that.keyboardFocusManager != null)
            return false;
        if (menuweek2Menu != null ? !menuweek2Menu.equals(that.menuweek2Menu) : that.menuweek2Menu != null)
            return false;
        if (popupStocks != null ? !popupStocks.equals(that.popupStocks) : that.popupStocks != null) return false;
        if (psdChangeListener != null ? !psdChangeListener.equals(that.psdChangeListener) : that.psdChangeListener != null)
            return false;
        if (searcherWholeMenu != null ? !searcherWholeMenu.equals(that.searcherWholeMenu) : that.searcherWholeMenu != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = holidays != null ? holidays.hashCode() : 0;
        result = 31 * result + (psdChangeListener != null ? psdChangeListener.hashCode() : 0);
        result = 31 * result + (searcherWholeMenu != null ? searcherWholeMenu.hashCode() : 0);
        result = 31 * result + (popupStocks != null ? popupStocks.hashCode() : 0);
        result = 31 * result + (keyboardFocusManager != null ? keyboardFocusManager.hashCode() : 0);
        result = 31 * result + (btnStock != null ? btnStock.hashCode() : 0);
        result = 31 * result + (btnEmpty != null ? btnEmpty.hashCode() : 0);
        result = 31 * result + (btnRedToGreen != null ? btnRedToGreen.hashCode() : 0);
        result = 31 * result + (menuweek2Menu != null ? menuweek2Menu.hashCode() : 0);
        return result;
    }
}
