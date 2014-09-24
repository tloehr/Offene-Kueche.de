package entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "allergene")
public class Allergene {
    private String kennung;
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
    @Column(name = "kennung", nullable = false, insertable = true, updatable = true, length = 10)
    public String getKennung() {
        return kennung;
    }

    public void setKennung(String kennung) {
        this.kennung = kennung;
    }

    @Basic
    @Column(name = "text", nullable = false, insertable = true, updatable = true, length = 500)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ManyToMany(mappedBy = "allergenes")
    private Collection<Produkte> products;

    @ManyToMany(mappedBy = "allergenes")
    private Collection<IngTypes> ingTypes;

    public Collection<IngTypes> getIngTypes() {
        return ingTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Allergene allergene = (Allergene) o;

        if (id != allergene.id) return false;
        if (kennung != null ? !kennung.equals(allergene.kennung) : allergene.kennung != null) return false;
        if (text != null ? !text.equals(allergene.text) : allergene.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (kennung != null ? kennung.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return kennung + " " + text;
    }
}
