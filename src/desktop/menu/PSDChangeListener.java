package desktop.menu;

import java.util.EventListener;

/**
 * Created by tloehr on 18.11.14.
 */
public interface PSDChangeListener extends EventListener {

    void menuEdited(PSDChangeEvent psdce);

    void menuReplaced(PSDChangeEvent psdce);

    void stockListChanged(PSDChangeEvent psdce);

    void customerListChanged(PSDChangeEvent psdce);

    void menufeatureChanged(PSDChangeEvent psdce);

    void menuweekAdded(PSDChangeEvent psdce);

    void menuweekDeleted(PSDChangeEvent psdce);

}
