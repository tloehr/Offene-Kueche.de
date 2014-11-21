package desktop.menu;

import entity.Menu;
import entity.Menuweek2Menu;

import java.util.EventObject;

/**
 * Created by tloehr on 18.11.14.
 */
public class PSDChangeEvent extends EventObject {

    private final Menu oldMenu;
    private final Menu newMenu;
    private final Menuweek2Menu menuweek2Menu;

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    public PSDChangeEvent(Object source, Menu newMenu, Menuweek2Menu menuweek2Menu) {
            super(source);
            this.oldMenu = null;
            this.newMenu = newMenu;
            this.menuweek2Menu = menuweek2Menu;
        }

    public PSDChangeEvent(Object source, Menu oldMenu, Menu newMenu, Menuweek2Menu menuweek2Menu) {
        super(source);
        this.oldMenu = oldMenu;
        this.newMenu = newMenu;
        this.menuweek2Menu = menuweek2Menu;
    }

    public Menu getOldMenu() {
        return oldMenu;
    }

    public Menu getNewMenu() {
        return newMenu;
    }

    public Menuweek2Menu getMenuweek2Menu() {
        return menuweek2Menu;
    }
}
