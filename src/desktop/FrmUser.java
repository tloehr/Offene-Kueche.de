/*
 * Created by JFormDesigner on Sat Feb 19 14:46:37 CET 2011
 */

package desktop;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Mitarbeiter;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Torsten Löhr
 */
public class FrmUser extends JInternalFrame {
    private final int MODE_EDIT = 0;
    private final int MODE_PASSWORD = 1;
    private final int MODE_BROWSE = 2;
    private final int MODE_NEW = 3;
    private final int MODE_USER_SELECTED = 4;


    //    private CardMonitor cardmonitor;
//    private CardStateListener csl;
//    private long cardID;
    private boolean userMode;
    //    private Card card;
    private int formMode;
    private Mitarbeiter currentMA;

    public FrmUser() {
        initComponents();
//        btnCard.setEnabled(false);
        setUserList();
        setTitle(tools.Tools.getWindowTitle("Benutzerverwaltung"));
//        cardID = -1;
        userMode = false;
        currentMA = null;

//        cardmonitor = ((FrmDesktop) Main.mainframe).getCardmonitor();

//        csl = new CardStateListener() {
//            @Override
//            public void cardStateChanged(CardStateChangedEvent evt) {
//                cardID = evt.getCardID();
//                userMode = evt.isUserMode();
//                card = evt.getCard();
//                btnCard.setEnabled(formMode == MODE_USER_SELECTED && cardID < Long.MAX_VALUE);
//            }
//        };
//        cardmonitor.addCardEventListener(csl);

        pack();
    }




    private void setUserList() {
        Query query = null;

        EntityManager em = Main.getEMF().createEntityManager();
        if (cbArchiv.isSelected()) {
            query = em.createQuery("SELECT m FROM Mitarbeiter m ORDER BY m.name, m.vorname");
        } else {
            query = em.createQuery("SELECT m FROM Mitarbeiter m WHERE (m.md5Key IS NOT null OR m.pin IS NOT null) ORDER BY m.name, m.vorname");
        }

        try {
            java.util.List<Mitarbeiter> ma = query.getResultList();
            listUser.setModel(Tools.newListModel(ma));
        } catch (Exception e1) { // nicht gefunden
            listUser.setModel(Tools.newListModel(null));
        } finally {
            em.close();
        }
        setFormMode(MODE_BROWSE);
    }

    private void cbArchivItemStateChanged(ItemEvent e) {
        setUserList();
    }

    private void listUserValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (listUser.getSelectedValue() != null) {
                currentMA = (Mitarbeiter) listUser.getSelectedValue();
                setFormMode(MODE_USER_SELECTED);
                txtNachname.setText(currentMA.getName());
                txtVorname.setText(currentMA.getVorname());
                txtUsername.setText(currentMA.getUsername());
                cbIsAdmin.setSelected(currentMA.isAdmin());
            } else {
                setFormMode(MODE_BROWSE);
                currentMA = null;
                txtNachname.setText("");
                txtVorname.setText("");
                txtUsername.setText("");
                cbIsAdmin.setSelected(false);
            }
//            btnCard.setEnabled(formMode == MODE_USER_SELECTED && cardID < Long.MAX_VALUE);
        }
    }

    private void setFormMode(int mode) {
        formMode = mode;
        listUser.setEnabled(formMode == MODE_BROWSE || formMode == MODE_USER_SELECTED);
        cbArchiv.setEnabled(formMode == MODE_BROWSE || formMode == MODE_USER_SELECTED);
        btnAdd.setEnabled(formMode == MODE_BROWSE || formMode == MODE_USER_SELECTED);
        btnEdit.setEnabled(formMode == MODE_USER_SELECTED);
        //btnCard.setEnabled();  nicht hier
        btnPassword.setEnabled(formMode == MODE_USER_SELECTED);
        btnLockAccount.setEnabled(formMode == MODE_USER_SELECTED);

        btnCancel.setEnabled(formMode != MODE_BROWSE && formMode != MODE_USER_SELECTED);
        btnSave.setEnabled(formMode != MODE_BROWSE && formMode != MODE_USER_SELECTED);

        setDataEnabled(formMode == MODE_EDIT || formMode == MODE_NEW);
//        setPasswordEnabled(formMode == MODE_PASSWORD);

    }

    private void setDataEnabled(boolean enabled) {
        txtNachname.setEnabled(enabled);
        txtUsername.setEnabled(enabled);
        txtVorname.setEnabled(enabled);
        cbIsAdmin.setEnabled(enabled);
    }

