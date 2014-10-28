package entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "stoffart")
public class IngTypes implements Comparable<IngTypes> {


    public IngTypes() {
        this.id = 0l;
    }

    public IngTypes(String bezeichnung, Warengruppe warengruppe) {
        this.bezeichnung = bezeichnung;
        this.einheit = 0;
        this.warengruppe = warengruppe;
    }

    public IngTypes(String bezeichnung, short einheit, short storagetype, Warengruppe warengruppe) {
        this.bezeichnung = bezeichnung;
        this.einheit = einheit;
        this.warengruppe = warengruppe;
        this.lagerart = storagetype;
    }


    @javax.persistence.Column(name = "Lagerart", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    private short lagerart;

    public short getLagerart() {
        return lagerart;
    }

    public void setLagerart(short lagerart) {
        this.lagerart = lagerart;
    }

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


    @javax.persistence.Column(name = "Bezeichnung", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    private String bezeichnung;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }


    @javax.persistence.Column(name = "Einheit", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    private short einheit;

    public short getEinheit() {
        return einheit;
    }

    public void setEinheit(short einheit) {
        this.einheit = einheit;
    }


    /**
     * Relationen
     */
    @JoinColumn(name = "warengruppe_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Warengruppe warengruppe;

    public Warengruppe getWarengruppe() {
        return warengruppe;
    }

    public void setWarengruppe(Warengruppe warengruppe) {
        this.warengruppe = warengruppe;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ingTypes")
    private Collection<Produkte> produkteCollection;

    public Collection<Produkte> getProdukteCollection() {
        return produkteCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "allergene2types", joinColumns =
    @JoinColumn(name = "allergenid"), inverseJoinColumns =
    @JoinColumn(name = "typeid"))
    private Set<Allergene> allergenes;

    public Set<Allergene> getAllergenes() {
        return allergenes;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "additives2types", joinColumns =
    @JoinColumn(name = "addid"), inverseJoinColumns =
    @JoinColumn(name = "typeid"))
    private Set<Additives> additives;

    public Set<Additives> getAdditives() {
        return additives;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "types2recipes", joinColumns =
    @JoinColumn(name = "recipeid"), inverseJoinColumns =
    @JoinColumn(name = "typeid"))
    private Set<Recipes> recipes;
    public Set<Recipes> getRecipes() {
        return recipes;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "types2menu", joinColumns =
    @JoinColumn(name = "menuid"), inverseJoinColumns =
    @JoinColumn(name = "typeid"))
    private Set<Menu> menus;

    public Set<Menu> getMenus() {
        return menus;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IngTypes ingTypes = (IngTypes) o;

        if (id != ingTypes.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) lagerart;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (int) einheit;
        result = 31 * result + (warengruppe != null ? warengruppe.hashCode() : 0);

        result = 31 * result + (allergenes != null ? allergenes.hashCode() : 0);
        result = 31 * result + (additives != null ? additives.hashCode() : 0);
        result = 31 * result + (recipes != null ? recipes.hashCode() : 0);
        result = 31 * result + (menus != null ? menus.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

    @Override
    public int compareTo(IngTypes o) {
        int sort = bezeichnung.compareTo(o.getBezeichnung());
        if (sort == 0) {
            sort = new Long(id).compareTo(o.getId());
        }
        return sort;
    }
}
