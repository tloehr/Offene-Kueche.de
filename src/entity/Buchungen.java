package entity;

import tools.Const;

import javax.persistence.*;
import java.math.BigDecimal;
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
@Table(name = "buchungen")
@NamedQueries({
        @NamedQuery(name = "Buchungen.findAll", query = "SELECT b FROM Buchungen b"),
        @NamedQuery(name = "Buchungen.findById", query = "SELECT b FROM Buchungen b WHERE b.id = :id"),
        @NamedQuery(name = "Buchungen.findByVorrat", query = "SELECT b FROM Buchungen b WHERE b.vorrat = :vorrat"),
        @NamedQuery(name = "Buchungen.findByMenge", query = "SELECT b FROM Buchungen b WHERE b.menge = :menge"),
        @NamedQuery(name = "Buchungen.findByDatum", query = "SELECT b FROM Buchungen b WHERE b.datum = :datum"),
        @NamedQuery(name = "Buchungen.findByMitarbeiter", query = "SELECT b FROM Buchungen b WHERE b.mitarbeiter = :mitarbeiter"),
        @NamedQuery(name = "Buchungen.findByText", query = "SELECT b FROM Buchungen b WHERE b.text = :text"),
        // BY Vorrat
        @NamedQuery(name = "Buchungen.findSUMByVorratAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v = :vorrat AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByVorratAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v = :vorrat " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByVorratInaktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v = :vorrat AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY EINGANGSDATUM
        @NamedQuery(name = "Buchungen.findSUMByVorratDatumAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.eingang BETWEEN :eingang1 AND :eingang2 AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByVorratDatumAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.eingang BETWEEN :eingang1 AND :eingang2 " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByVorratDatumInaktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.eingang BETWEEN :eingang1 AND :eingang2  AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY BEZEICHNUNG
        @NamedQuery(name = "Buchungen.findSUMByProduktBezeichnungAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt.bezeichnung LIKE :bezeichnung AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByProduktBezeichnungAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt.bezeichnung LIKE :bezeichnung " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByProduktBezeichnungInaktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt.bezeichnung LIKE :bezeichnung AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY PRODUKT
        @NamedQuery(name = "Buchungen.findSUMByProduktAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt = :produkt AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByProduktAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt = :produkt " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByProduktInaktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt = :produkt AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY LAGER
        @NamedQuery(name = "Buchungen.findSUMByLagerAktiv", query = "SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                " WHERE v.lager = :lager AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByLagerAlle", query = "SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                " WHERE v.lager = :lager " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByLagerInaktiv", query = "SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                " WHERE v.lager = :lager AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY LAGERART
        @NamedQuery(name = "Buchungen.findSUMByLagerartAktiv", query = "SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                " WHERE v.lager.lagerart = :lagerart AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByLagerartAlle", query = "SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                " WHERE v.lager.lagerart = :lagerart " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByLagerartInaktiv", query = "SELECT v, SUM(b.menge), 0 FROM Buchungen b JOIN b.vorrat v " + // die 0 ist ein kleiner Kniff und wird für da Umbuchen gebraucht.
                " WHERE v.lager.lagerart = :lagerart AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY WARENGRUPPE
        @NamedQuery(name = "Buchungen.findSUMByWarengruppeAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt.ingTypes.warengruppe = :warengruppe AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByWarengruppeAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt.ingTypes.warengruppe = :warengruppe " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByWarengruppeInaktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.produkt.ingTypes.warengruppe = :warengruppe AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // BY LIEFERANT
        @NamedQuery(name = "Buchungen.findSUMByLieferantAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.lieferant = :lieferant AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByLieferantAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.lieferant = :lieferant " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByLieferantInakiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.lieferant = :lieferant AND v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        // ALLE
        @NamedQuery(name = "Buchungen.findSUMByAlleAlle", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByAlleAktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByAlleInaktiv", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.ausgang < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findSUMByAlleAngebrochenen", query = "SELECT v, SUM(b.menge) FROM Buchungen b JOIN b.vorrat v " +
                " WHERE v.anbruch < " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES + " AND v.ausgang = " + Const.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " GROUP BY v"),
        @NamedQuery(name = "Buchungen.findByStatus", query = "SELECT b FROM Buchungen b WHERE b.status = :status")})


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

//    private long vorratId;
//
//    @javax.persistence.Column(name = "Vorrat_ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
//    @Basic
//    public long getVorratId() {
//        return vorratId;
//    }
//
//    public void setVorratId(long vorratId) {
//        this.vorratId = vorratId;
//    }

    private BigDecimal menge;

    @javax.persistence.Column(name = "Menge", nullable = false, insertable = true, updatable = true, length = 12, precision = 4)
    @Basic
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

    //private long mitarbeiterId;

//    @javax.persistence.Column(name = "mitarbeiter_ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
//    @Basic
//    public long getMitarbeiterId() {
//        return mitarbeiterId;
//    }
//
//    public void setMitarbeiterId(long mitarbeiterId) {
//        this.mitarbeiterId = mitarbeiterId;
//    }

    public Buchungen() {
    }

    public Buchungen(BigDecimal menge, Date datum) {
        this.menge = menge;
        this.datum = datum;
        this.status = BuchungenTools.BUCHEN_MANUELLE_KORREKTUR;
        Main.Main.debug(toString());
    }


    private String text;

    @javax.persistence.Column(name = "Text", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        Main.Main.debug(toString());
    }

    private byte status;

    @javax.persistence.Column(name = "Status", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
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

    /**
     * Relationen
     */

    @JoinColumn(name = "mitarbeiter_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Mitarbeiter mitarbeiter;

    @JoinColumn(name = "Vorrat_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Vorrat vorrat;

    @ManyToMany(mappedBy = "txs")
    private Collection<Preparation> preparations;


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

    public Vorrat getVorrat() {
        return vorrat;
    }

    public void setVorrat(Vorrat vorrat) {
        this.vorrat = vorrat;
        Main.Main.debug(toString());
    }

    public Mitarbeiter getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(Mitarbeiter mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
        Main.Main.debug(toString());
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
