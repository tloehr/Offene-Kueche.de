/*
 * Created by JFormDesigner on Tue Oct 14 15:21:24 CEST 2014
 */

package desktop.menu;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Menuweek;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

/**
 * @author Torsten Löhr
 */
public class PnlMenuWeek extends JPanel {


    private final Menuweek menuweek;
    private PnlSingleDayMenu mon, tue, wed, thu, fri, sat, sun;
    SimpleDateFormat sdf;

    private static final String format = "EEEE, d MMM yyyy";

    public PnlMenuWeek(Menuweek menuweek) {
        this.menuweek = menuweek;
        initComponents();

        initPanel();


    }


    private void initPanel(){

        sdf = new SimpleDateFormat(format);

        mon = new PnlSingleDayMenu(menuweek.getMon(), new LocalDate(menuweek.getWeek()));
        tue = new PnlSingleDayMenu(menuweek.getTue(), new LocalDate(menuweek.getWeek()).plusDays(1));
        wed = new PnlSingleDayMenu(menuweek.getWed(), new LocalDate(menuweek.getWeek()).plusDays(2));
        thu = new PnlSingleDayMenu(menuweek.getThu(), new LocalDate(menuweek.getWeek()).plusDays(3));
        fri = new PnlSingleDayMenu(menuweek.getFri(), new LocalDate(menuweek.getWeek()).plusDays(4));
        sat = new PnlSingleDayMenu(menuweek.getSat(), new LocalDate(menuweek.getWeek()).plusDays(5));
        sun = new PnlSingleDayMenu(menuweek.getSun(), new LocalDate(menuweek.getWeek()).plusDays(6));

        lblMon.setText(sdf.format(mon.getMenu().getDate()));
        lblTue.setText(sdf.format(tue.getMenu().getDate()));
        lblWed.setText(sdf.format(wed.getMenu().getDate()));
        lblThu.setText(sdf.format(thu.getMenu().getDate()));
        lblFri.setText(sdf.format(fri.getMenu().getDate()));
        lblSat.setText(sdf.format(sat.getMenu().getDate()));
        lblSun.setText(sdf.format(sun.getMenu().getDate()));

        int y = 5;
        int h = 4;
        add(mon, CC.xy(1, y+(h*0)));
        add(tue, CC.xy(1, y+(h*1)));
        add(wed, CC.xy(1, y+(h*2)));
        add(thu, CC.xy(1, y+(h*3)));
        add(fri, CC.xy(1, y+(h*4)));
        add(sat, CC.xy(1, y+(h*5)));
        add(sun, CC.xy(1, y+(h*6)));



    }

    private void btnSaveActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbFeature = new JComboBox();
        lblMon = new JLabel();
        lblTue = new JLabel();
        lblWed = new JLabel();
        lblThu = new JLabel();
        lblFri = new JLabel();
        lblSat = new JLabel();
        lblSun = new JLabel();
        panel1 = new JPanel();
        btnSave = new JButton();
        btnRevert = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow",
                "15*(default, $lgap), fill:default:grow"));
        add(cmbFeature, CC.xy(1, 1));

        //---- lblMon ----
        lblMon.setText("text");
        lblMon.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblMon, CC.xy(1, 3, CC.CENTER, CC.DEFAULT));

        //---- lblTue ----
        lblTue.setText("text");
        lblTue.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblTue, CC.xy(1, 7, CC.CENTER, CC.DEFAULT));

        //---- lblWed ----
        lblWed.setText("text");
        lblWed.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblWed, CC.xy(1, 11, CC.CENTER, CC.DEFAULT));

        //---- lblThu ----
        lblThu.setText("text");
        lblThu.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblThu, CC.xy(1, 15, CC.CENTER, CC.DEFAULT));

        //---- lblFri ----
        lblFri.setText("text");
        lblFri.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblFri, CC.xy(1, 19, CC.CENTER, CC.DEFAULT));

        //---- lblSat ----
        lblSat.setText("text");
        lblSat.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSat.setForeground(Color.red);
        add(lblSat, CC.xy(1, 23, CC.CENTER, CC.DEFAULT));

        //---- lblSun ----
        lblSun.setText("text");
        lblSun.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSun.setForeground(Color.red);
        add(lblSun, CC.xy(1, 27, CC.CENTER, CC.DEFAULT));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnSave ----
            btnSave.setText("save");
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel1.add(btnSave);

            //---- btnRevert ----
            btnRevert.setText("revert");
            panel1.add(btnRevert);
        }
        add(panel1, CC.xy(1, 31, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbFeature;
    private JLabel lblMon;
    private JLabel lblTue;
    private JLabel lblWed;
    private JLabel lblThu;
    private JLabel lblFri;
    private JLabel lblSat;
    private JLabel lblSun;
    private JPanel panel1;
    private JButton btnSave;
    private JButton btnRevert;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
