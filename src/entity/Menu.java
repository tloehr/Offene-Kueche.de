package entity;

import org.joda.time.LocalDate;
import tools.Tools;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "menu")
public class Menu {

    @javax.persistence.Column(name = "ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @JoinColumn(name = "menuweekid", referencedColumnName = "id")
    @ManyToOne(optional = true)
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


    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JoinColumn(name = "starterid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Recipes starter;

    public Recipes getStarter() {
        return starter;
    }

    public void setStarter(Recipes starter) {
        this.starter = starter;
    }

    @JoinColumn(name = "mainid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Recipes maincourse;

    public Recipes getMaincourse() {
        return maincourse;
    }

    public void setMaincourse(Recipes maincourse) {
        this.maincourse = maincourse;
    }

    @JoinColumn(name = "sauceid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Recipes sauce;

    public Recipes getSauce() {
        return sauce;
    }

    public void setSauce(Recipes sauce) {
        this.sauce = sauce;
    }

    @JoinColumn(name = "sidevegid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Recipes sideveggie;

    public Recipes getSideveggie() {
        return sideveggie;
    }

    public void setSideveggie(Recipes sideveggie) {
        this.sideveggie = sideveggie;
    }

    @JoinColumn(name = "sidedishid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Recipes sidedish;

    public Recipes getSidedish() {
        return sidedish;
    }

    public void setSidedish(Recipes sidedish) {
        this.sidedish = sidedish;
    }

    @JoinColumn(name = "dessertid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Recipes dessert;

    public Recipes getDessert() {
        return dessert;
    }

    public void setDessert(Recipes dessert) {
        this.dessert = dessert;
    }


    public boolean isInUse(){
        return !Tools.catchNull(text).isEmpty();
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "stock2menu", joinColumns =
    @JoinColumn(name = "menuid"), inverseJoinColumns =
    @JoinColumn(name = "stockid"))
    private Set<Stock> stocks;

    public Set<Stock> getStocks() {
        return stocks;
    }

    public Menu() {
    }

    public Menu(Menuweek menuweek, LocalDate date) {
        this.menuweek = menuweek;
        this.date = date.toDateTimeAtStartOfDay().toDate();
    }

    @Version
    @Column(name = "version")
    private Long version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menu menu = (Menu) o;

        if (id != menu.id) return false;
        if (date != null ? !date.equals(menu.date) : menu.date != null) return false;
        if (version != null ? !version.equals(menu.version) : menu.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }


}
