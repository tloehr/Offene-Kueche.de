package events;

import touch.TouchPanel;

import javax.swing.text.JTextComponent;
import java.util.EventObject;
import java.util.List;

/**
 * Dieses Ereignis dient der Kommunikation der TouchPanels mit dem aufrufenden FrmTouch. Da ich bei der
 * Touch Version keine Fenster verwenden möchte, müssen sich die einzelnen TouchPanel bei der Verwendung
 * des pnlMain abwechseln. Dies geht darüber, dass das FrmTouch sich als Listener bei den Panels registriert und
 * dann bei diesem Ereignis das Panel umschaltet.
 *
 */
public class PanelSwitchEvent extends EventObject {
    /**
     *
     * @return <b>true</b>, wenn die Auswahltasten abgeschaltet werden sollen. <b>false</b>, sonst.
     */
    public boolean isEnablePanelSelectButtons() {
        return enablePanelSelectButtons;
    }

    boolean enablePanelSelectButtons;

    /**
     * @return gibt das gewünchte Panel zurück, das angezeigt werden soll.
     */
    public TouchPanel getSwitchTo() {
        return switchTo;
    }

    TouchPanel switchTo;

    /**
     *
     * @param source - enthält das TouchPanel, welches die Nachricht gesendet hat.
     * @param switchTo - enthält das gewünschte TouchPanel
     * @param enablePanelSelectButtons - gibt an, ob die Auswahltasten in FrmTouch (z.B. btnWareneingang) abgeschaltet werden sollen oder nicht.
     *
     */
    public PanelSwitchEvent(Object source, TouchPanel switchTo, boolean enablePanelSelectButtons) {
        this(source);
        this.switchTo = switchTo;
        this.enablePanelSelectButtons = enablePanelSelectButtons;
    }

    public PanelSwitchEvent(Object source) {
        super(source);
    }
}
