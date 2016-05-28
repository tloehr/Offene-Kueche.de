package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.OverlayableUtils;
import entity.*;
import entity.Menu;
import org.apache.commons.collections.Closure;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
    private JButton btnEmpty;
    //    private JButton btnRedToGreen;
    private JButton btnCopy;
    private JButton btnPaste;
    private Menuweek2Menu menuweek2Menu;
    private ArrayList<MenuBlock> listOfBlocks;
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM yyyy");

    /**
     * updates the display of this single day menu when necessary. NOPs otherwise.
     *
     * @param updatedMenu
     */
    public void updateMenu(Menu updatedMenu) {
        if (menuweek2Menu.getMenu().equals(updatedMenu)) {
            menuweek2Menu.setMenu(updatedMenu);
            initPanel();
        }
    }

//    public void updateRecipe(Recipes recipe) {
//
//
//
//        if (menuweek2Menu.getMenu().equals(updatedMenu)) {
//            menuweek2Menu.setMenu(updatedMenu);
//            initPanel();
//        }
//    }


    public PnlSingleDayMenu(Menuweek2Menu menuweek2Menu, HashMap<LocalDate, String> holidays, PSDChangeListener psdChangeListener) {
        super();
        this.menuweek2Menu = menuweek2Menu;
        this.holidays = holidays;
        this.psdChangeListener = psdChangeListener;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        initPanel();
    }


    private Menuweek2Menu replaceMenu(Menu replaceBy) {
        Menuweek2Menu myMenuweek2Menu = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Menu oldMenu = null;
        try {

            em.getTransaction().begin();
            myMenuweek2Menu = em.merge(menuweek2Menu);
            em.lock(myMenuweek2Menu, LockModeType.OPTIMISTIC);

            oldMenu = em.merge(menuweek2Menu.getMenu());
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
            psdChangeListener.menuReplaced(new PSDChangeEvent(this, oldMenu, myMenuweek2Menu));
        }
        return myMenuweek2Menu;
    }


