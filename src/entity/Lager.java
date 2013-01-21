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
        @NamedQuery(name = "Lager.findAll", query = "SELECT l FROM Lager l"),
        @NamedQuery(name = "Lager.findAllSorted", query = "SELECT l FROM Lager l ORDER BY l.bezeichnung"),
        @NamedQuery(name = "Lager.findByLagerartSorted", query = "SELECT l FROM Lager l WHERE l.lagerart = :lagerart ORDER BY l.bezeichnung"),
        @NamedQuery(name = "Lager.findById", query = "SELECT l FROM Lager l WHERE l.id = :id"),
        @NamedQuery(name = "Lager.findByBezeichnung", query = "SELECT l FROM Lager l WHERE l.bezeichnung = :bezeichnung"),
        @NamedQuery(name = "Lager.findByLagerart", query = "SELECT l FROM Lager l WHERE l.lagerart = :lagerart"),
        @NamedQuery(name = "Lager.findByOrt", query = "SELECT l FROM Lager l WHERE l.ort = :ort")})

public class Lager {


    public Lager() {
        this.id = 0l;
    }

    public Lager(String bezeichnung, short lagerart, String ort) {
        this.id = 0l;
        this.bezeichnung = bezeichnung;
        this.lagerart = lagerart;
        this.ort = ort;
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

    private short lagerart;

    @javax.persistence.Column(name = "Lagerart", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    public short getLagerart() {
        return lagerart;
    }

    public void setLagerart(short lagerart) {
        this.lagerart = lagerart;
    }

    private String ort;

    @javax.persistence.Column(name = "Ort", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lager lager = (Lager) o;

        if (id != lager.id) return false;
        if (lagerart != lager.lagerart) return false;
        if (bezeichnung != null ? !bezeichnung.equals(lager.bezeichnung) : lager.bezeichnung != null) return false;
        if (ort != null ? !ort.equals(lager.ort) : lager.ort != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (int) lagerart;
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }
}
