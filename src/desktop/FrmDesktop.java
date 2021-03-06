/*
 * Created by JFormDesigner on Wed Feb 09 16:13:45 CET 2011
 */

package desktop;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import desktop.menu.FrmMenu;
import desktop.products.FrmProdukte;
import desktop.products.FrmReassignProduct;
import entity.Mitarbeiter;
import org.apache.commons.collections.Closure;
import threads.HeapStat;
import threads.PrintProcessor;
import threads.SoundProcessor;
import tools.Tools;
import touch.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

/**
 * @author Torsten Löhr
 */
public class FrmDesktop extends JFrame {
    //    boolean ADMIN = true;
    JInternalFrame einbuchen, ausbuchen, umbuchen, stock2product;
    JFrame menuweek, types, produkte;
    FrmStock vorrat = null;
    FrmUser user = null;
    HeapStat hs;
    WindowAdapter wa;
    DlgLogin dlg;

    public PrintProcessor getPrintProcessor() {
        return pp;
    }

    PrintProcessor pp;
    SoundProcessor sp;

    private MyInternalFrameListener myInternalFrameListener;
    private MyFrameListener myFrameListener;


    public FrmIngType getTypesFrame() {
        return (FrmIngType) types;
    }

    public FrmProdukte getProductsFrame() {
        return (FrmProdukte) produkte;
    }

    public FrmMenu getMenuweek() {
        return (FrmMenu) menuweek;
    }

