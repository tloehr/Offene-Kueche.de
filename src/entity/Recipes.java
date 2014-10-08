package entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "recipes")
public class Recipes {
    private String title;
    private String text;
    private Collection<Recipefeature> features;

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
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 500)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    @JoinTable(name = "recipe2feature", joinColumns =
    @JoinColumn(name = "recipeid"), inverseJoinColumns =
    @JoinColumn(name = "featureid"))
    public Collection<Recipefeature> getFeatures() {
        return features;
    }

    public void setFeatures(Collection<Recipefeature> features) {
        this.features = features;
    }

    @Version
    @Column(name = "version")
    private Long version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipes recipes = (Recipes) o;

        if (id != recipes.id) return false;
        if (version != recipes.version) return false;
        if (text != null ? !text.equals(recipes.text) : recipes.text != null) return false;
        if (title != null ? !title.equals(recipes.title) : recipes.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

}
