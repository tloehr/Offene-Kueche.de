package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import entity.Menu;
import entity.Recipes;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.joda.time.LocalDate;
import tools.GUITools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
    private JXSearchField searcherStarter, searcherMain, searcherSauce, searcherSideveggie, searcherSidedish, searcherDessert;
    private JidePopup popup;
    private DefaultListModel<Recipes> dlm;
    private boolean reactToTextChange;

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
//
//        if (menu == null) {
//            menu = new Menu(date.toDate());
//        }

        this.menu = menu;

        this.date = new LocalDate(menu.getDate());


        dlm = new DefaultListModel();
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

        menuComplete.setLayout(new BoxLayout(menuComplete, BoxLayout.PAGE_AXIS));
        menuLine1.setLayout(new BoxLayout(menuLine1, BoxLayout.LINE_AXIS));
        menuLine2.setLayout(new BoxLayout(menuLine2, BoxLayout.LINE_AXIS));


//        final JButton buttonDelete = new JButton(Const.icon24remove);
//        buttonDelete.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                searcher.setText(null);
//                menu.setRecipe(null);
//            }
//        });

        reactToTextChange = false;

        searcherStarter = new JXSearchField("Vorspeise");
//        searcherStarter.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                super.keyPressed(e);
//                actionListener(e);
//            }
//        });
        searcherStarter.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherStarter.setInstantSearchDelay(0);
        searcherStarter.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherStarter.setText(menu.getStarter() == null ? "" : menu.getStarter().getTitle());
        searcherStarter.setToolTipText(menu.getStarter() == null ? "" : menu.getStarter().getText());
        searcherStarter.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelListener(ae);
            }
        });
        searcherStarter.setFindAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                actionListener(ae);
            }
        });
        searcherStarter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
//        searcherStarter.setFindPopupMenu(new JPopupMenu("test"));

        menuLine1.add(searcherStarter);

        searcherMain = new JXSearchField("Hauptgang");
        searcherMain.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherMain.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherMain.setText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getTitle());
        searcherMain.setToolTipText(menu.getMaincourse() == null ? "" : menu.getMaincourse().getText());
        searcherMain.setCancelAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        cancelListener(ae);
                    }
                });
        searcherMain.setFindAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        actionListener(ae);
                    }
                });
        searcherMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        menuLine1.add(searcherMain);

        searcherSauce = new JXSearchField("Sauce");
        searcherSauce.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSauce.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSauce.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherSauce.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherSauce.setCancelAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        cancelListener(ae);
                    }
                });
        searcherSauce.setFindAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        actionListener(ae);
                    }
                });
        searcherSauce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        menuLine1.add(searcherSauce);

        searcherSideveggie = new JXSearchField("Gemüse/Salat");
        searcherSideveggie.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSideveggie.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSideveggie.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherSideveggie.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherSideveggie.setCancelAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        cancelListener(ae);
                    }
                });
        searcherSideveggie.setFindAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        actionListener(ae);
                    }
                });
        searcherSideveggie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        menuLine2.add(searcherSideveggie);

        searcherSidedish = new JXSearchField("Kartoffeln/Reis/Nudeln");
        searcherSidedish.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSidedish.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSidedish.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherSidedish.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherSidedish.setCancelAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        cancelListener(ae);
                    }
                });
        searcherSidedish.setFindAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        actionListener(ae);
                    }
                });
        searcherSidedish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        menuLine2.add(searcherSidedish);

        searcherDessert = new JXSearchField("Dessert");
        searcherDessert.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherDessert.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherDessert.setText(menu.getSauce() == null ? "" : menu.getSauce().getTitle());
        searcherDessert.setToolTipText(menu.getSauce() == null ? "" : menu.getSauce().getText());
        searcherDessert.setCancelAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        cancelListener(ae);
                    }
                });
        searcherDessert.setFindAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        actionListener(ae);
                    }
                });
        searcherDessert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        menuLine2.add(searcherDessert);

//        searcherStarter.addCaretListener(new CaretListener() {
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
        add(menuComplete);
//        add(new JLabel(menu.getRecipe() == null ? "--" : menu.getRecipe().getTitle()));
    }

    private JButton getStockButton()


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
        } else {
            Main.fatal("schade im grunde");
        }

        reactToTextChange = true;

        if (popup != null) {
            popup.hidePopup();
            popup = null;
        }
    }

    private void actionListener(ActionEvent e) {
//        if (e.getKeyCode() != KeyEvent.VK_ENTER) return;

        if (!reactToTextChange) return;

        if (!((JTextField) e.getSource()).getText().trim().isEmpty() && dlm.isEmpty()) {
            if (JOptionPane.showInternalConfirmDialog(Main.getDesktop().getMenuweek(), "Das Rezept kenne ich noch gar nicht.\n" +
                            "Achte darauf, dass alles richtig geschrieben ist\n\n" +
                            "Soll ich das mit aufnehmen ?", "Neues Rezept",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)
                    == JOptionPane.YES_OPTION) {

//                   menu.setRecipe(new Recipes(searcher.getText().trim()));

                EntityManager em = Main.getEMF().createEntityManager();
                try {
                    em.getTransaction().begin();
                    Recipes newRecipe = em.merge(new Recipes(((JTextField) e.getSource()).getText().trim()));
                    em.getTransaction().commit();

                    setMenu(e.getSource(), newRecipe);
                    changeAction.execute(menu);
                } catch (Exception ex) {
                    Main.fatal(ex);
                } finally {
                    em.close();
                }
            }
        }
    }


    private void searcherAction(final ActionEvent ae) {
//        if (!reactToCaret) return;
//        if (e.getDot() == 0) return;
        if (!reactToTextChange) return;

        if (popup != null && (popup.getOwner() == null || !popup.getOwner().equals(ae.getSource()))) {
            popup.hidePopup();
            popup = null;
        }

        if (popup == null) {

            popup = new JidePopup();
            popup.setOwner((JComponent) ae.getSource());

            Main.debug(ae.getSource());

            popup.setMovable(false);


            final JList<Recipes> list = new JList(dlm);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setMenu(ae.getSource(), list.getSelectedValue());
                        ((JXSearchField) ae.getSource()).setText(list.getSelectedValue().getTitle());
                        changeAction.execute(menu);
                        popup.hidePopup();
                    }
                    super.mouseClicked(e);
                }
            });


            popup.setContentPane(new JScrollPane(list));
            popup.removeExcludedComponent((JXSearchField) ae.getSource());

            GUITools.showPopup(popup, SwingConstants.SOUTH);
        }

        searchRecipes(((JXSearchField) ae.getSource()).getText().trim());


    }


    private void setMenu(Object source, Recipes recipes) {
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
