package entity;

import javax.persistence.*;
import java.math.BigDecimal;
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
@Table(name = "produkte")
@NamedQueries({
        @NamedQuery(name = "Produkte.findAll", query = "SELECT p FROM Produkte p"),
        @NamedQuery(name = "Produkte.findAllSorted", query = "SELECT p FROM Produkte p ORDER BY p.bezeichnung"),
        @NamedQuery(name = "Produkte.findById", query = "SELECT p FROM Produkte p WHERE p.id = :id"),
        @NamedQuery(name = "Produkte.findByBezeichnung", query = "SELECT p FROM Produkte p WHERE p.bezeichnung = :bezeichnung"),
        @NamedQuery(name = "Produkte.findByGtin", query = "SELECT p FROM Produkte p WHERE p.gtin = :gtin"),
        @NamedQuery(name = "Produkte.findByPackGroesse", query = "SELECT p FROM Produkte p WHERE p.packGroesse = :packGroesse"),
        @NamedQuery(name = "Produkte.findByBezeichnungLike", query = "SELECT p FROM Produkte p WHERE p.bezeichnung LIKE :bezeichnung" +
                "    ORDER BY p.bezeichnung")
})
public class Produkte {

    public Produkte() {
        this.id = 0l;
        this.bezeichnung = "";
//        this.lagerart = -1;
//        this.einheit = -1;
        this.gtin = null;
        this.packGroesse = BigDecimal.ONE.negate();
        this.ingTypes = null;
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

    private String bezeichnung;

    @javax.persistence.Column(name = "Bezeichnung", nullable = false, insertable = true, updatable = true, length = 1000, precision = 0)
    @Basic
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    private String gtin;

    @javax.persistence.Column(name = "GTIN", nullable = true, insertable = true, updatable = true, length = 14, precision = 0)
    @Basic
    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

//
//    @javax.persistence.Column(name = "Lagerart", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
//    @Basic
//    private short lagerart;
//
//
//    @javax.persistence.Column(name = "Einheit", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
//    @Basic
//    private short einheit;


    @javax.persistence.Column(name = "PackGroesse", nullable = true, insertable = true, updatable = true, length = 12, precision = 4)
    @Basic
    private BigDecimal packGroesse;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Relationen
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produkt")
    private Collection<Vorrat> vorratCollection;

    public Collection<Vorrat> getVorratCollection() {
        return vorratCollection;
    }

    @JoinColumn(name = "Stoffart_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private IngTypes ingTypes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "additives2products", joinColumns =
    @JoinColumn(name = "prodid"), inverseJoinColumns =
    @JoinColumn(name = "addid"))
    private Set<Additives> additives;

    public Set<Additives> getAdditives() {
        return additives;
    }

    //
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "allergene2products", joinColumns =
    @JoinColumn(name = "prodid"), inverseJoinColumns =
    @JoinColumn(name = "allergenid"))
    private Set<Allergene> allergenes;

    public Set<Allergene> getAllergenes() {
        return allergenes;
    }

    public BigDecimal getPackGroesse() {
        return packGroesse;
    }

    public void setPackGroesse(BigDecimal packGroesse) {
        this.packGroesse = packGroesse;
    }


    public boolean isLoseWare() {
        return gtin == null;
    }


    public IngTypes getIngTypes() {
        return ingTypes;
    }

    public void setIngTypes(IngTypes ingTypes) {
        this.ingTypes = ingTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Produkte produkte = (Produkte) o;

//        if (einheit != produkte.einheit) return false;
        if (id != produkte.id) return false;
//        if (lagerart != produkte.lagerart) return false;
        if (bezeichnung != null ? !bezeichnung.equals(produkte.bezeichnung) : produkte.bezeichnung != null)
            return false;
        if (gtin != null ? !gtin.equals(produkte.gtin) : produkte.gtin != null) return false;
        if (packGroesse != null ? !packGroesse.equals(produkte.packGroesse) : produkte.packGroesse != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (gtin != null ? gtin.hashCode() : 0);
        result = 31 * result + (packGroesse != null ? packGroesse.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }
}
