package entity;

import tools.Const;
import tools.Tools;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "vorrat")
public class Stock {


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

    public Stock() {
    }

    public Stock(Long id) {
        this.id = id;
    }

    public Stock(Long id, Date eingang, Date anbruch, Date ausgang) {
        this.id = id;
        this.eingang = eingang;
        this.anbruch = anbruch;
        this.ausgang = ausgang;
    }


    public Stock(Produkte produkt, Lieferanten lieferant, Lager lager) {
        this.id = 0l;
        this.eingang = new Date();
        this.anbruch = Const.DATE_BIS_AUF_WEITERES;
        this.ausgang = Const.DATE_BIS_AUF_WEITERES;
        this.produkt = produkt;
        this.lieferant = lieferant;
        this.lager = lager;
    }


    @javax.persistence.Column(name = "Eingang", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date eingang;

    public Date getEingang() {
        return eingang;
    }

    public void setEingang(Date eingang) {
        this.eingang = eingang;
    }


    @javax.persistence.Column(name = "Anbruch", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date anbruch;

    public Date getAnbruch() {
        return anbruch;
    }

    public void setAnbruch(Date anbruch) {
        this.anbruch = anbruch;
    }


    @javax.persistence.Column(name = "Ausgang", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date ausgang;

    public Date getAusgang() {
        return ausgang;
    }

    public void setAusgang(Date ausgang) {
        this.ausgang = ausgang;
    }

    public Lieferanten getLieferant() {
        return lieferant;
    }

    public void setLieferant(Lieferanten lieferant) {
        this.lieferant = lieferant;
    }

    public Lager getLager() {
        return lager;
    }

    public void setLager(Lager lager) {
        this.lager = lager;
    }

    public Produkte getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkte produkt) {
        this.produkt = produkt;
    }

    public Collection<Buchungen> getBuchungenCollection() {
        return buchungenCollection;
    }

    public void setBuchungenCollection(Collection<Buchungen> buchungenCollection) {
        this.buchungenCollection = buchungenCollection;
    }

    public boolean isAusgebucht() {
        return ausgang.before(Const.DATE_BIS_AUF_WEITERES);
    }

    public boolean isAngebrochen() {
        return anbruch.before(Const.DATE_BIS_AUF_WEITERES);
    }

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Relationen
     */
    @JoinColumn(name = "Lieferanten_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Lieferanten lieferant;
    @JoinColumn(name = "Lager_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Lager lager;
    @JoinColumn(name = "Produkte_ID", referencedColumnName = "ID")
    @ManyToOne
    private Produkte produkt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stock")
    private Collection<Buchungen> buchungenCollection;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "stock2menu", joinColumns =
    @JoinColumn(name = "stockid"), inverseJoinColumns =
    @JoinColumn(name = "menuid"))
    private Set<Menu> menus;

    public Set<Menu> getMenus() {
        return menus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stock stock = (Stock) o;

        if (id != stock.id) return false;
//        if (lagerId != vorrat.lagerId) return false;
//        if (lieferantenId != vorrat.lieferantenId) return false;
//        if (produkteId != vorrat.produkteId) return false;
        if (anbruch != null ? !anbruch.equals(stock.anbruch) : stock.anbruch != null) return false;
        if (ausgang != null ? !ausgang.equals(stock.ausgang) : stock.ausgang != null) return false;
        if (eingang != null ? !eingang.equals(stock.eingang) : stock.eingang != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
//        result = 31 * result + (int) (produkteId ^ (produkteId >>> 32));
//        result = 31 * result + (int) (lagerId ^ (lagerId >>> 32));
//        result = 31 * result + (int) (lieferantenId ^ (lieferantenId >>> 32));
        result = 31 * result + (eingang != null ? eingang.hashCode() : 0);
        result = 31 * result + (anbruch != null ? anbruch.hashCode() : 0);
        result = 31 * result + (ausgang != null ? ausgang.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return Tools.left(produkt.getBezeichnung(), 20, "") + " [" + id + "] " + lager.getBezeichnung();
    }
}
