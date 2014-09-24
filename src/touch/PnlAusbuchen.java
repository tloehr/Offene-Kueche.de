/*
 * Created by JFormDesigner on Wed Feb 09 16:13:36 CET 2011
 */

package touch;


import Main.Main;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.LagerTools;
import entity.ProdukteTools;
import entity.Vorrat;
import entity.VorratTools;
import exceptions.OutOfRangeException;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallback;
import threads.SoundProcessor;
import tools.Tools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author Torsten LÃ¶hr
 */
public class PnlAusbuchen extends DefaultTouchPanel {

    //protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    Vorrat vorrat;
    BigDecimal menge;
    DecimalFormat format;
    SoundProcessor sp;

    public PnlAusbuchen(SoundProcessor sp) {
        initComponents();
        this.sp = sp;
        format = new DecimalFormat("######0.00");
    }

    @Override
    public void startAction() {
        clearVorrat();
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        String suchtext = txtSearch.getText().trim();
        long id = 0l;
        if (!suchtext.equals("")) {
            // Was genau wird gesucht ?
            if (suchtext.matches("^" + ProdukteTools.IN_STORE_PREFIX + "\\d{11}")) { // VorratID in EAN 13 Kodiert mit in-store Präfix (z.B. 20)
                // Ausschneiden der VorID aus dem EAN Code. IN-STORE-PREFIX und die Prüfsummenziffer weg.
                id = Long.parseLong(suchtext.substring(2, 12));
                txtSearch.setText(Long.toString(id));
            } else if (suchtext.matches("^\\d+")) { // Nur Ziffern, kann nur VorratID von Hand eingetippt sein.
                id = Long.parseLong(suchtext);
            }
            if (id != 0) {
                EntityManager em = Main.getEMF().createEntityManager();
                Query query1 = em.createNamedQuery("Vorrat.findByIdActive");
                query1.setParameter("id", id);
                java.util.List<Vorrat> vorraete = query1.getResultList();
                em.close();
                if (vorraete.size() == 1) {
                    vorrat = vorraete.get(0);
                    showVorrat();
                    if (btnSofortBuchen.isSelected()) {
                        em = Main.getEMF().createEntityManager();
                        try {
                            em.getTransaction().begin();

                            Vorrat myVorrat = em.merge(vorrat);
                            em.lock(myVorrat, LockModeType.OPTIMISTIC);

                            VorratTools.ausbuchen(em, myVorrat, "Abschlussbuchung");
                            em.getTransaction().commit();

                            vorrat = myVorrat;

                            Tools.log(txtLog, "[" + vorrat.getId() + "] \"" + vorrat.getProdukt().getBezeichnung() + "\" komplett ausgebucht");
                            sp.bell();
                            clearVorrat();
                        } catch (OptimisticLockException ole) {
                            Main.logger.info(ole);
                            em.getTransaction().rollback();
                        } catch (Exception e1) {
                            em.getTransaction().rollback();
                            Main.fatal(e1);
                            e1.printStackTrace();
                        } finally {
                            em.close();
                        }
                    }
                } else {
                    clearVorrat();
                    vorrat = null;
                    try {
                        em = Main.getEMF().createEntityManager();
                        Query query2 = em.createNamedQuery("Vorrat.findById");
                        query2.setParameter("id", id);
                        Vorrat meinVorrat = (Vorrat) query2.getSingleResult();
                        Tools.log(txtLog, "[" + id + "] \"" + meinVorrat.getProdukt().getBezeichnung() + "\" wurde bereits ausgebucht");
                        sp.warning();
                    } catch (Exception e2) {
                        Tools.log(txtLog, "[" + id + "] Nicht gefunden");
                        sp.warning();
                    } finally {
                        em.close();
                    }
                }

            } else {
                sp.warning();
            }
        }
    }

