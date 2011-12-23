package entity;

import tools.Const;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Vorrat.findAll", query = "SELECT v FROM Vorrat v"),
        @NamedQuery(name = "Vorrat.findById", query = "SELECT v FROM Vorrat v WHERE v.id = :id"),
                // @NamedQuery(name = "Vorrat.findByIdMenge", query = "SELECT v, SUM(b.menge) WHERE v.id = :id"),
        @NamedQuery(name = "Vorrat.Buchungen.findVorraete", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v WHERE v.ausgang = :ausgang GROUP BY v"),
        @NamedQuery(name = "Vorrat.Buchungen.summeBestand", query = "SELECT SUM(b.menge) FROM Buchungen b JOIN b.vorrat v WHERE b.vorrat = :vorrat"),
        @NamedQuery(name = "Vorrat.findMitarbeiter", query = "SELECT b.mitarbeiter FROM Buchungen b JOIN b.vorrat v WHERE b.status = 1 and b.vorrat = :vorrat"),
        @NamedQuery(name = "Vorrat.findByIdActive", query = "SELECT v FROM Vorrat v WHERE v.id = :id AND v.ausgang = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "Vorrat.findByProdukt", query = "SELECT v FROM Vorrat v WHERE v.produkt = :produkt"),
        @NamedQuery(name = "Vorrat.findByProdukte", query = "SELECT v FROM Vorrat v WHERE v.produkt IN :produkte"),
        @NamedQuery(name = "Vorrat.findByLager", query = "SELECT v FROM Vorrat v WHERE v.lager = :lager "),
        @NamedQuery(name = "Vorrat.findByLieferant", query = "SELECT v FROM Vorrat v WHERE v.lieferant = :lieferant"),
        // Erweiterung für Suche innerhalb eines Tages
        @NamedQuery(name = "Vorrat.findByEingang", query = "SELECT v FROM Vorrat v WHERE v.eingang >= :eingang1 AND v.eingang <= :eingang2"),
        @NamedQuery(name = "Vorrat.findByAnbruch", query = "SELECT v FROM Vorrat v WHERE v.anbruch = :anbruch"),
        @NamedQuery(name = "Vorrat.findByAusgang", query = "SELECT v FROM Vorrat v WHERE v.ausgang = :ausgang")})
public class Vorrat {


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

    public Vorrat() {
    }

    public Vorrat(Long id) {
        this.id = id;
    }

   public Vorrat(Long id, Date eingang, Date anbruch, Date ausgang) {
        this.id = id;
        this.eingang = eingang;
        this.anbruch = anbruch;
        this.ausgang = ausgang;
    }


    public Vorrat(Produkte produkt, Lieferanten lieferant, Lager lager) {
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

    public boolean isAusgebucht(){
        return ausgang.before(Const.DATE_BIS_AUF_WEITERES);
    }

    public boolean isAngebrochen(){
        return anbruch.before(Const.DATE_BIS_AUF_WEITERES);
    }

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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorrat")
    private Collection<Buchungen> buchungenCollection;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vorrat vorrat = (Vorrat) o;

        if (id != vorrat.id) return false;
//        if (lagerId != vorrat.lagerId) return false;
//        if (lieferantenId != vorrat.lieferantenId) return false;
//        if (produkteId != vorrat.produkteId) return false;
        if (anbruch != null ? !anbruch.equals(vorrat.anbruch) : vorrat.anbruch != null) return false;
        if (ausgang != null ? !ausgang.equals(vorrat.ausgang) : vorrat.ausgang != null) return false;
        if (eingang != null ? !eingang.equals(vorrat.eingang) : vorrat.eingang != null) return false;

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
        return "Vorrat{" +
                "id=" + id +
                ", eingang=" + eingang +
                ", anbruch=" + anbruch +
                ", ausgang=" + ausgang +
                ", lieferant=" + lieferant +
                ", lager=" + lager +
                ", produkt=" + produkt +
                '}';
    }
}
