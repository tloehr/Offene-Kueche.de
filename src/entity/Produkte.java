package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

/**
 * Created by tloehr on 27.10.14.
 */
@Entity
@Table(name = "produkte")
public class Produkte {

    public Produkte() {
    }

    @Id
    @Column(name = "ID", nullable = false, insertable = true, updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Bezeichnung", nullable = false, insertable = true, updatable = true, length = 1000)
    private String bezeichnung;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Basic
    @Column(name = "GTIN", nullable = true, insertable = true, updatable = true, length = 14)
    private String gtin;

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    @Basic
    @Column(name = "PackGroesse", nullable = true, insertable = true, updatable = true, precision = 2)
    private BigDecimal packGroesse;

    public BigDecimal getPackGroesse() {
        return packGroesse;
    }

    public void setPackGroesse(BigDecimal packGroesse) {
        this.packGroesse = packGroesse;
    }


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

    public IngTypes getIngTypes() {
        return ingTypes;
    }

    public void setIngTypes(IngTypes ingTypes) {
        this.ingTypes = ingTypes;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "additives2products", joinColumns =
    @JoinColumn(name = "prodid"), inverseJoinColumns =
    @JoinColumn(name = "addid"))
    private Set<Additives> additives;

    public Set<Additives> getAdditives() {
        return additives;
    }

    public void setAdditives(Set<Additives> additives) {
        this.additives = additives;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "allergene2products", joinColumns =
    @JoinColumn(name = "prodid"), inverseJoinColumns =
    @JoinColumn(name = "allergenid"))
    private Set<Allergene> allergenes;

    public Set<Allergene> getAllergenes() {
        return allergenes;
    }

    public void setAllergenes(Set<Allergene> allergenes) {
        this.allergenes = allergenes;
    }

    public boolean isLoseWare() {
        return gtin == null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Produkte produkte = (Produkte) o;

        if (id != produkte.id) return false;
        if (version != produkte.version) return false;
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
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

}
