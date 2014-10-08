package entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by tloehr on 08.10.14.
 */
@Entity
public class Recipefeature {
    private String text;
    private short flag;
    private Collection<Recipes> recipes;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "text", nullable = false, insertable = true, updatable = true, length = 200)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "flag", nullable = false, insertable = true, updatable = true)
    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    @ManyToMany(mappedBy = "features")
    public Collection<Recipes> getRecipes() {
        return recipes;
    }

    public void setRecipes(Collection<Recipes> recipes) {
        this.recipes = recipes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipefeature that = (Recipefeature) o;

        if (flag != that.flag) return false;
        if (id != that.id) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) flag;
        return result;
    }


}