    public FrmDesktop() {
        initComponents();

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (Main.getCurrentUser() != null) {
                    hs.touch();
                }
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (Main.getCurrentUser() != null) {
                    hs.touch();
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

        pack();
        setTitle(tools.Tools.getWindowTitle("Desktop"));

        myInternalFrameListener = new MyInternalFrameListener();
        myFrameListener = new MyFrameListener();

        hs = new HeapStat(pbHeap, jpTimeout, null, new Closure() {
            @Override
            public void execute(Object o) {
                Main.debug("TIMEOUT");
                logout();
                loginMode();
            }
        });
        hs.start();
        pp = new PrintProcessor(pbPrint);
        pp.start();
        sp = new SoundProcessor();
        sp.start();
        soundMenuItem.setSelected(Main.getProps().getProperty("sound").equalsIgnoreCase("on"));

        lblJDBC.setText(Main.getJdbcurl());

        menuweekMenuItem.setEnabled(Main.isDevmode());

    }

    private void loginDlgClosing(java.awt.event.WindowEvent evt) {
        if (Main.getCurrentUser() == null) {
            dispose();
        }
    }

    private void loginMode() {
        lblUsername.setText("Kein Benutzer");
        logoutMenuItem.setEnabled(false);
        aktionenMenu.setEnabled(false);
        stammdatenMenu.setEnabled(false);
        systemMenu.setEnabled(false);
        dlg = new DlgLogin(JOptionPane.getFrameForComponent(this), this, new Closure() {
            @Override
            public void execute(Object o) {
                Main.setCurrentUser((Mitarbeiter) o);
                hs.touch();
                stammdatenMenu.setEnabled(true);
                aktionenMenu.setEnabled(true);
                systemMenu.setEnabled(true);
                logoutMenuItem.setEnabled(true);
                lblUsername.setText(Main.getCurrentUser().getName() + ", " + Main.getCurrentUser().getVorname());
                userMenuItem.setEnabled(Main.getCurrentUser().isAdmin());
                dlg.dispose();
            }
        });
        wa = new java.awt.event.WindowAdapter() {

            public void windowClosed(WindowEvent e) {
                loginDlgClosing(e);
            }
        };
        dlg.addWindowListener(wa);
        dlg.setVisible(true);
    }

    private void myDispose(JInternalFrame frm) {
        if (frm != null) {
            frm.dispose();
        }
    }

    private void logout() {
        Main.setCurrentUser(null);
        myDispose(einbuchen);
        myDispose(vorrat);

        if (menuweek != null) {
            menuweek.dispose();
        }
        if (types != null) {
            types.dispose();
        }
        if (produkte != null) {
            produkte.dispose();
        }
        //myDispose(drucker);
        myDispose(user);
        logoutMenuItem.setEnabled(false);
    }

    private void formWindowOpened(WindowEvent e) {
        loginMode();
    }

    private void formWindowClosing(WindowEvent e) {
        tools.Tools.saveProperties();
    }

    private void logoutMenuItemActionPerformed(ActionEvent e) {
        logout();
        loginMode();
    }

    private void exitMenuItemActionPerformed(ActionEvent e) {
        dispose();
    }

    private void einbuchenMenuItemActionPerformed(ActionEvent e) {
        einbuchen = new JInternalFrame();
        einbuchen.setResizable(true);
        einbuchen.setMaximizable(true);
        einbuchen.setIconifiable(true);
        einbuchen.setClosable(true);
        einbuchen.setTitle(Tools.getWindowTitle("Wareneingang"));

        einbuchen.getContentPane().setLayout(new BoxLayout(einbuchen.getContentPane(), BoxLayout.PAGE_AXIS));

        einbuchen.getContentPane().add(new PnlWareneingang(pp));
        //einbuchen.validate();
        einbuchen.pack();

        einbuchen.addInternalFrameListener(myInternalFrameListener);
        einbuchenMenuItem.setEnabled(false);

        desktopPane.add(einbuchen);
        einbuchen.setVisible(true);

        try {
            einbuchen.setMaximum(true);
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            einbuchen.setSelected(true);
        } catch (PropertyVetoException e1) {
            Main.debug(e1);
        }
    }

    private void vorraeteMenuItemActionPerformed(ActionEvent e) {
        if (vorrat != null) {
            vorrat.toFront();
            return;
        }

        vorrat = new FrmStock();
        vorrat.addInternalFrameListener(myInternalFrameListener);
//        vorraeteMenuItem.setEnabled(false);
        desktopPane.add(vorrat);
        Tools.centerOnParent(desktopPane, vorrat);
        try {
            vorrat.setMaximum(true);
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();
        }
        vorrat.toFront();
        try {
            vorrat.setMaximum(true);
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

//    private void druckerMenuItemActionPerformed(ActionEvent e) {
//        drucker = new FrmPrinterSelection();
//        drucker.addInternalFrameListener(myInternalFrameListener);
//        druckerMenuItem.setEnabled(false);
//        desktopPane.add(drucker);
//        Tools.centerOnParent(desktopPane, drucker);
//    }

    private void userMenuItemActionPerformed(ActionEvent e) {
        user = new FrmUser();
        user.addInternalFrameListener(myInternalFrameListener);
        userMenuItem.setEnabled(false);
        desktopPane.add(user);
        Tools.centerOnParent(desktopPane, user);
    }

    private void ausbuchenMenuItemActionPerformed(ActionEvent e) {
        ausbuchen = new JInternalFrame();
        ausbuchen.setResizable(true);
        ausbuchen.setMaximizable(true);
        ausbuchen.setIconifiable(true);
        ausbuchen.setClosable(true);
        ausbuchen.setTitle(Tools.getWindowTitle("Warenausgang"));

        ausbuchen.getContentPane().setLayout(new BoxLayout(ausbuchen.getContentPane(), BoxLayout.PAGE_AXIS));

        ausbuchen.getContentPane().add(new PnlAusbuchen(sp));
        //ausbuchen.validate();
        ausbuchen.pack();

        ausbuchen.addInternalFrameListener(myInternalFrameListener);
        ausbuchenMenuItem.setEnabled(false);

        desktopPane.add(ausbuchen);

        ausbuchen.setVisible(true);

        try {
            ausbuchen.setMaximum(true);
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //Tools.centerOnParent(desktopPane, ausbuchen);
        try {
            ausbuchen.setSelected(true);
        } catch (PropertyVetoException e1) {
            Main.debug(e1);
        }
    }

    private void produkteMenuItemActionPerformed(ActionEvent e) {
        if (produkte != null) {
            produkte.toFront();
            return;
        }

        produkte = new FrmProdukte();
        produkte.addWindowListener(myFrameListener);
//        produkteMenuItem.setEnabled(false);
        produkte.setVisible(true);

        produkte.toFront();
        produkte.setExtendedState(MAXIMIZED_BOTH);
    }

    private void umbuchenMenuItemActionPerformed(ActionEvent e) {
        umbuchenMenuItem.setEnabled(false);
        umbuchen = openTouchPanelInDesktop(new PnlUmbuchen(sp), "Umbuchung von Beständen");
    }

    private JInternalFrame openTouchPanelInDesktop(DefaultTouchPanel pnl, String title) {
        JInternalFrame frame = new JInternalFrame();
        frame.setResizable(true);
        frame.setMaximizable(true);
        frame.setIconifiable(true);
        frame.setClosable(true);
        frame.setTitle(Tools.getWindowTitle(title));

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        frame.getContentPane().add(pnl);
        //frame.validate();
        frame.pack();
        desktopPane.add(frame);

        frame.addInternalFrameListener(myInternalFrameListener);

        frame.setVisible(true);

        try {
            frame.setMaximum(true);
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e1) {
            Main.debug(e1);
        }
        return frame;
    }

    private void soundMenuItemItemStateChanged(ItemEvent e) {
        Main.getProps().setProperty("sound", soundMenuItem.isSelected() ? "on" : "off");
        if (soundMenuItem.isSelected()) {
            sp.unpause();
        } else {
            sp.pause();
        }
    }

    private void typeMenuItemActionPerformed(ActionEvent e) {
        types = new FrmIngType();
        types.addWindowListener(myFrameListener);
        typeMenuItem.setEnabled(false);
        types.setVisible(true);
        Tools.centerOnParent(desktopPane, types);
        types.toFront();

    }

    private void menuweekMenuItemActionPerformed(ActionEvent e) {
        menuweek = new FrmMenu();
        menuweek.setTitle(Tools.getWindowTitle("Speiseplan"));
        menuweek.addWindowListener(myFrameListener);
        menuweekMenuItem.setEnabled(false);

        menuweek.setVisible(true);

//        Tools.centerOnParent(desktopPane, menuweek);
        menuweek.toFront();

        menuweek.setExtendedState(MAXIMIZED_BOTH);
    }


    private void menuItemStock2productFixingActionPerformed(ActionEvent e) {

        if (stock2product != null) {
            stock2product.toFront();
            return;
        }

        stock2product = new FrmReassignProduct(pp);
        stock2product.addInternalFrameListener(myInternalFrameListener);

        desktopPane.add(stock2product);
        Tools.centerOnParent(desktopPane, stock2product);
        stock2product.toFront();
        try {
            stock2product.setMaximum(true);
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        logoutMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        stammdatenMenu = new JMenu();
        MenuStocks = new JMenu();
        vorraeteMenuItem = new JMenuItem();
        menuItemStock2productFixing = new JMenuItem();
        produkteMenuItem = new JMenuItem();
        typeMenuItem = new JMenuItem();
        menuweekMenuItem = new JMenuItem();
        aktionenMenu = new JMenu();
        einbuchenMenuItem = new JMenuItem();
        ausbuchenMenuItem = new JMenuItem();
        umbuchenMenuItem = new JMenuItem();
        systemMenu = new JMenu();
        userMenuItem = new JMenuItem();
        soundMenuItem = new JCheckBoxMenuItem();
        jpTimeout = new JProgressBar();
        desktopPane = new JDesktopPane();
        pnlStatus = new JPanel();
        lblUsername = new JLabel();
        hSpacer1 = new JPanel(null);
        pbPrint = new JProgressBar();
        hSpacer2 = new JPanel(null);
        lblJDBC = new JLabel();
        hSpacer3 = new JPanel(null);
        pbHeap = new JProgressBar();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                formWindowOpened(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "default:grow",
                "$ugap, fill:default:grow, fill:default"));

        //======== menuBar ========
        {

            //======== fileMenu ========
            {
                fileMenu.setText("Datei");
                fileMenu.setFont(new Font("sansserif", Font.PLAIN, 18));

                //---- logoutMenuItem ----
                logoutMenuItem.setText("Abmelden");
                logoutMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                logoutMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        logoutMenuItemActionPerformed(e);
                    }
                });
                fileMenu.add(logoutMenuItem);

                //---- exitMenuItem ----
                exitMenuItem.setText("Beenden");
                exitMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                exitMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exitMenuItemActionPerformed(e);
                        exitMenuItemActionPerformed(e);
                    }
                });
                fileMenu.add(exitMenuItem);
            }
            menuBar.add(fileMenu);

