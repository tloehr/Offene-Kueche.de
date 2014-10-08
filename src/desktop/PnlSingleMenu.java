/*
 * Created by JFormDesigner on Wed Oct 08 16:52:07 CEST 2014
 */

package desktop;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

/**
 * @author Torsten Löhr
 */
public class PnlSingleMenu extends JPanel {
    public PnlSingleMenu() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtMenu = new JTextField();
        scrollPane1 = new JScrollPane();
        list1 = new JList();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default",
            "2*(default, $lgap), default"));
        add(txtMenu, CC.xy(1, 1));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(list1);
        }
        add(scrollPane1, CC.xywh(3, 1, 1, 5));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField txtMenu;
    private JScrollPane scrollPane1;
    private JList list1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
