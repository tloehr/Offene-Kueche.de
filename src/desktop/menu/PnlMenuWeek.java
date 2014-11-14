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
import java.util.HashMap;

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
    final HashMap<LocalDate, String> holidays;


    public PnlMenuWeek(Menuweek menuweek, Closure changeAction, HashMap<LocalDate, String> holidays) {
        this.holidays = holidays;
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


//        sdf = new SimpleDateFormat(format);

        mon = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.MONDAY - 1), holidays);
        mon.setChangeAction(getChangeEvent4Daily(mon));

        tue = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.TUESDAY - 1), holidays);
        tue.setChangeAction(getChangeEvent4Daily(tue));

        wed = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.WEDNESDAY - 1), holidays);
        wed.setChangeAction(getChangeEvent4Daily(wed));

        thu = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.THURSDAY - 1), holidays);
        thu.setChangeAction(getChangeEvent4Daily(thu));

        fri = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.FRIDAY - 1), holidays);
        fri.setChangeAction(getChangeEvent4Daily(fri));

        sat = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.SATURDAY - 1), holidays);
        sat.setChangeAction(getChangeEvent4Daily(sat));

        sun = new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.SUNDAY - 1), holidays);
        sun.setChangeAction(getChangeEvent4Daily(sun));


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

        lblID.setText(menuweek.getId() > 0 ? Long.toString(menuweek.getId()) : "--");

    }


    private Closure getChangeEvent4Daily(final PnlSingleDayMenu pnl) {
        return new Closure() {
            @Override
            public void execute(Object o) {
                if (o == null) return;

//                EntityManager em = Main.getEMF().createEntityManager();
//                try {
//                    em.getTransaction().begin();
//
//                    Menuweek2Menu newMenuweek2Menu = null;
//                    if (o instanceof Menuweek2Menu) {
//                        Menuweek2Menu intermediate = (Menuweek2Menu) o;
//                        Menu myMenu = em.merge(intermediate.getMenu());
//                        em.lock(myMenu, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                        newMenuweek2Menu = em.merge(intermediate);
//                        newMenuweek2Menu.setMenu(myMenu);
//                    } else {
//                        Pair<Menuweek2Menu, Menuweek2Menu> pair = (Pair<Menuweek2Menu, Menuweek2Menu>) o;
//                        newMenuweek2Menu = em.merge(pair.getSecond());
//                        em.remove(em.merge(pair.getFirst()));     // to make sure that an orphan is created
//                    }
//
//                    Menuweek myMenuweek = em.merge(newMenuweek2Menu.getMenuweek());
//                    em.lock(myMenuweek, LockModeType.OPTIMISTIC);
//
//
//                    myMenuweek.getMenuweek2menus().set(new LocalDate(newMenuweek2Menu.getDate()).getDayOfWeek() - 1, newMenuweek2Menu);
//                    myMenuweek.touch();
//
//                    em.getTransaction().commit();
//                    menuweek = myMenuweek;
//
////                    pnl.setMenu(menuweek.getMenus().get(pnl.getDate().getDayOfWeek() - 1));
//
//                    notifyCaller(newMenuweek2Menu);
//                } catch (OptimisticLockException ole) {
//                    Main.warn(ole);
//                    em.getTransaction().rollback();
//                } catch (Exception exc) {
//                    Main.error(exc.getMessage());
//                    em.getTransaction().rollback();
//                    Main.fatal(exc.getMessage());
//                } finally {
//                    em.close();
////                    notifyCaller();
//                }


            }
        };
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
        popup.setOwner(btnAddCustomer);
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
                    notifyCaller(menuweek);
                } catch (OptimisticLockException ole) {
                    Main.warn(ole);
                    em.getTransaction().rollback();
                } catch (Exception exc) {
                    em.getTransaction().rollback();
                    Main.fatal(e);
                } finally {
                    em.close();
//                    notifyCaller();
                    popup = null;
                }


            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        GUITools.showPopup(popup, SwingUtilities.CENTER);

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
            notifyCaller(menuweek);
        }
    }

    private void notifyCaller(Object object) {
        lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(menuweek.getLastsave()) + " Uhr");
        lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
        changeAction.execute(object);
    }

    private void btnAddMenuWeekActionPerformed(ActionEvent e) {

//        try {
//            Main.getDesktop().getMenuweek().addMenu((Menuweek) menuweek.clone());

//        Main.getDesktop().getMenuweek().addMenu(new Menuweek(menuweek.getMenuweekall(), menuweek.getRecipefeature()));
//        } catch (CloneNotSupportedException e1) {
//            Main.fatal(e1);
//        }
    }

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
        btnRemoveThis = new JButton();
        lblID = new JLabel();
        cmbFeature = new JComboBox<Recipefeature>();
        panel11 = new JScrollPane();
        panel3 = new JPanel();
        pnlMon = new JPanel();
        pnlTue = new JPanel();
        pnlWed = new JPanel();
        pnlThu = new JPanel();
        pnlFri = new JPanel();
        pnlSat = new JPanel();
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

            //---- btnRemoveThis ----
            btnRemoveThis.setText(null);
            btnRemoveThis.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/editdelete.png")));
            panel2.add(btnRemoveThis);
        }
        add(panel2, CC.xy(1, 1));

        //---- lblID ----
        lblID.setText("text");
        lblID.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblID, CC.xy(3, 1, CC.RIGHT, CC.DEFAULT));

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

                //======== pnlMon ========
                {
                    pnlMon.setLayout(new BoxLayout(pnlMon, BoxLayout.X_AXIS));
                }
                panel3.add(pnlMon);

                //======== pnlTue ========
                {
                    pnlTue.setLayout(new BoxLayout(pnlTue, BoxLayout.X_AXIS));
                }
                panel3.add(pnlTue);

                //======== pnlWed ========
                {
                    pnlWed.setLayout(new BoxLayout(pnlWed, BoxLayout.X_AXIS));
                }
                panel3.add(pnlWed);

                //======== pnlThu ========
                {
                    pnlThu.setLayout(new BoxLayout(pnlThu, BoxLayout.X_AXIS));
                }
                panel3.add(pnlThu);

                //======== pnlFri ========
                {
                    pnlFri.setLayout(new BoxLayout(pnlFri, BoxLayout.X_AXIS));
                }
                panel3.add(pnlFri);

                //======== pnlSat ========
                {
                    pnlSat.setLayout(new BoxLayout(pnlSat, BoxLayout.X_AXIS));
                }
                panel3.add(pnlSat);

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
                lstCustomers.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
    private JButton btnRemoveThis;
    private JLabel lblID;
    private JComboBox<Recipefeature> cmbFeature;
    private JScrollPane panel11;
    private JPanel panel3;
    private JPanel pnlMon;
    private JPanel pnlTue;
    private JPanel pnlWed;
    private JPanel pnlThu;
    private JPanel pnlFri;
    private JPanel pnlSat;
    private JPanel pnlSun;
    private JPanel panel12;
    private JScrollPane scrollPane1;
    private JList<Customer> lstCustomers;
    private JButton btnAddCustomer;
    private JLabel lblMessage;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
