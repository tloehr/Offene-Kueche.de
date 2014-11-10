package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JidePopupMenu;
import entity.Menu;
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
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

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
    private JXSearchField searcherMain, searcherSauce, searcherSideveggie, searcherSidedish, searcherDessert, searcherWholeMenu;
    private JidePopup popupR, popupM;
    private DefaultListModel<Recipes> dlmR;
    private DefaultListModel<Menu> dlmM;
    private boolean reactToTextChange;

    private int focusid = -1, currentFocusID = -1;

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

        reactToTextChange = true;

        this.menu = menu;

        this.date = new LocalDate(menu.getDate());


        dlmR = new DefaultListModel<Recipes>();
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

//        final JButton buttonDelete = new JButton(Const.icon24remove);
//        buttonDelete.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                searcher.setText(null);
//                menu.setRecipe(null);
//            }
//        });

        reactToTextChange = false;


//        menuLine1.add(searcherStarter);


        searcherMain = new JXSearchField("Hauptgang");
        searcherMain.setText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getTitle());
        searcherMain.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                enterPressed(e);
            }
        });
        searcherMain.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherMain.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherMain.setToolTipText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getText());
        searcherMain.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ((JTextComponent) e.getSource()).selectAll();
                focusid = 2;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (popupR != null) {
                    popupR.hidePopup();
                    popupR = null;
                }

                createRecipeIfNecessary((JTextField) e.getSource());
            }
        });
        searcherMain.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });
        searcherMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherRecipeAction(ae);
            }
        });
        menuLine1.add(searcherMain);
        menuLine1.add(new JButton(Const.icon24add));

        searcherSauce = new JXSearchField("Sauce");
        searcherSauce.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                enterPressed(e);
            }
        });
        searcherSauce.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSauce.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSauce.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherSauce.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherSauce.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ((JTextComponent) e.getSource()).selectAll();
                focusid = 3;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (popupR != null) {
                    popupR.hidePopup();
                    popupR = null;
                }

                createRecipeIfNecessary((JTextField) e.getSource());
            }
        });
        searcherSauce.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });

        searcherSauce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherRecipeAction(ae);
            }
        });
        menuLine1.add(searcherSauce);
        menuLine1.add(new JButton(Const.icon24add));

        searcherSideveggie = new JXSearchField("Gemüse/Salat");
        searcherSideveggie.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                enterPressed(e);
            }
        });
        searcherSideveggie.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSideveggie.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSideveggie.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherSideveggie.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherSideveggie.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ((JTextComponent) e.getSource()).selectAll();
                focusid = 4;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (popupR != null) {
                    popupR.hidePopup();
                    popupR = null;
                }

                createRecipeIfNecessary((JTextField) e.getSource());
            }
        });
        searcherSideveggie.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });

        searcherSideveggie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherRecipeAction(ae);
            }
        });
        menuLine2.add(searcherSideveggie);
        menuLine2.add(new JButton(Const.icon24add));

        searcherSidedish = new JXSearchField("Kartoffeln/Reis/Nudeln");
        searcherSidedish.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                enterPressed(e);
            }
        });
        searcherSidedish.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSidedish.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSidedish.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherSidedish.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherSidedish.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ((JTextComponent) e.getSource()).selectAll();
                focusid = 5;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (popupR != null) {
                    popupR.hidePopup();
                    popupR = null;
                }
                createRecipeIfNecessary((JTextField) e.getSource());
            }
        });
        searcherSidedish.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });

        searcherSidedish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherRecipeAction(ae);
            }
        });
        menuLine2.add(searcherSidedish);

        searcherDessert = new JXSearchField("Dessert");
        searcherDessert.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                enterPressed(e);
            }
        });
        searcherDessert.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherDessert.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherDessert.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherDessert.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherDessert.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ((JTextComponent) e.getSource()).selectAll();
                focusid = 6;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (popupR != null) {
                    popupR.hidePopup();
                    popupR = null;
                }
                createRecipeIfNecessary((JTextField) e.getSource());
            }
        });
        searcherDessert.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });

        searcherDessert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherRecipeAction(ae);
            }
        });
        menuLine2.add(searcherDessert);
        menuLine2.add(new JButton(Const.icon24add));


        searcherWholeMenu = new JXSearchField("Titel auf der Speisekarte");
        searcherWholeMenu.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherWholeMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherWholeMenu.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ((JTextComponent) e.getSource()).selectAll();
                focusid = -1;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (popupR != null) {
                    popupR.hidePopup();
                    popupR = null;
                }
                createRecipeIfNecessary((JTextField) e.getSource());
            }
        });
        searcherWholeMenu.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });

        searcherWholeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
