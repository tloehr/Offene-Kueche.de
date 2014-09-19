/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L�hr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie �bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder modifizieren, gem�� Version 2 der Lizenz.
 *
 * Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */

package tools;

import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import printer.Printers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author  tloehr
 */
public class DlgException extends javax.swing.JDialog {
    private Throwable exc;
    /** Creates new form DlgException */
    public DlgException(Throwable ex) {
        super(new java.awt.Frame(), false);
        this.exc = ex;
        initComponents();
        String type = ex.getClass().getName();
        //this.lblHeader.setText("Ausnahmezustand: " + type);
        Main.getLogger().error("Ausnahmezustand: " + type, ex);
        txtException.setText(getExceptionAsHTML());
        txtException.setCaretPosition(0);
        //SyslogTools.error(txtException.getText());
        this.setVisible(true);
    }
    
    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        btnClose = new JButton();
        lblHeader = new JLabel();
        jSeparator1 = new JSeparator();
        jLabel2 = new JLabel();
        jScrollPane1 = new JScrollPane();
        txtException = new JTextPane();
        btnPrint = new JButton();
        btnExit = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow, 2*($lcgap, default)",
            "fill:default, $lgap, fill:default:grow, 3*($lgap, fill:default)"));

        //---- btnClose ----
        btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/cancel.png")));
        btnClose.setText("Schliessen");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCloseActionPerformed(e);
            }
        });
        contentPane.add(btnClose, CC.xy(5, 9));

        //---- lblHeader ----
        lblHeader.setFont(new Font("Dialog", Font.BOLD, 18));
        lblHeader.setForeground(Color.red);
        lblHeader.setText("Ausnahmezustand");
        contentPane.add(lblHeader, CC.xywh(1, 1, 5, 1));
        contentPane.add(jSeparator1, CC.xywh(1, 3, 5, 1));

        //---- jLabel2 ----
        jLabel2.setText("Es ist ein Fehler aufgetreten. Bitte verst\u00e4ndigen Sie den Administrator.");
        contentPane.add(jLabel2, CC.xywh(1, 5, 5, 1));

        //======== jScrollPane1 ========
        {

            //---- txtException ----
            txtException.setContentType("text/html");
            txtException.setEditable(false);
            jScrollPane1.setViewportView(txtException);
        }
        contentPane.add(jScrollPane1, CC.xywh(1, 7, 5, 1));

        //---- btnPrint ----
        btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/printer.png")));
        btnPrint.setText("Drucken");
        btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnPrintActionPerformed(e);
            }
        });
        contentPane.add(btnPrint, CC.xy(3, 9));

        //---- btnExit ----
        btnExit.setIcon(new ImageIcon(getClass().getResource("/artwork/24x24/exit.png")));
        btnExit.setText("Programm beenden");
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExitActionPerformed(e);
            }
        });
        contentPane.add(btnExit, CC.xy(1, 9));
        setSize(769, 490);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        Main.fatal("Benutzer hat das Programm nach Exception beendet.");
        System.exit(1);
    }//GEN-LAST:event_btnExitActionPerformed
    
    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        Printers.print(this, txtException.getText(), false);

    }//GEN-LAST:event_btnPrintActionPerformed
    
    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnClose;
    private JLabel lblHeader;
    private JSeparator jSeparator1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JTextPane txtException;
    private JButton btnPrint;
    private JButton btnExit;
    // End of variables declaration//GEN-END:variables
    
    
    private String getExceptionAsHTML() {
        String html = "";
        StackTraceElement[] stacktrace = exc.getStackTrace();


            html += "<h1>Ausnahmezustand aufgetreten</h1>";
            html += "<h2>"+exc.getClass().getName()+"</h2>";
            html += "<p>"+exc.getMessage()+"</p>";
            html += "<table border=\"1\" cellspacing=\"0\"><tr>"
                    + "<th>Methode</th><th>Zeile</th><th>Klasse</th><th>Datei</th></tr>";


            for (int exception = 0; exception < stacktrace.length; exception++ ){
                StackTraceElement element = stacktrace[exception];
                html += "<tr>";
                html += "<td>" + element.getMethodName() + "</td>";
                html += "<td>" + element.getLineNumber() + "</td>";
                html += "<td>" + element.getClassName() + "</td>";
                html += "<td>" + element.getFileName() + "</td>";
                html += "</tr>";
            }
            html += "</table>";


//        html = "<html><head>"
//                + "<title>" + SYSTools.getWindowTitle("") + "</title>"
//                + "<script type=\"text/javascript\">"
//                + "window.onload = function() {"
//                + "window.print();"
//                + "}</script></head><body>" + html + "</body></html>";
        return html;
    }
}
