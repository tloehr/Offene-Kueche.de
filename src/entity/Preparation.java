package entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "preparation")
public class Preparation {
    private Date date;
    private long recipeid;
    private String text;

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

    @Basic
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "recipeid", nullable = false, insertable = true, updatable = true)
    public long getRecipeid() {
        return recipeid;
    }

    public void setRecipeid(long recipeid) {
        this.recipeid = recipeid;
    }

    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tx2preparation", joinColumns =
    @JoinColumn(name = "prepid"), inverseJoinColumns =
    @JoinColumn(name = "txid"))
    private Collection<Buchungen> txs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Preparation that = (Preparation) o;

        if (id != that.id) return false;
        if (recipeid != that.recipeid) return false;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (recipeid ^ (recipeid >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);

        return result;
    }
}