//    private Recipes mergeChanges(EntityManager em, Recipes recipe, List<Ingtypes2Recipes> listIngTypes2Recipes) {
//
//        EntityManager em = Main.getEMF().createEntityManager();
//        Recipes editedRecipe = null;
//        try {
//            em.getTransaction().begin();
//
//            editedRecipe = em.merge(recipe);
//            em.lock(editedRecipe, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//            editedRecipe.getIngTypes2Recipes().clear();
//            for (Ingtypes2Recipes its : listIngTypes2Recipes) {
//                editedRecipe.getIngTypes2Recipes().add(em.merge(its));
//            }
//
//            em.getTransaction().commit();
//
//        } catch (OptimisticLockException ole) {
//            em.getTransaction().rollback();
//            Main.warn(ole);
//        } catch (Exception exc) {
//            Main.error(exc.getMessage());
//            em.getTransaction().rollback();
//            Main.fatal(exc.getMessage());
//        } finally {
//            em.close();
//        }
//        return myMenuweek2Menu;
//    }

    private ArrayList<Menu> mergeChanges(int dishIndex, RecipeChangeEvent rce) {

        ArrayList<Menu> affectedMenus = new ArrayList<Menu>();

        Menuweek2Menu myMenuweek2Menu = null;
        Menuweek myMenuweek = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Menu editedMenu = null;
        try {
            em.getTransaction().begin();


            myMenuweek2Menu = em.merge(menuweek2Menu);


//            myMenuweek = em.merge(menuweek2Menu.getMenuweek());
//            em.lock(myMenuweek, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//            myMenuweek2Menu.setMenuweek(myMenuweek);

            Recipes newRecipe = null;

            if (rce.getNewRecipe() != null) {
                newRecipe = em.merge(rce.getNewRecipe());
            }

            if (rce.getIngtypes2RecipesList() != null) {
                em.lock(newRecipe, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                newRecipe.getIngTypes2Recipes().clear();
                for (Ingtypes2Recipes its : rce.getIngtypes2RecipesList()) {
                    newRecipe.getIngTypes2Recipes().add(em.merge(its));
                }
            }

            editedMenu = MenuTools.setDish(em.merge(menuweek2Menu.getMenu()), newRecipe, dishIndex);

            if (rce.getStocks() != null) {
                // overwrite all other menus in this week who share the same recipe
                for (Menuweek allMenuWMenuweek : myMenuweek2Menu.getMenuweek().getMenuweekall().getMenuweeks()) {
                    for (Menuweek2Menu allMenuweeks2Menu : allMenuWMenuweek.getMenuweek2menus()) {
                        for (int dish : MenuTools.indicesOf(allMenuweeks2Menu.getMenu(), rce.getNewRecipe())) {
                            Menu otherMenu = em.merge(allMenuweeks2Menu.getMenu());
                            MenuTools.clearStocklist(otherMenu, dish);
                            for (Stock stock : rce.getStocks()) {
                                otherMenu = MenuTools.add2Stocklist(otherMenu, em.merge(stock), dish);

                            }
                            affectedMenus.add(otherMenu);
                        }
                    }
                }
            }

            editedMenu.setText(MenuTools.getPrettyString(editedMenu));


            em.lock(myMenuweek2Menu, LockModeType.OPTIMISTIC);
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
            menuweek2Menu = myMenuweek2Menu;
        }


        return affectedMenus;
    }


    private Menuweek2Menu mergeChanges(Menu menu1) {
        Menuweek2Menu myMenuweek2Menu = null;
        EntityManager em = Main.getEMF().createEntityManager();
        Menu editedMenu = null;
        try {
            em.getTransaction().begin();

            editedMenu = em.merge(menu1);
            myMenuweek2Menu = em.merge(menuweek2Menu);

            em.lock(myMenuweek2Menu, LockModeType.OPTIMISTIC);
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
        }
        return myMenuweek2Menu;
    }


    private void initPanel() {
        removeAll();
        listOfBlocks = new ArrayList<MenuBlock>();

        final JPanel menuComplete = new JPanel();
        JPanel menuLine0 = new JPanel();
        final JPanel menuLine1 = new JPanel();
        final JPanel menuLine2 = new JPanel();
        JPanel menuLine3 = new JPanel();

        menuComplete.setLayout(new BoxLayout(menuComplete, BoxLayout.PAGE_AXIS));

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

        String[] names = new String[]{"Vorspeise", "Hauptgericht", "Sauce", "Gemüse/Beilagen/Salat", "Kartoffeln/Reis/Nudeln", "Dessert"};

        for (final int dishIndex : MenuTools.DISHES) {
            listOfBlocks.add(new MenuBlock(MenuTools.getDish(menuweek2Menu.getMenu(), dishIndex), MenuTools.getStocklist(menuweek2Menu.getMenu(), dishIndex), names[dishIndex], new RecipeChangeListener() {
                @Override
                public void recipeChanged(RecipeChangeEvent rce) {

                    if (dishIndex == MenuTools.MAIN) {

                        ArrayList<Menuweek2Menu> listMenus = MenuTools.getMenus(rce.getNewRecipe(), MenuTools.MAIN);
                        listMenus.remove(menuweek2Menu); // remove me from the list

                        for (Menuweek2Menu m2m : listMenus) {
                            Main.debug(m2m.getDate() + " " + m2m.getMenu().getText());
                        }
                    }

                    Menu oldMenu = menuweek2Menu.getMenu();
                    ArrayList<Menu> affectedMenus = mergeChanges(dishIndex, rce);
                    searcherWholeMenu.setText(menuweek2Menu.getMenu().getText());

                    for (Menu afftectedMenu : affectedMenus) {
                        psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, afftectedMenu, menuweek2Menu));
                    }
                }
            }

            ));
        }


        menuLine1.add(listOfBlocks.get(0));
        menuLine1.add(listOfBlocks.get(1));
        menuLine1.add(listOfBlocks.get(2));
        menuLine2.add(listOfBlocks.get(3));
        menuLine2.add(listOfBlocks.get(4));
        menuLine2.add(listOfBlocks.get(5));

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

        btnCopy = new JButton(Const.icon24copy);
        btnCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.cbCopy(menuweek2Menu.clone());


