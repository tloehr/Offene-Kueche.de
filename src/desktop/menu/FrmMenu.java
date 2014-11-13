/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import Main.Main;
import entity.*;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class FrmMenu extends JInternalFrame {
    //    LocalDate week;
    ArrayList<Menuweekall> listAll;

    JPanel pnlMain;
    private HashMap<LocalDate, String> holidays;

    public FrmMenu() {
        initComponents();
        initFrame();
        pack();
    }

    private void addMenu() {
        Menuweek myMenuweek = null;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            myMenuweek = em.merge(new Menuweek((Menuweekall) cmbWeeks.getSelectedItem(), RecipeFeatureTools.getAll().get(0)));
            em.getTransaction().commit();
        } catch (Exception exc) {
            em.getTransaction().rollback();
            Main.fatal(exc.getMessage());
        } finally {
            em.close();
            //                    notifyCaller();
        }

        ((Menuweekall) cmbWeeks.getSelectedItem()).getMenuweeks().add(myMenuweek);
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

            EntityManager em = Main.getEMF().createEntityManager();
            try {
                em.getTransaction().begin();
                Menuweekall m = em.merge(new Menuweekall(new LocalDate().dayOfWeek().withMinimumValue().toDate(), RecipeFeatureTools.getAll().get(0)));
                listAll.add(m);
                em.getTransaction().commit();
            } catch (Exception exc) {
                Main.debug(exc.getMessage());
                em.getTransaction().rollback();
                Main.fatal(exc.getMessage());
            } finally {
                em.close();
            }

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

        int year = new LocalDate(((Menuweekall) cmbWeeks.getSelectedItem()).getWeek()).getYear();
        holidays = Tools.getHolidays(year, year);
        createThePanels((Menuweekall) cmbWeeks.getSelectedItem());

    }

    private void createThePanels(final Menuweekall menuweekall) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pnlMain.removeAll();
                for (final Menuweek menuweek : menuweekall.getMenuweeks()) {
                    pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o == null) return;

                            if (o instanceof Menuweek2Menu) {
                                Menuweek2Menu myMenuweek2Menu = (Menuweek2Menu) o;
//                                int weekday = new LocalDate(myMenu.getDate()).getDayOfWeek() - 1;
                                for (final Menuweek m1 : menuweekall.getMenuweeks()) {
                                    ArrayList<Integer> foundIndices = new ArrayList<Integer>();
                                    for (int index = 0; index < menuweek.getMenuweek2menus().size(); index++) {
                                        if (menuweek.getMenuweek2menus().get(index).getMenu().equals(myMenuweek2Menu.getMenu())) {
                                            menuweek.getMenuweek2menus().set(index, myMenuweek2Menu);
                                        }
                                    }
                                }
                                createThePanels(menuweekall);
                            } else if (o instanceof Menuweek) {
                                //Menuweek myMenuweek = (Menuweek) o;
                                return;
                            } else {
                                return;
                            }


                        }
                    }, holidays));
                }
                revalidate();
                repaint();
            }
        });
    }

    private void cmbWeeksItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;

        int year = new LocalDate(((Menuweekall) cmbWeeks.getSelectedItem()).getWeek()).getYear();
        holidays = Tools.getHolidays(year, year);

        createThePanels((Menuweekall) cmbWeeks.getSelectedItem());

    }

    private void btnAddWeekmenuActionPerformed(ActionEvent e) {
        addMenu();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        cmbWeeks = new JComboBox<Menuweekall>();
        btnAddWeekmenu = new JButton();

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

            //---- btnAddWeekmenu ----
            btnAddWeekmenu.setText(null);
            btnAddWeekmenu.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
            btnAddWeekmenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddWeekmenuActionPerformed(e);
                }
            });
            panel1.add(btnAddWeekmenu);
        }
        contentPane.add(panel1, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JComboBox<Menuweekall> cmbWeeks;
    private JButton btnAddWeekmenu;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
