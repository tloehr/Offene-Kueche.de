package entity;

import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "menuweek2menu")
public class Menuweek2Menu implements Cloneable {

    @Column(name = "ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @JoinColumn(name = "menuid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu menu;

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        menuweek.touch();
    }

    @JoinColumn(name = "menuweekid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menuweek menuweek;

    public Menuweek getMenuweek() {
        return menuweek;
    }

    public void setMenuweek(Menuweek menuweek) {
        this.menuweek = menuweek;
    }


    @Basic
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.DATE)
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public Menuweek2Menu() {
    }

    public Menuweek2Menu(Menuweek menuweek, LocalDate date) {
        this.menu = new Menu();
        this.menuweek = menuweek;
        this.date = date.toDateTimeAtStartOfDay().toDate();
    }

    public Menuweek2Menu(Menu menu, Menuweek menuweek, LocalDate date) {
        this.menu = menu;
        this.menuweek = menuweek;
        this.date = date.toDateTimeAtStartOfDay().toDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menuweek2Menu that = (Menuweek2Menu) o;

        if (id != that.id) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (menu != null ? !menu.equals(that.menu) : that.menu != null) return false;
        if (menuweek != null ? !menuweek.equals(that.menuweek) : that.menuweek != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (menu != null ? menu.hashCode() : 0);
        result = 31 * result + (menuweek != null ? menuweek.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }


    @Version
    @Column(name = "version")
    private Long version;


    @Override
    public Object clone()  {
        Menuweek2Menu clone = new Menuweek2Menu();
        clone.setMenuweek(menuweek);
        clone.setMenu(menu.clone());
        return clone;
    }
}
