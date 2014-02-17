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
@Table(name = "warengruppe")
@NamedQueries({
        @NamedQuery(name = "Warengruppe.findAll", query = "SELECT w FROM Warengruppe w"),
        @NamedQuery(name = "Warengruppe.findById", query = "SELECT w FROM Warengruppe w WHERE w.id = :id"),
        @NamedQuery(name = "Warengruppe.findAllSorted", query = "SELECT w FROM Warengruppe w ORDER BY w.bezeichnung"),
        @NamedQuery(name = "Warengruppe.findByBezeichnung", query = "SELECT w FROM Warengruppe w WHERE w.bezeichnung = :bezeichnung")})
public class Warengruppe {


    public Warengruppe() {
    }

    public Warengruppe(String bezeichnung) {
        this.bezeichnung = bezeichnung;
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

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warengruppe that = (Warengruppe) o;

        if (id != that.id) return false;
        if (bezeichnung != null ? !bezeichnung.equals(that.bezeichnung) : that.bezeichnung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "warengruppe")
    private Collection<Stoffart> stoffartCollection;

    public Collection<Stoffart> getStoffartCollection() {
        return stoffartCollection;
    }
}
