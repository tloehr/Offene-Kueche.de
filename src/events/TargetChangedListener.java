/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package events;

import java.util.EventListener;

/**
 *
 * @author tloehr
 */
public interface TargetChangedListener extends EventListener {

    public void targetChanged(TargetChangedEvent evt);
}