//    private void setPasswordEnabled(boolean visible) {
//        txtPassword.setVisible(visible);
//        lblPassword.setVisible(visible);
//        txtPassword.setEnabled(visible);
//    }

    private void btnEditActionPerformed(ActionEvent e) {
        setFormMode(MODE_EDIT);
        txtUsername.requestFocus();
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        setUserList();
    }

    private void thisInternalFrameClosing(InternalFrameEvent e) {
//        cardmonitor.removeCardEventListener(csl);
    }

    private void btnAddActionPerformed(ActionEvent e) {
        setFormMode(MODE_NEW);
        txtNachname.setText("");
        txtVorname.setText("");
        txtUsername.setText("");
        cbIsAdmin.setSelected(false);
        txtUsername.requestFocus();
    }

    private void btnPasswordActionPerformed(ActionEvent e) {
        String pattern= "[0-9]{4}";
        String newPin = JOptionPane.showInputDialog(Main.mainframe, "Bitte eine neue PIN eingeben", "PIN setzen", JOptionPane.OK_CANCEL_OPTION);

        if (!newPin.matches(pattern)){
            JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                                                "Die PIN muss aus genau 4 Zahlen bestehen.",
                                                "Problem",
                                                JOptionPane.ERROR_MESSAGE);
            return;
        }

        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Mitarbeiter myMA = em.merge(currentMA);
            myMA.setMd5Key(null);

            Query query = em.createQuery("SELECT m FROM Mitarbeiter m WHERE m.pin = :pin");
            int pin = Integer.parseInt(newPin);
            query.setParameter("pin", Integer.toString(pin));

            if (!query.getResultList().isEmpty()){
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                                    "Diese PIN ist schon vergeben.",
                                    "Problem",
                                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            myMA.setPin(Integer.toString(pin));

            em.getTransaction().commit();

            currentMA = myMA;

            JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                    "Die neue PIN lautet: " + pin,
                    "Neue PIN",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e1) {
            // Pech
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void btnLockAccountActionPerformed(ActionEvent e) {
        if (JOptionPane.showInternalConfirmDialog(this.getDesktopPane(),
                "Soll diese Kennung dauerhaft für den Zugriff gesperrt werden ?",
                "Benutzerkonto sperren",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            EntityManager em = Main.getEMF().createEntityManager();
            try {
                em.getTransaction().begin();
                currentMA.setMd5Key(null);
                currentMA.setPin(null);
                currentMA.setAdmin(Boolean.FALSE);
                em.merge(currentMA);
                em.getTransaction().commit();
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                        "Kennung gesperrt.",
                        "Benutzerkonto sperren",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                // Pech
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
            setUserList();
        }
    }

    private void btnSaveActionPerformed(ActionEvent e) {

//        if (formMode == MODE_NEW || formMode == MODE_EDIT) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT m FROM Mitarbeiter m WHERE m.username = :username");
        query.setParameter("username", txtUsername.getText());
        java.util.List<Mitarbeiter> ma = query.getResultList();
        em.close();

        if (ma.size() == 0 || formMode == MODE_NEW || (ma.size() == 1 && ma.get(0).equals(currentMA))) {
            if (txtUsername.getText().equals("")) {
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                        "Der Benutzername darf nicht leer sein, bitte ändern.",
                        "Daten ändern",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (txtNachname.getText().equals("")) {
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                        "Der Nachname darf nicht leer sein, bitte ändern.",
                        "Daten ändern",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (txtVorname.getText().equals("")) {
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                        "Der Vorname darf nicht leer sein, bitte ändern.",
                        "Daten ändern",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (formMode == MODE_NEW) {
                    currentMA = new Mitarbeiter();
                }
                currentMA.setUsername(txtUsername.getText());
                currentMA.setName(txtNachname.getText());
                currentMA.setVorname(txtVorname.getText());
                currentMA.setAdmin(new Boolean(cbIsAdmin.isSelected()));

                em = Main.getEMF().createEntityManager();
                try {
                    em.getTransaction().begin();
                    if (formMode == MODE_NEW) {
                        em.persist(currentMA);
                    } else {
                        em.merge(currentMA);
                    }

                    em.getTransaction().commit();
                    cbArchiv.setSelected(true);
                } catch (Exception e2) {
                    // Pech
                    em.getTransaction().rollback();
                } finally {
                    em.close();
                }
            }
        } else {
            JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                    "Der Benutzername ist schon vergeben, bitte ändern.",
                    "Daten ändern",
                    JOptionPane.INFORMATION_MESSAGE);
        }

//        } else { // Password
//            if (!txtPassword.getText().equals("")) {
//                currentMA.setMd5Key(Tools.hashword(txtPassword.getText()));
//                EntityManager em = Main.getEMF().createEntityManager();
//                try {
//                    em.getTransaction().begin();
//                    em.merge(currentMA);
//                    em.getTransaction().commit();
//                    cbArchiv.setSelected(true);
//                } catch (Exception e2) {
//                    // Pech
//                    Main.logger.debug(e2.getMessage(), e2);
//                    em.getTransaction().rollback();
//                } finally {
//                    em.close();
//                }
//            } else {
//                JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
//                        "Das Passwort darf nicht leer sein.",
//                        "Passwort setzen",
//                        JOptionPane.INFORMATION_MESSAGE);
//            }
//        }
        //cbArchiv.setSelected(true);
        //setUserList();
    }

    private void txtUsernameCaretUpdate(CaretEvent e) {

    }

    private void btnPinActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Mitarbeiter myMA = em.merge(currentMA);
            myMA.setMd5Key(null);

            Query query = em.createQuery("SELECT m FROM Mitarbeiter m WHERE m.pin = :pin");
            int pin = 0;
            do {
                pin = (int) Math.floor(Math.random() * 9000) + 1000;
                query.setParameter("pin", Integer.toString(pin));
            } while (!query.getResultList().isEmpty());

            myMA.setPin(Integer.toString(pin));

            em.getTransaction().commit();

            currentMA = myMA;

            JOptionPane.showInternalMessageDialog(this.getDesktopPane(),
                    "Die neue PIN lautet: " + pin,
                    "Neue PIN",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e1) {
            // Pech
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        txtUsername = new JTextField();
        txtVorname = new JTextField();
        txtNachname = new JTextField();
        panel2 = new JPanel();
        btnPassword = new JButton();
        btnSave = new JButton();
        btnLockAccount = new JButton();
        btnCancel = new JButton();
        btnAdd = new JButton();
        btnEdit = new JButton();
        btnPin = new JButton();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        cbIsAdmin = new JCheckBox();
        scrollPane1 = new JScrollPane();
        listUser = new JList();
        cbArchiv = new JCheckBox();

        //======== this ========
        setVisible(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setClosable(true);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                thisInternalFrameClosing(e);
            }
        });
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //---- txtUsername ----
            txtUsername.setFont(new Font("sansserif", Font.PLAIN, 18));
            txtUsername.setEnabled(false);

            //---- txtVorname ----
            txtVorname.setFont(new Font("sansserif", Font.PLAIN, 18));
            txtVorname.setEnabled(false);

            //---- txtNachname ----
            txtNachname.setFont(new Font("sansserif", Font.PLAIN, 18));
            txtNachname.setEnabled(false);

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                    "5*(default, $lcgap), default:grow, 2*($lcgap, default)",
                    "fill:default, $lgap, default"));

                //---- btnPassword ----
                btnPassword.setFont(new Font("sansserif", Font.PLAIN, 18));
                btnPassword.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/password.png")));
                btnPassword.setToolTipText("Passwort setzen");
                btnPassword.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPasswordActionPerformed(e);
                    }
                });
                panel2.add(btnPassword, CC.xy(5, 1));

                //---- btnSave ----
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                btnSave.setToolTipText("Eingabe sichern");
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSaveActionPerformed(e);
                    }
                });
                panel2.add(btnSave, CC.xy(15, 1));

                //---- btnLockAccount ----
                btnLockAccount.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/lock.png")));
                btnLockAccount.setToolTipText("Kennung sperren");
                btnLockAccount.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnLockAccountActionPerformed(e);
                    }
                });
                panel2.add(btnLockAccount, CC.xy(9, 1));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                btnCancel.setToolTipText("Eingabe abbrechen");
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                panel2.add(btnCancel, CC.xy(13, 1));

                //---- btnAdd ----
                btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                btnAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddActionPerformed(e);
                    }
                });
                panel2.add(btnAdd, CC.xy(1, 1));

                //---- btnEdit ----
                btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit.png")));
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnEditActionPerformed(e);
                    }
                });
                panel2.add(btnEdit, CC.xy(3, 1));

                //---- btnPin ----
                btnPin.setFont(new Font("sansserif", Font.PLAIN, 18));
                btnPin.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/keypad.png")));
                btnPin.setToolTipText("Passwort setzen");
                btnPin.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPinActionPerformed(e);
                    }
                });
                panel2.add(btnPin, CC.xy(7, 1));
            }

            //---- label1 ----
            label1.setText("Benutzername");
            label1.setFont(new Font("sansserif", Font.PLAIN, 18));
            label1.setLabelFor(txtUsername);

            //---- label2 ----
            label2.setText("Vorname");
            label2.setFont(new Font("sansserif", Font.PLAIN, 18));
            label2.setLabelFor(txtVorname);

            //---- label3 ----
            label3.setText("Nachname");
            label3.setFont(new Font("sansserif", Font.PLAIN, 18));
            label3.setLabelFor(txtNachname);

            //---- cbIsAdmin ----
            cbIsAdmin.setText("Vollzugriff");
            cbIsAdmin.setFont(new Font("sansserif", Font.PLAIN, 18));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(cbIsAdmin, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(label2)
                                    .addComponent(label1)
                                    .addComponent(label3))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(txtNachname, GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                                    .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                                    .addComponent(txtVorname, GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)))
                            .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVorname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNachname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbIsAdmin)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 240, Short.MAX_VALUE)
                        .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            );
        }

        //======== scrollPane1 ========
        {

            //---- listUser ----
            listUser.setFont(new Font("sansserif", Font.PLAIN, 18));
            listUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listUser.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    listUserValueChanged(e);
                }
            });
            scrollPane1.setViewportView(listUser);
        }

        //---- cbArchiv ----
        cbArchiv.setText("Auch Ehemalige anzeigen");
        cbArchiv.setFont(new Font("sansserif", Font.PLAIN, 18));
        cbArchiv.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cbArchivItemStateChanged(e);
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(cbArchiv)
                        .addComponent(scrollPane1))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbArchiv)))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JTextField txtUsername;
    private JTextField txtVorname;
    private JTextField txtNachname;
    private JPanel panel2;
    private JButton btnPassword;
    private JButton btnSave;
    private JButton btnLockAccount;
    private JButton btnCancel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnPin;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JCheckBox cbIsAdmin;
    private JScrollPane scrollPane1;
    private JList listUser;
    private JCheckBox cbArchiv;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
