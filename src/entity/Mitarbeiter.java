package entity;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.06.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "mitarbeiter")
public class Mitarbeiter {


    public Mitarbeiter() {
        this.id = 0l;
    }

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

    private String username;

    @javax.persistence.Column(name = "Username", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String name;

    @javax.persistence.Column(name = "Name", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String vorname;

    @javax.persistence.Column(name = "Vorname", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    private boolean isAdmin;

    @javax.persistence.Column(name = "isAdmin", nullable = false, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    private String md5Key;

    @javax.persistence.Column(name = "MD5Key", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }

    private String pin;

    @javax.persistence.Column(name = "pin", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mitarbeiter that = (Mitarbeiter) o;

        if (id != that.id) return false;
        if (isAdmin != that.isAdmin) return false;
        if (md5Key != null ? !md5Key.equals(that.md5Key) : that.md5Key != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (pin != null ? !pin.equals(that.pin) : that.pin != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (vorname != null ? !vorname.equals(that.vorname) : that.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (isAdmin ? 1 : 0);
        result = 31 * result + (md5Key != null ? md5Key.hashCode() : 0);
        result = 31 * result + (pin != null ? pin.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return name + ", " + vorname + " [" + username + "]";
    }
}
