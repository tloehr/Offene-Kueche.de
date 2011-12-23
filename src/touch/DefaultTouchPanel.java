package touch;

import events.PanelSwitchEvent;
import events.PanelSwitchListener;
import events.TargetChangedEvent;
import events.TargetChangedListener;
import org.pushingpixels.trident.Timeline;
import tools.Tools;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.02.11
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTouchPanel extends JPanel implements TouchPanel {
    JTextComponent target;

    @Override
    public void cleanup() {
        Tools.unregisterAllListeners(listenerList);
    }

    @Override
    public void addPanelSwitchListener(PanelSwitchListener listener) {
        listenerList.add(PanelSwitchListener.class, listener);
    }

    @Override
    public void removePanelSwitchListener(PanelSwitchListener listener) {
        listenerList.remove(PanelSwitchListener.class, listener);
    }

    @Override
    public void startAction() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void firePanelSwitchEvent(PanelSwitchEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == PanelSwitchListener.class) {
                ((PanelSwitchListener) listeners[i + 1]).panelSwitched(evt);
            }
        }
    }
    public void txtComponentFocusGained(FocusEvent e) {
        target = (JTextComponent) e.getSource();

        if (!(e.getOppositeComponent() instanceof AbstractButton)) { // Sonst blinkt das die ganze Zeit bei der Eingabe

            final Timeline timeline1 = new Timeline(target);

            timeline1.addPropertyToInterpolate("background", target.getBackground(), Color.red);
            timeline1.setDuration(140);
            timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);

            //Tools.flash(target, 2);
        }

        target.selectAll();

    }
}
