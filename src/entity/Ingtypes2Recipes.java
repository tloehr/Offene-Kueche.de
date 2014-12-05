package entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by tloehr on 04.12.14.
 */
@Entity
@Table(name = "ingtypes2recipes")
public class Ingtypes2Recipes {
    private long id;
    private Recipes recipe;
    private IngTypes ingType;
    private BigDecimal amount;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JoinColumn(name = "typeid", referencedColumnName = "ID")
    @ManyToOne(optional = false)


    public IngTypes getIngType() {
        return ingType;
    }

    public void setIngType(IngTypes ingType) {
        this.ingType = ingType;
    }

    @JoinColumn(name = "recipeid", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    public Recipes getRecipes() {
        return recipe;
    }

    public void setRecipes(Recipes recipe) {
        this.recipe = recipe;
    }

    @Basic
    @Column(name = "amount", nullable = true, insertable = true, updatable = true, precision = 2)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Ingtypes2Recipes() {
    }

    public Ingtypes2Recipes(Recipes recipe, IngTypes ingType) {
        this.recipe = recipe;
//        recipe.getIngTypes2Recipes().add(this);
        this.ingType = ingType;
        this.amount = BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ingtypes2Recipes that = (Ingtypes2Recipes) o;

        if (id != that.id) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (ingType != null ? !ingType.equals(that.ingType) : that.ingType != null) return false;
        if (recipe != null ? !recipe.equals(that.recipe) : that.recipe != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (recipe != null ? recipe.hashCode() : 0);
        result = 31 * result + (ingType != null ? ingType.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
