/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import Main.Main;
import com.toedter.calendar.JDateChooser;
import entity.Menuweek;
import entity.MenuweekTools;
import entity.Recipefeature;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class FrmMenu extends JInternalFrame {
    LocalDate week;
    ArrayList<Menuweek> menus;
//    JScrollPane scrlMain;
    JPanel pnlMain;

    public FrmMenu() {
        week = new LocalDate().dayOfWeek().withMinimumValue();
        initComponents();
        initFrame();
    }

    public void addMenu(final Menuweek menuweek) {
        menus.add(menuweek);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
                    @Override
                    public void execute(Object o) {
                        Main.debug(o);
                    }
                }));
                revalidate();
                repaint();
            }
        });
    }

    public void deleteMenu(final Menuweek menuweek, final PnlMenuWeek pnlMenuWeek) {
        menus.remove(menuweek);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pnlMain.remove(pnlMenuWeek);
                revalidate();
                repaint();
            }
        });
    }

    private void initFrame() {
        menus = MenuweekTools.getAll(week);
        pnlMain = new JPanel(new GridLayout(1,0,10,0));
        add(new JScrollPane(pnlMain), BorderLayout.CENTER);

        EntityManager em = Main.getEMF().createEntityManager();
        Recipefeature featureNormal = em.find(Recipefeature.class, 4l);
        em.close();

        pnlMain.removeAll();


        if (menus.isEmpty()) {
            pnlMain.add(new PnlMenuWeek(new Menuweek(week.toDate(), featureNormal), new Closure() {
                @Override
                public void execute(Object o) {
                    Main.debug(o);
                }
            }));
        } else {
            for (Menuweek menuweek : menus) {
                pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
                    @Override
                    public void execute(Object o) {
                        Main.debug(o);
                    }
                }));
            }
        }

        jdcWeek.setDate(week.toDate());
        jdcWeek.setFont(new Font("SansSerif", Font.PLAIN, 18));


    }

    private void jdcWeekPropertyChange(PropertyChangeEvent e) {
        week = new LocalDate(e.getNewValue()).dayOfWeek().withMinimumValue();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jdcWeek = new JDateChooser();

        //======== this ========
        setVisible(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //---- jdcWeek ----
        jdcWeek.setDateFormatString("'KW'w yyyy");
        jdcWeek.setFont(new Font("Dialog", Font.PLAIN, 20));
        jdcWeek.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcWeekPropertyChange(e);
            }
        });
        contentPane.add(jdcWeek, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JDateChooser jdcWeek;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
