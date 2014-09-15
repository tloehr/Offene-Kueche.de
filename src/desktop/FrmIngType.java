/*
 * Created by JFormDesigner on Mon Sep 15 15:37:14 CEST 2014
 */

package desktop;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Stoffart;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.BeanTableModel;
import tools.Const;
import tools.Pair;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Torsten Löhr
 */
public class FrmIngType extends JInternalFrame {

    private Pair<Integer, Object> criteria;

    public FrmIngType() {
        initComponents();
        criteria = new Pair<Integer, Object>(Const.ALLE, null);
        loadTable();

        setTitle(Tools.getWindowTitle("Produkte-Verwaltung"));
        pack();
    }


    private void loadTable() {

        java.util.List list = null;


        if (criteria.getFirst() == Const.ALLE) {
            EntityManager em = Main.getEMF().createEntityManager();
            Query query = em.createQuery("" +
                    " SELECT t FROM Stoffart t" +
                    " ORDER BY t.bezeichnung ");
            list = query.getResultList();
            em.close();
        } else if (criteria.getFirst() == Const.NAME_NR) {
//            list = ProdukteTools.searchProdukte(criteria.getSecond().toString());
        } else if (criteria.getFirst() == Const.STOFFART) {
//            list = ProdukteTools.getProdukte((Stoffart) criteria.getSecond());
        } else if (criteria.getFirst() == Const.WARENGRUPPE) {
//            list = ProdukteTools.searchProdukte((Warengruppe) criteria.getSecond());
        }

        tblTypes.setModel(new BeanTableModel<Stoffart>(Stoffart.class, list));

//        TableRowSorter sorter = new TableRowSorter(tblProdukt.getModel());
        //        sorter.setComparator(ProdukteTableModel.COL_LAGERART, new Comparator<Short>() {
        //            public int compare(Short l1, Short l2) {
        //                return LagerTools.LAGERART[l1].compareTo(LagerTools.LAGERART[l2]);
        //            }
        //        });


        tblTypes.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        thisComponentResized(null);

    }

    private void btnSearchAllActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void xSearchField1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void thisComponentResized(ComponentEvent e) {
        Tools.packTable(tblTypes, 0);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspSearch = new JScrollPane();
        pnlSearch = new JXTaskPaneContainer();
        xTaskPane1 = new JXTaskPane();
        btnSearchAll = new JButton();
        xSearchField1 = new JXSearchField();
        xTaskPane2 = new JXTaskPane();
        xTaskPane3 = new JXTaskPane();
        scrollPane1 = new JScrollPane();
        tblTypes = new JTable();

        //======== this ========
        setVisible(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, default:grow",
            "default:grow, 2*($lgap, default)"));

        //======== jspSearch ========
        {

            //======== pnlSearch ========
            {

                //======== xTaskPane1 ========
                {
                    xTaskPane1.setSpecial(true);
                    xTaskPane1.setTitle("Suchen");
                    xTaskPane1.setFont(new Font("sansserif", Font.BOLD, 18));
                    xTaskPane1.setLayout(new VerticalLayout(10));

                    //---- btnSearchAll ----
                    btnSearchAll.setText("Alle");
                    btnSearchAll.setFont(new Font("sansserif", Font.PLAIN, 18));
                    btnSearchAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSearchAllActionPerformed(e);
                        }
                    });
                    xTaskPane1.add(btnSearchAll);

                    //---- xSearchField1 ----
                    xSearchField1.setPrompt("Suchtext hier eingeben");
                    xSearchField1.setFont(new Font("sansserif", Font.PLAIN, 18));
                    xSearchField1.setMinimumSize(new Dimension(230, 36));
                    xSearchField1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            xSearchField1ActionPerformed(e);
                        }
                    });
                    xTaskPane1.add(xSearchField1);
                }
                pnlSearch.add(xTaskPane1);

                //======== xTaskPane2 ========
                {
                    xTaskPane2.setTitle("Warengruppen");
                    xTaskPane2.setFont(new Font("sansserif", Font.BOLD, 18));
                    xTaskPane2.setCollapsed(true);
                    xTaskPane2.setLayout(new VerticalLayout(10));
                }
                pnlSearch.add(xTaskPane2);

                //======== xTaskPane3 ========
                {
                    xTaskPane3.setLayout(new VerticalLayout());
                }
                pnlSearch.add(xTaskPane3);
            }
            jspSearch.setViewportView(pnlSearch);
        }
        contentPane.add(jspSearch, CC.xywh(1, 1, 1, 5, CC.FILL, CC.FILL));

        //======== scrollPane1 ========
        {

            //---- tblTypes ----
            tblTypes.setFont(new Font("SansSerif", Font.PLAIN, 18));
            scrollPane1.setViewportView(tblTypes);
        }
        contentPane.add(scrollPane1, CC.xywh(3, 1, 1, 5, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspSearch;
    private JXTaskPaneContainer pnlSearch;
    private JXTaskPane xTaskPane1;
    private JButton btnSearchAll;
    private JXSearchField xSearchField1;
    private JXTaskPane xTaskPane2;
    private JXTaskPane xTaskPane3;
    private JScrollPane scrollPane1;
    private JTable tblTypes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
