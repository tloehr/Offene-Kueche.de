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

/**
 * @author Torsten Löhr
 */
public class PnlMenuWeek extends JPanel {


    private Menuweek menuweek;
    private final Closure changeAction;
    private PnlSingleDayMenu mon, tue, wed, thu, fri, sat, sun;
    SimpleDateFormat sdf;
    JidePopup popup;
    private boolean initPhase;

    private static final String format = "EEEE, d MMM yyyy";

    public PnlMenuWeek(Menuweek menuweek, Closure changeAction) {
        initPhase = true;
        this.menuweek = menuweek;
        this.changeAction = changeAction;
        initComponents();
        initPanel();
        initPhase = false;
    }


    private void initPanel() {

        cmbFeature.setModel(Tools.newComboboxModel(RecipeFeatureTools.getAll()));
        cmbFeature.setRenderer(RecipeFeatureTools.getListCellRenderer());
        cmbFeature.setSelectedItem(menuweek.getRecipefeature());


        sdf = new SimpleDateFormat(format);

        mon = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.MONDAY - 1));
        mon.setChangeAction(getChangeEvent4Daily(mon));

        tue = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.TUESDAY - 1));
        tue.setChangeAction(getChangeEvent4Daily(tue));

        wed = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.WEDNESDAY - 1));
        wed.setChangeAction(getChangeEvent4Daily(wed));

        thu = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.THURSDAY - 1));
        thu.setChangeAction(getChangeEvent4Daily(thu));

        fri = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.FRIDAY - 1));
        fri.setChangeAction(getChangeEvent4Daily(fri));

        sat = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.SATURDAY - 1));
        sat.setChangeAction(getChangeEvent4Daily(sat));

        sun = new PnlSingleDayMenu(menuweek.getMenus().get(DateTimeConstants.SUNDAY - 1));
        sun.setChangeAction(getChangeEvent4Daily(sun));

        lblMon.setText(sdf.format(mon.getMenu().getDate()));
        lblTue.setText(sdf.format(tue.getMenu().getDate()));
        lblWed.setText(sdf.format(wed.getMenu().getDate()));
        lblThu.setText(sdf.format(thu.getMenu().getDate()));
        lblFri.setText(sdf.format(fri.getMenu().getDate()));
        lblSat.setText(sdf.format(sat.getMenu().getDate()));
        lblSun.setText(sdf.format(sun.getMenu().getDate()));

        pnlMon.add(mon);
        pnlTue.add(tue);
        pnlWed.add(wed);
        pnlThu.add(thu);
        pnlFri.add(fri);
        pnlSat.add(sat);
        pnlSun.add(sun);

        lstCustomers.setModel(Tools.newListModel(new ArrayList<Customer>(menuweek.getCustomers())));
        lstCustomers.setCellRenderer(CustomerTools.getListCellRenderer());

        if (menuweek.getId() != 0l) {
            lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(menuweek.getLastsave()) + " Uhr");
        }

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

                    myMenuweek.getMenus().set(pnl.getDate().getDayOfWeek() -1, em.merge(pnl.getMenu()));

                    em.getTransaction().commit();

                    menuweek = myMenuweek;

                    pnl.setMenu(menuweek.getMenus().get(pnl.getDate().getDayOfWeek()-1));

                } catch (OptimisticLockException ole) {
                    Main.warn(ole);
                    em.getTransaction().rollback();
                } catch (Exception exc) {
                    Main.error(exc.getMessage());
                    em.getTransaction().rollback();
                    Main.fatal(exc.getMessage());
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
        if (initPhase) return;

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
        lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(menuweek.getLastsave()) + " Uhr");
        changeAction.execute(menuweek);
    }

//    private void btnNewMenuweekActionPerformed(ActionEvent e) {
//        Main.getDesktop().getMenuweek().addMenu(new Menuweek(menuweek.getWeek(), menuweek.getRecipefeature()));
//    }

