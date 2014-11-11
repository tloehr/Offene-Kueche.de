package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JidePopupMenu;
import entity.Menu;
import entity.MenuTools;
import entity.Recipes;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.joda.time.LocalDate;
import tools.Const;
import tools.GUITools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by tloehr on 14.10.14.
 */
public class PnlSingleDayMenu extends JPanel {


//    private final int STARTER = 0;
//    private final int MAIN = 1;
//    private final int SAUCE = 2;
//    private final int SIDEVEGGIE = 3;
//    private final int SIDEDISH = 4;
//    private final int DESSERT = 5;

    private Closure changeAction;
    private Menu menu;
    private LocalDate date;
    //    private JTextField searcher;
//    private JLabel lblLEDStarter;
    private JTextField searcherWholeMenu;
    private JidePopup popupM;
    KeyboardFocusManager keyboardFocusManager;
    private DefaultListModel<Menu> dlmM;


    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setChangeAction(Closure changeAction) {
        this.changeAction = changeAction;
    }

    public PnlSingleDayMenu(Menu menu) {
        super();

        this.menu = menu;

        this.date = new LocalDate(menu.getDate());


        dlmM = new DefaultListModel<Menu>();
        initPanel();
    }

    public LocalDate getDate() {
        return date;
    }

    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel menuComplete = new JPanel();
        JPanel menuLine1 = new JPanel();
        JPanel menuLine2 = new JPanel();
        JPanel menuLine3 = new JPanel();

        menuComplete.setLayout(new BoxLayout(menuComplete, BoxLayout.PAGE_AXIS));
        menuLine1.setLayout(new BoxLayout(menuLine1, BoxLayout.LINE_AXIS));
        menuLine2.setLayout(new BoxLayout(menuLine2, BoxLayout.LINE_AXIS));
        menuLine3.setLayout(new BoxLayout(menuLine3, BoxLayout.LINE_AXIS));

        keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();


        menuLine1.add(new MenuBlock(menu.getStarter(), "Vorspeise", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menu.setStarter(rce.getNewRecipe());
                changeAction.execute(menu);
                searcherWholeMenu.setText(MenuTools.getPrettyString(menu));
            }
        }));

        menuLine1.add(new MenuBlock(menu.getMaincourse(), "Hauptgericht", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menu.setMaincourse(rce.getNewRecipe());
                changeAction.execute(menu);
                searcherWholeMenu.setText(MenuTools.getPrettyString(menu));
            }
        }));

        menuLine1.add(new MenuBlock(menu.getSauce(), "Sauce", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menu.setSauce(rce.getNewRecipe());
                changeAction.execute(menu);
                searcherWholeMenu.setText(MenuTools.getPrettyString(menu));
            }
        }));

        menuLine2.add(new MenuBlock(menu.getSideveggie(), "Gemüse/Beilagen/Salat", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menu.setSideveggie(rce.getNewRecipe());
                changeAction.execute(menu);
                searcherWholeMenu.setText(MenuTools.getPrettyString(menu));
            }
        }));

        menuLine2.add(new MenuBlock(menu.getSidedish(), "Kartoffeln/Reis/Nudeln", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menu.setSidedish(rce.getNewRecipe());
                changeAction.execute(menu);
                searcherWholeMenu.setText(MenuTools.getPrettyString(menu));
            }
        }));

        menuLine2.add(new MenuBlock(menu.getDessert(), "Dessert", new RecipeChangeListener() {
            @Override
            public void recipeChanged(RecipeChangeEvent rce) {
                menu.setDessert(rce.getNewRecipe());
                changeAction.execute(menu);
                searcherWholeMenu.setText(MenuTools.getPrettyString(menu));
            }
        }));

//        menuLine1.add(searcherStarter);


        searcherWholeMenu = new JXSearchField("Titel auf der Speisekarte");
        searcherWholeMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));
        menuLine3.add(searcherWholeMenu);
        searcherWholeMenu.setText(menu.getText());
        menuLine3.add(new JButton(Const.icon24find));

//        searcherWholeMenu.addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                super.focusGained(e);
//                ((JTextComponent) e.getSource()).selectAll();
//                focusid = -1;
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//                super.focusLost(e);
//                if (popupR != null) {
//                    popupR.hidePopup();
//                    popupR = null;
//                }
//                createRecipeIfNecessary((JTextField) e.getSource());
//            }
//        });
//        searcherWholeMenu.setCancelAction(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                cancelListener(ae);
//            }
//        });
//
//        searcherWholeMenu.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
////                searcherRecipeAction(ae);
//            }
//        });
//        menuLine3.add(searcherWholeMenu);


//        searcher.addCaretListener(new CaretListener() {
//            @Override
//            public void caretUpdate(CaretEvent e) {
//
//            }
//        });
//        searcher.addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusLost(FocusEvent e) {
//                super.focusLost(e);
//                searcherFocusLostListener(e);
//            }
//        });
//        searcher.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                buttonDelete.requestFocus();
//            }
//        });


//        topLine.add(buttonDelete);

        menuComplete.add(menuLine1);
        menuComplete.add(menuLine2);
        menuComplete.add(menuLine3);
        add(menuComplete);
//        add(new JLabel(menu.getRecipe() == null ? "--" : menu.getRecipe().getTitle()));
    }


    private void searchMenus(String searchPattern) {
        dlmM.clear();

        EntityManager em = Main.getEMF().createEntityManager();

        Query query = em.createQuery("SELECT r FROM Menu r WHERE (r.text LIKE :pattern) ORDER BY r.text ");
        query.setParameter("pattern", "%" + searchPattern + "%");

        ArrayList<Menu> listMenus = new ArrayList<Menu>(query.getResultList());
        em.close();

        for (Menu menu : listMenus) {
            dlmM.addElement(menu);
        }
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