//                searcherRecipeAction(ae);
            }
        });
        menuLine3.add(searcherWholeMenu);


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
        reactToTextChange = true;
        menuComplete.add(menuLine1);
        menuComplete.add(menuLine2);
        menuComplete.add(menuLine3);
        add(menuComplete);
//        add(new JLabel(menu.getRecipe() == null ? "--" : menu.getRecipe().getTitle()));
    }

//    private JButton getStockButton()


    private void escPressed(KeyEvent e) {
        reactToTextChange = false;

        if (e.getSource().equals(searcherStarter)) {
            searcherStarter.setText(menu.getStarter() == null ? "" : menu.getStarter().getTitle());
            searcherStarter.setToolTipText(menu.getStarter() == null ? "" : menu.getStarter().getText());
        } else if (e.getSource().equals(searcherMain)) {
            searcherMain.setText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getTitle());
            searcherMain.setToolTipText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getText());
        } else if (e.getSource().equals(searcherSauce)) {
            searcherSauce.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
            searcherSauce.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        } else if (e.getSource().equals(searcherSideveggie)) {
            searcherSideveggie.setText(menu.getSideveggie() == null ? "" : menu.getSideveggie().getTitle());
            searcherSideveggie.setToolTipText(menu.getSideveggie() == null ? "" : menu.getSideveggie().getText());
        } else if (e.getSource().equals(searcherSidedish)) {
            searcherSidedish.setText(menu.getSidedish() == null ? "" : menu.getSidedish().getTitle());
            searcherSidedish.setToolTipText(menu.getSidedish() == null ? "" : menu.getSidedish().getText());
        } else if (e.getSource().equals(searcherDessert)) {
            searcherDessert.setText(menu.getDessert() == null ? "" : menu.getDessert().getTitle());
            searcherDessert.setToolTipText(menu.getDessert() == null ? "" : menu.getDessert().getText());
        } else if (e.getSource().equals(searcherWholeMenu)) {
            searcherWholeMenu.setText(menu.getText());
        } else {
            Main.fatal("schade im grunde");
        }

        reactToTextChange = true;

        if (popupR != null) {
            popupR.hidePopup();
            popupR = null;
        }
    }

    private void cancelListener(ActionEvent e) {
        reactToTextChange = false;

        if (e.getSource().equals(searcherStarter)) {
            searcherStarter.setText(menu.getStarter() == null ? "" : menu.getStarter().getTitle());
            searcherStarter.setToolTipText(menu.getStarter() == null ? "" : menu.getStarter().getText());
        } else if (e.getSource().equals(searcherMain)) {
            searcherMain.setText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getTitle());
            searcherMain.setToolTipText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getText());
        } else if (e.getSource().equals(searcherSauce)) {
            searcherSauce.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
            searcherSauce.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        } else if (e.getSource().equals(searcherSideveggie)) {
            searcherSideveggie.setText(menu.getSideveggie() == null ? "" : menu.getSideveggie().getTitle());
            searcherSideveggie.setToolTipText(menu.getSideveggie() == null ? "" : menu.getSideveggie().getText());
        } else if (e.getSource().equals(searcherSidedish)) {
            searcherSidedish.setText(menu.getSidedish() == null ? "" : menu.getSidedish().getTitle());
            searcherSidedish.setToolTipText(menu.getSidedish() == null ? "" : menu.getSidedish().getText());
        } else if (e.getSource().equals(searcherDessert)) {
            searcherDessert.setText(menu.getDessert() == null ? "" : menu.getDessert().getTitle());
            searcherDessert.setToolTipText(menu.getDessert() == null ? "" : menu.getDessert().getText());
        } else if (e.getSource().equals(searcherWholeMenu)) {
            searcherWholeMenu.setText(menu.getText());
        } else {
            Main.fatal("schade im grunde");
        }

        reactToTextChange = true;

        if (popupR != null) {
            popupR.hidePopup();
            popupR = null;
        }
    }

    private void enterPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) return;
        createRecipeIfNecessary((JTextField) e.getSource());
    }

    private void createRecipeIfNecessary(JTextField source) {
        if (source.getText().trim().isEmpty()) return;
        Recipes thisRecipe = findInDLMr(source.getText().trim());

        if (thisRecipe == null & JOptionPane.showInternalConfirmDialog(Main.getDesktop().getMenuweek(), "Das Rezept kenne ich noch gar nicht.\n" +
                        "Achte darauf, dass alles richtig geschrieben ist\n\n" +
                        "Soll ich das mit aufnehmen ?", "Neues Rezept: '" + source.getText().trim() + "'",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION) {

            EntityManager em = Main.getEMF().createEntityManager();
            try {
                em.getTransaction().begin();
                thisRecipe = em.merge(new Recipes(source.getText().trim()));
                em.getTransaction().commit();
            } catch (Exception ex) {
                Main.fatal(ex);
            } finally {
                em.close();
            }
        }


        if (thisRecipe != null) {
            setRecipe(source, thisRecipe);
            changeAction.execute(menu);
        }
    }


    private Recipes findInDLMr(String searchPattern) {
        if (dlmR.isEmpty()) return null;

        Recipes found = null;

        for (Recipes r : Collections.list(dlmR.elements())) {
            if (r.getTitle().equals(searchPattern)) {
                found = r;
                break;
            }
        }

        return found;


    }


    private void searcherRecipeAction(final ActionEvent ae) {
//        if (!reactToCaret) return;
//        if (e.getDot() == 0) return;
        if (!reactToTextChange) return;
        if (!((JComponent) ae.getSource()).hasFocus()) return;

//        if (popupR != null && currentFocusID != focusid) {
//            popupR.hidePopup();
//            popupR = null;
//        }
//        currentFocusID = focusid;

        if (popupR == null) {

            popupR = new JidePopup();
            popupR.setOwner((JComponent) ae.getSource());

            Main.debug(ae.getSource());

            popupR.setMovable(false);


            final JList<Recipes> list = new JList(dlmR);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setRecipe(ae.getSource(), list.getSelectedValue());
                        ((JXSearchField) ae.getSource()).setText(list.getSelectedValue().getTitle());
                        changeAction.execute(menu);
                        popupR.hidePopup();
                    }
                    super.mouseClicked(e);
                }
            });


            popupR.setContentPane(new JScrollPane(list));
            popupR.removeExcludedComponent((JXSearchField) ae.getSource());

            GUITools.showPopup(popupR, SwingConstants.SOUTH);
        }

        searchRecipes(((JXSearchField) ae.getSource()).getText().trim());

        if (dlmR.isEmpty() && popupR.isShowing()) {
            popupR.hidePopup();

        }

        if (!dlmR.isEmpty() && popupR != null && !popupR.isShowing()) {
            popupR.setOwner((JComponent) ae.getSource());
            GUITools.showPopup(popupR, SwingConstants.SOUTH);
        }


    }


    private void setRecipe(Object source, Recipes recipes) {
        if (source.equals(searcherStarter)) {
            menu.setStarter(recipes);
            searcherDessert.setToolTipText(recipes == null ? "" : recipes.getText());
        } else if (source.equals(searcherMain)) {
            menu.setMaincourse(recipes);
            searcherMain.setToolTipText(recipes == null ? "" : recipes.getText());
        } else if (source.equals(searcherSauce)) {
            menu.setSauce(recipes);
            searcherSauce.setToolTipText(recipes == null ? "" : recipes.getText());
        } else if (source.equals(searcherSideveggie)) {
            menu.setSideveggie(recipes);
            searcherSideveggie.setToolTipText(recipes == null ? "" : recipes.getText());
        } else if (source.equals(searcherSidedish)) {
            menu.setSidedish(recipes);
            searcherSidedish.setToolTipText(recipes == null ? "" : recipes.getText());
        } else if (source.equals(searcherDessert)) {
            menu.setDessert(recipes);
            searcherDessert.setToolTipText(recipes == null ? "" : recipes.getText());
        } else {
            Main.fatal("schade im grunde");
        }
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


    private void searchRecipes(String searchPattern) {
        dlmR.clear();


        EntityManager em = Main.getEMF().createEntityManager();

        Query query = em.createQuery("SELECT r FROM Recipes r WHERE (r.text LIKE :pattern) OR (r.title LIKE :pattern) ORDER BY r.title ");
        query.setParameter("pattern", "%" + searchPattern + "%");

        ArrayList<Recipes> listRecipes = new ArrayList<Recipes>(query.getResultList());
        em.close();

        for (Recipes recipes : listRecipes) {
            dlmR.addElement(recipes);
        }
    }

    private class MenuBlock extends JPanel {

        private Recipes recipe;
        private final JTextField searcher;
        private JidePopup popup;
        private DefaultListModel<Recipes> dlm;
        private Closure changeEvent;
        private JButton btnMenu;
        private boolean initPhase;


        MenuBlock(Recipes recipe, String overlay) {
            super();
            initPhase = true;
            this.recipe = recipe;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

            searcher = new JTextField(20);
            DefaultOverlayable ovrComment = new DefaultOverlayable(searcher);
            JLabel lblOverlay = new JLabel(overlay);
            lblOverlay.setForeground(Color.LIGHT_GRAY);
            lblOverlay.setFont(new Font("SansSerif", Font.PLAIN, 18));
            ovrComment.addOverlayComponent(lblOverlay, DefaultOverlayable.CENTER);

            searcher.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    //                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    //                    enterPressed(e);
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                        cancel();
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
                    createRecipeIfNecessary((JTextField) e.getSource());
                }
            });

            searcher.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    caretListener(e);
                }
            });

