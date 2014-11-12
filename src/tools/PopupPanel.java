package tools;

import com.jidesoft.popup.JidePopup;

/**
 * Created by tloehr on 12.11.14.
 */
public  abstract class PopupPanel extends JidePopup {
    public abstract Object getResult();
    public abstract void setStartFocus();
    public abstract boolean isSaveOK();
}