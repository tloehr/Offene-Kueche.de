/*
 * Created by JFormDesigner on Fri Feb 07 15:36:07 CET 2014
 */

package touch;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Mitarbeiter;
import events.PanelSwitchEvent;
import events.PanelSwitchListener;
import org.apache.commons.collections.Closure;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

/**
 * @author Torsten Löhr
 */
public class PnlPIN extends DefaultTouchPanel {
    private final Closure loginAction;

    public PnlPIN(Closure loginAction) {
        this.loginAction = loginAction;
        initComponents();
        startAction();
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void addPanelSwitchListener(PanelSwitchListener listener) {
        super.addPanelSwitchListener(listener);
    }

    @Override
    public void removePanelSwitchListener(PanelSwitchListener listener) {
        super.removePanelSwitchListener(listener);
    }

    @Override
    public void startAction() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                passwordField1.requestFocus();
            }
        });
    }

    @Override
    public void firePanelSwitchEvent(PanelSwitchEvent evt) {
        super.firePanelSwitchEvent(evt);
    }

    @Override
    public void txtComponentFocusGained(FocusEvent e) {
        super.txtComponentFocusGained(e);
    }

    private void passwordField1CaretUpdate(CaretEvent e) {


    }

    private void btnExitActionPerformed(ActionEvent e) {
        Main.Main.getEMF().close();
        System.exit(0);
    }

    private void btnClearActionPerformed(ActionEvent e) {
        passwordField1.setText(null);
        passwordField1.requestFocus();
    }

    private void button0ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button03ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button02ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button01ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button06ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button05ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button04ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button09ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button08ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void button07ActionPerformed(ActionEvent e) {
        numButtonActionPerformed(e);
    }

    private void numButtonActionPerformed(ActionEvent e) {
        String num = ((JButton) e.getSource()).getText().trim();
        String currentPW = Tools.catchNull(new String(passwordField1.getPassword()));
        passwordField1.setText(currentPW + num);
        passwordField1.requestFocus();
    }

    private void passwordField1ActionPerformed(ActionEvent e) {

        if (passwordField1.getPassword().length < 4) return;

        EntityManager em = Main.Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT m FROM Mitarbeiter m WHERE m.pin = :pin");
        query.setParameter("pin", new String(passwordField1.getPassword()));

        try {
            Mitarbeiter user = (Mitarbeiter) query.getSingleResult();
            Main.Main.logger.info(user.getName() + ", " + user.getVorname() + " angemeldet.");

            loginAction.execute(user);

            //            user = (Mitarbeiter) query.getSingleResult();
            //            success = true;
            //            cardLogin = false;
            //            dispose();
        } catch (Exception e1) {
            Main.Main.logger.warn(e1.getMessage(), e1);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    passwordField1.setText(null);
                }
            });
            lblReply.setText("falsche PIN");
             passwordField1.requestFocus();
