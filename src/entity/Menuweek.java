package entity;

import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tloehr on 14.10.14.
 */
@Entity
@Table(name = "menuweek")
public class Menuweek {
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
    @Column(name = "week", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.DATE)
    private Date week;

    public Date getWeek() {
        return week;
    }

    public void setWeek(Date week) {
        this.week = week;
    }

    @JoinColumn(name = "featureid", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Recipefeature recipefeature;

    public Recipefeature getRecipefeature() {
        return recipefeature;
    }

    public void setRecipefeature(Recipefeature recipefeature) {
        this.recipefeature = recipefeature;
    }

    @JoinColumn(name = "mon", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu mon;

    public Menu getMon() {
        return mon;
    }

    public void setMon(Menu mon) {
        this.mon = mon;
    }

    @JoinColumn(name = "tue", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu tue;

    public Menu getTue() {
        return tue;
    }

    public void setTue(Menu tue) {
        this.tue = tue;
    }


    @JoinColumn(name = "wed", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu wed;

    public Menu getWed() {
        return wed;
    }

    public void setWed(Menu wed) {
        this.wed = wed;
    }

    @JoinColumn(name = "thu", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu thu;

    public Menu getThu() {
        return thu;
    }

    public void setThu(Menu thu) {
        this.thu = thu;
    }


    @JoinColumn(name = "fri", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu fri;

    public Menu getFri() {
        return fri;
    }

    public void setFri(Menu fri) {
        this.fri = fri;
    }

    @JoinColumn(name = "sat", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu sat;

    public Menu getSat() {
        return sat;
    }

    public void setSat(Menu sat) {
        this.sat = sat;
    }

    @JoinColumn(name = "sun", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Menu sun;

    public Menu getSun() {
        return sun;
    }

    public void setSun(Menu sun) {
        this.sun = sun;
    }


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "menuweek2customer", joinColumns =
    @JoinColumn(name = "menuweekid"), inverseJoinColumns =
    @JoinColumn(name = "customerid"))
    private Set<Customer> customers;

    public Set<Customer> getCustomers() {
        return customers;
    }

    public Menuweek(Date week) {
        this.week = new LocalDate(week).dayOfWeek().withMinimumValue().toDateTimeAtStartOfDay().toDate();
        customers = new HashSet<Customer>();
    }

    public Menuweek() {
    }

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menuweek menuweek = (Menuweek) o;

        if (id != menuweek.id) return false;
        if (fri != null ? !fri.equals(menuweek.fri) : menuweek.fri != null) return false;
        if (mon != null ? !mon.equals(menuweek.mon) : menuweek.mon != null) return false;
        if (recipefeature != null ? !recipefeature.equals(menuweek.recipefeature) : menuweek.recipefeature != null)
            return false;
        if (sat != null ? !sat.equals(menuweek.sat) : menuweek.sat != null) return false;
        if (sun != null ? !sun.equals(menuweek.sun) : menuweek.sun != null) return false;
        if (thu != null ? !thu.equals(menuweek.thu) : menuweek.thu != null) return false;
        if (tue != null ? !tue.equals(menuweek.tue) : menuweek.tue != null) return false;
        if (version != null ? !version.equals(menuweek.version) : menuweek.version != null) return false;
        if (wed != null ? !wed.equals(menuweek.wed) : menuweek.wed != null) return false;
        if (week != null ? !week.equals(menuweek.week) : menuweek.week != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (week != null ? week.hashCode() : 0);
        result = 31 * result + (mon != null ? mon.hashCode() : 0);
        result = 31 * result + (tue != null ? tue.hashCode() : 0);
        result = 31 * result + (wed != null ? wed.hashCode() : 0);
        result = 31 * result + (thu != null ? thu.hashCode() : 0);
        result = 31 * result + (fri != null ? fri.hashCode() : 0);
        result = 31 * result + (sat != null ? sat.hashCode() : 0);
        result = 31 * result + (sun != null ? sun.hashCode() : 0);
        result = 31 * result + (recipefeature != null ? recipefeature.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
