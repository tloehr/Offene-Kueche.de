/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package touch;

import events.PanelSwitchEvent;
import events.PanelSwitchListener;
import events.TargetChangedEvent;
import events.TargetChangedListener;

/**
 * Alles was die Panels für das Touch Interface gemeinsam haben
 */
public interface TouchPanel {
    
    /**
     * Sie räumen hinter sich auf.
     */
    public void cleanup();

    public void addPanelSwitchListener(PanelSwitchListener listener);

    public void removePanelSwitchListener(PanelSwitchListener listener);

    /*
     * setzt das Panel in den gewünschten Anfangszustand. Das geht nicht über den
     * Konstruktor, weil dessen Aktivitäten schon rum sind, bis wir den
     * Listener registriert haben.
     */
    public void startAction();

    public void firePanelSwitchEvent(PanelSwitchEvent evt);
}
