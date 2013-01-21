/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import javax.smartcardio.Card;
import java.util.EventObject;

/**
 *
 * @author tloehr
 */
public class CardStateChangedEvent extends EventObject {
    Card card;

    public Card getCard() {
        return card;
    }

    boolean cardPresent = false;

    public boolean isCardPresent() {
        return cardPresent;
    }

    /**
     * @return true, wenn sich die Karte im UserMode befindet. Wenn cardID == -1 ist, dann ist dieser Wert ohne Bedeutung.
     */
    public boolean isUserMode() {
        return userMode;
    }

    boolean userMode = false;

    long cardID = -1l;

    /**
     * @return ID der Memocard. -1 wenn keine Karte eingelegt.
     */
    public long getCardID() {
        return cardID;
    }


    public CardStateChangedEvent(Object source, Card card, long cardid, boolean cardPresent, boolean userMode) {
        this(source);
        this.cardID = cardid;
        this.cardPresent = cardPresent;
        this.userMode = userMode;
        this.card = card;
    }

    public CardStateChangedEvent(Object source) {
        super(source);
    }
}
