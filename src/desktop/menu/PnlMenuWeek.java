/*
 * Created by JFormDesigner on Tue Oct 14 15:21:24 CEST 2014
 */

package desktop.menu;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JidePopupMenu;
import entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import printer.Printers;
import tools.Const;
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

    //    private PnlSingleDayMenu tue, wed, thu, fri, sat, sun;
    SimpleDateFormat sdf;
    JidePopup popup;
    private boolean initPhase;
    private ArrayList<JPanel> listPanels;
    private final PSDChangeListener bottomUpListener, topDownListener;
    final HashMap<LocalDate, String> holidays;


    public PnlMenuWeek(Menuweek param_menuweek, PSDChangeListener bottomUpListener, HashMap<LocalDate, String> holidays) {
        this.bottomUpListener = bottomUpListener;
        this.holidays = holidays;
        initPhase = true;
        menuweek = param_menuweek;

        topDownListener = new PSDChangeListener() {
            @Override
            public void menuEdited(PSDChangeEvent psdce) {
                for (JPanel pnlWeekday : listPanels) {
                    for (Component comp : pnlWeekday.getComponents()) {
                        if (comp instanceof PnlSingleDayMenu && !comp.equals(psdce.getSource())) {
                            ((PnlSingleDayMenu) comp).updateMenu(psdce.getNewMenu());
                        }
                    }
                }
                menuweek = psdce.getMenuweek();
            }

            @Override
            public void menuReplaced(PSDChangeEvent psdce) {
                for (JPanel pnlWeekday : listPanels) {
                    for (Component comp : pnlWeekday.getComponents()) {
                        if (comp instanceof PnlSingleDayMenu && !comp.equals(psdce.getSource())) {
                            ((PnlSingleDayMenu) comp).updateMenu(psdce.getNewMenu());
                        }
                    }
                }
                menuweek = psdce.getMenuweek();
            }

            @Override
            public void stockListChanged(PSDChangeEvent psdce) {
                for (JPanel pnlWeekday : listPanels) {
                    for (Component comp : pnlWeekday.getComponents()) {
                        if (comp instanceof PnlSingleDayMenu && !comp.equals(psdce.getSource())) {
                            ((PnlSingleDayMenu) comp).updateMenu(psdce.getNewMenu());
                        }
                    }
                }
                menuweek = psdce.getMenuweek();
            }

            @Override
            public void customerListChanged(PSDChangeEvent psdce) {
                // don't care
            }

            @Override
            public void menufeatureChanged(PSDChangeEvent psdce) {
                // don't care
            }

            @Override
            public void menuweekAdded(PSDChangeEvent psdce) {
                // don't care
            }

            @Override
            public void menuweekDeleted(PSDChangeEvent psdce) {
                // don't care
            }
        };

        initComponents();
        initPanel();
        initPhase = false;
    }


    private void initPanel() {

        cmbFeature.setModel(Tools.newComboboxModel(RecipeFeatureTools.getAll()));
        cmbFeature.setRenderer(RecipeFeatureTools.getListCellRenderer());
        cmbFeature.setSelectedItem(menuweek.getRecipefeature());

        pnlMon.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.MONDAY - 1), holidays, getPSDChangeListener()));
        pnlTue.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.TUESDAY - 1), holidays, getPSDChangeListener()));
        pnlWed.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.WEDNESDAY - 1), holidays, getPSDChangeListener()));
        pnlThu.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.THURSDAY - 1), holidays, getPSDChangeListener()));
        pnlFri.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.FRIDAY - 1), holidays, getPSDChangeListener()));
        pnlSat.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.SATURDAY - 1), holidays, getPSDChangeListener()));
        pnlSun.add(new PnlSingleDayMenu(menuweek.getMenuweek2menus().get(DateTimeConstants.SUNDAY - 1), holidays, getPSDChangeListener()));


        listPanels = new ArrayList<JPanel>();
        listPanels.add(pnlMon);
        listPanels.add(pnlTue);
        listPanels.add(pnlWed);
        listPanels.add(pnlThu);
        listPanels.add(pnlFri);
        listPanels.add(pnlSat);
        listPanels.add(pnlSun);

        lstCustomers.setModel(Tools.newListModel(new ArrayList<Customer>(menuweek.getCustomers())));
        lstCustomers.setCellRenderer(CustomerTools.getListCellRenderer());

        if (menuweek.getId() != 0l) {
            lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(menuweek.getLastsave()) + " Uhr");

        }

        lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");

    }


    private PSDChangeListener getPSDChangeListener() {
        return new PSDChangeListener() {
            @Override
            public void menuEdited(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.menuEdited(psdce);
            }

            @Override
            public void menuReplaced(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.menuReplaced(psdce);
            }

            @Override
            public void menufeatureChanged(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.menufeatureChanged(psdce);
            }

            @Override
            public void customerListChanged(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.customerListChanged(psdce);
            }

            @Override
            public void stockListChanged(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.stockListChanged(psdce);
            }

            @Override
            public void menuweekAdded(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.menuweekAdded(psdce);
            }

            @Override
            public void menuweekDeleted(PSDChangeEvent psdce) {
                lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(psdce.getChangeDate()) + " Uhr");
                lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
                bottomUpListener.menuweekDeleted(psdce);
            }
        };
    }


    public PSDChangeListener getTopDownListener() {
        return topDownListener;
    }