//                JidePopupMenu jMenu = new JidePopupMenu();

//                for (final Menuweek menuweek : menuweek2Menu.getMenuweek().getMenuweekall().getMenuweeks()) {
//                    JMenu innerMenu = new JMenu("Speiseplan: " + menuweek.getRecipefeature().getText());
//                    innerMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));
////                    for (final Menuweek2Menu myMenuweek2Menu : menuweek.getMenuweek2menus()) {
////                        JMenuItem mi = new JMenuItem(sdf.format(myMenuweek2Menu.getDate()));
////                        innerMenu.add(mi);
////                        mi.setEnabled(!myMenuweek2Menu.equals(menuweek2Menu));
////                        mi.setFont(new Font("SansSerif", myMenuweek2Menu.getMenu().isEmpty() ? Font.PLAIN : Font.BOLD, 18));
////                        mi.setIcon(myMenuweek2Menu.getMenu().isEmpty() ? null : Const.icon16info);
////                        mi.addActionListener(new ActionListener() {
////                            @Override
////                            public void actionPerformed(ActionEvent e) {
////                                EntityManager em = Main.getEMF().createEntityManager();
////                                Menu oldMenu = null;
////                                Menu replaceMenu = null;
////                                Menuweek2Menu otherMenuweek2menu = null;
////                                try {
////                                    em.getTransaction().begin();
////
////                                    replaceMenu = em.merge(menuweek2Menu.getMenu());
////                                    otherMenuweek2menu = em.merge(myMenuweek2Menu);
////
////                                    oldMenu = em.merge(otherMenuweek2menu.getMenu());
////                                    oldMenu.getMenu2menuweeks().remove(otherMenuweek2menu);
////
////                                    otherMenuweek2menu.setMenu(replaceMenu);
////                                    replaceMenu.getMenu2menuweeks().add(otherMenuweek2menu);
////
////                                    em.lock(otherMenuweek2menu, LockModeType.OPTIMISTIC);
////                                    em.lock(replaceMenu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
////
////                                    em.getTransaction().commit();
////                                } catch (OptimisticLockException ole) {
////                                    em.getTransaction().rollback();
////                                    Main.warn(ole);
////                                } catch (Exception exc) {
////                                    Main.error(exc.getMessage());
////                                    em.getTransaction().rollback();
////                                    Main.fatal(exc.getMessage());
////                                } finally {
////                                    em.close();
////                                    psdChangeListener.menuReplaced(new PSDChangeEvent(this, oldMenu, replaceMenu, otherMenuweek2menu));
////                                }
////                            }
////                        });
////                    }
//                    jMenu.add(innerMenu);
//                }

            }
        });


        btnPaste = new JButton(Const.icon24paste);
        btnPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!Main.copyQueueContainsClass(Menuweek2Menu.class)) return;


                JidePopupMenu jMenu = new JidePopupMenu();

                for (final Object obj : Main.getCopyQueue()) {

                    if (obj instanceof Menuweek2Menu) {
                        JMenu innerMenu = new JMenu(sdf.format(((Menuweek2Menu) obj).getDate()) + " " + Tools.left(((Menuweek2Menu) obj).getMenu().getText(), 15));
                        innerMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));

                        EntityManager em = Main.getEMF().createEntityManager();
                        Menu oldMenu = null;
                        Menu replaceMenu = null;
                        Menuweek2Menu myMenuweek2menu = null;
                        try {
                            em.getTransaction().begin();

                            replaceMenu = em.merge(((Menuweek2Menu) obj).getMenu());
                            myMenuweek2menu = em.merge(menuweek2Menu);

                            oldMenu = em.merge(myMenuweek2menu.getMenu());
                            oldMenu.getMenu2menuweeks().remove(menuweek2Menu);

                            myMenuweek2menu.setMenu(replaceMenu);
                            replaceMenu.getMenu2menuweeks().add(myMenuweek2menu);

                            em.lock(myMenuweek2menu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                            em.lock(replaceMenu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                            em.getTransaction().commit();

                            menuweek2Menu = myMenuweek2menu;

                        } catch (OptimisticLockException ole) {
                            em.getTransaction().rollback();
                            Main.warn(ole);
                        } catch (Exception exc) {
                            Main.error(exc.getMessage());
                            em.getTransaction().rollback();
                            Main.fatal(exc.getMessage());
                        } finally {
                            em.close();
                            psdChangeListener.menuReplaced(new PSDChangeEvent(this, oldMenu, replaceMenu, menuweek2Menu));
                        }


                        jMenu.add(innerMenu);
                    }

                }

            }
        });

