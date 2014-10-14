package desktop.menu;

import entity.Menu;
import org.joda.time.LocalDate;

import javax.swing.*;

/**
 * Created by tloehr on 14.10.14.
 */
public class PnlSingleDayMenu extends JPanel {

    private Menu menu;
    private LocalDate date;

    public PnlSingleDayMenu(Menu menu, LocalDate date) {
        super();
        this.menu = menu;
        this.date = menu == null ? date : new LocalDate(menu.getDate());

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel topLine = new JPanel();
        topLine.setLayout(new BoxLayout(topLine, BoxLayout.LINE_AXIS));
        topLine.add(new JTextField());
        topLine.add(new JButton("X"));

        add(topLine);
        add(new JLabel(menu == null ? "--" : menu.getRecipe().getTitle()));
    }
}
