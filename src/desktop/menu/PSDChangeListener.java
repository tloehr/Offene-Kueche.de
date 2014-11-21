package desktop.menu;

import java.util.EventListener;

/**
 * Created by tloehr on 18.11.14.
 */
public interface PSDChangeListener extends EventListener {

    void menuEdited(PSDChangeEvent psdce);

    void menuReplaced(PSDChangeEvent psdce);

    void stockListChanged(PSDChangeEvent psdce);

}
