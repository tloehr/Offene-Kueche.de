package entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "additives")
public class Additives {
    private String symbol;
    private String name;
    private String text;
    private short group;
    private String display;


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
    @Column(name = "symbol", nullable = false, insertable = true, updatable = true, length = 10)
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 1000)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "group", nullable = false, insertable = true, updatable = true)
    public short getGroup() {
        return group;
    }

    public void setGroup(short group) {
        this.group = group;
    }

    @Basic
    @Column(name = "display", nullable = true, insertable = true, updatable = true, length = 500)
    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }


    @ManyToMany(mappedBy = "additives")
    private Collection<Produkte> products;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Additives additives = (Additives) o;

        if (group != additives.group) return false;
        if (id != additives.id) return false;
        if (display != null ? !display.equals(additives.display) : additives.display != null) return false;
        if (name != null ? !name.equals(additives.name) : additives.name != null) return false;
        if (symbol != null ? !symbol.equals(additives.symbol) : additives.symbol != null) return false;
        if (text != null ? !text.equals(additives.text) : additives.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) group;
        result = 31 * result + (display != null ? display.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return symbol + " " + name + " " + text;
    }
}
