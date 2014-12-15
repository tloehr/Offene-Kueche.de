/*
 * Created by JFormDesigner on Wed Oct 08 15:05:11 CEST 2014
 */

package desktop.menu;

import Main.Main;
import com.jidesoft.swing.JidePopupMenu;
import com.toedter.calendar.JCalendar;
import entity.*;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;
import printer.Printers;
import tools.Const;
import tools.GUITools;
import tools.PopupPanel;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class FrmMenu extends JFrame {
    //    LocalDate week;
    ArrayList<Menuweekall> listAll;

    JPanel pnlMain;
    private HashMap<LocalDate, String> holidays;

    public FrmMenu() {
        initComponents();
        initFrame();
        pack();
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
                em.getTransaction().commit();
                listAll.add(m);
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
                    pnlMain.add(new PnlMenuWeek(menuweek, new PSDChangeListener() {
                        @Override
                        public void menuEdited(PSDChangeEvent psdce) {
                            for (Component comp : pnlMain.getComponents()) {
                                if (comp instanceof PnlMenuWeek) {
                                    ((PnlMenuWeek) comp).getTopDownListener().menuEdited(psdce);
                                }
                            }
                        }

                        @Override
                        public void menuReplaced(PSDChangeEvent psdce) {
                            EntityManager em = Main.getEMF().createEntityManager();
                            Menuweek myMenuweek = em.merge(psdce.getMenuweek());
                            Menuweekall myMenuweekall = myMenuweek.getMenuweekall();
                            em.refresh(myMenuweekall);
                            em.close();

                            int index = cmbWeeks.getSelectedIndex();
                            listAll.set(index, myMenuweekall);
                            cmbWeeks.setModel(Tools.newComboboxModel(listAll));
                            cmbWeeks.setSelectedIndex(index);

                            createThePanels(myMenuweekall);

                        }

                        @Override
                        public void stockListChanged(PSDChangeEvent psdce) {
                            for (Component comp : pnlMain.getComponents()) {
                                if (comp instanceof PnlMenuWeek) {
                                    ((PnlMenuWeek) comp).getTopDownListener().menuEdited(psdce);
                                }
                            }
                        }

                        @Override
                        public void customerListChanged(PSDChangeEvent psdce) {

                        }

                        @Override
                        public void menufeatureChanged(PSDChangeEvent psdce) {

                        }

                        @Override
                        public void menuweekAdded(PSDChangeEvent psdce) {
//                            EntityManager em = Main.getEMF().createEntityManager();
//                            Menuweek myMenuweek = em.merge(psdce.getMenuweek());
//                            Menuweekall myMenuweekall = myMenuweek.getMenuweekall();
//                            em.refresh(myMenuweekall);
//                            em.close();


                            int index = cmbWeeks.getSelectedIndex();
                            listAll.set(index, psdce.getMenuweek().getMenuweekall());
                            cmbWeeks.setModel(Tools.newComboboxModel(listAll));
                            cmbWeeks.setSelectedIndex(index);

                            createThePanels(psdce.getMenuweek().getMenuweekall());
                        }

                        @Override
                        public void menuweekDeleted(PSDChangeEvent psdce) {

                        }
                    }, holidays));


//                    pnlMain.add(new PnlMenuWeek(menuweek, new Closure() {
//                                           @Override
//                                           public void execute(Object o) {
//                                               if (o == null) return;
//
//                                               if (o instanceof Menuweek2Menu) {
//                                                   EntityManager em = Main.getEMF().createEntityManager();
//                                                   Menuweek2Menu myMenuweek2Menu = em.merge((Menuweek2Menu) o);
//                                                   Menuweekall myMenuweekall = em.merge(myMenuweek2Menu.getMenuweek().getMenuweekall());
//                                                   em.refresh(myMenuweekall);
//                                                   em.close();
//
//                                                   int index = cmbWeeks.getSelectedIndex();
//                                                   listAll.set(index, myMenuweekall);
//                                                   cmbWeeks.setModel(Tools.newComboboxModel(listAll));
//                                                   cmbWeeks.setSelectedIndex(index);
//
//                                                   createThePanels(myMenuweekall);
//                                               } else if (o instanceof Menuweek) {
//
//                                                   EntityManager em = Main.getEMF().createEntityManager();
//
//                                                   // Menuweekall myMenuweekall = em.find(Menuweekall.class, ((Menuweek) o).getMenuweekall().getId());
//
//                                                   Menuweek myMenuweek = em.merge((Menuweek) o);
//                                                   Menuweekall myMenuweekall = myMenuweek.getMenuweekall();
//                                                   em.refresh(myMenuweekall);
//                                                   em.close();
//
//                                                   int index = cmbWeeks.getSelectedIndex();
//                                                   listAll.set(index, myMenuweekall);
//                                                   cmbWeeks.setModel(Tools.newComboboxModel(listAll));
//                                                   cmbWeeks.setSelectedIndex(index);
//
//                                                   createThePanels(myMenuweekall);
//
//                                               } else if (o instanceof Menuweekall) {
//
//                                                   EntityManager em = Main.getEMF().createEntityManager();
//
//                                                   Menuweekall myMenuweekall = em.merge((Menuweekall) o);
//                                                   em.refresh(myMenuweekall);
//                                                   em.close();
//
//                                                   int index = cmbWeeks.getSelectedIndex();
//                                                   listAll.set(index, myMenuweekall);
//                                                   cmbWeeks.setModel(Tools.newComboboxModel(listAll));
//                                                   cmbWeeks.setSelectedIndex(index);
//
//                                                   createThePanels(myMenuweekall);
//
//                                               } else if (o instanceof PSDChangeEvent) {
//
//                                                   for (Component comp : pnlMain.getComponents()) {
//                                                       if (comp instanceof PnlMenuWeek) {
//                                                           ((PnlMenuWeek) comp).notifyMeAbout((PSDChangeEvent) o);
//                                                       }
//                                                   }
//
//                                               } else {
//                                                   return;
//                                               }
//
//
//                                           }
//                                       }, holidays));

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

    private boolean createNewMenuweekall(Date nextWeek) {
        boolean success = false;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            // check if there is already a menuweekall for that week created in the meantime
            Query conflictQuery = em.createQuery("SELECT c FROM Menuweekall c WHERE c.week = :week");
            conflictQuery.setParameter("week", nextWeek);
            ArrayList<Menuweekall> listConflict = new ArrayList<Menuweekall>(conflictQuery.getResultList());

            if (!listConflict.isEmpty()) {
                throw new OptimisticLockException("week for 'Menuweekall' already taken");
            }

            Menuweekall m = em.merge(new Menuweekall(nextWeek, RecipeFeatureTools.getAll().get(0)));
            em.getTransaction().commit();
            success = true;
            listAll.add(m);
            Collections.sort(listAll);
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception exc) {
            Main.debug(exc.getMessage());
            em.getTransaction().rollback();
            Main.fatal(exc.getMessage());
        } finally {
            em.close();
        }
        return success;
    }

    private void btnAddWeekmenuAllActionPerformed(ActionEvent e) {
        final Date nextDate;
        if (!listAll.isEmpty()) {
            nextDate = new LocalDate(listAll.get(0).getWeek()).plusWeeks(1).dayOfWeek().withMinimumValue().toDate();
        } else {
            nextDate = new LocalDate().dayOfWeek().withMinimumValue().toDate();
        }


        JidePopupMenu jMenu = new JidePopupMenu();
        JMenuItem miNext = new JMenuItem("Nächsten Plan erstellen: " + DateFormat.getDateInstance().format(nextDate));
        miNext.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JMenuItem miSelect = new JMenuItem("Woche wählen und Plan erstellen");
        miSelect.setFont(new Font("SansSerif", Font.PLAIN, 18));
        jMenu.add(miNext);
        jMenu.add(miSelect);

        miNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!createNewMenuweekall(nextDate)) {
                    listAll.clear();
                }
                initFrame();
            }
        });

        miSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final JCalendar jdc = new JCalendar();
                PopupPanel pnl = new PopupPanel() {
                    @Override
                    public Object getResult() {
                        return jdc.getDate();
                    }

                    @Override
                    public void setStartFocus() {
                        jdc.requestFocus();
                    }

                    @Override
                    public boolean isSaveOK() {
                        return true;
                    }
                };
                pnl.add(jdc);

                GUITools.showPopup(GUITools.createPanelPopup(pnl, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (!createNewMenuweekall(new LocalDate(jdc.getDate()).dayOfWeek().withMinimumValue().toDate())) {
                            listAll.clear();
                        }
                        initFrame();
                    }
                }, btnAddWeekmenuAll), SwingUtilities.SOUTH_WEST);
            }
        });


        jMenu.show(btnAddWeekmenuAll, 0, btnAddWeekmenuAll.getPreferredSize().height);
    }

    private void btnPrintActionPerformed(ActionEvent e) {

        JidePopupMenu jMenu = new JidePopupMenu();
        JMenuItem miPrintAllMenus = new JMenuItem("Alle Speisepläne drucken");
        miPrintAllMenus.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JMenuItem miIngTypesAndStocks = new JMenuItem("Zutaten und Vorratslisten drucken", Const.icon24ledGreenOff);
        miIngTypesAndStocks.setFont(new Font("SansSerif", Font.PLAIN, 18));

        jMenu.add(miPrintAllMenus);
        jMenu.add(miIngTypesAndStocks);

        miPrintAllMenus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String html = "";

                for (Menuweek menuweek : ((Menuweekall) cmbWeeks.getSelectedItem()).getMenuweeks()) {

                    EntityManager em = Main.getEMF().createEntityManager();
                    Menuweek mergedMenuweek = em.merge(menuweek);
                    em.refresh(mergedMenuweek);
                    em.close();

                    html += MenuweekTools.getAsHTML(mergedMenuweek);
                    html += Tools.isLastElement(mergedMenuweek, ((Menuweekall) cmbWeeks.getSelectedItem()).getMenuweeks()) ? "" : "<p style=\"page-break-before:always\"\\>";
                }
                Printers.print(Main.getDesktop(), html, true);
            }
        });


        miIngTypesAndStocks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Printers.print(Main.getDesktop(), MenuweekallTools.getIngTypesAndStocksAsHTML(((Menuweekall) cmbWeeks.getSelectedItem())), true);
            }
        });


        jMenu.show(btnPrint, 0, btnPrint.getPreferredSize().height);


    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        cmbWeeks = new JComboBox<Menuweekall>();
        btnAddWeekmenuAll = new JButton();
        btnPrint = new JButton();

        //======== this ========
        setVisible(true);
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

            //---- btnAddWeekmenuAll ----
            btnAddWeekmenuAll.setText(null);
            btnAddWeekmenuAll.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
            btnAddWeekmenuAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddWeekmenuAllActionPerformed(e);
                }
            });
            panel1.add(btnAddWeekmenuAll);

            //---- btnPrint ----
            btnPrint.setText(null);
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/printer.png")));
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            panel1.add(btnPrint);
        }
        contentPane.add(panel1, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JComboBox<Menuweekall> cmbWeeks;
    private JButton btnAddWeekmenuAll;
    private JButton btnPrint;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
