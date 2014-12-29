package entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by tloehr on 12.09.14.
 */
@Entity
@Table(name = "recipes")
public class Recipes {
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
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215)
    private String text;

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
    private Collection<Recipefeature> features;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "recipes2recipes", joinColumns =
    @JoinColumn(name = "owner"), inverseJoinColumns =
    @JoinColumn(name = "owned"))
    private Set<Recipes> subrecipes;

    public Set<Recipes> getSubrecipes() {
        return subrecipes;
    }

    @OneToMany(mappedBy = "recipes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingtypes2Recipes> ingTypes2Recipes;

    public List<Ingtypes2Recipes> getIngTypes2Recipes() {
        return ingTypes2Recipes;
    }

    public Recipes() {
    }

    public Recipes(String title) {
        this.title = title;
        ingTypes2Recipes = new ArrayList<Ingtypes2Recipes>();
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
        if (features != null ? !features.equals(recipes.features) : recipes.features != null) return false;
        if (text != null ? !text.equals(recipes.text) : recipes.text != null) return false;
        if (title != null ? !title.equals(recipes.title) : recipes.title != null) return false;
        if (version != null ? !version.equals(recipes.version) : recipes.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (features != null ? features.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return title;
    }
}
