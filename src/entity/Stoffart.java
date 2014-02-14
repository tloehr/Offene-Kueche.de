package entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Stoffart.findAll", query = "SELECT s FROM Stoffart s"),
        @NamedQuery(name = "Stoffart.findAllSorted", query = "SELECT s FROM Stoffart s ORDER BY s.bezeichnung"),
        @NamedQuery(name = "Stoffart.findById", query = "SELECT s FROM Stoffart s WHERE s.id = :id"),
        @NamedQuery(name = "Stoffart.findByBezeichnungLike", query = "SELECT s FROM Stoffart s WHERE s.bezeichnung LIKE :bezeichnung"),
        @NamedQuery(name = "Stoffart.findByBezeichnung", query = "SELECT s FROM Stoffart s WHERE s.bezeichnung = :bezeichnung"),
        @NamedQuery(name = "Stoffart.findByEinheit", query = "SELECT s FROM Stoffart s WHERE s.einheit = :einheit"),
        @NamedQuery(name = "Stoffart.findByWarengruppe", query = "SELECT s FROM Stoffart s WHERE s.warengruppe = :warengruppe")})
public class Stoffart implements Comparable<Stoffart> {


    public Stoffart() {
        this.id = 0l;
    }

    public Stoffart(String bezeichnung, short einheit, Warengruppe warengruppe) {
        this.bezeichnung = bezeichnung;
        this.einheit = einheit;
        this.warengruppe = warengruppe;
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

    @javax.persistence.Column(name = "Bezeichnung", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    private short einheit;

    @javax.persistence.Column(name = "Einheit", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    public short getEinheit() {
        return einheit;
    }

    public void setEinheit(short einheit) {
        this.einheit = einheit;
    }

    public Warengruppe getWarengruppe() {
        return warengruppe;
    }

    public void setWarengruppe(Warengruppe warengruppe) {
        this.warengruppe = warengruppe;
    }

    //    private long warengruppeId;
//
//    @javax.persistence.Column(name = "warengruppe_ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
//    @Basic
//    public long getWarengruppeId() {
//        return warengruppeId;
//    }
//
//    public void setWarengruppeId(long warengruppeId) {
//        this.warengruppeId = warengruppeId;
//    }

    /**
     * Relationen
     */
    @JoinColumn(name = "warengruppe_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Warengruppe warengruppe;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stoffart")
    private Collection<Produkte> produkteCollection;

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stoffart stoffart = (Stoffart) o;

        if (einheit != stoffart.einheit) return false;
        if (id != stoffart.id) return false;
        //if (warengruppeId != stoffart.warengruppeId) return false;
        if (bezeichnung != null ? !bezeichnung.equals(stoffart.bezeichnung) : stoffart.bezeichnung != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (int) einheit;
        //result = 31 * result + (int) (warengruppeId ^ (warengruppeId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

    @Override
    public int compareTo(Stoffart o) {
        int sort = bezeichnung.compareTo(o.getBezeichnung());
        if (sort == 0) {
            sort = new Long(id).compareTo(o.getId());
        }
        return sort;
    }
}
