/*
 * Created by JFormDesigner on Wed Feb 09 16:13:36 CET 2011
 */

package touch;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Mitarbeiter;
import events.PanelSwitchEvent;
import events.PanelSwitchListener;
import threads.*;
import tools.NoneSelectedButtonGroup;
import tools.Tools;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Torsten Löhr
 */
public class FrmTouch extends JFrame {

    Color prevBackground;

    TouchPanel currentPanel;
    HeapStat hs;
    CardMonitor cm;
    PrintProcessor pp;
    SoundProcessor sp;
    CardStateListener csl;
    private NoneSelectedButtonGroup bg1;

    MouseAdapter ma = new MouseAdapter() {

        public void mousePressed(MouseEvent e) {
            prevBackground = ((JComponent) e.getSource()).getBackground();
            ((JComponent) e.getSource()).setBackground(Color.red);
        }

        public void mouseReleased(MouseEvent e) {
            ((JComponent) e.getSource()).setBackground(prevBackground);
        }

    };


    PanelSwitchListener psl = new PanelSwitchListener() {
        @Override
        public void panelSwitched(PanelSwitchEvent evt) {
//            btnWareneingang.setEnabled(evt.isEnablePanelSelectButtons());
//            btnWarenausgang.setEnabled(evt.isEnablePanelSelectButtons());
//            currentPanel = evt.getSwitchTo();
//            currentPanel.addTargetChangeListener(tl);
//            currentPanel.addPanelSwitchListener(psl);
//            if (currentPanel != null) {
//                pnlCenter.remove((JPanel) currentPanel);
//            }
//            pnlCenter.add((JPanel) currentPanel);
//            pack();
//            //pnlCenter.getViewport().add((Component) currentPanel);
//            currentPanel.startAction();
        }
    };

