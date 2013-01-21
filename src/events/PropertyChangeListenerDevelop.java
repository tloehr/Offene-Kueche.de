/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author tloehr
 */
public class PropertyChangeListenerDevelop implements PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getSource() instanceof javax.swing.table.TableColumn){
            Main.Main.logger.debug("Source (javax.swing.table.TableColumn): "+((javax.swing.table.TableColumn) evt.getSource()).getModelIndex());
        } else {
            Main.Main.logger.debug("Source: "+evt.getSource().toString());
        }

        Main.Main.logger.debug("Property Change: "+evt.getPropertyName());
        Main.Main.logger.debug("Old Value: "+evt.getOldValue().toString());
        Main.Main.logger.debug("New Value: "+evt.getNewValue().toString());
        Main.Main.logger.debug("==================");
    }

}