    private void txtSearchFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void btnKomplettAusbuchenActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Vorrat myVorrat = VorratTools.ausbuchen(em, vorrat, "Abschlussbuchung");
            em.getTransaction().commit();
            Tools.log(txtLog, "[" + vorrat.getId() + "] \"" + vorrat.getProdukt().getBezeichnung() + "\" komplett ausgebucht");
            sp.bell();
            vorrat = myVorrat;
            clearVorrat();
        } catch (OptimisticLockException ole) {
            Main.logger.info(ole);
            em.getTransaction().rollback();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            clearVorrat();
        } finally {
            em.close();
        }
    }

    private void txtMengeCaretUpdate(CaretEvent e) {
        try {
            //double dbl = Double.parseDouble(txtMenge.getText().replaceAll(",", "\\."));
            //double dbl = Double.parseDouble(txtMenge.getText().replaceAll(",", "\\."));
            menge = new BigDecimal(txtMenge.getText().replaceAll(",", "\\."));
            Main.debug("parsed BigDecimal: " + menge.toString());
            btnMengeAusbuchen.setEnabled(true);
        } catch (NumberFormatException e1) {
            menge = BigDecimal.ZERO;
            btnMengeAusbuchen.setEnabled(false);
        }
    }

    private void txtMengeActionPerformed(ActionEvent e) {
        if (btnMengeAusbuchen.isEnabled()) {
            btnMengeAusbuchen.doClick();
        }
    }

    private void txtMengeFocusGained(FocusEvent e) {
        txtComponentFocusGained(e);
    }

    private void btnHalbAusbuchenActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Vorrat myVorrat = VorratTools.ausbuchen(em, vorrat, menge.divide(new BigDecimal(2)), "Ausbuchung Hälfte");
            em.getTransaction().commit();
            Tools.log(txtLog, "[" + vorrat.getId() + "] \"" + vorrat.getProdukt().getBezeichnung() + "\" zur Hälfte ausgebucht");
            sp.bell();
            vorrat = myVorrat;
            clearVorrat();
            //success("Ausbuchung erfolgreich");
        } catch (OptimisticLockException ole) {
            Main.logger.info(ole);
            em.getTransaction().rollback();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            clearVorrat();
        } finally {
            em.close();
        }
    }

    private void btnMengeAusbuchenActionPerformed(ActionEvent e) {
        EntityManager em = Main.getEMF().createEntityManager();
        try {
            em.getTransaction().begin();
            Vorrat myVorrat = VorratTools.ausbuchen(em, vorrat, menge, "Ausbuchung");
            String message = "[" + vorrat.getId() + "] \"" + vorrat.getProdukt().getBezeichnung() + "\" " + menge + " " + lblEinheit.getText() + " ausgebucht.";
            em.getTransaction().commit();
            vorrat = myVorrat;
            message += vorrat.isAusgebucht() ? " Vorrat damit abgeschlossen." : "";
            sp.bell();
            Tools.log(txtLog, message);
            //Main.debug(">>" + vorrat.getProdukt().getBezeichnung() + "<< " + menge + " " + lblEinheit.getText() + " ausgebucht");

            clearVorrat();

        } catch (OptimisticLockException ole) {
            Main.logger.info(ole);
            em.getTransaction().rollback();
        } catch (OutOfRangeException ex) {
            clearVorrat();
            Tools.log(txtLog, "Menge falsch. Mindestens: " + format.format(ex.getValidMin()) + " Höchstens: " + format.format(ex.getValidMax()));
            sp.error();
            em.getTransaction().rollback();
        } catch (Exception te) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void showVorrat() {
        info(vorrat.getProdukt().getBezeichnung(), lblVorrat);
        lblEinheit.setText(LagerTools.EINHEIT[vorrat.getProdukt().getIngTypes().getEinheit()]);
        txtMenge.setText(format.format(VorratTools.getSummeBestand(vorrat)));
        txtMenge.setEnabled(true);
        btnKomplettAusbuchen.setEnabled(true);
        btnHalbAusbuchen.setEnabled(true);
        btnMengeAusbuchen.setEnabled(true);
        txtSearch.requestFocus();
    }

    private void clearVorrat() {
        lblVorrat.setText(" ");
        lblVorrat.setIcon(null);
        //lblVorrat.setForeground(Color.red);
        txtMenge.setText("0,00");
        txtSearch.setText("");
        txtSearch.requestFocus();
        txtMenge.setEnabled(false);
        lblEinheit.setText(" ");
        btnKomplettAusbuchen.setEnabled(false);
        btnHalbAusbuchen.setEnabled(false);
        btnMengeAusbuchen.setEnabled(false);
        txtSearch.requestFocus();
    }

    private void btnSofortBuchenItemStateChanged(ItemEvent e) {
        Tools.log(txtLog, btnSofortBuchen.isSelected() ? "Verpackte Ware wird direkt ausgebucht, sobald alle Informationen vorliegen." : "Ware wird nur auf Knopfdruck ausgebucht.");
        txtSearch.requestFocus();
    }

//    private void success(String message) {
//        lblVorrat.setText(message);
//        lblVorrat.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/apply.png")));
//        Timeline timeline1 = new Timeline(lblVorrat);
//        timeline1.addPropertyToInterpolate("foreground", lblVorrat.getForeground(), Color.green);
//        timeline1.setDuration(300);
//        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
//    }
//
//    private void error(String message) {
//        lblVorrat.setText(message);
//        lblVorrat.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/stop.png")));
//        Timeline timeline1 = new Timeline(lblVorrat);
//        timeline1.addPropertyToInterpolate("foreground", lblVorrat.getForeground(), Color.red);
//        timeline1.setDuration(300);
//        timeline1.playLoop(4, Timeline.RepeatBehavior.REVERSE);
//    }
//
//    private void info(String message) {
//        lblVorrat.setText(message);
//        lblVorrat.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/info.png")));
//    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtSearch = new JTextField();
        btnKomplettAusbuchen = new JButton();
        txtMenge = new JTextField();
        lblVorrat = new JLabel();
        lblEinheit = new JLabel();
        btnHalbAusbuchen = new JButton();
        btnMengeAusbuchen = new JButton();
        btnSofortBuchen = new JToggleButton();
        scrollPane1 = new JScrollPane();
        txtLog = new JTextArea();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default, $lcgap, pref, $lcgap, default",
            "default, 4*($lgap, fill:50dlu), $lgap, fill:default:grow, $lgap, fill:50dlu, $lgap, default"));

        //---- txtSearch ----
        txtSearch.setFont(new Font("sansserif", Font.BOLD, 36));
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchActionPerformed(e);
            }
        });
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtSearchFocusGained(e);
            }
        });
        add(txtSearch, CC.xywh(3, 3, 5, 1));

        //---- btnKomplettAusbuchen ----
        btnKomplettAusbuchen.setFont(new Font("sansserif", Font.BOLD, 28));
        btnKomplettAusbuchen.setText("Komplett ausbuchen");
        btnKomplettAusbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/switchuser.png")));
        btnKomplettAusbuchen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnKomplettAusbuchenActionPerformed(e);
            }
        });
        add(btnKomplettAusbuchen, CC.xywh(3, 13, 3, 1));

        //---- txtMenge ----
        txtMenge.setFont(new Font("sansserif", Font.BOLD, 36));
        txtMenge.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMenge.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtMengeCaretUpdate(e);
            }
        });
        txtMenge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtMengeActionPerformed(e);
            }
        });
        txtMenge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtMengeFocusGained(e);
            }
        });
        add(txtMenge, CC.xy(3, 7));

        //---- lblVorrat ----
        lblVorrat.setFont(new Font("sansserif", Font.BOLD, 28));
        lblVorrat.setText(" ");
        lblVorrat.setBorder(new DropShadowBorder(Color.black, 5, 0.5f, 12, true, true, true, true));
        add(lblVorrat, CC.xywh(3, 5, 5, 1));

        //---- lblEinheit ----
        lblEinheit.setFont(new Font("sansserif", Font.BOLD, 28));
        add(lblEinheit, CC.xy(5, 7));

        //---- btnHalbAusbuchen ----
        btnHalbAusbuchen.setFont(new Font("sansserif", Font.BOLD, 28));
        btnHalbAusbuchen.setText("H\u00e4lfte ausbuchen");
        btnHalbAusbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/half_icon.png")));
        btnHalbAusbuchen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnHalbAusbuchenActionPerformed(e);
            }
        });
        add(btnHalbAusbuchen, CC.xywh(3, 9, 5, 1));

        //---- btnMengeAusbuchen ----
        btnMengeAusbuchen.setFont(new Font("sansserif", Font.BOLD, 28));
        btnMengeAusbuchen.setText("Menge ausbuchen");
        btnMengeAusbuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/kcmpartitions.png")));
        btnMengeAusbuchen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnMengeAusbuchenActionPerformed(e);
            }
        });
        add(btnMengeAusbuchen, CC.xy(7, 7));

        //---- btnSofortBuchen ----
        btnSofortBuchen.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/agt_member.png")));
        btnSofortBuchen.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnSofortBuchenItemStateChanged(e);
            }
        });
        add(btnSofortBuchen, CC.xy(7, 13));

        //======== scrollPane1 ========
        {

            //---- txtLog ----
            txtLog.setFont(new Font("sansserif", Font.PLAIN, 24));
            txtLog.setLineWrap(true);
            txtLog.setWrapStyleWord(true);
            txtLog.setBackground(Color.lightGray);
            txtLog.setEditable(false);
            scrollPane1.setViewportView(txtLog);
        }
        add(scrollPane1, CC.xywh(3, 11, 5, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

//    private void success(String message, JLabel lbl) {
//        lbl.setText(message);
//        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/apply.png")));
//        Timeline timeline1 = new Timeline(lbl);
//        timeline1.addPropertyToInterpolate("foreground", Color.black, Color.green);
//        timeline1.setDuration(400);
//        final JLabel lbl1 = lbl;
//        timeline1.addCallback(new TimelineCallback() {
//            @Override
//            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
//                if (timelineState1 == Timeline.TimelineState.DONE) {
//                    fadeout(lbl1);
//                }
//            }
//
//            @Override
//            public void onTimelinePulse(float v, float v1) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
//    }
//
//    private void error(String message, JLabel lbl) {
//        lbl.setText(message);
//        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/stop.png")));
//        Timeline timeline1 = new Timeline(lbl);
//        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), Color.red);
//        timeline1.setDuration(300);
//        final JLabel lbl1 = lbl;
//        timeline1.addCallback(new TimelineCallback() {
//            @Override
//            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
//                if (timelineState1 == Timeline.TimelineState.DONE) {
//                    fadeout(lbl1);
//                }
//            }
//
//            @Override
//            public void onTimelinePulse(float v, float v1) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//        timeline1.playLoop(4, Timeline.RepeatBehavior.REVERSE);
//    }

    private void fadeout(JLabel lbl) {
        //lbl.setIcon(null);
        final JLabel lbl1 = lbl;
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), lbl.getBackground());
        timeline1.setDuration(700);
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    lbl1.setText("");
                    lbl1.setForeground(Color.black);
                    lbl1.setIcon(null);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.play();
    }

    private void info(String message, JLabel lbl) {
        lbl.setText(message);
        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/info.png")));
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField txtSearch;
    private JButton btnKomplettAusbuchen;
    private JTextField txtMenge;
    private JLabel lblVorrat;
    private JLabel lblEinheit;
    private JButton btnHalbAusbuchen;
    private JButton btnMengeAusbuchen;
    private JToggleButton btnSofortBuchen;
    private JScrollPane scrollPane1;
    private JTextArea txtLog;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
