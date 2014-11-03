/*
 * Created by JFormDesigner on Mon Nov 03 13:13:38 CET 2014
 */

package tools;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Torsten Löhr
 */
public class PnlIngTradeType extends JPanel {
    //    private final Produkte product;

    public static final int SIZE_SMALL = 12;
    public static final int SIZE_MEDIUM = 18;
    public static final int SIZE_LARGE = 24;

    private IngTypes ingType;
    private final Closure changeEvent;
    private boolean tradeTypeEdit, ingTypeEdit;
    private final int size;

    public PnlIngTradeType(IngTypes ingType, Closure changeEvent, int size) {
        this.ingType = ingType;
        this.changeEvent = changeEvent;
        this.size = size;


        initComponents();
        initPanel();
    }

    private void initPanel() {
        ingTypeEdit = false;
        tradeTypeEdit = false;
        setTradeTypeEnabled(false);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                cmbUnit.setModel(new DefaultComboBoxModel(IngTypesTools.EINHEIT));
                cmbStorageType.setModel(new DefaultComboBoxModel(LagerTools.LAGERART));

                IngTypesTools.loadInto(cmbStoffart);
                WarengruppeTools.loadInto(cmbTradeType);
                cmbStoffart.setSelectedItem(ingType);

                Font font = new Font("SansSerif", Font.PLAIN, size);

                cmbStoffart.setFont(font);
                cmbTradeType.setFont(font);

                txtNewStoffart.setFont(font);
                txtNewWarengruppe.setFont(font);
                cmbUnit.setFont(font);
                cmbStorageType.setFont(font);

                if (size == SIZE_LARGE) {
                    btnAddStoffart.setIcon(Const.icon32add);
                    btnAddWarengruppe.setIcon(Const.icon32add);
                } else if (size == SIZE_MEDIUM) {
                    btnAddStoffart.setIcon(Const.icon24add);
                    btnAddWarengruppe.setIcon(Const.icon24add);
                }


            }
        });
    }

    private void cmbStoffartItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;

        ingType = (IngTypes) cmbStoffart.getSelectedItem();
        cmbTradeType.setSelectedItem(ingType.getWarengruppe());
