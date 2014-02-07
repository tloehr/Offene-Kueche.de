/*
 * Created by JFormDesigner on Fri Feb 07 15:36:07 CET 2014
 */

package touch;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Mitarbeiter;
import events.PanelSwitchEvent;
import events.PanelSwitchListener;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.FocusEvent;

/**
 * @author Torsten Löhr
 */
public class PnlPIN extends DefaultTouchPanel {
    public PnlPIN() {
        initComponents();
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
        super.startAction();
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

        if (passwordField1.getPassword().length < 4) return;

        char[] password = passwordField1.getPassword();

        EntityManager em = Main.Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT m FROM Mitarbeiter m WHERE m.pin = :pin");
        query.setParameter("pin", tools.Tools.hashword(new String(password)));

        try {
            Mitarbeiter user = (Mitarbeiter) query.getSingleResult();
            Main.Main.logger.info(user.getName() + ", " + user.getVorname() + " angemeldet.");
//            user = (Mitarbeiter) query.getSingleResult();
//            success = true;
//            cardLogin = false;
//            dispose();
        } catch (Exception e1) {
            Main.Main.logger.warn(e1.getMessage(), e1);
            lblReply.setText("Benutzername oder Passwort falsch.");
//            user = null;
//            success = false;
//            cardLogin = false;
        } finally {
            em.close();
        }



    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        passwordField1 = new JPasswordField();
        btnClear = new JButton();
        lblReply = new JLabel();
        button07 = new JButton();
        button08 = new JButton();
        button09 = new JButton();
        button04 = new JButton();
        button05 = new JButton();
        button06 = new JButton();
        button01 = new JButton();
        button02 = new JButton();
        button03 = new JButton();
        button0 = new JButton();
        btnEnter = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, 2*(default:grow, $ugap), default:grow, $lcgap, default",
            "default, $lgap, 2*(default, $ugap), 3*(default:grow, $ugap), default:grow, $lgap, default"));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- passwordField1 ----
            passwordField1.setFont(new Font("Arial", Font.BOLD, 48));
            passwordField1.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    passwordField1CaretUpdate(e);
                }
            });
            panel1.add(passwordField1);

            //---- btnClear ----
            btnClear.setText(null);
            btnClear.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/editclear.png")));
            panel1.add(btnClear);
        }
        add(panel1, CC.xywh(3, 3, 5, 1, CC.FILL, CC.FILL));

        //---- lblReply ----
        lblReply.setText("--");
        lblReply.setFont(new Font("Arial", Font.PLAIN, 48));
        lblReply.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblReply, CC.xywh(3, 5, 5, 1));

        //---- button07 ----
        button07.setText("7");
        button07.setFont(new Font("Arial", button07.getFont().getStyle(), 62));
        add(button07, CC.xy(3, 7, CC.FILL, CC.FILL));

        //---- button08 ----
        button08.setText("8");
        button08.setFont(new Font("Arial", button08.getFont().getStyle(), 62));
        add(button08, CC.xy(5, 7, CC.FILL, CC.FILL));

        //---- button09 ----
        button09.setText("9");
        button09.setFont(new Font("Arial", button09.getFont().getStyle(), 62));
        add(button09, CC.xy(7, 7, CC.FILL, CC.FILL));

        //---- button04 ----
        button04.setText("4");
        button04.setFont(new Font("Arial", button04.getFont().getStyle(), 62));
        add(button04, CC.xy(3, 9, CC.FILL, CC.FILL));

        //---- button05 ----
        button05.setText("5");
        button05.setFont(new Font("Arial", button05.getFont().getStyle(), 62));
        add(button05, CC.xy(5, 9, CC.FILL, CC.FILL));

        //---- button06 ----
        button06.setText("6");
        button06.setFont(new Font("Arial", button06.getFont().getStyle(), 62));
        add(button06, CC.xy(7, 9, CC.FILL, CC.FILL));

        //---- button01 ----
        button01.setText("1");
        button01.setFont(new Font("Arial", button01.getFont().getStyle(), 62));
        add(button01, CC.xy(3, 11, CC.FILL, CC.FILL));

        //---- button02 ----
        button02.setText("2");
        button02.setFont(new Font("Arial", button02.getFont().getStyle(), 62));
        add(button02, CC.xy(5, 11, CC.FILL, CC.FILL));

        //---- button03 ----
        button03.setText("3");
        button03.setFont(new Font("Arial", button03.getFont().getStyle(), 62));
        add(button03, CC.xy(7, 11, CC.FILL, CC.FILL));

        //---- button0 ----
        button0.setText("0");
        button0.setFont(new Font("Arial", button0.getFont().getStyle(), 62));
        add(button0, CC.xy(3, 13, CC.FILL, CC.FILL));

        //---- btnEnter ----
        btnEnter.setText(null);
        btnEnter.setFont(new Font("Arial", Font.PLAIN, 24));
        btnEnter.setIcon(new ImageIcon(getClass().getResource("/artwork/128x128/key_enter_128.png")));
        add(btnEnter, CC.xywh(5, 13, 3, 1, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JPasswordField passwordField1;
    private JButton btnClear;
    private JLabel lblReply;
    private JButton button07;
    private JButton button08;
    private JButton button09;
    private JButton button04;
    private JButton button05;
    private JButton button06;
    private JButton button01;
    private JButton button02;
    private JButton button03;
    private JButton button0;
    private JButton btnEnter;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
