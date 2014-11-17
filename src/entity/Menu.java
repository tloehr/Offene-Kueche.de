package entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "menu")
public class Menu implements Cloneable {

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


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "stock2menu", joinColumns =
    @JoinColumn(name = "menuid"), inverseJoinColumns =
    @JoinColumn(name = "stockid"))
    private Set<Stock> stocks;

    public Set<Stock> getStocks() {
        return stocks;
    }

    public Menu() {
        stocks = new HashSet<Stock>();
        menu2menuweeks = new ArrayList<Menuweek2Menu>();
    }


    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<Menuweek2Menu> menu2menuweeks;

    public List<Menuweek2Menu> getMenu2menuweeks() {
        return menu2menuweeks;
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
//        if (date != null ? !date.equals(menu.date) : menu.date != null) return false;
//        if (dessert != null ? !dessert.equals(menu.dessert) : menu.dessert != null) return false;
//        if (maincourse != null ? !maincourse.equals(menu.maincourse) : menu.maincourse != null) return false;
//        if (menuweek != null ? !menuweek.equals(menu.menuweek) : menu.menuweek != null) return false;
//        if (sauce != null ? !sauce.equals(menu.sauce) : menu.sauce != null) return false;
//        if (sidedish != null ? !sidedish.equals(menu.sidedish) : menu.sidedish != null) return false;
//        if (sideveggie != null ? !sideveggie.equals(menu.sideveggie) : menu.sideveggie != null) return false;
//        if (starter != null ? !starter.equals(menu.starter) : menu.starter != null) return false;
//        if (stocks != null ? !stocks.equals(menu.stocks) : menu.stocks != null) return false;
//        if (text != null ? !text.equals(menu.text) : menu.text != null) return false;
//        if (version != null ? !version.equals(menu.version) : menu.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
//        result = 31 * result + (menuweek != null ? menuweek.hashCode() : 0);
//        result = 31 * result + (date != null ? date.hashCode() : 0);
//        result = 31 * result + (text != null ? text.hashCode() : 0);
//        result = 31 * result + (starter != null ? starter.hashCode() : 0);
//        result = 31 * result + (maincourse != null ? maincourse.hashCode() : 0);
//        result = 31 * result + (sauce != null ? sauce.hashCode() : 0);
//        result = 31 * result + (sideveggie != null ? sideveggie.hashCode() : 0);
//        result = 31 * result + (sidedish != null ? sidedish.hashCode() : 0);
//        result = 31 * result + (dessert != null ? dessert.hashCode() : 0);
//        result = 31 * result + (stocks != null ? stocks.hashCode() : 0);
//        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public Menu clone() {
        Menu myClone = new Menu();

        myClone.setStarter(starter);
        myClone.setMaincourse(maincourse);
        myClone.setSauce(sauce);
        myClone.setSideveggie(sideveggie);
        myClone.setSidedish(sidedish);
        myClone.setDessert(dessert);
        myClone.setText(text);

        return myClone;
    }
}
