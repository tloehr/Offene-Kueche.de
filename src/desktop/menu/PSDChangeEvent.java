package desktop.menu;

import entity.Menu;
import entity.Menuweek;
import entity.Menuweek2Menu;

import java.util.Date;
import java.util.EventObject;

/**
 * Created by tloehr on 18.11.14.
 */
public class PSDChangeEvent extends EventObject {

    private final Menu oldMenu;
    private final Menu newMenu;
    private final Menuweek2Menu menuweek2Menu;
    private final Menuweek menuweek;
    private final Date changeDate;


    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    public PSDChangeEvent(Object source, Menuweek menuweek) {
        super(source);
        this.menuweek = menuweek;
        this.oldMenu = null;
        this.newMenu = null;
        this.menuweek2Menu = null;
        this.changeDate = menuweek.getLastsave();
    }



    public PSDChangeEvent(Object source, Menu newMenu, Menuweek2Menu menuweek2Menu) {
        super(source);
        this.oldMenu = null;
        this.newMenu = newMenu;
        this.menuweek2Menu = menuweek2Menu;
        this.menuweek = menuweek2Menu.getMenuweek();
        this.changeDate = menuweek2Menu.getMenuweek().getLastsave();
    }

    public PSDChangeEvent(Object source, Menu oldMenu, Menu newMenu, Menuweek2Menu menuweek2Menu) {
        super(source);
        this.oldMenu = oldMenu;
        this.newMenu = newMenu;
        this.menuweek2Menu = menuweek2Menu;
        this.menuweek = menuweek2Menu.getMenuweek();
        this.changeDate = menuweek2Menu.getMenuweek().getLastsave();
    }

    public Menu getOldMenu() {
        return oldMenu;
    }

    public Menu getNewMenu() {
        return newMenu;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public Menuweek2Menu getMenuweek2Menu() {
        return menuweek2Menu;
    }

    public Menuweek getMenuweek() {
        return menuweek;
    }
}
