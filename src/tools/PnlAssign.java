/*
 * Created by JFormDesigner on Tue Sep 23 15:48:16 CEST 2014
 */

package tools;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.ArrayUtils;

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

/**
 * @author Torsten Löhr
 */
public class PnlAssign<T> extends JPanel {

    private ArrayList<T> assigned;
    private final ArrayList<T> all;
    private final ListCellRenderer<T> cellRenderer;

    public PnlAssign(ArrayList<T> assigned, ArrayList<T> all, ListCellRenderer<T> cellRenderer) {
        this.assigned = assigned;
        this.all = all;
        this.cellRenderer = cellRenderer;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        listAll.setModel(Tools.newListModel(all));
        listAssigned.setModel(Tools.newListModel(assigned));

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
        return assigned;
    }

    private void okButtonActionPerformed(ActionEvent e) {
        assigned.clear();
        for (T t : Collections.list(((DefaultListModel<T>) listAssigned.getModel()).elements())) {
            assigned.add(t);
        }
        setVisible(false);
    }

    private void btnPlusActionPerformed(ActionEvent e) {
        for (int index : listAll.getSelectedIndices()) {
            if (!((DefaultListModel<T>) listAssigned.getModel()).contains(((DefaultListModel<T>) listAll.getModel()).getElementAt(index))) {
                ((DefaultListModel<T>) listAssigned.getModel()).addElement(((DefaultListModel<T>) listAll.getModel()).getElementAt(index));
            }
        }
    }

    private void btnMinusActionPerformed(ActionEvent e) {
        // a little complicated but it doesnt work as a simple "removeElement" because the model changes during the process.
        assigned.clear();
        for (int index = 0; index < listAssigned.getModel().getSize(); index++) {
            if (ArrayUtils.contains(listAssigned.getSelectedIndices(), index)) {
                assigned.add(((DefaultListModel<T>) listAssigned.getModel()).getElementAt(index));
            }
        }

        listAssigned.setModel(Tools.newListModel(assigned));
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        assigned = null;
        setVisible(false);
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        ArrayList<T> searchList = new ArrayList<T>();
        String searchText = txtSearch.getText().trim();
        for (T t : all) {
            if (t.toString().toLowerCase().indexOf(searchText.toLowerCase()) > 0) {
                searchList.add(t);
            }
        }
        listAll.setModel(Tools.newListModel(searchList));
    }

    private void btnClearSearchActionPerformed(ActionEvent e) {
        txtSearch.setText(null);
        listAll.setModel(Tools.newListModel(all));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        txtSearch = new JTextField();
        btnClearSearch = new JButton();
        scrollPane2 = new JScrollPane();
        listAll = new JList();
        scrollPane3 = new JScrollPane();
        listAssigned = new JList();
        btnPlus = new JButton();
        btnMinux = new JButton();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setVisible(true);
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("9dlu, 9dlu, 9dlu, 9dlu"));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "180dlu, $lcgap, 180dlu",
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
                    btnClearSearch.setText("text");
                    btnClearSearch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnClearSearchActionPerformed(e);
                        }
                    });
                    panel1.add(btnClearSearch);
                }
                contentPanel.add(panel1, CC.xy(1, 1));

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(listAll);
                }
                contentPanel.add(scrollPane2, CC.xy(1, 3, CC.DEFAULT, CC.FILL));

                //======== scrollPane3 ========
                {

                    //---- listAssigned ----
                    listAssigned.setBackground(new Color(255, 204, 153));
                    scrollPane3.setViewportView(listAssigned);
                }
                contentPanel.add(scrollPane3, CC.xywh(3, 1, 1, 3, CC.DEFAULT, CC.FILL));

                //---- btnPlus ----
                btnPlus.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_add.png")));
                btnPlus.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPlusActionPerformed(e);
                    }
                });
                contentPanel.add(btnPlus, CC.xy(1, 5));

                //---- btnMinux ----
                btnMinux.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/edit_remove.png")));
                btnMinux.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnMinusActionPerformed(e);
                    }
                });
                contentPanel.add(btnMinux, CC.xy(3, 5));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.createEmptyBorder("4dlu, 0dlu, 0dlu, 0dlu"));
                buttonBar.setLayout(new FormLayout(
                    "$glue, $button, $rgap, $button",
                    "pref"));

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, CC.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton, CC.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
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
    private JScrollPane scrollPane2;
    private JList listAll;
    private JScrollPane scrollPane3;
    private JList listAssigned;
    private JButton btnPlus;
    private JButton btnMinux;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}