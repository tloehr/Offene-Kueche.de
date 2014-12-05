package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.OverlayableUtils;
import entity.Menu;
import entity.*;
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
    private JButton btnEmpty;
    //    private JButton btnRedToGreen;
    private JButton btnCopyTo;
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


        listOfBlocks.add(new MenuBlock(menuweek2Menu.getMenu().getStarter(), menuweek2Menu.getMenu().getStarterStocks(), "Vorspeise", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setStarter(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                Menu oldMenu = menuweek2Menu.getMenu();
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, menuweek2Menu.getMenu(), menuweek2Menu));
            }

            @Override
            public void stocksChanged(Set<Stock> stocks) {
                menuweek2Menu.getMenu().getStarterStocks().clear();
                menuweek2Menu.getMenu().getStarterStocks().addAll(stocks);
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.stockListChanged(new PSDChangeEvent(this, menuweek2Menu.getMenu(), menuweek2Menu));
            }
        }));

        listOfBlocks.add(new MenuBlock(menuweek2Menu.getMenu().getMaincourse(), menuweek2Menu.getMenu().getMainStocks(), "Hauptgericht", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setMaincourse(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                Menu oldMenu = menuweek2Menu.getMenu();
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, menuweek2Menu.getMenu(), menuweek2Menu));
            }

            @Override
            public void stocksChanged(Set<Stock> stocks) {
                menuweek2Menu.getMenu().getMainStocks().clear();
                menuweek2Menu.getMenu().getMainStocks().addAll(stocks);
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.stockListChanged(new PSDChangeEvent(this, menuweek2Menu.getMenu(), menuweek2Menu));
            }
        }));
        listOfBlocks.add(new MenuBlock(menuweek2Menu.getMenu().getSauce(), menuweek2Menu.getMenu().getSauceStocks(), "Sauce", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSauce(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                Menu oldMenu = menuweek2Menu.getMenu();
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, menuweek2Menu.getMenu(), menuweek2Menu));
            }

            @Override
            public void stocksChanged(Set<Stock> stocks) {
                menuweek2Menu.getMenu().getSauceStocks().clear();
                menuweek2Menu.getMenu().getSauceStocks().addAll(stocks);
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.stockListChanged(new PSDChangeEvent(this, menuweek2Menu.getMenu(), menuweek2Menu));
            }
        }));
        listOfBlocks.add(new MenuBlock(menuweek2Menu.getMenu().getSideveggie(), menuweek2Menu.getMenu().getSideveggieStocks(), "Gemüse/Beilagen/Salat", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSideveggie(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                Menu oldMenu = menuweek2Menu.getMenu();
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, menuweek2Menu.getMenu(), menuweek2Menu));
            }

            @Override
            public void stocksChanged(Set<Stock> stocks) {
                menuweek2Menu.getMenu().getSideveggieStocks().clear();
                menuweek2Menu.getMenu().getSideveggieStocks().addAll(stocks);
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.stockListChanged(new PSDChangeEvent(this, menuweek2Menu.getMenu(), menuweek2Menu));
            }
        }));
        listOfBlocks.add(new MenuBlock(menuweek2Menu.getMenu().getSidedish(), menuweek2Menu.getMenu().getSidedishStocks(), "Kartoffeln/Reis/Nudeln", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setSidedish(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                Menu oldMenu = menuweek2Menu.getMenu();
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, menuweek2Menu.getMenu(), menuweek2Menu));
            }

            @Override
            public void stocksChanged(Set<Stock> stocks) {
                menuweek2Menu.getMenu().getSidedishStocks().clear();
                menuweek2Menu.getMenu().getSidedishStocks().addAll(stocks);
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.stockListChanged(new PSDChangeEvent(this, menuweek2Menu.getMenu(), menuweek2Menu));
            }
        }));
        listOfBlocks.add(new MenuBlock(menuweek2Menu.getMenu().getDessert(), menuweek2Menu.getMenu().getDessertStocks(), "Dessert", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menuweek2Menu.getMenu().setDessert(rce.getNewRecipe());
                searcherWholeMenu.setText(MenuTools.getPrettyString(menuweek2Menu.getMenu()));
                menuweek2Menu.getMenu().setText(searcherWholeMenu.getText().toString());
                Menu oldMenu = menuweek2Menu.getMenu();
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.menuEdited(new PSDChangeEvent(this, oldMenu, menuweek2Menu.getMenu(), menuweek2Menu));
            }

            @Override
            public void stocksChanged(Set<Stock> stocks) {
                menuweek2Menu.getMenu().getDessertStocks().clear();
                menuweek2Menu.getMenu().getDessertStocks().addAll(stocks);
                menuweek2Menu = mergeChanges(menuweek2Menu.getMenu());
                psdChangeListener.stockListChanged(new PSDChangeEvent(this, menuweek2Menu.getMenu(), menuweek2Menu));
            }
        }));

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


        btnCopyTo = new JButton(Const.icon24copy);
        btnCopyTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JidePopupMenu jMenu = new JidePopupMenu();

                for (final Menuweek menuweek : menuweek2Menu.getMenuweek().getMenuweekall().getMenuweeks()) {
                    JMenu innerMenu = new JMenu("Speiseplan: " + menuweek.getRecipefeature().getText());
                    innerMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    for (final Menuweek2Menu myMenuweek2Menu : menuweek.getMenuweek2menus()) {
                        JMenuItem mi = new JMenuItem(sdf.format(myMenuweek2Menu.getDate()));
                        innerMenu.add(mi);
                        mi.setEnabled(!myMenuweek2Menu.equals(menuweek2Menu));
                        mi.setFont(new Font("SansSerif", myMenuweek2Menu.getMenu().isEmpty() ? Font.PLAIN : Font.BOLD, 18));
                        mi.setIcon(myMenuweek2Menu.getMenu().isEmpty() ? null : Const.icon16info);
                        mi.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                EntityManager em = Main.getEMF().createEntityManager();
                                Menu oldMenu = null;
                                Menu replaceMenu = null;
                                Menuweek2Menu otherMenuweek2menu = null;
                                try {
                                    em.getTransaction().begin();

                                    replaceMenu = em.merge(menuweek2Menu.getMenu());
                                    otherMenuweek2menu = em.merge(myMenuweek2Menu);

                                    oldMenu = em.merge(otherMenuweek2menu.getMenu());
                                    oldMenu.getMenu2menuweeks().remove(otherMenuweek2menu);

                                    otherMenuweek2menu.setMenu(replaceMenu);
                                    replaceMenu.getMenu2menuweeks().add(otherMenuweek2menu);

                                    em.lock(otherMenuweek2menu, LockModeType.OPTIMISTIC);
                                    em.lock(replaceMenu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

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
                                    psdChangeListener.menuReplaced(new PSDChangeEvent(this, oldMenu, replaceMenu, otherMenuweek2menu));
                                }
                            }
                        });
                    }
                    jMenu.add(innerMenu);
                }
                jMenu.show(btnCopyTo, 0, btnCopyTo.getPreferredSize().height);
            }
        });
        btnCopyTo.setEnabled(!menuweek2Menu.getMenu().isEmpty());


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
        menuLine3.add(btnCopyTo);
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
        private JLabel lblBadge;
        private DefaultOverlayable ovrBadge;

        MenuBlock(final Recipes recipeIn, Set<Stock> stocks, String overlay, RecipeChangeListener rclIn) {
            super();
            this.stocks = stocks;
            this.rcl = rclIn;
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
                        if (JOptionPane.showInternalConfirmDialog(Main.getDesktop().getMenuweek(), "Du hast das Rezept noch nicht bestätigt.\nSollen wir das jetzt machen ?", "Bisher unbekanntes Rezept", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48stop) == JOptionPane.YES_OPTION) {
                            menuitemSave();
                            keyboardFocusManager.focusNextComponent();
                        }
                    }
                }
            });


            lblBadge = new JLabel(Integer.toString(stocks.size()), Const.icon24redBadge, SwingConstants.CENTER);
            lblBadge.setHorizontalTextPosition(SwingConstants.CENTER);
            lblBadge.setVerticalTextPosition(SwingConstants.CENTER);
            lblBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
            lblBadge.setForeground(Color.YELLOW);

            ovrBadge = new DefaultOverlayable(btnMenu);
            ovrBadge.addOverlayComponent(lblBadge, DefaultOverlayable.NORTH_EAST);
            btnMenu.setToolTipText(MenuTools.getStocksAsHTMLList(stocks));
            ovrBadge.setPreferredSize(btnMenu.getPreferredSize());
            ovrBadge.setOverlayVisible(!stocks.isEmpty());
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


