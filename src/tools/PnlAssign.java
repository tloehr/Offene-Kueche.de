/*
 * Created by JFormDesigner on Tue Sep 23 15:48:16 CEST 2014
 */

package tools;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

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
public class PnlAssign<T> extends PopupPanel {

    private HashSet<T> assigned;
    private final ArrayList<T> all;
    private final ListCellRenderer<T> cellRenderer;


    public PnlAssign(Set<T> assigned, ArrayList<T> all, ListCellRenderer<T> cellRenderer) {
        this(new ArrayList<T>(assigned), all, cellRenderer);
    }

    public JTextField getDefaultFocusComponent() {
        return txtSearch;
    }

    public PnlAssign(ArrayList<T> assigned, ArrayList<T> all, ListCellRenderer<T> cellRenderer) {
        this.assigned = new HashSet<T>(assigned);
        this.all = all;
        this.cellRenderer = cellRenderer;
        initComponents();
        initPanel();
    }

    @Override
    public Object getResult() {
        return getAssigned();
    }

    @Override
    public void setStartFocus() {
        txtSearch.requestFocus();
    }

    @Override
    public boolean isSaveOK() {
        return true;
    }

    public void setVisibleRowCount(final int rowcount) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listAll.setVisibleRowCount(rowcount);
                listAssigned.setVisibleRowCount(rowcount);
                revalidate();
                repaint();
            }
        });

    }

    private void initPanel() {
        listAll.setModel(Tools.newListModel(all, assigned));
        listAssigned.setModel(Tools.newListModel(new ArrayList<T>(assigned)));

        listAll.setCellRenderer(cellRenderer);
        listAssigned.setCellRenderer(cellRenderer);

        listAll.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAssigned.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnPlusActionPerformed(null);
                }
            }
        });

        listAssigned.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnMinusActionPerformed(null);
                }
            }
        });

        listAll.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
            }
        });
    }

    public ArrayList<T> getAssigned() {
        assigned.clear();
        for (T t : Collections.list(((DefaultListModel<T>) listAssigned.getModel()).elements())) {
            assigned.add(t);
        }
        return new ArrayList<T>(assigned);
    }


    private void btnPlusActionPerformed(ActionEvent e) {
        assigned.addAll(listAll.getSelectedValuesList());

        listAll.setModel(Tools.newListModel(all, assigned));
        listAssigned.setModel(Tools.newListModel(new ArrayList<T>(assigned)));
    }

    private void btnMinusActionPerformed(ActionEvent e) {
        assigned.removeAll(listAssigned.getSelectedValuesList());

        listAll.setModel(Tools.newListModel(all, assigned));
        listAssigned.setModel(Tools.newListModel(new ArrayList<T>(assigned)));
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        ArrayList<T> searchList = new ArrayList<T>();
        String searchText = txtSearch.getText().trim();

        for (T t : all) {
            if (t.toString().toLowerCase().indexOf(searchText.toLowerCase()) >= 0) {
                searchList.add(t);
            }
        }
        listAll.setModel(Tools.newListModel(searchList, assigned));
    }

    private void btnClearSearchActionPerformed(ActionEvent e) {
        txtSearch.setText(null);
        listAll.setModel(Tools.newListModel(all, assigned));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        txtSearch = new JTextField();
        btnClearSearch = new JButton();
        scrollPane3 = new JScrollPane();
        listAssigned = new JList();
        scrollPane2 = new JScrollPane();
        listAll = new JList();
        btnPlus = new JButton();
        btnMinux = new JButton();

        //======== this ========
        setVisible(true);
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("9dlu, 9dlu, 9dlu, 9dlu"));
            dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.X_AXIS));

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "[pref,150dlu]:grow, $ugap, [pref,150dlu]:grow",
                        "default, $lgap, fill:default:grow, $lgap, default"));

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

                    //---- listAssigned ----
                    listAssigned.setBackground(new Color(255, 204, 153));
                    scrollPane3.setViewportView(listAssigned);
                }
                contentPanel.add(scrollPane3, CC.xywh(3, 1, 1, 3, CC.FILL, CC.FILL));

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(listAll);
                }
                contentPanel.add(scrollPane2, CC.xy(1, 3, CC.FILL, CC.FILL));

                //---- btnPlus ----
                btnPlus.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
                btnPlus.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPlusActionPerformed(e);
                    }
                });
                contentPanel.add(btnPlus, CC.xy(1, 5, CC.FILL, CC.FILL));

                //---- btnMinux ----
                btnMinux.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_remove.png")));
                btnMinux.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnMinusActionPerformed(e);
                    }
                });
                contentPanel.add(btnMinux, CC.xy(3, 5, CC.FILL, CC.FILL));
            }
            dialogPane.add(contentPanel);
        }
        add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JTextField txtSearch;
    private JButton btnClearSearch;
    private JScrollPane scrollPane3;
    private JList listAssigned;
    private JScrollPane scrollPane2;
    private JList listAll;
    private JButton btnPlus;
    private JButton btnMinux;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
