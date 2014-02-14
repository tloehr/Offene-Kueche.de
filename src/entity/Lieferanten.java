package entity;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Lieferanten.findAll", query = "SELECT l FROM Lieferanten l"),
        @NamedQuery(name = "Lieferanten.findAllSorted", query = "SELECT l FROM Lieferanten l ORDER BY l.firma"),
        @NamedQuery(name = "Lieferanten.findById", query = "SELECT l FROM Lieferanten l WHERE l.id = :id"),
        @NamedQuery(name = "Lieferanten.findByFirma", query = "SELECT l FROM Lieferanten l WHERE l.firma = :firma"),
        @NamedQuery(name = "Lieferanten.findByName", query = "SELECT l FROM Lieferanten l WHERE l.name = :name"),
        @NamedQuery(name = "Lieferanten.findByVorname", query = "SELECT l FROM Lieferanten l WHERE l.vorname = :vorname"),
        @NamedQuery(name = "Lieferanten.findByStrasse", query = "SELECT l FROM Lieferanten l WHERE l.strasse = :strasse"),
        @NamedQuery(name = "Lieferanten.findByOrt", query = "SELECT l FROM Lieferanten l WHERE l.ort = :ort"),
        @NamedQuery(name = "Lieferanten.findByTel", query = "SELECT l FROM Lieferanten l WHERE l.tel = :tel"),
        @NamedQuery(name = "Lieferanten.findByFax", query = "SELECT l FROM Lieferanten l WHERE l.fax = :fax")})
public class Lieferanten {


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

    private String firma;

    @javax.persistence.Column(name = "Firma", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    private String name;

    @javax.persistence.Column(name = "Name", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String vorname;

    @javax.persistence.Column(name = "Vorname", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    private String strasse;

    @javax.persistence.Column(name = "Strasse", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    private String ort;

    @javax.persistence.Column(name = "Ort", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    private String tel;

    @javax.persistence.Column(name = "Tel", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    private String fax;

    @javax.persistence.Column(name = "Fax", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Lieferanten() {
    }

    public Lieferanten(String firma) {
        this.firma = firma;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lieferanten that = (Lieferanten) o;

        if (id != that.id) return false;
        if (fax != null ? !fax.equals(that.fax) : that.fax != null) return false;
        if (firma != null ? !firma.equals(that.firma) : that.firma != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (ort != null ? !ort.equals(that.ort) : that.ort != null) return false;
        if (strasse != null ? !strasse.equals(that.strasse) : that.strasse != null) return false;
        if (tel != null ? !tel.equals(that.tel) : that.tel != null) return false;
        if (vorname != null ? !vorname.equals(that.vorname) : that.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (firma != null ? firma.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return firma;
    }
}