//            passwordField1.requestFocus();
            //            user = null;
            //            success = false;
            //            cardLogin = false;
        } finally {
            em.close();
        }
    }

    private void btnEnterActionPerformed(ActionEvent e) {
        passwordField1ActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        passwordField1 = new JPasswordField();
        btnClear = new JButton();
        lblReply = new JLabel();
        button01 = new JButton();
        button02 = new JButton();
        button03 = new JButton();
        button04 = new JButton();
        button05 = new JButton();
        button06 = new JButton();
        button07 = new JButton();
        button08 = new JButton();
        button09 = new JButton();
        btnExit = new JButton();
        button0 = new JButton();
        btnEnter = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $lcgap, 2*(87dlu, $ugap), 87dlu, $lcgap, default:grow",
            "default:grow, $lgap, 49dlu, $ugap, 34dlu, 4*($ugap, 82dlu), $lgap, default:grow"));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- passwordField1 ----
            passwordField1.setFont(new Font("Arial", Font.BOLD, 48));
            passwordField1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    passwordField1ActionPerformed(e);
                }
            });
            panel1.add(passwordField1);

            //---- btnClear ----
            btnClear.setText(null);
            btnClear.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/editclear.png")));
            btnClear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnClearActionPerformed(e);
                }
            });
            panel1.add(btnClear);
        }
        add(panel1, CC.xywh(3, 3, 5, 1, CC.FILL, CC.FILL));

        //---- lblReply ----
        lblReply.setText(null);
        lblReply.setFont(new Font("Arial", Font.PLAIN, 48));
        lblReply.setHorizontalAlignment(SwingConstants.CENTER);
        lblReply.setForeground(Color.red);
        add(lblReply, CC.xywh(3, 5, 5, 1));

        //---- button01 ----
        button01.setText("1");
        button01.setFont(new Font("Arial", button01.getFont().getStyle(), 62));
        button01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button01ActionPerformed(e);
            }
        });
        add(button01, CC.xy(3, 7, CC.FILL, CC.FILL));

        //---- button02 ----
        button02.setText("2");
        button02.setFont(new Font("Arial", button02.getFont().getStyle(), 62));
        button02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button02ActionPerformed(e);
            }
        });
        add(button02, CC.xy(5, 7, CC.FILL, CC.FILL));

        //---- button03 ----
        button03.setText("3");
        button03.setFont(new Font("Arial", button03.getFont().getStyle(), 62));
        button03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button03ActionPerformed(e);
            }
        });
        add(button03, CC.xy(7, 7, CC.FILL, CC.FILL));

        //---- button04 ----
        button04.setText("4");
        button04.setFont(new Font("Arial", button04.getFont().getStyle(), 62));
        button04.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button04ActionPerformed(e);
            }
        });
        add(button04, CC.xy(3, 9, CC.FILL, CC.FILL));

        //---- button05 ----
        button05.setText("5");
        button05.setFont(new Font("Arial", button05.getFont().getStyle(), 62));
        button05.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button05ActionPerformed(e);
            }
        });
        add(button05, CC.xy(5, 9, CC.FILL, CC.FILL));

        //---- button06 ----
        button06.setText("6");
        button06.setFont(new Font("Arial", button06.getFont().getStyle(), 62));
        button06.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button06ActionPerformed(e);
            }
        });
        add(button06, CC.xy(7, 9, CC.FILL, CC.FILL));

        //---- button07 ----
        button07.setText("7");
        button07.setFont(new Font("Arial", button07.getFont().getStyle(), 62));
        button07.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button07ActionPerformed(e);
            }
        });
        add(button07, CC.xy(3, 11, CC.FILL, CC.FILL));

        //---- button08 ----
        button08.setText("8");
        button08.setFont(new Font("Arial", button08.getFont().getStyle(), 62));
        button08.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button08ActionPerformed(e);
            }
        });
        add(button08, CC.xy(5, 11, CC.FILL, CC.FILL));

        //---- button09 ----
        button09.setText("9");
        button09.setFont(new Font("Arial", button09.getFont().getStyle(), 62));
        button09.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button09ActionPerformed(e);
            }
        });
        add(button09, CC.xy(7, 11, CC.FILL, CC.FILL));

        //---- btnExit ----
        btnExit.setText(null);
        btnExit.setFont(new Font("Arial", Font.PLAIN, 24));
        btnExit.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/exit.png")));
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExitActionPerformed(e);
            }
        });
        add(btnExit, CC.xy(3, 13, CC.FILL, CC.FILL));

        //---- button0 ----
        button0.setText("0");
        button0.setFont(new Font("Arial", button0.getFont().getStyle(), 62));
        button0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button0ActionPerformed(e);
            }
        });
        add(button0, CC.xy(5, 13, CC.FILL, CC.FILL));

        //---- btnEnter ----
        btnEnter.setText(null);
        btnEnter.setFont(new Font("Arial", Font.PLAIN, 24));
        btnEnter.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/newline.png")));
        btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEnterActionPerformed(e);
            }
        });
        add(btnEnter, CC.xy(7, 13, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JPasswordField passwordField1;
    private JButton btnClear;
    private JLabel lblReply;
    private JButton button01;
    private JButton button02;
    private JButton button03;
    private JButton button04;
    private JButton button05;
    private JButton button06;
    private JButton button07;
    private JButton button08;
    private JButton button09;
    private JButton btnExit;
    private JButton button0;
    private JButton btnEnter;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