//    private void btnDeleteThisMenuweekActionPerformed(ActionEvent e) {
//        if (JOptionPane.showInternalConfirmDialog(Main.getDesktop().getMenuweek(), "Diesen Wochen plan willst Du löschen\n\n" +
//                        "Bist du ganz sicher ?", "Wochenplan löschen",
//                JOptionPane.OK_CANCEL_OPTION,
//                JOptionPane.QUESTION_MESSAGE)
//                == JOptionPane.YES_OPTION) {
//
//            //                   menu.setRecipe(new Recipes(searcher.getText().trim()));
//
//            EntityManager em = Main.getEMF().createEntityManager();
//            try {
//                em.getTransaction().begin();
//                Menuweek myMenuweek = em.merge(menuweek);
//                em.remove(myMenuweek);
//                em.getTransaction().commit();
//
//                Main.getDesktop().getMenuweek().deleteMenu(myMenuweek, this);
//
//            } catch (Exception ex) {
//                Main.fatal(ex);
//            } finally {
//                em.close();
//            }
//        }
//    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        btnPrint = new JButton();
        cmbFeature = new JComboBox<Recipefeature>();
        panel11 = new JScrollPane();
        panel3 = new JPanel();
        lblMon = new JLabel();
        pnlMon = new JPanel();
        lblTue = new JLabel();
        pnlTue = new JPanel();
        lblWed = new JLabel();
        pnlWed = new JPanel();
        lblThu = new JLabel();
        pnlThu = new JPanel();
        lblFri = new JLabel();
        pnlFri = new JPanel();
        lblSat = new JLabel();
        pnlSat = new JPanel();
        lblSun = new JLabel();
        pnlSun = new JPanel();
        panel12 = new JPanel();
        scrollPane1 = new JScrollPane();
        lstCustomers = new JList<Customer>();
        btnAddCustomer = new JButton();
        lblMessage = new JLabel();

        //======== this ========
        setBorder(new DropShadowBorder(Color.black, 8, 0.6f, 12, true, true, true, true));
        setLayout(new FormLayout(
            "default:grow, $lcgap, default",
            "2*(default, $lgap), top:pref, $lgap, pref"));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnPrint ----
            btnPrint.setText(null);
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/printer.png")));
            btnPrint.setToolTipText("Diesen Wochenplan l\u00f6schen");
            panel2.add(btnPrint);
        }
        add(panel2, CC.xywh(1, 1, 3, 1));

        //---- cmbFeature ----
        cmbFeature.setFont(new Font("SansSerif", Font.PLAIN, 18));
        cmbFeature.setToolTipText("Art des Men\u00fcs");
        cmbFeature.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbFeatureItemStateChanged(e);
            }
        });
        add(cmbFeature, CC.xywh(1, 3, 3, 1));

        //======== panel11 ========
        {

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));

                //---- lblMon ----
                lblMon.setText("text");
                lblMon.setFont(new Font("SansSerif", Font.BOLD, 18));
                panel3.add(lblMon);

                //======== pnlMon ========
                {
                    pnlMon.setLayout(new BoxLayout(pnlMon, BoxLayout.X_AXIS));
                }
                panel3.add(pnlMon);

                //---- lblTue ----
                lblTue.setText("text");
                lblTue.setFont(new Font("SansSerif", Font.BOLD, 18));
                panel3.add(lblTue);

                //======== pnlTue ========
                {
                    pnlTue.setLayout(new BoxLayout(pnlTue, BoxLayout.X_AXIS));
                }
                panel3.add(pnlTue);

                //---- lblWed ----
                lblWed.setText("text");
                lblWed.setFont(new Font("SansSerif", Font.BOLD, 18));
                panel3.add(lblWed);

                //======== pnlWed ========
                {
                    pnlWed.setLayout(new BoxLayout(pnlWed, BoxLayout.X_AXIS));
                }
                panel3.add(pnlWed);

                //---- lblThu ----
                lblThu.setText("text");
                lblThu.setFont(new Font("SansSerif", Font.BOLD, 18));
                panel3.add(lblThu);

                //======== pnlThu ========
                {
                    pnlThu.setLayout(new BoxLayout(pnlThu, BoxLayout.X_AXIS));
                }
                panel3.add(pnlThu);

                //---- lblFri ----
                lblFri.setText("text");
                lblFri.setFont(new Font("SansSerif", Font.BOLD, 18));
                panel3.add(lblFri);

                //======== pnlFri ========
                {
                    pnlFri.setLayout(new BoxLayout(pnlFri, BoxLayout.X_AXIS));
                }
                panel3.add(pnlFri);

                //---- lblSat ----
                lblSat.setText("text");
                lblSat.setFont(new Font("SansSerif", Font.BOLD, 18));
                lblSat.setForeground(Color.red);
                panel3.add(lblSat);

                //======== pnlSat ========
                {
                    pnlSat.setLayout(new BoxLayout(pnlSat, BoxLayout.X_AXIS));
                }
                panel3.add(pnlSat);

                //---- lblSun ----
                lblSun.setText("text");
                lblSun.setFont(new Font("SansSerif", Font.BOLD, 18));
                lblSun.setForeground(Color.red);
                panel3.add(lblSun);

                //======== pnlSun ========
                {
                    pnlSun.setLayout(new BoxLayout(pnlSun, BoxLayout.X_AXIS));
                }
                panel3.add(pnlSun);
            }
            panel11.setViewportView(panel3);
        }
        add(panel11, CC.xy(1, 5, CC.FILL, CC.DEFAULT));

        //======== panel12 ========
        {
            panel12.setLayout(new FormLayout(
                "default:grow",
                "default:grow, default"));

            //======== scrollPane1 ========
            {

                //---- lstCustomers ----
                lstCustomers.setToolTipText("Kundenliste");
                scrollPane1.setViewportView(lstCustomers);
            }
            panel12.add(scrollPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

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
            panel12.add(btnAddCustomer, CC.xy(1, 2));
        }
        add(panel12, CC.xy(3, 5, CC.DEFAULT, CC.FILL));

        //---- lblMessage ----
        lblMessage.setText(null);
        lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblMessage, CC.xywh(1, 7, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JButton btnPrint;
    private JComboBox<Recipefeature> cmbFeature;
    private JScrollPane panel11;
    private JPanel panel3;
    private JLabel lblMon;
    private JPanel pnlMon;
    private JLabel lblTue;
    private JPanel pnlTue;
    private JLabel lblWed;
    private JPanel pnlWed;
    private JLabel lblThu;
    private JPanel pnlThu;
    private JLabel lblFri;
    private JPanel pnlFri;
    private JLabel lblSat;
    private JPanel pnlSat;
    private JLabel lblSun;
    private JPanel pnlSun;
    private JPanel panel12;
    private JScrollPane scrollPane1;
    private JList<Customer> lstCustomers;
    private JButton btnAddCustomer;
    private JLabel lblMessage;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
