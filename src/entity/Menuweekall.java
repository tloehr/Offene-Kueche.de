package entity;

import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by tloehr on 14.10.14.
 */
@Entity
@Table(name = "menuweekall")
public class Menuweekall {
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


    public Menuweekall(Date week) {
        LocalDate ldWeek = new LocalDate(week).dayOfWeek().withMinimumValue();
        this.week = ldWeek.toDateTimeAtStartOfDay().toDate();
        this.menuweeks = new ArrayList<Menuweek>();
    }

    public Menuweekall() {
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menuweekall")
    private Collection<Menuweek> menuweeks;

    public Collection<Menuweek> getMenuweeks() {
        return menuweeks;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menuweekall menuweek = (Menuweekall) o;

        if (id != menuweek.id) return false;
        if (week != null ? !week.equals(menuweek.week) : menuweek.week != null) return false;
        if (version != null ? !version.equals(menuweek.version) : menuweek.version != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (week != null ? week.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