//
//    public void notifyMeAbout(PSDChangeEvent psdce) {
//
//
//
//    }


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
                    bottomUpListener.customerListChanged(new PSDChangeEvent(this, menuweek));
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
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception exc) {
            em.getTransaction().rollback();
            Main.fatal(e);
        } finally {
            em.close();
            bottomUpListener.menufeatureChanged(new PSDChangeEvent(this, menuweek));
        }
    }

//    private void notifyCaller(Object object) {
//        lblMessage.setText("Änderungen zuletzt gespeichert. " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(menuweek.getLastsave()) + " Uhr");
//        lblID.setText(menuweek.getId() > 0 ? "#" + Long.toString(menuweek.getId()) : "--");
//
//
//        changeAction.execute(object);
//    }


    private void addMenuweek(Menuweek newMenuweek) {
        Menuweek myMenuweek = null;
        Menuweekall myMenuweekall = null;
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            myMenuweek = em.merge(newMenuweek);

            myMenuweekall = em.merge(myMenuweek.getMenuweekall());
            em.lock(myMenuweekall, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            Main.warn(ole);
        } catch (Exception exc) {
            em.getTransaction().rollback();
            Main.fatal(exc.getMessage());
        } finally {
            em.close();
            bottomUpListener.menuweekAdded(new PSDChangeEvent(this, myMenuweek));
        }

    }

    private void btnAddNewMenuweekActionPerformed(ActionEvent e) {

        JidePopupMenu jMenu = new JidePopupMenu();
        JMenuItem miEmpty = new JMenuItem("Leeren Speiseplan erstellen");
        miEmpty.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JMenuItem miTemplate = new JMenuItem("Aus dieser Vorlage einen neuen Speiseplan erstellen");
        miTemplate.setFont(new Font("SansSerif", Font.PLAIN, 18));
        jMenu.add(miEmpty);
        jMenu.add(miTemplate);

        miEmpty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMenuweek(new Menuweek(menuweek.getMenuweekall(), menuweek.getRecipefeature()));
            }
        });

        miTemplate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMenuweek(menuweek.clone());
            }
        });


        jMenu.show(btnAddNewMenuweek, 0, btnAddNewMenuweek.getPreferredSize().height);


    }

    private void btnRemoveThisActionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(Main.getDesktop().getMenuweek(), "Wirklich ?", "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Const.icon48stop) == JOptionPane.YES_OPTION) {
            Menuweek myMenuweek = null;
            Menuweekall myMenuweekall = null;
            EntityManager em = Main.getEMF().createEntityManager();
            try {
                em.getTransaction().begin();
                myMenuweek = em.merge(menuweek);
                em.remove(myMenuweek);
                myMenuweekall = em.merge(myMenuweek.getMenuweekall());
                em.lock(myMenuweekall, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                em.getTransaction().commit();
            } catch (OptimisticLockException ole) {
                em.getTransaction().rollback();
                Main.warn(ole);
            } catch (Exception exc) {
                em.getTransaction().rollback();
                Main.fatal(exc.getMessage());
            } finally {
                em.close();
                bottomUpListener.menuweekDeleted(new PSDChangeEvent(this, myMenuweek));
            }
        }
    }

    private void btnPrintActionPerformed(ActionEvent e) {

        JidePopupMenu jMenu = new JidePopupMenu();
        JMenuItem miPrintAllMenus = new JMenuItem("Diesen Speiseplan drucken (Vollansicht)", Const.icon24Pageprinter);
        miPrintAllMenus.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JMenuItem miPrintAllMenusCustomer = new JMenuItem("Diesen Speiseplan drucken (Kundenansicht)", Const.icon24Pageprinter);
        miPrintAllMenusCustomer.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JMenuItem miIngTypesAndStocks = new JMenuItem("Zutaten und Vorratslisten drucken", Const.icon24ingtype);
        miIngTypesAndStocks.setFont(new Font("SansSerif", Font.PLAIN, 18));

        jMenu.add(miPrintAllMenus);
        jMenu.add(miPrintAllMenusCustomer);
        jMenu.add(miIngTypesAndStocks);

        miPrintAllMenus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Printers.print(Main.getDesktop(), MenuweekTools.getAsHTML(menuweek, false), true);
            }
        });


        miPrintAllMenusCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Printers.print(Main.getDesktop(), MenuweekTools.getAsHTML(menuweek, true), true);
            }
        });

        miIngTypesAndStocks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Printers.print(Main.getDesktop(), MenuweekTools.getIngredientsAsHTML(menuweek), true);
            }
        });


        jMenu.show(btnPrint, 0, btnPrint.getPreferredSize().height);

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
        btnAddNewMenuweek = new JButton();
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
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            panel2.add(btnPrint);

            //---- btnAddNewMenuweek ----
            btnAddNewMenuweek.setText(null);
            btnAddNewMenuweek.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
            btnAddNewMenuweek.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddNewMenuweekActionPerformed(e);
                }
            });
            panel2.add(btnAddNewMenuweek);

            //---- btnRemoveThis ----
            btnRemoveThis.setText(null);
            btnRemoveThis.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/editdelete.png")));
            btnRemoveThis.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnRemoveThisActionPerformed(e);
                }
            });
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
    private JButton btnAddNewMenuweek;
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