            //======== stammdatenMenu ========
            {
                stammdatenMenu.setText("Stammdaten");
                stammdatenMenu.setFont(new Font("sansserif", Font.PLAIN, 18));

                //======== MenuStocks ========
                {
                    MenuStocks.setText("Vorr\u00e4te");
                    MenuStocks.setFont(new Font("SansSerif", Font.PLAIN, 18));

                    //---- vorraeteMenuItem ----
                    vorraeteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.ALT_MASK));
                    vorraeteMenuItem.setText("Bearbeiten");
                    vorraeteMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                    vorraeteMenuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            vorraeteMenuItemActionPerformed(e);
                        }
                    });
                    MenuStocks.add(vorraeteMenuItem);

                    //---- menuItemStock2productFixing ----
                    menuItemStock2productFixing.setText("Vorrat/Produkt Zuordnung");
                    menuItemStock2productFixing.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    menuItemStock2productFixing.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            menuItemStock2productFixingActionPerformed(e);
                        }
                    });
                    MenuStocks.add(menuItemStock2productFixing);
                }
                stammdatenMenu.add(MenuStocks);

                //---- produkteMenuItem ----
                produkteMenuItem.setText("Produkte");
                produkteMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                produkteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_MASK));
                produkteMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        produkteMenuItemActionPerformed(e);
                    }
                });
                stammdatenMenu.add(produkteMenuItem);

                //---- typeMenuItem ----
                typeMenuItem.setText("Stoffart");
                typeMenuItem.setFont(new Font("SansSerif", Font.PLAIN, 18));
                typeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_MASK));
                typeMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        typeMenuItemActionPerformed(e);
                    }
                });
                stammdatenMenu.add(typeMenuItem);

                //---- menuweekMenuItem ----
                menuweekMenuItem.setText("Speisepl\u00e4ne");
                menuweekMenuItem.setFont(new Font("SansSerif", Font.PLAIN, 18));
                menuweekMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuweekMenuItemActionPerformed(e);
                    }
                });
                stammdatenMenu.add(menuweekMenuItem);
            }
            menuBar.add(stammdatenMenu);

            //======== aktionenMenu ========
            {
                aktionenMenu.setText("Aktionen");
                aktionenMenu.setFont(new Font("sansserif", Font.PLAIN, 18));

                //---- einbuchenMenuItem ----
                einbuchenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK));
                einbuchenMenuItem.setText("Einbuchen");
                einbuchenMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                einbuchenMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        einbuchenMenuItemActionPerformed(e);
                    }
                });
                aktionenMenu.add(einbuchenMenuItem);

                //---- ausbuchenMenuItem ----
                ausbuchenMenuItem.setText("Ausbuchen");
                ausbuchenMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                ausbuchenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_MASK));
                ausbuchenMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ausbuchenMenuItemActionPerformed(e);
                    }
                });
                aktionenMenu.add(ausbuchenMenuItem);

                //---- umbuchenMenuItem ----
                umbuchenMenuItem.setText("Umbuchen");
                umbuchenMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                umbuchenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.ALT_MASK));
                umbuchenMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        umbuchenMenuItemActionPerformed(e);
                    }
                });
                aktionenMenu.add(umbuchenMenuItem);
            }
            menuBar.add(aktionenMenu);

            //======== systemMenu ========
            {
                systemMenu.setText("System");
                systemMenu.setFont(new Font("sansserif", Font.PLAIN, 18));

                //---- userMenuItem ----
                userMenuItem.setText("Benutzerdaten");
                userMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                userMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        userMenuItemActionPerformed(e);
                    }
                });
                systemMenu.add(userMenuItem);

                //---- soundMenuItem ----
                soundMenuItem.setText("System-T\u00f6ne");
                soundMenuItem.setFont(new Font("sansserif", Font.PLAIN, 18));
                soundMenuItem.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        soundMenuItemItemStateChanged(e);
                    }
                });
                systemMenu.add(soundMenuItem);
            }
            menuBar.add(systemMenu);
        }
        setJMenuBar(menuBar);
        contentPane.add(jpTimeout, CC.xy(1, 1));
        contentPane.add(desktopPane, CC.xy(1, 2));

        //======== pnlStatus ========
        {
            pnlStatus.setBorder(new EtchedBorder());
            pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.X_AXIS));

            //---- lblUsername ----
            lblUsername.setText("jLabel1");
            lblUsername.setFont(new Font("sansserif", Font.PLAIN, 18));
            pnlStatus.add(lblUsername);
            pnlStatus.add(hSpacer1);

            //---- pbPrint ----
            pbPrint.setToolTipText("Druckauftr\u00e4ge");
            pnlStatus.add(pbPrint);
            pnlStatus.add(hSpacer2);

            //---- lblJDBC ----
            lblJDBC.setText("text");
            pnlStatus.add(lblJDBC);
            pnlStatus.add(hSpacer3);

            //---- pbHeap ----
            pbHeap.setToolTipText("Speicherauslastung");
            pnlStatus.add(pbHeap);
        }
        contentPane.add(pnlStatus, CC.xy(1, 3));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem logoutMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu stammdatenMenu;
    private JMenu MenuStocks;
    private JMenuItem vorraeteMenuItem;
    private JMenuItem menuItemStock2productFixing;
    private JMenuItem produkteMenuItem;
    private JMenuItem typeMenuItem;
    private JMenuItem menuweekMenuItem;
    private JMenu aktionenMenu;
    private JMenuItem einbuchenMenuItem;
    private JMenuItem ausbuchenMenuItem;
    private JMenuItem umbuchenMenuItem;
    private JMenu systemMenu;
    private JMenuItem userMenuItem;
    private JCheckBoxMenuItem soundMenuItem;
    private JProgressBar jpTimeout;
    private JDesktopPane desktopPane;
    private JPanel pnlStatus;
    private JLabel lblUsername;
    private JPanel hSpacer1;
    private JProgressBar pbPrint;
    private JPanel hSpacer2;
    private JLabel lblJDBC;
    private JPanel hSpacer3;
    private JProgressBar pbHeap;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public void dispose() {
        tools.Tools.saveProperties();
        Main.getEMF().close();
        hs.interrupt();
//        cardmonitor.interrupt();
        super.dispose();
        System.exit(0);
    }

    private class MyFrameListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (e.getSource() instanceof FrmMenu) {
                menuweek.removeWindowListener(myFrameListener);
                menuweek = null;
                menuweekMenuItem.setEnabled(true);
            } else if (e.getSource() instanceof FrmIngType) {
                types.removeWindowListener(myFrameListener);
                types = null;
                typeMenuItem.setEnabled(true);
            } else if (e.getSource() instanceof FrmProdukte) {
                produkte.removeWindowListener(myFrameListener);
                produkte = null;
            }
            super.windowClosing(e);
        }

    }

    private class MyInternalFrameListener extends InternalFrameAdapter {

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            if (e.getSource() == einbuchen) {
                einbuchen.removeInternalFrameListener(myInternalFrameListener);
                ((TouchPanel) einbuchen.getContentPane().getComponent(0)).cleanup();
                einbuchen = null;
                einbuchenMenuItem.setEnabled(true);
            } else if (e.getSource() == ausbuchen) {
                ausbuchen.removeInternalFrameListener(myInternalFrameListener);
                ((TouchPanel) ausbuchen.getContentPane().getComponent(0)).cleanup();
                ausbuchen = null;
                ausbuchenMenuItem.setEnabled(true);
            } else if (e.getSource() == umbuchen) {
                umbuchen.removeInternalFrameListener(myInternalFrameListener);
                ((TouchPanel) umbuchen.getContentPane().getComponent(0)).cleanup();
                umbuchen = null;
                umbuchenMenuItem.setEnabled(true);
            } else if (e.getSource() instanceof FrmStock) {
                vorrat.removeInternalFrameListener(myInternalFrameListener);
                vorrat = null;
//                vorraeteMenuItem.setEnabled(true);
//            } else if (e.getSource() instanceof FrmPrinterSelection) {
//                drucker.removeInternalFrameListener(myInternalFrameListener);
//                drucker = null;
//                druckerMenuItem.setEnabled(true);
            } else if (e.getSource() instanceof FrmUser) {
                user.removeInternalFrameListener(myInternalFrameListener);
                user = null;
                userMenuItem.setEnabled(true);

            } else if (e.getSource() instanceof FrmReassignProduct) {
                stock2product.removeInternalFrameListener(myInternalFrameListener);
                stock2product = null;
            }
            super.internalFrameClosed(e);
        }
    }

}
