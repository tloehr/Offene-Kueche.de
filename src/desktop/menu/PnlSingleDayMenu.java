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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
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
    private boolean reactToCaret;

    public Menu getMenu() {
        return menu;
    }


    public void setChangeAction(Closure changeAction) {
        this.changeAction = changeAction;
    }

    public PnlSingleDayMenu(Menu menu, LocalDate date) {
        super();

        reactToCaret = true;

        if (menu == null) {
            menu = new Menu(date.toDate());
        }

        this.menu = menu;

        this.date = menu == null ? date : new LocalDate(menu.getDate());


        dlm = new DefaultListModel();
        initPanel();
    }

    public LocalDate getDate() {
        return date;
    }

    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel topLine = new JPanel();
        topLine.setLayout(new GridLayout(2, 3, 3, 3));

//        final JButton buttonDelete = new JButton(Const.icon24remove);
//        buttonDelete.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                searcher.setText(null);
//                menu.setRecipe(null);
//            }
//        });

        searcherStarter = new JXSearchField("Vorspeise");
        searcherStarter.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherStarter.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherStarter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        topLine.add(searcherStarter);

        searcherMain = new JXSearchField("Hauptgang");
        searcherMain.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherMain.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        topLine.add(searcherMain);

        searcherSauce = new JXSearchField("Sauce");
        searcherSauce.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSauce.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSauce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        topLine.add(searcherSauce);

        searcherSideveggie = new JXSearchField("Gemüse/Salat");
        searcherSideveggie.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSideveggie.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSideveggie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        topLine.add(searcherSideveggie);

        searcherSidedish = new JXSearchField("Kartoffeln/Reis/Nudeln");
        searcherSidedish.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherSidedish.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherSidedish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        topLine.add(searcherSidedish);

        searcherDessert = new JXSearchField("Dessert");
        searcherDessert.setSearchMode(JXSearchField.SearchMode.INSTANT);
        searcherDessert.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searcherDessert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searcherAction(ae);
            }
        });
        topLine.add(searcherDessert);

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
//
        add(topLine);
//        add(new JLabel(menu.getRecipe() == null ? "--" : menu.getRecipe().getTitle()));
    }

    private void searcherFocusLostListener(FocusEvent e) {
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

        if (popup == null) {

            popup = new JidePopup();
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
            popup.setOwner(this);
            popup.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    popup = null;
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {

                }
            });
            GUITools.showPopup(popup, SwingConstants.SOUTH);
        }

        searchRecipes(((JXSearchField) ae.getSource()).getText().trim());


    }


    private void setMenu(Object source, Recipes recipes) {
        if (source.equals(searcherStarter)) {
            menu.setStarter(recipes);
        } else if (source.equals(searcherMain)) {
            menu.setMaincourse(recipes);
        } else if (source.equals(searcherSauce)) {
            menu.setSauce(recipes);
        } else if (source.equals(searcherSideveggie)) {
            menu.setSideveggie(recipes);
        } else if (source.equals(searcherSidedish)) {
            menu.setSidedish(recipes);
        } else if (source.equals(searcherDessert)) {
            menu.setDessert(recipes);
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