//        cmbUnit.setSelectedIndex(ingType.getEinheit());
//        cmbStorageType.setSelectedIndex(ingType.getLagerart());
        changeEvent.execute(e.getItem());

    }

    private void btnAddStoffartActionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (pnlStoffart.getLayout());
        cl.show(pnlStoffart, "add");
        txtNewStoffart.setText("");
        txtNewStoffart.requestFocus();
        ingTypeEdit = true;
        setTradeTypeEnabled(true);
    }


    private void setTradeTypeEnabled(boolean enable) {
        cmbTradeType.setEnabled(enable);
        btnAddWarengruppe.setEnabled(enable);
        txtNewWarengruppe.setEnabled(enable);
        btnApplyWarengruppe.setEnabled(enable);
        btnCancelWarengruppe.setEnabled(enable);
    }


    private void cmbUnitItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        changeEvent.execute(e.getItem());
    }

    private void btnApplyStoffartActionPerformed(ActionEvent e) {
        if (tradeTypeEdit) {
            btnApplyWarengruppe.doClick();
        }

        if (!txtNewStoffart.getText().isEmpty() && cmbTradeType.getSelectedItem() != null) {
            IngTypes ingTypes = IngTypesTools.add(txtNewStoffart.getText(), (short) cmbUnit.getSelectedIndex(), (short) cmbStorageType.getSelectedIndex(), (Warengruppe) cmbTradeType.getSelectedItem());

            IngTypesTools.loadInto(cmbStoffart);
            cmbStoffart.setSelectedItem(ingTypes);
        }
        CardLayout cl = (CardLayout) (pnlStoffart.getLayout());
        cl.show(pnlStoffart, "select");
        ingTypeEdit = false;
    }

    private void btnCancelStoffartActionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (pnlStoffart.getLayout());
        cl.show(pnlStoffart, "select");
        ingTypeEdit = false;
        setTradeTypeEnabled(false);
    }


    private void btnAddWarengruppeActionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (pnlWarengruppe.getLayout());
        cl.show(pnlWarengruppe, "add");
        tradeTypeEdit = true;
        txtNewWarengruppe.setText("");
        txtNewWarengruppe.requestFocus();
    }


    private void btnApplyWarengruppeActionPerformed(ActionEvent e) {
        if (!txtNewWarengruppe.getText().isEmpty()) {
            Warengruppe tradeType = WarengruppeTools.add(txtNewWarengruppe.getText());
            WarengruppeTools.loadInto(cmbStorageType);
            cmbTradeType.setSelectedItem(tradeType);
        }
        CardLayout cl = (CardLayout) (pnlWarengruppe.getLayout());
        cl.show(pnlWarengruppe, "select");
        tradeTypeEdit = false;
        setTradeTypeEnabled(false);
    }

    private void btnCancelWarengruppeActionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (pnlWarengruppe.getLayout());
        cl.show(pnlWarengruppe, "select");
        tradeTypeEdit = false;
        setTradeTypeEnabled(ingTypeEdit);
    }

    private void cmbTradeTypeItemStateChanged(ItemEvent e) {
//        if (e.getStateChange() != ItemEvent.SELECTED) return;
//
//        Warengruppe tradeType = (Warengruppe) e.getItem();
//        ArrayList<IngTypes> list = new ArrayList<IngTypes>(tradeType.getIngTypesCollection());
//        Collections.sort(list);
//        cmbStoffart.setModel(Tools.newComboboxModel(list));

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        pnlStoffart = new JPanel();
        pnlSelStoffart = new JPanel();
        cmbStoffart = new JComboBox();
        btnAddStoffart = new JButton();
        pnlAddStoffart = new JPanel();
        txtNewStoffart = new JTextField();
        hSpacer1 = new JPanel(null);
        cmbUnit = new JComboBox();
        hSpacer2 = new JPanel(null);
        cmbStorageType = new JComboBox();
        hSpacer3 = new JPanel(null);
        btnApplyStoffart = new JButton();
        btnCancelStoffart = new JButton();
        pnlWarengruppe = new JPanel();
        pnlSelWarengruppe = new JPanel();
        cmbTradeType = new JComboBox();
        btnAddWarengruppe = new JButton();
        pnlAddWarengruppe = new JPanel();
        txtNewWarengruppe = new JTextField();
        hSpacer4 = new JPanel(null);
        btnApplyWarengruppe = new JButton();
        btnCancelWarengruppe = new JButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "default:grow",
                "default, $lgap, default"));

            //======== pnlStoffart ========
            {
                pnlStoffart.setLayout(new CardLayout());

                //======== pnlSelStoffart ========
                {
                    pnlSelStoffart.setLayout(new BoxLayout(pnlSelStoffart, BoxLayout.X_AXIS));

                    //---- cmbStoffart ----
                    cmbStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    cmbStoffart.setModel(new DefaultComboBoxModel(new String[] {
                        "item 1",
                        "item 2",
                        "item 3"
                    }));
                    cmbStoffart.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbStoffartItemStateChanged(e);
                        }
                    });
                    pnlSelStoffart.add(cmbStoffart);

                    //---- btnAddStoffart ----
                    btnAddStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    btnAddStoffart.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                    btnAddStoffart.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnAddStoffartActionPerformed(e);
                        }
                    });
                    pnlSelStoffart.add(btnAddStoffart);
                }
                pnlStoffart.add(pnlSelStoffart, "select");

                //======== pnlAddStoffart ========
                {
                    pnlAddStoffart.setLayout(new BoxLayout(pnlAddStoffart, BoxLayout.X_AXIS));

                    //---- txtNewStoffart ----
                    txtNewStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    txtNewStoffart.setText(" ");
                    pnlAddStoffart.add(txtNewStoffart);
                    pnlAddStoffart.add(hSpacer1);

                    //---- cmbUnit ----
                    cmbUnit.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    cmbUnit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbUnitItemStateChanged(e);
                        }
                    });
                    pnlAddStoffart.add(cmbUnit);
                    pnlAddStoffart.add(hSpacer2);

                    //---- cmbStorageType ----
                    cmbStorageType.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    pnlAddStoffart.add(cmbStorageType);
                    pnlAddStoffart.add(hSpacer3);

                    //---- btnApplyStoffart ----
                    btnApplyStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    btnApplyStoffart.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                    btnApplyStoffart.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnApplyStoffartActionPerformed(e);
                        }
                    });
                    pnlAddStoffart.add(btnApplyStoffart);

                    //---- btnCancelStoffart ----
                    btnCancelStoffart.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    btnCancelStoffart.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                    btnCancelStoffart.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnCancelStoffartActionPerformed(e);
                        }
                    });
                    pnlAddStoffart.add(btnCancelStoffart);
                }
                pnlStoffart.add(pnlAddStoffart, "add");
            }
            panel1.add(pnlStoffart, CC.xy(1, 1));

            //======== pnlWarengruppe ========
            {
                pnlWarengruppe.setEnabled(false);
                pnlWarengruppe.setLayout(new CardLayout());

                //======== pnlSelWarengruppe ========
                {
                    pnlSelWarengruppe.setLayout(new BoxLayout(pnlSelWarengruppe, BoxLayout.X_AXIS));

                    //---- cmbTradeType ----
                    cmbTradeType.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    cmbTradeType.setModel(new DefaultComboBoxModel(new String[] {
                        "item 1",
                        "item 2",
                        "item 3"
                    }));
                    cmbTradeType.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbTradeTypeItemStateChanged(e);
                        }
                    });
                    pnlSelWarengruppe.add(cmbTradeType);

                    //---- btnAddWarengruppe ----
                    btnAddWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    btnAddWarengruppe.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/edit_add.png")));
                    btnAddWarengruppe.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnAddWarengruppeActionPerformed(e);
                        }
                    });
                    pnlSelWarengruppe.add(btnAddWarengruppe);
                }
                pnlWarengruppe.add(pnlSelWarengruppe, "select");

                //======== pnlAddWarengruppe ========
                {
                    pnlAddWarengruppe.setLayout(new BoxLayout(pnlAddWarengruppe, BoxLayout.X_AXIS));

                    //---- txtNewWarengruppe ----
                    txtNewWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    txtNewWarengruppe.setText(" ");
                    pnlAddWarengruppe.add(txtNewWarengruppe);
                    pnlAddWarengruppe.add(hSpacer4);

                    //---- btnApplyWarengruppe ----
                    btnApplyWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    btnApplyWarengruppe.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ok.png")));
                    btnApplyWarengruppe.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnApplyWarengruppeActionPerformed(e);
                        }
                    });
                    pnlAddWarengruppe.add(btnApplyWarengruppe);

                    //---- btnCancelWarengruppe ----
                    btnCancelWarengruppe.setFont(new Font("SansSerif", Font.PLAIN, 24));
                    btnCancelWarengruppe.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/cancel.png")));
                    btnCancelWarengruppe.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnCancelWarengruppeActionPerformed(e);
                        }
                    });
                    pnlAddWarengruppe.add(btnCancelWarengruppe);
                }
                pnlWarengruppe.add(pnlAddWarengruppe, "add");
            }
            panel1.add(pnlWarengruppe, CC.xy(1, 3));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JPanel pnlStoffart;
    private JPanel pnlSelStoffart;
    private JComboBox cmbStoffart;
    private JButton btnAddStoffart;
    private JPanel pnlAddStoffart;
    private JTextField txtNewStoffart;
    private JPanel hSpacer1;
    private JComboBox cmbUnit;
    private JPanel hSpacer2;
    private JComboBox cmbStorageType;
    private JPanel hSpacer3;
    private JButton btnApplyStoffart;
    private JButton btnCancelStoffart;
    private JPanel pnlWarengruppe;
    private JPanel pnlSelWarengruppe;
    private JComboBox cmbTradeType;
    private JButton btnAddWarengruppe;
    private JPanel pnlAddWarengruppe;
    private JTextField txtNewWarengruppe;
    private JPanel hSpacer4;
    private JButton btnApplyWarengruppe;
    private JButton btnCancelWarengruppe;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
