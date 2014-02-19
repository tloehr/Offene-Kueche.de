/*
 * Created by JFormDesigner on Wed Feb 09 16:13:46 CET 2011
 */

package desktop;

import entity.Mitarbeiter;
import org.apache.commons.collections.Closure;
import touch.PnlPIN;

import javax.swing.*;
import java.awt.*;

/**
 * @author Torsten Löhr
 */
public class DlgLogin extends JDialog {

    Mitarbeiter user;
    FrmDesktop frmDesktop;

    public Mitarbeiter getUser() {
        return user;
    }

    public DlgLogin(Frame owner, FrmDesktop parent, Closure loginAction) {
        super(owner, false);
        setLocationRelativeTo(parent);
        this.frmDesktop = parent;

        initComponents();

        setContentPane(new PnlPIN(loginAction));
        pack();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            tools.Tools.centerOnParent(frmDesktop, this);
        }
        super.setVisible(b);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Anmeldung");
        setModal(true);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
