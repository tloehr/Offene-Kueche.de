/*
 * Created by JFormDesigner on Tue Sep 23 15:48:16 CEST 2014
 */

package desktop.products;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Additives;
import entity.AdditivesTools;
import entity.Allergene;
import entity.AllergeneTools;
import tools.Tools;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Torsten Löhr
 */
public class PnlAssignAdditives extends JDialog {

    private HashSet<Allergene> assignedAllergenes;
    private HashSet<Additives> assignedAdditives;
    private final ArrayList all;
    private int response;

    public PnlAssignAdditives(JFrame owner, Set<Additives> assignedAdditives, Set<Allergene> assignedAllergenes) {
        super(owner, true);
        this.assignedAdditives = new HashSet<Additives>(assignedAdditives);
        this.assignedAllergenes = new HashSet<Allergene>(assignedAllergenes);
        this.all = AllergeneTools.getAll();
        this.all.addAll(AdditivesTools.getAll());
        response = JOptionPane.CANCEL_OPTION;

        initComponents();
        initPanel();
    }


    private void initPanel() {

        setListAll();

        listAdditives.setModel(Tools.newListModel(new ArrayList<Additives>(assignedAdditives)));
        listAllergenes.setModel(Tools.newListModel(new ArrayList<Allergene>(assignedAllergenes)));

//        listAll.setCellRenderer(new DefaultListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                return new DefaultListCellRenderer().getListCellRendererComponent(list, (value instanceof Additives ? "Z." : "A.") + value.toString(), index, isSelected, cellHasFocus);
//            }
//        });

        listAdditives.setCellRenderer(AdditivesTools.getListCellRenderer());
        listAllergenes.setCellRenderer(AllergeneTools.getListCellRenderer());

        listAll.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAdditives.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAllergenes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnPlusActionPerformed(null);
                }
            }
        });

        listAdditives.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnMinusAdditivesActionPerformed(null);
                }
            }
        });

        listAllergenes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnMinusAllergenesActionPerformed(null);
                }
            }
        });

        listAll.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
            }
        });
        listAdditives.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
            }
        });
        listAllergenes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
            }
        });

    }

    public HashSet<Allergene> getAssignedAllergenes() {
        assignedAllergenes.clear();
        for (Allergene t : Collections.list(((DefaultListModel<Allergene>) listAllergenes.getModel()).elements())) {
            assignedAllergenes.add(t);
        }
        return new HashSet<Allergene>(assignedAllergenes);
    }


    public HashSet<Additives> getAssignedAdditives() {
        assignedAdditives.clear();
        for (Additives t : Collections.list(((DefaultListModel<Additives>) listAdditives.getModel()).elements())) {
            assignedAdditives.add(t);
        }
        return new HashSet<Additives>(assignedAdditives);
    }

    private void btnPlusActionPerformed(ActionEvent e) {
        for (Object obj : listAll.getSelectedValuesList()) {
            if (obj instanceof Additives) {
                assignedAdditives.add((Additives) obj);
            } else if (obj instanceof Allergene) {
                assignedAllergenes.add((Allergene) obj);
            }
        }

        setListAll();

        listAdditives.setModel(Tools.newListModel(new ArrayList<Additives>(assignedAdditives)));
        listAllergenes.setModel(Tools.newListModel(new ArrayList<Allergene>(assignedAllergenes)));
    }


    private void txtSearchActionPerformed(ActionEvent e) {
        ArrayList searchList = new ArrayList();
        String searchText = txtSearch.getText().trim();

        for (Object t : all) {
            if (t.toString().toLowerCase().indexOf(searchText.toLowerCase()) >= 0) {
                searchList.add(t);
            }
        }

        assignedAllergenes.removeAll(listAllergenes.getSelectedValuesList());
        HashSet exclude = new HashSet(assignedAllergenes);
        exclude.addAll(assignedAdditives);
        listAll.setModel(Tools.newListModel(searchList, exclude));
        exclude.clear();

    }

    private void btnClearSearchActionPerformed(ActionEvent e) {
        txtSearch.setText(null);
        assignedAllergenes.removeAll(listAllergenes.getSelectedValuesList());
        setListAll();

    }

    private void btnMinusAllergenesActionPerformed(ActionEvent e) {
        assignedAllergenes.removeAll(listAllergenes.getSelectedValuesList());
        setListAll();

        listAllergenes.setModel(Tools.newListModel(new ArrayList<Allergene>(assignedAllergenes)));
    }

    private void btnMinusAdditivesActionPerformed(ActionEvent e) {
        assignedAdditives.removeAll(listAdditives.getSelectedValuesList());
        setListAll();

        listAdditives.setModel(Tools.newListModel(new ArrayList<Additives>(assignedAdditives)));
    }

    void setListAll() {
        HashSet exclude = new HashSet(assignedAllergenes);
        exclude.addAll(assignedAdditives);
        listAll.setModel(Tools.newListModel(all, exclude));
        exclude.clear();
    }

    public int getResponse() {
        return response;
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        response = JOptionPane.OK_OPTION;
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        txtSearch = new JTextField();
        btnClearSearch = new JButton();
        scrollPane3 = new JScrollPane();
        listAllergenes = new JList();
        scrollPane2 = new JScrollPane();
        listAll = new JList();
        btnMinusAllergenes = new JButton();
        listAdditives = new JList();
        btnPlus = new JButton();
        btnMinusAdditives = new JButton();
        panel2 = new JPanel();
        btnApply = new JButton();

        //======== this ========
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("9dlu, 9dlu, 9dlu, 9dlu"));
            dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.X_AXIS));

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "[pref,150dlu]:grow, $ugap, [pref,150dlu]:grow",
                        "default, $lgap, fill:default:grow, $lgap, default, $lgap, fill:default:grow, $lgap, default, 12dlu, default"));

                //======== panel1 ========
                {
                    panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                    //---- txtSearch ----
                    txtSearch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtSearchActionPerformed(e);
                        }
                    });
                    panel1.add(txtSearch);

                    //---- btnClearSearch ----
                    btnClearSearch.setText(null);
                    btnClearSearch.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/editclear.png")));
                    btnClearSearch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnClearSearchActionPerformed(e);
                        }
                    });
                    panel1.add(btnClearSearch);
                }
                contentPanel.add(panel1, CC.xy(1, 1, CC.FILL, CC.FILL));

                //======== scrollPane3 ========
                {

                    //---- listAllergenes ----
                    listAllergenes.setBackground(new Color(255, 204, 153));
                    scrollPane3.setViewportView(listAllergenes);
                }
                contentPanel.add(scrollPane3, CC.xywh(3, 1, 1, 3, CC.FILL, CC.FILL));

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(listAll);
                }
                contentPanel.add(scrollPane2, CC.xywh(1, 3, 1, 5, CC.FILL, CC.FILL));

                //---- btnMinusAllergenes ----
                btnMinusAllergenes.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_remove.png")));
                btnMinusAllergenes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnMinusAllergenesActionPerformed(e);
                    }
                });
                contentPanel.add(btnMinusAllergenes, CC.xy(3, 5, CC.FILL, CC.FILL));

                //---- listAdditives ----
                listAdditives.setBackground(new Color(0, 153, 51));
                listAdditives.setForeground(Color.yellow);
                contentPanel.add(listAdditives, CC.xy(3, 7));

                //---- btnPlus ----
                btnPlus.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
                btnPlus.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPlusActionPerformed(e);
                    }
                });
                contentPanel.add(btnPlus, CC.xy(1, 9, CC.FILL, CC.FILL));

                //---- btnMinusAdditives ----
                btnMinusAdditives.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_remove.png")));
                btnMinusAdditives.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnMinusAdditivesActionPerformed(e);
                    }
                });
                contentPanel.add(btnMinusAdditives, CC.xy(3, 9, CC.FILL, CC.FILL));

                //======== panel2 ========
                {
                    panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                    //---- btnApply ----
                    btnApply.setText(null);
                    btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/apply.png")));
                    btnApply.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnApplyActionPerformed(e);
                        }
                    });
                    panel2.add(btnApply);
                }
                contentPanel.add(panel2, CC.xy(3, 11, CC.RIGHT, CC.DEFAULT));
            }
            dialogPane.add(contentPanel);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(755, 635);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JTextField txtSearch;
    private JButton btnClearSearch;
    private JScrollPane scrollPane3;
    private JList listAllergenes;
    private JScrollPane scrollPane2;
    private JList listAll;
    private JButton btnMinusAllergenes;
    private JList listAdditives;
    private JButton btnPlus;
    private JButton btnMinusAdditives;
    private JPanel panel2;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
