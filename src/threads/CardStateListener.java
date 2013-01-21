/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package threads;

import java.util.EventListener;

/**
 *
 * @author tloehr
 */
public interface CardStateListener extends EventListener {
    public void cardStateChanged(CardStateChangedEvent evt);
}
