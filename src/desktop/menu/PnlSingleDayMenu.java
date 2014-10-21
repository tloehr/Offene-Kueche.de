package desktop.menu;

import Main.Main;
import com.jidesoft.popup.JidePopup;
import entity.Menu;
import org.joda.time.LocalDate;
import tools.GUITools;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

/**
 * Created by tloehr on 14.10.14.
 */
public class PnlSingleDayMenu extends JPanel {

    private Menu menu;
    private LocalDate date;
    private JTextField searcher;
    private JidePopup popup;
    private DefaultListModel dlm;

    public PnlSingleDayMenu(Menu menu, LocalDate date) {
        super();
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

        topLine.add(searcher);
        topLine.add(new JButton("X"));

        add(topLine);
        add(new JLabel(menu == null ? "--" : menu.getRecipe().getTitle()));
    }


    private void searcherCaretUpdate(CaretEvent e){

        if (e.getDot() == 0) return;

        if (popup == null){

            popup = new JidePopup();
            popup.setMovable(false);

            dlm.clear();
            dlm.addElement("eins");
            dlm.addElement("zwei");
            dlm.addElement("drei");
            dlm.addElement(new Date());

            final JList list = new JList(dlm);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2){
                        Main.debug(list.getSelectedValue());
                        popup.hidePopup();
                    }
                    super.mouseClicked(e);
                }
            });

            popup.setContentPane(new JScrollPane(list));
            popup.removeExcludedComponent(searcher);
//            popup.setDefaultFocusComponent(txt);
            popup.setOwner(this);
            popup.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    popup = null;
                    Main.debug("popupMenuWillBecomeInvisible");
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {

                }
            });
            GUITools.showPopup(popup, SwingConstants.SOUTH);
        }

        dlm.addElement(new Date());


    }

}
