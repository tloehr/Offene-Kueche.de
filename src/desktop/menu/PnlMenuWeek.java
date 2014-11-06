/*
 * Created by JFormDesigner on Tue Oct 14 15:21:24 CEST 2014
 */

package desktop.menu;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import tools.GUITools;
import tools.PnlAssign;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlMenuWeek extends JPanel {


    private Menuweek menuweek;
    private final Closure changeAction;
    private PnlSingleDayMenu mon, tue, wed, thu, fri, sat, sun;
    SimpleDateFormat sdf;
    JidePopup popup;

    private static final String format = "EEEE, d MMM yyyy";

    public PnlMenuWeek(Menuweek menuweek, Closure changeAction) {
        this.menuweek = menuweek;
        this.changeAction = changeAction;
        initComponents();
        initPanel();
    }


    private void initPanel() {

        cmbFeature.setModel(Tools.newComboboxModel(RecipeFeatureTools.getAll()));
        cmbFeature.setRenderer(RecipeFeatureTools.getListCellRenderer());
        cmbFeature.setSelectedItem(menuweek.getRecipefeature());


        sdf = new SimpleDateFormat(format);

        mon = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()));
        mon.setChangeAction(getChangeEvent4Daily(mon));

        tue = new PnlSingleDayMenu(menuweek.getTue(), new LocalDate(menuweek.getWeek()).plusDays(1));
        tue.setChangeAction(getChangeEvent4Daily(tue));

        wed = new PnlSingleDayMenu(menuweek.getWed(), new LocalDate(menuweek.getWeek()).plusDays(2));
        wed.setChangeAction(getChangeEvent4Daily(wed));

        thu = new PnlSingleDayMenu(menuweek.getThu(), new LocalDate(menuweek.getWeek()).plusDays(3));
        thu.setChangeAction(getChangeEvent4Daily(thu));

        fri = new PnlSingleDayMenu(menuweek.getFri(), new LocalDate(menuweek.getWeek()).plusDays(4));
        fri.setChangeAction(getChangeEvent4Daily(fri));

        sat = new PnlSingleDayMenu(menuweek.getSat(), new LocalDate(menuweek.getWeek()).plusDays(5));
        sat.setChangeAction(getChangeEvent4Daily(sat));

        sun = new PnlSingleDayMenu(menuweek.getSun(), new LocalDate(menuweek.getWeek()).plusDays(6));
        sun.setChangeAction(getChangeEvent4Daily(sun));

        lblMon.setText(sdf.format(mon.getMenu().getDate()));
        lblTue.setText(sdf.format(tue.getMenu().getDate()));
        lblWed.setText(sdf.format(wed.getMenu().getDate()));
        lblThu.setText(sdf.format(thu.getMenu().getDate()));
        lblFri.setText(sdf.format(fri.getMenu().getDate()));
        lblSat.setText(sdf.format(sat.getMenu().getDate()));
        lblSun.setText(sdf.format(sun.getMenu().getDate()));

        int y = 5;
        int h = 4;
        add(mon, CC.xy(1, y + (h * 0)));
        add(tue, CC.xy(1, y + (h * 1)));
        add(wed, CC.xy(1, y + (h * 2)));
        add(thu, CC.xy(1, y + (h * 3)));
        add(fri, CC.xy(1, y + (h * 4)));
        add(sat, CC.xy(1, y + (h * 5)));
        add(sun, CC.xy(1, y + (h * 6)));

        lstCustomers.setModel(Tools.newListModel(new ArrayList<Customer>(menuweek.getCustomers())));
        lstCustomers.setCellRenderer(CustomerTools.getListCellRenderer());
    }


    private Closure getChangeEvent4Daily(final PnlSingleDayMenu pnl) {
        return new Closure() {
            @Override
            public void execute(Object o) {
                if (o == null) return;


                EntityManager em = Main.getEMF().createEntityManager();
                try {
                    em.getTransaction().begin();

                    Menuweek myMenuweek = em.merge(menuweek);
                    em.lock(myMenuweek, LockModeType.OPTIMISTIC);

                    switch (pnl.getDate().getDayOfWeek()) {
                        case DateTimeConstants.MONDAY: {
                            myMenuweek.setMon(em.merge(pnl.getMenu()));
                            break;
                        }
                        case DateTimeConstants.TUESDAY: {
                            myMenuweek.setTue(em.merge(pnl.getMenu()));
                            break;
                        }
                        case DateTimeConstants.WEDNESDAY: {
                            myMenuweek.setWed(em.merge(pnl.getMenu()));
                            break;
                        }
                        case DateTimeConstants.THURSDAY: {
                            myMenuweek.setThu(em.merge(pnl.getMenu()));
                            break;
                        }
                        case DateTimeConstants.FRIDAY: {
                            myMenuweek.setFri(em.merge(pnl.getMenu()));
                            break;
                        }
                        case DateTimeConstants.SATURDAY: {
                            myMenuweek.setSat(em.merge(pnl.getMenu()));
                            break;
                        }
                        case DateTimeConstants.SUNDAY: {
                            myMenuweek.setSun(em.merge(pnl.getMenu()));
                            break;
                        }
                        default: {
                            Main.fatal(o.toString());
                        }
                    }

                    em.getTransaction().commit();

                    menuweek = myMenuweek;

                } catch (OptimisticLockException ole) {
                    Main.warn(ole);
                    em.getTransaction().rollback();
                } catch (Exception exc) {
                    em.getTransaction().rollback();
                    Main.fatal(exc);
                } finally {
                    em.close();
                    notifyCaller();
                }


            }
        };
    }


    private void btnSaveActionPerformed(ActionEvent e) {
//        EntityManager em = Main.getEMF().createEntityManager();
//        try {
//            em.getTransaction().begin();
//            Menuweek myMenuweek = em.merge(menuweek);
//            em.lock(myMenuweek, LockModeType.OPTIMISTIC);
//
//            entity.Menu monMenu = em.merge(mon.getMenu());
//            em.lock(monMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setMon(monMenu);
//
//            entity.Menu tueMenu = em.merge(tue.getMenu());
//            em.lock(tueMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setTue(tueMenu);
//
//            entity.Menu wedMenu = em.merge(wed.getMenu());
//            em.lock(wedMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setWed(wedMenu);
//
//            entity.Menu thuMenu = em.merge(thu.getMenu());
//            em.lock(thuMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setThu(thuMenu);
//
//            entity.Menu friMenu = em.merge(fri.getMenu());
//            em.lock(friMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setFri(friMenu);
//
//            entity.Menu satMenu = em.merge(sat.getMenu());
//            em.lock(satMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setSat(satMenu);
//
//            entity.Menu sunMenu = em.merge(sun.getMenu());
//            em.lock(sunMenu, LockModeType.OPTIMISTIC);
//            myMenuweek.setSun(sunMenu);
//
//            myMenuweek.setRecipefeature(em.merge((Recipefeature) cmbFeature.getSelectedItem()));
//
//            myMenuweek.getCustomers().clear();
//            myMenuweek.getCustomers().addAll(lstCustomers.getSelectedValuesList());
//
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            Main.fatal(ex);
//        } finally {
//            em.close();
//        }
    }

    private void btnAddCustomerActionPerformed(ActionEvent e) {
        if (popup != null && popup.isVisible()) {
            popup.hidePopup();
        }

        Tools.unregisterListeners(popup);
        popup = new JidePopup();

        final PnlAssign<Customer> pnlAssign = new PnlAssign<Customer>(new ArrayList<Customer>(menuweek.getCustomers()), CustomerTools.getAll(), CustomerTools.getListCellRenderer());

        popup.setMovable(false);
        popup.setTransient(true);
        popup.setOwner(lstCustomers);
        popup.getContentPane().add(pnlAssign);
        popup.setFocusable(true);
        popup.setDefaultFocusComponent(pnlAssign.getDefaultFocusComponent());

        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                if (pnlAssign.getAssigned() == null) return;
                if (CollectionUtils.isEqualCollection(pnlAssign.getAssigned(), menuweek.getCustomers())) return;

                EntityManager em = Main.getEMF().createEntityManager();
                try {
                    em.getTransaction().begin();

                    Menuweek myMenuweek = em.merge(menuweek);
                    em.lock(myMenuweek, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                    myMenuweek.getCustomers().clear();
                    for (Customer customer : pnlAssign.getAssigned()) {
                        myMenuweek.getCustomers().add(em.merge(customer));
                    }

                    em.getTransaction().commit();

                    menuweek = myMenuweek;
                    lstCustomers.setModel(Tools.newListModel(new ArrayList<Customer>(menuweek.getCustomers())));
                } catch (OptimisticLockException ole) {
                    Main.warn(ole);
                    em.getTransaction().rollback();
                } catch (Exception exc) {
                    em.getTransaction().rollback();
                    Main.fatal(e);
                } finally {
                    em.close();
                    notifyCaller();
                    popup = null;
                }


            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        GUITools.showPopup(popup, SwingUtilities.CENTER);

//        SwingUtilities.convertPointToScreen(btnAddCustomer, tblProdukt);
//
//        popup.showPopup(p.x - (pnlAssign.getPreferredSize().width / 2), p.y + 10);
    }

    private void cmbFeatureItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;


        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();

            Menuweek myMenuweek = em.merge(menuweek);
            em.lock(myMenuweek, LockModeType.OPTIMISTIC);

            myMenuweek.setRecipefeature(em.merge((Recipefeature) cmbFeature.getSelectedItem()));

            em.getTransaction().commit();

            menuweek = myMenuweek;
            lstCustomers.setModel(Tools.newListModel(new ArrayList<Customer>(menuweek.getCustomers())));
        } catch (OptimisticLockException ole) {
            Main.warn(ole);
            em.getTransaction().rollback();
        } catch (Exception exc) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
            notifyCaller();
        }
    }

    private void notifyCaller() {
        lblMessage.setText("Änderungen gespeichert. " + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + " Uhr");
        changeAction.execute(menuweek);
    }

    private void btnNewMenuweekActionPerformed(ActionEvent e) {
        Main.getDesktop().getMenuweek().addMenu(new Menuweek(menuweek.getWeek(), menuweek.getRecipefeature()));
    }

    private void btnDeleteThisMenuweekActionPerformed(ActionEvent e) {
        if (JOptionPane.showInternalConfirmDialog(Main.getDesktop().getMenuweek(), "Diesen Wochen plan willst Du löschen\n\n" +
                        "Bist du ganz sicher ?", "Wochenplan löschen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION) {

            //                   menu.setRecipe(new Recipes(searcher.getText().trim()));

            EntityManager em = Main.getEMF().createEntityManager();
            try {
                em.getTransaction().begin();
                Menuweek myMenuweek = em.merge(menuweek);
                em.remove(myMenuweek);
                em.getTransaction().commit();

                Main.getDesktop().getMenuweek().deleteMenu(myMenuweek, this);

            } catch (Exception ex) {
                Main.fatal(ex);
            } finally {
                em.close();
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbFeature = new JComboBox<Recipefeature>();
        scrollPane1 = new JScrollPane();
        lstCustomers = new JList<Customer>();
        lblMon = new JLabel();
        lblTue = new JLabel();
        lblWed = new JLabel();
        lblThu = new JLabel();
        lblFri = new JLabel();
        lblSat = new JLabel();
        lblSun = new JLabel();
        panel2 = new JPanel();
        btnAddCustomer = new JButton();
        lblMessage = new JLabel();
        panel1 = new JPanel();
        btnNewMenuweek = new JButton();
        btnDeleteThisMenuweek = new JButton();

        //======== this ========
        setBorder(new DropShadowBorder(Color.black, 8, 0.6f, 12, true, true, true, true));
        setLayout(new FormLayout(
                "default:grow, $lcgap, default",
                "15*(default, $lgap), fill:default:grow"));

        //---- cmbFeature ----
        cmbFeature.setFont(new Font("SansSerif", Font.PLAIN, 18));
        cmbFeature.setToolTipText("Art des Men\u00fcs");
        cmbFeature.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbFeatureItemStateChanged(e);
            }
        });
        add(cmbFeature, CC.xywh(1, 1, 3, 1));

        //======== scrollPane1 ========
        {

            //---- lstCustomers ----
            lstCustomers.setToolTipText("Kundenliste");
            scrollPane1.setViewportView(lstCustomers);
        }
        add(scrollPane1, CC.xywh(3, 3, 1, 25));

        //---- lblMon ----
        lblMon.setText("text");
        lblMon.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblMon, CC.xy(1, 3, CC.CENTER, CC.DEFAULT));

        //---- lblTue ----
        lblTue.setText("text");
        lblTue.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblTue, CC.xy(1, 7, CC.CENTER, CC.DEFAULT));

        //---- lblWed ----
        lblWed.setText("text");
        lblWed.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblWed, CC.xy(1, 11, CC.CENTER, CC.DEFAULT));

        //---- lblThu ----
        lblThu.setText("text");
        lblThu.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblThu, CC.xy(1, 15, CC.CENTER, CC.DEFAULT));

        //---- lblFri ----
        lblFri.setText("text");
        lblFri.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblFri, CC.xy(1, 19, CC.CENTER, CC.DEFAULT));

        //---- lblSat ----
        lblSat.setText("text");
        lblSat.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSat.setForeground(Color.red);
        add(lblSat, CC.xy(1, 23, CC.CENTER, CC.DEFAULT));

        //---- lblSun ----
        lblSun.setText("text");
        lblSun.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSun.setForeground(Color.red);
        add(lblSun, CC.xy(1, 27, CC.CENTER, CC.DEFAULT));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnAddCustomer ----
            btnAddCustomer.setText(null);
            btnAddCustomer.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/edit.png")));
            btnAddCustomer.setToolTipText("Kundeliste bearbeiten");
            btnAddCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddCustomerActionPerformed(e);
                }
            });
            panel2.add(btnAddCustomer);
        }
        add(panel2, CC.xy(3, 29, CC.RIGHT, CC.DEFAULT));

        //---- lblMessage ----
        lblMessage.setText(null);
        lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblMessage, CC.xy(1, 31, CC.DEFAULT, CC.TOP));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnNewMenuweek ----
            btnNewMenuweek.setText(null);
            btnNewMenuweek.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
            btnNewMenuweek.setToolTipText("Neuen Wochenplan erstellen");
            btnNewMenuweek.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnNewMenuweekActionPerformed(e);
                }
            });
            panel1.add(btnNewMenuweek);

            //---- btnDeleteThisMenuweek ----
            btnDeleteThisMenuweek.setText(null);
            btnDeleteThisMenuweek.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_remove.png")));
            btnDeleteThisMenuweek.setToolTipText("Diesen Wochenplan l\u00f6schen");
            btnDeleteThisMenuweek.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnDeleteThisMenuweekActionPerformed(e);
                }
            });
            panel1.add(btnDeleteThisMenuweek);
        }
        add(panel1, CC.xy(3, 31));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox<Recipefeature> cmbFeature;
    private JScrollPane scrollPane1;
    private JList<Customer> lstCustomers;
    private JLabel lblMon;
    private JLabel lblTue;
    private JLabel lblWed;
    private JLabel lblThu;
    private JLabel lblFri;
    private JLabel lblSat;
    private JLabel lblSun;
    private JPanel panel2;
    private JButton btnAddCustomer;
    private JLabel lblMessage;
    private JPanel panel1;
    private JButton btnNewMenuweek;
    private JButton btnDeleteThisMenuweek;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
