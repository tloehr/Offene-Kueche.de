/*
 * Created by JFormDesigner on Wed Feb 09 16:13:46 CET 2011
 */

package desktop;

import Main.Main;
import entity.Warengruppe;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class DlgWarengruppe extends JDialog {
    private Warengruppe warengruppe;

    public DlgWarengruppe(Frame owner, JInternalFrame realParent) {
        super(owner, true);
        setLocationRelativeTo(realParent);
        initComponents();
    }

    public DlgWarengruppe(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void txtWarengruppeCaretUpdate(CaretEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        Query query = em.createQuery("SELECT w FROM Warengruppe w WHERE w.bezeichnung = :bezeichnung");
        query.setParameter("bezeichnung", txtWarengruppe.getText());
        try {
            List warengruppen = query.getResultList();
            btnSave.setEnabled(!txtWarengruppe.getText().trim().equals("") && warengruppen.isEmpty());
        } catch (Exception e1) { // nicht gefunden
            //
        } finally {
            em.close();
        }
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        em.getTransaction().begin();
        try {
            em.getTransaction().begin();
            warengruppe = new Warengruppe(txtWarengruppe.getText());
            em.persist(warengruppe);
            em.getTransaction().commit();
        } catch (Exception e1) {
            Main.logger.fatal(e1.getMessage(), e1);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        dispose();
    }

     public Warengruppe getWarengruppe() {
        return warengruppe;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtWarengruppe = new JTextField();
        btnSave = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Neue Warengruppe");
        Container contentPane = getContentPane();

        //---- txtWarengruppe ----
        txtWarengruppe.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
        txtWarengruppe.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtWarengruppeCaretUpdate(e);
            }
        });

        //---- btnSave ----
        btnSave.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
        btnSave.setText("Speichern");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSaveActionPerformed(e);
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(btnSave, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                        .addComponent(txtWarengruppe, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(txtWarengruppe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                    .addComponent(btnSave)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField txtWarengruppe;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
