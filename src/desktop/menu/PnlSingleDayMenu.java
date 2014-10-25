package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import entity.Menu;
import entity.Recipes;
import org.joda.time.LocalDate;
import tools.Const;
import tools.GUITools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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

    private Menu menu;
    private LocalDate date;
    private JTextField searcher;
    private JidePopup popup;
    private DefaultListModel<Recipes> dlm;
    private boolean reactToCaret;

    public Menu getMenu() {
        return menu;
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

    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel topLine = new JPanel();
        topLine.setLayout(new BoxLayout(topLine, BoxLayout.LINE_AXIS));

        searcher = new JTextField();
        searcher.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                searcherCaretUpdate(e);
            }
        });
        searcher.setFont(new Font("SansSerif", Font.PLAIN, 18));

        topLine.add(searcher);


        JButton buttonDelete = new JButton("X");
        buttonDelete.setIcon(Const.icon24remove);
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searcher.setText(null);
                menu.setRecipe(null);
            }
        });
        topLine.add(buttonDelete);

        add(topLine);
        add(new JLabel(menu.getRecipe() == null ? "--" : menu.getRecipe().getTitle()));
    }


    private void searcherCaretUpdate(CaretEvent e) {
        if (!reactToCaret) return;
        if (e.getDot() == 0) return;

        if (popup == null) {

            popup = new JidePopup();
            popup.setMovable(false);


            final JList<Recipes> list = new JList(dlm);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        menu.setRecipe(list.getSelectedValue());
                        reactToCaret = false;
                        searcher.setText(menu.getRecipe().getTitle());
                        reactToCaret = true;
                        popup.hidePopup();
                    }
                    super.mouseClicked(e);
                }
            });

            popup.setContentPane(new JScrollPane(list));
            popup.removeExcludedComponent(searcher);
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

        searchRecipes(searcher.getText().trim());


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
