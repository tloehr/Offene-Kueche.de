/*
 * Created by JFormDesigner on Wed Feb 09 16:13:46 CET 2011
 */

package desktop;

import entity.Mitarbeiter;
import threads.CardMonitor;
import threads.CardStateChangedEvent;
import threads.CardStateListener;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Torsten Löhr
 */
public class DlgLogin extends JDialog {

    CardMonitor cm;
    CardStateListener csl;
    Mitarbeiter user;
    protected boolean success = false;
    protected boolean cardLogin = false;
    FrmDesktop frmDesktop;

    public boolean isCardLogin() {
        return cardLogin;
    }

    public boolean isSuccess() {
        return success;
    }

    public Mitarbeiter getUser() {
        return user;
    }

    public DlgLogin(Frame owner, FrmDesktop parent) {
        super(owner, false);
        setLocationRelativeTo(parent);
        this.frmDesktop = parent;

        csl = new CardStateListener() {

            @Override
            public void cardStateChanged(CardStateChangedEvent evt) {
                if (evt.isCardPresent() && evt.isUserMode() && evt.getCardID() < Long.MAX_VALUE) {
                    Query query = Main.Main.getEM().createNamedQuery("Mitarbeiter.findByCardID");
                    query.setParameter("cardId", evt.getCardID());
                    try {
                        user = (Mitarbeiter) query.getSingleResult();
                        Main.Main.logger.info(user.getName() + ", " + user.getVorname() + " angemeldet.");
                        success = true;
                        cardLogin = true;
                        dispose();
                    } catch (Exception e) {
                        Main.Main.logger.warn(e.getMessage(), e);
                        user = null;
                        success = false;
                        cardLogin = false;
                    }
                }

            }
        };

        initComponents();

        cm = frmDesktop.getCardmonitor();
        cm.addCardEventListener(csl);
        if (cm.getTerminal() != null) {
            lblTerminal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/greenled.png")));
            lblTerminal.setText("Terminal: " + cm.getTerminal().getName());
        } else {
            lblTerminal.setText("Kein PC/SC Kartenterminal gefunden");
        }

        if (Main.Main.props.containsKey("benutzer")){
            txtUsername.setText(Main.Main.props.getProperty("benutzer"));
        }

        if (Main.Main.props.containsKey("passwort")){
            txtPasswort.setText(Main.Main.props.getProperty("passwort"));
        }


    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            tools.Tools.centerOnParent(frmDesktop, this);
        }
        super.setVisible(b);
    }

    @Override
    public void dispose() {
        cm.removeCardEventListener(csl);
        super.dispose();
    }

    public DlgLogin(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void formWindowClosing(WindowEvent e) {
        cm.removeCardEventListener(csl);
    }

    private void txtUsernameActionPerformed(ActionEvent e) {
        txtPasswort.requestFocus();
    }

    private void btnLoginActionPerformed(ActionEvent e) {
        String username = txtUsername.getText().trim();
        char[] password = txtPasswort.getPassword();

        Query query = Main.Main.getEM().createNamedQuery("Mitarbeiter.findForLogin");
        query.setParameter("username", username);
        query.setParameter("mD5Key", tools.Tools.hashword(new String(password)));

        try {
            user = (Mitarbeiter) query.getSingleResult();
            Main.Main.logger.info(user.getName() + ", " + user.getVorname() + " angemeldet.");
            user = (Mitarbeiter) query.getSingleResult();
            success = true;
            cardLogin = false;
            dispose();
        } catch (Exception e1) {
            Main.Main.logger.warn(e1.getMessage(), e1);
            lblReply.setText("Benutzername oder Passwort falsch.");
            user = null;
            success = false;
            cardLogin = false;
        }
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        dispose();
    }

    private void txtPasswortActionPerformed(ActionEvent e) {
         btnLogin.doClick();
    }

    private void txtUsernameFocusGained(FocusEvent e) {
        txtUsername.selectAll();
    }

    private void txtPasswortFocusGained(FocusEvent e) {
        txtPasswort.selectAll();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jLabel1 = new JLabel();
        txtUsername = new JTextField();
        jLabel2 = new JLabel();
        btnLogin = new JButton();
        lblTerminal = new JLabel();
        btnCancel = new JButton();
        lblReply = new JLabel();
        txtPasswort = new JPasswordField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Anmeldung");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();

        //---- jLabel1 ----
        jLabel1.setText("Benutzername");
        jLabel1.setFont(new Font("sansserif", Font.PLAIN, 18));

        //---- txtUsername ----
        txtUsername.setFont(new Font("sansserif", Font.PLAIN, 18));
        txtUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsernameActionPerformed(e);
            }
        });
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtUsernameFocusGained(e);
            }
        });

        //---- jLabel2 ----
        jLabel2.setText("Passwort");
        jLabel2.setFont(new Font("sansserif", Font.PLAIN, 18));

        //---- btnLogin ----
        btnLogin.setText("Anmelden");
        btnLogin.setFont(new Font("sansserif", Font.PLAIN, 18));
        btnLogin.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/ok.png")));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLoginActionPerformed(e);
            }
        });

        //---- lblTerminal ----
        lblTerminal.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
        lblTerminal.setText("Kartenterminal");
        lblTerminal.setFont(new Font("sansserif", Font.PLAIN, 18));

        //---- btnCancel ----
        btnCancel.setText("Beenden");
        btnCancel.setFont(new Font("sansserif", Font.PLAIN, 18));
        btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/exit.png")));
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });

        //---- lblReply ----
        lblReply.setForeground(Color.red);
        lblReply.setText(" ");
        lblReply.setFont(new Font("sansserif", Font.PLAIN, 18));

        //---- txtPasswort ----
        txtPasswort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPasswortActionPerformed(e);
            }
        });
        txtPasswort.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPasswortFocusGained(e);
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(lblReply, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                        .addComponent(lblTerminal, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jLabel1)
                                .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(txtPasswort, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                                .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(btnCancel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnLogin)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPasswort, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblTerminal)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblReply)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLogin)
                        .addComponent(btnCancel))
                    .addContainerGap(8, Short.MAX_VALUE))
        );
        contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {txtPasswort, txtUsername});
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel jLabel1;
    private JTextField txtUsername;
    private JLabel jLabel2;
    private JButton btnLogin;
    private JLabel lblTerminal;
    private JButton btnCancel;
    private JLabel lblReply;
    private JPasswordField txtPasswort;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
