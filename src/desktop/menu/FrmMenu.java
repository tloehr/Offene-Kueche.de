/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import Main.Main;
import entity.Menuweek;
import entity.Menuweekall;
import entity.MenuweekallTools;
import entity.Recipefeature;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class FrmMenu extends JInternalFrame {
//    LocalDate week;
    ArrayList<Menuweekall> listAll;
    //    JScrollPane scrlMain;
    JPanel pnlMain;

    public FrmMenu() {

        initComponents();
        initFrame();
        pack();
    }

//    public void addMenu(final Menuweek menuweek) {
//        menus.add(menuweek);
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        Main.debug(o);
//                    }
//                }));
//                revalidate();
//                repaint();
//            }
//        });
//    }
//
//    public void deleteMenu(final Menuweek menuweek, final PnlMenuWeek pnlMenuWeek) {
//        menus.remove(menuweek);
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                pnlMain.remove(pnlMenuWeek);
//                revalidate();
//                repaint();
//            }
//        });
//    }

    private void initFrame() {

        listAll = MenuweekallTools.getAll();

        if (listAll.isEmpty()){
            listAll.add(new Menuweekall(new LocalDate().dayOfWeek().withMinimumValue().toDate()));
        }

        cmbWeeks.setRenderer(new );

        jdcWeek.setDate(week.toDate());
        jdcWeek.setFont(new Font("SansSerif", Font.PLAIN, 18));

        menuweekall = MenuweekallTools.get(week);
        if (menuweekall == null) {
            menuweekall = new Menuweekall(week.toDate());
        }

        if (pnlMain == null) {
            pnlMain = new JPanel(new GridLayout(1, 0, 10, 0));
            add(new JScrollPane(pnlMain), BorderLayout.CENTER);
        } else {
            pnlMain.removeAll();
        }


        if (menuweekall.getMenuweeks().isEmpty()) {
            EntityManager em = Main.getEMF().createEntityManager();
            Recipefeature featureNormal = em.find(Recipefeature.class, 4l);
            em.close();

            menuweekall.getMenuweeks().add(new Menuweek(menuweekall, featureNormal));
        }


        for (Menuweek menuweek : menuweekall.getMenuweeks()) {
            pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
                @Override
                public void execute(Object o) {
                    Main.debug(o);
                }
            }));
        }

    }

    private void jdcWeekPropertyChange(PropertyChangeEvent e) {
        week = new LocalDate(e.getNewValue()).dayOfWeek().withMinimumValue();
        initFrame();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        cmbWeeks = new JComboBox<LocalDate>();
        btnAddWeek = new JButton();

        //======== this ========
        setVisible(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- cmbWeeks ----
            cmbWeeks.setFont(new Font("SansSerif", Font.PLAIN, 18));
            panel1.add(cmbWeeks);

            //---- btnAddWeek ----
            btnAddWeek.setText(null);
            btnAddWeek.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
            panel1.add(btnAddWeek);
        }
        contentPane.add(panel1, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JComboBox<LocalDate> cmbWeeks;
    private JButton btnAddWeek;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
