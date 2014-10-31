package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "buchungen")
public class Buchungen {
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


    @javax.persistence.Column(name = "Menge", nullable = false, insertable = true, updatable = true, length = 12, precision = 4)
    @Basic
    private BigDecimal menge;

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }


    @javax.persistence.Column(name = "Datum", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Buchungen() {
    }

    public Buchungen(BigDecimal menge, Date datum) {
        this.menge = menge;
        this.datum = datum;
        this.status = BuchungenTools.BUCHEN_MANUELLE_KORREKTUR;
    }

    @javax.persistence.Column(name = "Text", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        Main.Main.debug(toString());
    }

    @javax.persistence.Column(name = "Status", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    private byte status;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
        Main.Main.debug(toString());
    }

    @Version
    @Column(name = "version")
    private Long version;

    /***
     *               _       _   _
     *      _ __ ___| | __ _| |_(_) ___  _ __  ___
     *     | '__/ _ \ |/ _` | __| |/ _ \| '_ \/ __|
     *     | | |  __/ | (_| | |_| | (_) | | | \__ \
     *     |_|  \___|_|\__,_|\__|_|\___/|_| |_|___/
     *
     */
    @JoinColumn(name = "mitarbeiter_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Mitarbeiter mitarbeiter;

    public Mitarbeiter getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(Mitarbeiter mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

    @JoinColumn(name = "Vorrat_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Vorrat vorrat;

    public Vorrat getVorrat() {
        return vorrat;
    }

    public void setVorrat(Vorrat vorrat) {
        this.vorrat = vorrat;
    }

//    @ManyToMany(mappedBy = "txs")
//    private Collection<Menu> menus;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buchungen buchungen = (Buchungen) o;

        if (id != buchungen.id) return false;
        //if (mitarbeiterId != buchungen.mitarbeiterId) return false;
        if (status != buchungen.status) return false;
        //if (vorratId != buchungen.vorratId) return false;
        if (datum != null ? !datum.equals(buchungen.datum) : buchungen.datum != null) return false;
        if (menge != null ? !menge.equals(buchungen.menge) : buchungen.menge != null) return false;
        if (text != null ? !text.equals(buchungen.text) : buchungen.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        //result = 31 * result + (int) (vorratId ^ (vorratId >>> 32));
        result = 31 * result + (menge != null ? menge.hashCode() : 0);
        result = 31 * result + (datum != null ? datum.hashCode() : 0);
        //result = 31 * result + (int) (mitarbeiterId ^ (mitarbeiterId >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) status;
        return result;
    }

    @Override
    public String toString() {
        return "Buchungen{" +
                "id=" + id +
                ", menge=" + menge +
                ", datum=" + datum +
                ", text='" + text + '\'' +
                ", status=" + status +
                ", mitarbeiter=" + mitarbeiter +
                ", vorrat=" + vorrat +
                '}';
    }
}
