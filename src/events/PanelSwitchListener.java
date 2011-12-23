package events;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.02.11
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public interface PanelSwitchListener extends EventListener {

    public void panelSwitched(PanelSwitchEvent evt);

}
