package entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "menu")
public class Menu {
    private Date date;
    private String text;
    private Collection<Buchungen> txs;
    private Collection<IngTypes> ingTypes;
//    private Collection<Menuweek2Customer> menuweek2Customers;
    private Recipes recipe;

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
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "recipeid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Recipes getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipes recipe) {
        this.recipe = recipe;
    }


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tx2menu", joinColumns =
    @JoinColumn(name = "menuid"), inverseJoinColumns =
    @JoinColumn(name = "txid"))
    public Collection<Buchungen> getTxs() {
        return txs;
    }

    public void setTxs(Collection<Buchungen> txs) {
        this.txs = txs;
    }

    @ManyToMany(mappedBy = "menus")
    public Collection<IngTypes> getIngTypes() {
        return ingTypes;
    }

    public void setIngTypes(Collection<IngTypes> ingTypes) {
        this.ingTypes = ingTypes;
    }


//    @JoinColumn(name = "menuweekid", referencedColumnName = "id", nullable = false)
//    @OneToMany
//    public Collection<Menuweek2Customer> getMenuweek2Customers() {
//        return menuweek2Customers;
//    }
//
//    public void setMenuweek2Customers(Collection<Menuweek2Customer> menuweek2Customers) {
//        this.menuweek2Customers = menuweek2Customers;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menu menu = (Menu) o;

        if (id != menu.id) return false;
        if (date != null ? !date.equals(menu.date) : menu.date != null) return false;
        if (ingTypes != null ? !ingTypes.equals(menu.ingTypes) : menu.ingTypes != null) return false;

        if (recipe != null ? !recipe.equals(menu.recipe) : menu.recipe != null) return false;
        if (text != null ? !text.equals(menu.text) : menu.text != null) return false;
        if (txs != null ? !txs.equals(menu.txs) : menu.txs != null) return false;
        if (version != null ? !version.equals(menu.version) : menu.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (txs != null ? txs.hashCode() : 0);
        result = 31 * result + (ingTypes != null ? ingTypes.hashCode() : 0);
        result = 31 * result + (recipe != null ? recipe.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