//        btnCopy.setEnabled(!menuweek2Menu.getMenu().isEmpty());


        btnEmpty = new JButton(Const.icon24clear);
        btnEmpty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuweek2Menu = replaceMenu(new Menu());
                initPanel();
            }
        });


        menuLine3.add(ovrComment);
        menuLine3.add(getMenuFindButton());
        menuLine3.add(btnCopy);
        menuLine3.add(btnEmpty);

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

        //todo: not yet
        btn.setEnabled(false);
        return btn;
    }


    private class MenuBlock extends JPanel {

        private final Set<Stock> stocks;
        private final RecipeChangeListener rcl;
        private Recipes recipe;
        private final JTextField searcher;
        private JidePopup popup;
        private DefaultListModel<Recipes> dlm;
        private JButton btnMenu;
        private boolean initPhase;
        private JList<Recipes> jList;
        private JScrollPane scrl;
        private JLabel lblBadgeRed, lblBadgeGreen;
        private DefaultOverlayable ovrBadge;

        MenuBlock(final Recipes recipeIn, Set<Stock> stocks, String overlay, RecipeChangeListener rclIn) {
            super();
            this.stocks = stocks;
            this.rcl = rclIn;
            lblBadgeRed = new JLabel(Const.icon24redBadge);
            lblBadgeRed.setHorizontalTextPosition(SwingConstants.CENTER);
            lblBadgeGreen = new JLabel(Const.icon24greenBadgeWide);
            lblBadgeGreen.setHorizontalTextPosition(SwingConstants.CENTER);
            initPhase = true;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            dlm = new DefaultListModel<Recipes>();

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


                    if (jList != null && jList.getSelectedValue() != null) {
                        setRecipe(jList.getSelectedValue());
                    } else {
                        createRecipeIfNecessary(searcher.getText());
                    }
                    keyboardFocusManager.focusNextComponent();


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
                    } else if (searcher.getText().isEmpty()) {
                        setRecipe(null);
                    } else {
                        createRecipeIfNecessary(searcher.getText());
                    }
                    keyboardFocusManager.focusNextComponent();
                }
            });

            btnMenu = new JButton() {
                @Override
                public void repaint(long tm, int x, int y, int width, int height) {
                    super.repaint(tm, x, y, width, height);
                    OverlayableUtils.repaintOverlayable(this);
                }
            };

            btnMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopupMenu jMenu = new JidePopupMenu();
                    JMenuItem miOn = new JMenuItem("Speichern", Const.icon24ledGreenOn);
                    miOn.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    JMenuItem miOff = new JMenuItem("Leer", Const.icon24ledGreenOff);
                    miOff.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    JMenuItem miStocks = new JMenuItem("Vorräte zuordnen", Const.icon24box);
                    miStocks.setFont(new Font("SansSerif", Font.PLAIN, 18));
