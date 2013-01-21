/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Damit das hier unter Linux läuft muss der pcscd installiert sein.
 * unbedingt nötig sind auch die DEV pakete, sonst kann Java nicht auf die
 * Headers zugreifen:
 *
 * apt-get install libpcsclite1 pcscd libccid libpcsclite-dev
 *
 * Mehr dazu: http://konstantin.filtschew.de/blog/2009/10/08/smart-cards-durch-die-in-java-eingebaute-java-smartcard-io-javax-smartcardio-ansprechen/
 *
 */
package threads;

import Main.Main;
import org.apache.commons.lang.ArrayUtils;
import tools.Tools;

import javax.smartcardio.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author tloehr
 */
public class CardMonitor extends Thread {

    private boolean waiting;

    public boolean isWaiting() {
        return waiting;
    }

    private boolean interrupted;

    public Card getCard() {
        return card;
    }

    private Card card;

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
        if (!suspended) {
            synchronized (this) {
                notify();
            }
        }
    }

    private boolean suspended;
    protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    private CardTerminal terminal;

    public CardTerminal getTerminal() {
        return terminal;
    }

    private long prevCardID = Long.MAX_VALUE;
    private long cardID = Long.MAX_VALUE;

    public boolean isUserMode() {
        return userMode;
    }

    private boolean userMode;

    public void addCardEventListener(CardStateListener listener) {
        listenerList.add(CardStateListener.class, listener);
    }

    public void removeCardEventListener(CardStateListener listener) {
        listenerList.remove(CardStateListener.class, listener);
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    // This private class is used to fire MyEvents
    void fireEvent(CardStateChangedEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == CardStateListener.class) {
                ((CardStateListener) listeners[i + 1]).cardStateChanged(evt);
            }
        }
    }

    public CardMonitor() {
        super();
        this.setName("CardMonitor");
        interrupted = false;
        suspended = false;
        waiting = false;
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            Main.logger.debug("Terminals: " + terminals);
            if (terminals.size() == 0) {
                terminal = null;
                Main.logger.debug("Kein Lesegerät vorhanden");
                interrupted = true;
            } else {
                terminal = terminals.get(0);
            }
        } catch (Exception ex) {
            terminal = null;
            interrupted = true;
        }
    }

    public void run() {
        while (!interrupted) {
            try { // outer try für den Thread
                try { // inner try für das Kartenterminal
                    card = terminal.connect("T=0");
                    cardID = getCardID(card);
                    userMode = !isIssuerMode(card);
                    if (prevCardID != cardID) {
                        // Kartenwechsel stattgefunden
                        Main.logger.debug("CardID = " + cardID);

                        fireEvent(new CardStateChangedEvent(this, card, cardID, true, userMode));
                        prevCardID = cardID;
                    }
                    //Main.logger.debug("Card disconnected");
                    card.disconnect(true);
                } catch (CardNotPresentException ce1) {
                    // Main.logger.debug("Karte entfernt");
                    cardID = Long.MAX_VALUE;
                    prevCardID = Long.MAX_VALUE;
                    userMode = false;
                    fireEvent(new CardStateChangedEvent(this, null, cardID, false, userMode));
                } catch (CardException ce2) {
                    // Main.logger.debug("Karte fehlt");
                    cardID = Long.MAX_VALUE;
                    prevCardID = Long.MAX_VALUE;
                    userMode = false;
                    fireEvent(new CardStateChangedEvent(this, null, cardID, false, userMode));
                }
                Thread.sleep(1000); // Millisekunden
                synchronized (this) {
                    if (suspended) {
                        Main.logger.debug("CardMonitor suspended");
                        waiting = true;
                        wait();
                        Main.logger.debug("CardMonitor resumed");
                    }
                }
                waiting = false;
            } catch (InterruptedException ie) {
                interrupted = false;
                Main.logger.debug("CardMonitor interrupted!");
            }
        }

    }

    /**
     * Liest die ersten 2 Words aus der Issuer Area (GemClub Memo Card) aus und wandelt diese in einen Long.
     *
     * @param card
     * @return
     * @throws CardException
     */
    long getCardID(Card card) throws CardException {
        byte[] ba = Arrays.copyOfRange(Tools.command_apdu(card, Tools.APDU_READ, null), 0, 8);
        ArrayUtils.reverse(ba);

        return new BigInteger(ba).longValue();
    }

    boolean isIssuerMode(Card card) throws CardException {
        byte[] ba = Arrays.copyOfRange(Tools.command_apdu(card, Tools.APDU_GET_USERMODE, null), 0, 4);
        return ba[3] == 0x40;
    }
}
