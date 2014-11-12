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
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlSelect<T> extends PopupPanel{

    private final ArrayList<T> all;
    private final ListCellRenderer<T> cellRenderer;

    public JTextField getDefaultFocusComponent() {
        return txtSearch;
    }

    @Override
    public Object getResult() {
        return new ArrayList<T>(listAll.getSelectedValuesList());
    }

    @Override
    public void setStartFocus() {

    }

    @Override
    public boolean isSaveOK() {
        return true;
    }

    public PnlSelect(ArrayList<T> all, ListCellRenderer<T> cellRenderer, int selectionMode) {
        this.all = all;
        this.cellRenderer = cellRenderer;
        initComponents();
        initPanel();
        listAll.setSelectionMode(selectionMode);
    }

    private void initPanel() {
        listAll.setModel(Tools.newListModel(all));

        listAll.setCellRenderer(cellRenderer);

        listAll.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

//        listAll.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2) {
//                    btnApplyActionPerformed(null);
//                }
//            }
//        });


        listAll.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
            }
        });
    }



    private void txtSearchActionPerformed(ActionEvent e) {
        ArrayList<T> searchList = new ArrayList<T>();
        String searchText = txtSearch.getText().trim();

        for (T t : all) {
            if (t.toString().toLowerCase().indexOf(searchText.toLowerCase()) >= 0) {
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
                    "[pref,150dlu]:grow",
                    "default, $lgap, fill:default:grow"));

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

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(listAll);
                }
                contentPanel.add(scrollPane2, CC.xy(1, 3, CC.FILL, CC.FILL));
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
    private JScrollPane scrollPane2;
    private JList listAll;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