//                    miStocks.setToolTipText("wirkt sich auf alle gleichen Rezepte in dieser Woche aus");

                    jMenu.add(miOn);
                    jMenu.add(miOff);
                    jMenu.add(new JSeparator());
                    jMenu.add(miStocks);

                    miOn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            menuitemSave();
                            keyboardFocusManager.focusNextComponent();
                        }
                    });
                    miOn.setEnabled(recipe != null && recipe.getId() == 0l);

                    miOff.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setRecipe(null);
                        }
                    });
                    miOff.setEnabled(recipe != null && recipe.getId() != 0l);

                    miStocks.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            menuitemStock();
                        }
                    });
                    miStocks.setEnabled(recipe != null && recipe.getId() != 0l);

                    jMenu.show(btnMenu, 0, btnMenu.getPreferredSize().height);
                }
            });
            btnMenu.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        menuitemSave();
                        keyboardFocusManager.focusNextComponent();
                    }
                }
            });
            btnMenu.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    if (recipe != null && recipe.getId() == 0) {
                        if (JOptionPane.showConfirmDialog(Main.getDesktop().getMenuweek(), "Du hast das Rezept noch nicht bestätigt.\nSollen wir das jetzt machen ?", "Bisher unbekanntes Rezept", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48stop) == JOptionPane.YES_OPTION) {
                            menuitemSave();
                            keyboardFocusManager.focusNextComponent();
                        }
                    }
                }
            });

            ovrBadge = new DefaultOverlayable(btnMenu);

            if (!stocks.isEmpty()) {
                lblBadgeRed.setText(Integer.toString(stocks.size()));
                lblBadgeRed.setHorizontalTextPosition(SwingConstants.CENTER);
                lblBadgeRed.setVerticalTextPosition(SwingConstants.CENTER);
                lblBadgeRed.setFont(new Font("SansSerif", Font.BOLD, 11));
                lblBadgeRed.setForeground(Color.YELLOW);
                ovrBadge.addOverlayComponent(lblBadgeRed, DefaultOverlayable.NORTH_EAST);
            }

            if (recipeIn != null && (!recipeIn.getIngTypes2Recipes().isEmpty() || !recipeIn.getSubrecipes().isEmpty())) {
                lblBadgeGreen.setText(Integer.toString(recipeIn.getIngTypes2Recipes().size()) + "," + Integer.toString(recipeIn.getSubrecipes().size()));
                lblBadgeGreen.setHorizontalTextPosition(SwingConstants.CENTER);
                lblBadgeGreen.setVerticalTextPosition(SwingConstants.CENTER);
                lblBadgeGreen.setFont(new Font("SansSerif", Font.BOLD, 11));
                lblBadgeGreen.setForeground(Color.BLACK);
                ovrBadge.addOverlayComponent(lblBadgeGreen, DefaultOverlayable.SOUTH_WEST);
            }

            btnMenu.setToolTipText("<html>" + HTML.ul(RecipeTools.getSubRecipesAsHTML(recipeIn)) + RecipeTools.getIngTypesAsHTMLList(recipeIn, HTML.h2("Zutaten")) + MenuTools.getStocksAsHTMLList(stocks) + "</html>");
            ovrBadge.setPreferredSize(btnMenu.getPreferredSize());
