package entity;

import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.*;

/**
 * Created by tloehr on 14.10.14.
 */
@Entity
@Table(name = "menuweek")
public class Menuweek implements Cloneable {
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @JoinColumn(name = "menuweekallid", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Menuweekall menuweekall;

    public Menuweekall getMenuweekall() {
        return menuweekall;
    }

    public void setMenuweekall(Menuweekall menuweekall) {
        this.menuweekall = menuweekall;
        lastsave = new Date();
    }

    @JoinColumn(name = "featureid", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Recipefeature recipefeature;

    public Recipefeature getRecipefeature() {
        return recipefeature;
    }

    public void setRecipefeature(Recipefeature recipefeature) {
        this.recipefeature = recipefeature;
        lastsave = new Date();
    }

    @Basic
    @Column(name = "lastsave", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastsave;

    public Date getLastsave() {
        return lastsave;
    }


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "menuweek2customer", joinColumns =
    @JoinColumn(name = "menuweekid"), inverseJoinColumns =
    @JoinColumn(name = "customerid"))
    private Set<Customer> customers;

    public Set<Customer> getCustomers() {
        return customers;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menuweek")
    @OrderBy(value = "date asc")
    private List<Menuweek2Menu> menuweek2menus;

    public List<Menuweek2Menu> getMenuweek2menus() {
        return menuweek2menus;
    }

    public Menuweek(Menuweekall menuweekall, Recipefeature recipefeature) {
        this.menuweekall = menuweekall;
        this.recipefeature = recipefeature;
        customers = new HashSet<Customer>();
        menuweek2menus = new ArrayList<Menuweek2Menu>();

        for (int weekday = 0; weekday < 7; weekday++) {
            menuweek2menus.add(new Menuweek2Menu(this, new LocalDate(menuweekall.getWeek()).plusDays(weekday)));
        }

        lastsave = new Date();
    }

    public void touch() {
        lastsave = new Date();
    }

//    public void setMenu(int weekday, Menu menu) {
//
//        menus.set(weekday-1, menu);
//
//        for (int w = DateTimeConstants.MONDAY; w <= DateTimeConstants.SUNDAY; w++) {
//            menus.add(new Menu(this, new LocalDate(menuweekall.getWeek()).plusDays(weekday - 1)));
//        }
//    }

//
//    /**
//     * @param weekday according to Joda DateTimeConstants 1 monday ... 7 sunday
//     */
//    public Menu getMenu(int weekday) {
//
//        return menus.get(weekday-1);
//
////        Menu foundMenu = null;
////
////        for (Menu menu : menus) {
////            if (new LocalDate(menu.getDate()).plusDays(weekday - 1).equals(new LocalDate(menuweekall.getWeek()))) {
////                foundMenu = menu;
////                break;
////            }
////        }
////
////        return foundMenu;
//    }

    public Menuweek() {
    }

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menuweek menuweek = (Menuweek) o;

        if (id != menuweek.id) return false;
        if (lastsave != null ? !lastsave.equals(menuweek.lastsave) : menuweek.lastsave != null) return false;
        if (menuweekall != null ? !menuweekall.equals(menuweek.menuweekall) : menuweek.menuweekall != null)
            return false;
        if (recipefeature != null ? !recipefeature.equals(menuweek.recipefeature) : menuweek.recipefeature != null)
            return false;
        if (version != null ? !version.equals(menuweek.version) : menuweek.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (menuweekall != null ? menuweekall.hashCode() : 0);
        result = 31 * result + (recipefeature != null ? recipefeature.hashCode() : 0);
        result = 31 * result + (lastsave != null ? lastsave.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }


    @Override
    public Menuweek clone() {

        Menuweek clone = new Menuweek(menuweekall, recipefeature);

        clone.getMenuweek2menus().clear();
        for (int weekday = 0; weekday < 7; weekday++) {
            Menu clonedMenu = menuweek2menus.get(weekday).getMenu() == null ? null : menuweek2menus.get(weekday).getMenu().clone();
            clone.getMenuweek2menus().add(new Menuweek2Menu(clonedMenu, clone, new LocalDate(menuweekall.getWeek()).plusDays(weekday)));
        }

        clone.getCustomers().clear();
        for (Customer customer : customers) {
            clone.getCustomers().add(customer);
        }

        return clone;
    }
}