//            searcher.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent ae) {
//                    searcherRecipeAction(ae);
//                }
//            });

            btnMenu = new JButton();
            btnMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopupMenu jMenu = new JidePopupMenu();
                    JMenuItem miOn = new JMenuItem(Const.icon24ledGreenOn);
                    JMenuItem miOff = new JMenuItem(Const.icon24ledGreenOff);
                    jMenu.add(miOn);
                    jMenu.add(miOff);


                    jMenu.show(btnMenu, 0, btnMenu.getPreferredSize().height);
                }
            });
            menuLine1.add(btnMenuStarter);
            initPhase = false;
        }


        void setAccepted() {
            btnMenu.setIcon(Const.icon24ledGreenOn);
            searcher.setEditable(false);
        }

        void setEmpty() {
            btnMenu.setIcon(Const.icon24ledGreenOff);
            searcher.setText(null);
            searcher.setEditable(true);
            recipe = null;
        }

        void setRecipe(Recipes recipe) {
            this.recipe = recipe;
            searcher.setText(recipe == null ? "" : recipe.getTitle());
            searcher.setToolTipText(recipe == null ? "" : recipe.getText());
        }

        void cancel() {
            searcher.setText(recipe == null ? "" : recipe.getTitle());
        }

        void caretListener(CaretEvent ce) {
            private void searcherRecipeAction ( final ActionEvent ae){
                //        if (!reactToCaret) return;
                //        if (e.getDot() == 0) return;
                if (!reactToTextChange) return;
                if (!((JComponent) ae.getSource()).hasFocus()) return;

                //        if (popupR != null && currentFocusID != focusid) {
                //            popupR.hidePopup();
                //            popupR = null;
                //        }
                //        currentFocusID = focusid;

                if (popupR == null) {

                    popupR = new JidePopup();
                    popupR.setOwner((JComponent) ae.getSource());

                    Main.debug(ae.getSource());

                    popupR.setMovable(false);


                    final JList<Recipes> list = new JList(dlmR);
                    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    list.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                setRecipe(ae.getSource(), list.getSelectedValue());
                                ((JXSearchField) ae.getSource()).setText(list.getSelectedValue().getTitle());
                                changeAction.execute(menu);
                                popupR.hidePopup();
                            }
                            super.mouseClicked(e);
                        }
                    });


                    popupR.setContentPane(new JScrollPane(list));
                    popupR.removeExcludedComponent((JXSearchField) ae.getSource());

                    GUITools.showPopup(popupR, SwingConstants.SOUTH);
                }

                searchRecipes(((JXSearchField) ae.getSource()).getText().trim());

                if (dlmR.isEmpty() && popupR.isShowing()) {
                    popupR.hidePopup();

                }

                if (!dlmR.isEmpty() && popupR != null && !popupR.isShowing()) {
                    popupR.setOwner((JComponent) ae.getSource());
                    GUITools.showPopup(popupR, SwingConstants.SOUTH);
                }


            }
        }

    }


}