//            final PnlAssign<Stock> pnlAssign = new PnlAssign<Stock>(new ArrayList<Stock>(stocks), Main.getStockList(false), new DefaultListRenderer());
//            pnlAssign.setVisibleRowCount(30);

            final PnlRecipeMenuStock pnlAssign = new PnlRecipeMenuStock(recipe, new ArrayList<Stock>(stocks));


            int response = JOptionPane.showInternalConfirmDialog(Main.getDesktop().getMenuweek(), pnlAssign, "Zuordnungen zu Rezept", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);



            if (response == JOptionPane.OK_OPTION) {

//                if (CollectionUtils.isEqualCollection((ArrayList<Stock>) pnlAssign.getResult(), stocks))
//                    return;

//                stocks.clear();
//                stocks.addAll(((ArrayList<Stock>) pnlAssign.getResult()));
//
//                ovrBadge.setOverlayVisible(!stocks.isEmpty());
//                btnMenu.setToolTipText(MenuTools.getStocksAsHTMLList(stocks));

                Pair<java.util.List<Stock>, Recipes> pair = (Pair<java.util.List<Stock>, Recipes>) pnlAssign.getResult();

                grmpf;
                recipe.getIngTypes2Recipes().clear();
                recipe.getIngTypes2Recipes().addAll(pair.getSecond().getIngTypes2Recipes());

                setRecipe(recipe);
                rcl.stocksChanged(new HashSet<Stock>(pair.getFirst()));

            }


//            popupStocks = GUITools.createPanelPopup(pnlAssign, new Closure() {
//                @Override
//                public void execute(Object o) {
//                    if (o == null) return;
//                    if (CollectionUtils.isEqualCollection((ArrayList<Stock>) pnlAssign.getResult(), stocks))
//                        return;
////                    stocks.clear();
////                    for (Stock stock : pnlAssign.getAssigned()) {
////                        stocks.add(stock);
////                    }
////                    lblBadge.setIcon(stocks.isEmpty() ? Const.icon16yellow : Const.icon16green);
//                    ovrBadge.setOverlayVisible(!stocks.isEmpty());
//                    btnMenu.setToolTipText(MenuTools.getStocksAsHTMLList(stocks));
//                    rcl.stocksChanged(new HashSet<Stock>((ArrayList<Stock>) pnlAssign.getResult()));
//                }
//            }, ovrBadge);
//
//            GUITools.showPopup(popupStocks, SwingUtilities.CENTER);
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

        void stocksChanged(Set<Stock> stocks);
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

//        public boolean isRecipeDeleted() {
//            return newRecipe == null;
//        }

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
