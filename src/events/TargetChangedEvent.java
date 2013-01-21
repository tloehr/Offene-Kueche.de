/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package events;

import javax.swing.text.JTextComponent;
import java.util.EventObject;

/**
 *
 * @author tloehr
 */
public class TargetChangedEvent extends EventObject {
    JTextComponent target;

    public JTextComponent getTarget() {
        return target;
    }

    public TargetChangedEvent(Object source, JTextComponent target) {
        this(source);
        this.target = target;
    }

    public TargetChangedEvent(Object source) {
        super(source);
    }


}