    public FrmTouch() {
        initComponents();

        String title = "Touchscreen";
        if (Main.isDebug()){
            title += " ("+Main.getProps().getProperty("javax.persistence.jdbc.url")+")";
        }

        setTitle(tools.Tools.getWindowTitle(title));

        sp = new SoundProcessor();
        sp.start();
        btnSound.setSelected(Main.getProps().getProperty("sound").equalsIgnoreCase("on"));

        bg1 = new NoneSelectedButtonGroup();
        bg1.add(btnWareneingang);
        bg1.add(btnWarenausgang);
        bg1.add(btnUmbuchen);

        hs = new HeapStat(pbHeap, lblClock);
        hs.start();

        pp = new PrintProcessor(pbMain);
        pp.start();

        cm = new CardMonitor();
        csl = new CardStateListener() {

            @Override
            public void cardStateChanged(CardStateChangedEvent evt) {
                if (evt.isCardPresent() && evt.isUserMode() && evt.getCardID() < Long.MAX_VALUE) {
                    Query query = Main.getEM().createNamedQuery("Mitarbeiter.findByCardID");
                    query.setParameter("cardId", evt.getCardID());
                    try {
                        Main.currentUser = (Mitarbeiter) query.getSingleResult();
                        login();
                    } catch (Exception e) {
                        logout();
                    }
                } else {
                    logout();
                }
            }
        };

        cm.addCardEventListener(csl);
        if (cm.getTerminal() == null) {
            lblUsername.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
            lblUsername.setText("Kein PC/SC Kartenterminal gefunden");
            cm.interrupt();
        } else {
            cm.start();
        }

        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void dispose() {
        tools.Tools.saveProperties();
        Main.getEM().close();
        cm.removeCardEventListener(csl);
        cm.interrupt();
        hs.interrupt();
        pp.interrupt();
        super.dispose();
        System.exit(0);
    }


    private void btnWarenausgangItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            setCurrentPanelTo(new PnlAusbuchen(sp));
        }
        if (Tools.getSelection(bg1).isEmpty()) {
            clearCenterPanel();
        }
    }


    private void btnWareneingangItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            setCurrentPanelTo(new PnlWareneingang(pp));
        }
        if (Tools.getSelection(bg1).isEmpty()) {
            clearCenterPanel();
        }
    }

    private void btnLogoutActionPerformed(ActionEvent e) {
        dispose();
        System.exit(0);
    }

    private void thisComponentResized(ComponentEvent e) {
        Main.logger.debug(((Frame) e.getComponent()).getWidth() + " x " + ((Frame) e.getComponent()).getHeight());
    }

    private void thisWindowClosing(WindowEvent e) {
        tools.Tools.saveProperties();
    }

    private void btnUmbuchenItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            setCurrentPanelTo(new PnlUmbuchen(sp));
        }
        if (Tools.getSelection(bg1).isEmpty()) {
            clearCenterPanel();
        }
    }

    private void btnSoundItemStateChanged(ItemEvent e) {
        if (btnSound.isSelected()) {
            sp.unpause();
        } else {
            sp.pause();
        }
        Main.getProps().setProperty("sound", btnSound.isSelected() ? "on" : "off");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlCenter = new JPanel();
        pnlLower = new JPanel();
        btnWareneingang = new JToggleButton();
        panel1 = new JPanel();
        pbMain = new JProgressBar();
        pbHeap = new JProgressBar();
        lblUsername = new JLabel();
        btnSound = new JToggleButton();
        btnQuit = new JButton();
        lblClock = new JLabel();
        btnWarenausgang = new JToggleButton();
        btnUmbuchen = new JToggleButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();

        //======== pnlCenter ========
        {
            pnlCenter.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
            pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.X_AXIS));
        }

        //======== pnlLower ========
        {
            pnlLower.setBackground(new Color(214, 217, 223));
            pnlLower.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, 100dlu",
                "fill:default, $lgap, default, $lgap, fill:default"));

            //---- btnWareneingang ----
            btnWareneingang.setFont(new Font("sansserif", Font.BOLD, 24));
            btnWareneingang.setText("Wareneingang");
            btnWareneingang.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
            btnWareneingang.setEnabled(false);
            btnWareneingang.setHorizontalAlignment(SwingConstants.LEADING);
            btnWareneingang.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnWareneingangItemStateChanged(e);
                }
            });
            pnlLower.add(btnWareneingang, CC.xy(1, 1));

            //======== panel1 ========
            {
                panel1.setBorder(new EtchedBorder());
                panel1.setLayout(new FormLayout(
                    "default:grow, default",
                    "2*($lgap), 3*(fill:default, $rgap), fill:default:grow"));

                //---- pbMain ----
                pbMain.setStringPainted(true);
                pbMain.setString("test");
                panel1.add(pbMain, CC.xywh(1, 3, 2, 1));

                //---- pbHeap ----
                pbHeap.setStringPainted(true);
                panel1.add(pbHeap, CC.xywh(1, 5, 2, 1));

                //---- lblUsername ----
                lblUsername.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
                lblUsername.setText("jLabel1");
                lblUsername.setForeground(Color.black);
                lblUsername.setFont(new Font("sansserif", Font.PLAIN, 18));
                panel1.add(lblUsername, CC.xywh(1, 7, 2, 1));

                //---- btnSound ----
                btnSound.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/soundoff.png")));
                btnSound.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/soundon.png")));
                btnSound.setRolloverEnabled(false);
                btnSound.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        btnSoundItemStateChanged(e);
                    }
                });
                panel1.add(btnSound, CC.xy(1, 9));

                //---- btnQuit ----
                btnQuit.setFont(new Font("Lucida Grande", Font.BOLD, 36));
                btnQuit.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/exit.png")));
                btnQuit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnLogoutActionPerformed(e);
                    }
                });
                panel1.add(btnQuit, CC.xy(2, 9));
            }
            pnlLower.add(panel1, CC.xywh(5, 1, 1, 5));

            //---- lblClock ----
            lblClock.setText("08:01 Uhr");
            lblClock.setHorizontalAlignment(SwingConstants.CENTER);
            pnlLower.add(lblClock, CC.xywh(3, 1, 1, 5));

            //---- btnWarenausgang ----
            btnWarenausgang.setFont(new Font("sansserif", Font.BOLD, 24));
            btnWarenausgang.setText("Warenausgang");
            btnWarenausgang.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_remove.png")));
            btnWarenausgang.setEnabled(false);
            btnWarenausgang.setHorizontalAlignment(SwingConstants.LEADING);
            btnWarenausgang.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnWarenausgangItemStateChanged(e);
                }
            });
            pnlLower.add(btnWarenausgang, CC.xy(1, 3));

            //---- btnUmbuchen ----
            btnUmbuchen.setText("Umbuchen");
            btnUmbuchen.setFont(new Font("sansserif", Font.BOLD, 24));
            btnUmbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/reload.png")));
            btnUmbuchen.setEnabled(false);
            btnUmbuchen.setHorizontalAlignment(SwingConstants.LEADING);
            btnUmbuchen.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnUmbuchenItemStateChanged(e);
                }
            });
            pnlLower.add(btnUmbuchen, CC.xy(1, 5));
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(pnlCenter, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1096, Short.MAX_VALUE)
                        .addComponent(pnlLower, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1096, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlCenter, GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlLower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlCenter;
    private JPanel pnlLower;
    private JToggleButton btnWareneingang;
    private JPanel panel1;
    private JProgressBar pbMain;
    private JProgressBar pbHeap;
    private JLabel lblUsername;
    private JToggleButton btnSound;
    private JButton btnQuit;
    private JLabel lblClock;
    private JToggleButton btnWarenausgang;
    private JToggleButton btnUmbuchen;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    // Eigene Methoden
    private void setCurrentPanelTo(TouchPanel pnl) {
        if (currentPanel != null) {
            clearCenterPanel();
        }

        currentPanel = pnl;
        currentPanel.addPanelSwitchListener(psl);

        pnlCenter.add((JPanel) currentPanel);
        currentPanel.startAction();
        //((JPanel) currentPanel).validate();
        validate();
    }

    private void login() {
        lblUsername.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/greenled.png")));
        lblUsername.setText(Main.currentUser.getName() + ", " + Main.currentUser.getVorname());
        setButtons(true);
    }

    private void logout() {
        lblUsername.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/yellowled.png")));
        lblUsername.setText("Niemand angemeldet.");
        setButtons(false);
        bg1.clearSelection();
        setCurrentPanelTo(new DefaultTouchPanel());
    }

    private void setButtons(boolean enabled) {
        btnWareneingang.setEnabled(enabled);
        btnWarenausgang.setEnabled(enabled);
        btnUmbuchen.setEnabled(enabled);
    }

    private void clearCenterPanel() {
        if (currentPanel != null) {
            pnlCenter.remove((JPanel) currentPanel);
            currentPanel.cleanup();
            pnlCenter.repaint();
            currentPanel = null;
        }
    }
}
