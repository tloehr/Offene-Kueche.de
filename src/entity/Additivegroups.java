package entity;

import javax.persistence.*;

/**
 * Created by tloehr on 06.01.15.
 */
@Entity
@Table(name = "additivegroups")
public class Additivegroups implements Comparable<Additivegroups> {

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Basic
    @Column(name = "groupname", nullable = false, insertable = true, updatable = true, length = 512)
    private String groupname;
    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Additivegroups that = (Additivegroups) o;

        if (id != that.id) return false;
        if (groupname != null ? !groupname.equals(that.groupname) : that.groupname != null) return false;

        return true;
    }

    @Override
    public int compareTo(Additivegroups o) {
        return Long.compare(id, o.getId());
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (groupname != null ? groupname.hashCode() : 0);
        return result;
    }
}
