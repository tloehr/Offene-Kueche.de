/*
 * Created by JFormDesigner on Tue Oct 14 15:21:24 CEST 2014
 */

package desktop.menu;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Customer;
import entity.Menuweek;
import entity.RecipeFeatureTools;
import entity.Recipefeature;
import org.joda.time.LocalDate;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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


    private void initPanel() {

        cmbFeature.setModel(Tools.newComboboxModel(RecipeFeatureTools.getAll()));

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
        add(mon, CC.xy(1, y + (h * 0)));
        add(tue, CC.xy(1, y + (h * 1)));
        add(wed, CC.xy(1, y + (h * 2)));
        add(thu, CC.xy(1, y + (h * 3)));
        add(fri, CC.xy(1, y + (h * 4)));
        add(sat, CC.xy(1, y + (h * 5)));
        add(sun, CC.xy(1, y + (h * 6)));


    }

    private void btnSaveActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Menuweek myMenuweek = em.merge(menuweek);
            em.lock(myMenuweek, LockModeType.OPTIMISTIC);

            entity.Menu monMenu = em.merge(mon.getMenu());
            em.lock(monMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setMon(monMenu);

            entity.Menu tueMenu = em.merge(tue.getMenu());
            em.lock(tueMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setTue(tueMenu);

            entity.Menu wedMenu = em.merge(wed.getMenu());
            em.lock(wedMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setWed(wedMenu);

            entity.Menu thuMenu = em.merge(thu.getMenu());
            em.lock(thuMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setThu(thuMenu);

            entity.Menu friMenu = em.merge(fri.getMenu());
            em.lock(friMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setFri(friMenu);

            entity.Menu satMenu = em.merge(sat.getMenu());
            em.lock(satMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setSat(satMenu);

            entity.Menu sunMenu = em.merge(sun.getMenu());
            em.lock(sunMenu, LockModeType.OPTIMISTIC);
            myMenuweek.setSun(sunMenu);

            myMenuweek.setRecipefeature(em.merge((Recipefeature) cmbFeature.getSelectedItem()));

            myMenuweek.getCustomers().clear();
            myMenuweek.getCustomers().addAll(lstCustomers.getSelectedValuesList());

            em.getTransaction().commit();
        } catch (Exception ex) {
            Main.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void btnAddCustomerActionPerformed(ActionEvent e) {
        adhjakd;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbFeature = new JComboBox<Recipefeature>();
        scrollPane1 = new JScrollPane();
        lstCustomers = new JList<Customer>();
        lblMon = new JLabel();
        lblTue = new JLabel();
        lblWed = new JLabel();
        lblThu = new JLabel();
        lblFri = new JLabel();
        lblSat = new JLabel();
        lblSun = new JLabel();
        panel2 = new JPanel();
        btnAddCustomer = new JButton();
        panel1 = new JPanel();
        btnSave = new JButton();
        btnRevert = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $lcgap, default",
            "15*(default, $lgap), fill:default:grow"));
        add(cmbFeature, CC.xywh(1, 1, 3, 1));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(lstCustomers);
        }
        add(scrollPane1, CC.xywh(3, 3, 1, 25));

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

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnAddCustomer ----
            btnAddCustomer.setText(null);
            btnAddCustomer.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/edit_add.png")));
            btnAddCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddCustomerActionPerformed(e);
                }
            });
            panel2.add(btnAddCustomer);
        }
        add(panel2, CC.xy(3, 29, CC.RIGHT, CC.DEFAULT));

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
        add(panel1, CC.xywh(1, 31, 3, 1, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox<Recipefeature> cmbFeature;
    private JScrollPane scrollPane1;
    private JList<Customer> lstCustomers;
    private JLabel lblMon;
    private JLabel lblTue;
    private JLabel lblWed;
    private JLabel lblThu;
    private JLabel lblFri;
    private JLabel lblSat;
    private JLabel lblSun;
    private JPanel panel2;
    private JButton btnAddCustomer;
    private JPanel panel1;
    private JButton btnSave;
    private JButton btnRevert;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