//            ovrBadge.setOverlayVisible(!stocks.isEmpty());
            setRecipe(recipeIn);

            add(ovrComment);
            add(ovrBadge);

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

        public void menuitemStock() {
            if (recipe == null || recipe.getId() == 0) return;

            if (popupStocks != null && popupStocks.isVisible()) {
                popupStocks.hidePopup();
            }
            Tools.unregisterListeners(popupStocks);

            final MyJDialog dlg = new MyJDialog(Main.getDesktop().getMenuweek());

            final PnlRecipeMenuStock pnlAssign = new PnlRecipeMenuStock(recipe, new ArrayList<Stock>(stocks), new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    dlg.pack();
                }
            });

            pnlAssign.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dlg.dispose();
                    if (e.getActionCommand().equals("OK")) {
                        Pair<java.util.List<Stock>, DefaultTreeModel> pair = (Pair<java.util.List<Stock>, DefaultTreeModel>) pnlAssign.getResult();

                        recipe.getIngTypes2Recipes().clear();
                        recipe.getSubrecipes().clear();

                        for (int childNum = 0; childNum < pair.getSecond().getChildCount(pair.getSecond().getRoot()); childNum++) {

                            DefaultMutableTreeNode child = (DefaultMutableTreeNode) pair.getSecond().getChild(pair.getSecond().getRoot(), childNum);

                            if (child.getUserObject() instanceof Recipes) {
                                recipe.getSubrecipes().add((Recipes) child.getUserObject());
                            } else if (child.getUserObject() instanceof Ingtypes2Recipes) {
                                recipe.getIngTypes2Recipes().add((Ingtypes2Recipes) child.getUserObject());
                            }

                        }

                        rcl.recipeChanged(new RecipeChangeEvent(searcher, recipe, recipe.getIngTypes2Recipes(), new HashSet<Stock>(pair.getFirst())));
                    }
                }
            });

            dlg.setModal(true);
            dlg.getContentPane().setLayout(new BoxLayout(dlg.getContentPane(), BoxLayout.X_AXIS));
            dlg.getContentPane().add(pnlAssign);
            dlg.setResizable(true);
            dlg.setTitle("Zuordnungen zu Rezept");


            dlg.pack();
            dlg.setVisible(true);

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
            ovrBadge.removeOverlayComponent(lblBadgeRed);
            ovrBadge.removeOverlayComponent(lblBadgeGreen);
            if (recipe == null) {
                btnMenu.setIcon(Const.icon24ledGreenOff);
            } else if (recipe.getId() == 0) {
                btnMenu.setIcon(Const.icon24ledRedOn);
            } else {
                btnMenu.setIcon(Const.icon24ledGreenOn);
                if (!stocks.isEmpty()) {
                    lblBadgeRed.setText(Integer.toString(stocks.size()));
                    ovrBadge.addOverlayComponent(lblBadgeRed, DefaultOverlayable.NORTH_EAST);
                }
                if (recipe != null && (!recipe.getIngTypes2Recipes().isEmpty() || !recipe.getSubrecipes().isEmpty())) {
                    lblBadgeGreen.setText(Integer.toString(recipe.getIngTypes2Recipes().size()) + "," + Integer.toString(recipe.getSubrecipes().size()));
                    ovrBadge.addOverlayComponent(lblBadgeGreen, DefaultOverlayable.SOUTH_WEST);
                }
            }

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


//        void setRecipe(Recipes recipe, List<Ingtypes2Recipes> ingtypes2RecipesList) {
//            this.recipe = recipe;
//            searcher.setText(recipe == null ? "" : recipe.getTitle());
//            searcher.setToolTipText(recipe == null ? "" : recipe.getText());
//
//            if (!initPhase && ((recipe != null && recipe.getId() != 0) || recipe == null)) {
//                rcl.recipeChanged(new RecipeChangeEvent(searcher, recipe, ingtypes2RecipesList));
//            }
//
//            setAccepted();
//
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    revalidate();
//                    repaint();
//                }
//            });
//        }


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

//        void stocksChanged(Set<Stock> stocks);

//        void ingTypesListChanged(List<IngTypes> ingTypes);
    }

    private class RecipeChangeEvent extends EventObject {

        private final Recipes newRecipe;
        private final List<Ingtypes2Recipes> ingtypes2RecipesList;
        private final Set<Stock> stocks;

        public RecipeChangeEvent(Object source, Recipes newRecipe) {
            super(source);
            this.newRecipe = newRecipe;
            this.ingtypes2RecipesList = null;
            this.stocks = null;
        }

        public RecipeChangeEvent(Object source, Recipes newRecipe, List<Ingtypes2Recipes> ingtypes2RecipesList, Set<Stock> stocks1) {
            super(source);
            this.newRecipe = newRecipe;
            this.ingtypes2RecipesList = ingtypes2RecipesList;
            this.stocks = stocks1;
        }

//        public boolean isRecipeDeleted() {
//            return newRecipe == null;
//        }


        public List<Ingtypes2Recipes> getIngtypes2RecipesList() {
            return ingtypes2RecipesList;
        }

        public Set<Stock> getStocks() {
            return stocks;
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
//        if (btnRedToGreen != null ? !btnRedToGreen.equals(that.btnRedToGreen) : that.btnRedToGreen != null)
//            return false;

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
        result = 31 * result + (btnEmpty != null ? btnEmpty.hashCode() : 0);
//        result = 31 * result + (btnRedToGreen != null ? btnRedToGreen.hashCode() : 0);
        result = 31 * result + (menuweek2Menu != null ? menuweek2Menu.hashCode() : 0);
        return result;
    }
}
