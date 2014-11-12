/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import Main.Main;
import entity.Menuweek;
import entity.Menuweekall;
import entity.MenuweekallTools;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;
import tools.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

    public void addMenu(final Menuweek menuweek) {

        ((Menuweekall) cmbWeeks.getSelectedItem()).getMenuweeks().add(menuweek);

        createThePanels((Menuweekall) cmbWeeks.getSelectedItem());
    }
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

        if (listAll.isEmpty()) {
            listAll.add(new Menuweekall(new LocalDate().dayOfWeek().withMinimumValue().toDate()));
        }

        cmbWeeks.setRenderer(MenuweekallTools.getListCellRenderer());
        cmbWeeks.setModel(Tools.newComboboxModel(listAll));


        if (pnlMain == null) {
            pnlMain = new JPanel(new GridLayout(1, 0, 10, 0));
            add(new JScrollPane(pnlMain), BorderLayout.CENTER);
        } else {
            pnlMain.removeAll();
        }

//
//        if (menuweekall.getMenuweeks().isEmpty()) {
//            EntityManager em = Main.getEMF().createEntityManager();
//            Recipefeature featureNormal = em.find(Recipefeature.class, 4l);
//            em.close();
//
//            menuweekall.getMenuweeks().add(new Menuweek(menuweekall, featureNormal));
//        }
        createThePanels((Menuweekall) cmbWeeks.getSelectedItem());

    }

    private void createThePanels(final Menuweekall menuweekall) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pnlMain.removeAll();
                for (Menuweek menuweek : menuweekall.getMenuweeks()) {
                    pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
                        @Override
                        public void execute(Object o) {
                            Main.debug(o);
                        }
                    }));
                }
                revalidate();
                repaint();
            }
        });
    }

    private void cmbWeeksItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;

        createThePanels((Menuweekall) cmbWeeks.getSelectedItem());

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        cmbWeeks = new JComboBox<Menuweekall>();
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
            cmbWeeks.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbWeeksItemStateChanged(e);
                }
            });
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
    private JComboBox<Menuweekall> cmbWeeks;
    private JButton btnAddWeek;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
