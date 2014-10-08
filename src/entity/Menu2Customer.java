package entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by tloehr on 08.10.14.
 */
@Entity
public class Menu2Customer {

    private Menu menu;
    private Customer customer;
    private BigDecimal estimated;
    private BigDecimal ordered;
    private BigDecimal delivered;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private long id;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "estimated", nullable = true, insertable = true, updatable = true, precision = 2)
    public BigDecimal getEstimated() {
        return estimated;
    }

    public void setEstimated(BigDecimal estimated) {
        this.estimated = estimated;
    }

    @Basic
    @Column(name = "ordered", nullable = true, insertable = true, updatable = true, precision = 2)
    public BigDecimal getOrdered() {
        return ordered;
    }

    public void setOrdered(BigDecimal ordered) {
        this.ordered = ordered;
    }

    @Basic
    @Column(name = "delivered", nullable = true, insertable = true, updatable = true, precision = 2)
    public BigDecimal getDelivered() {
        return delivered;
    }

    public void setDelivered(BigDecimal delivered) {
        this.delivered = delivered;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "menuid", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @JoinColumn(name = "customerid", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menu2Customer that = (Menu2Customer) o;

        if (id != that.id) return false;
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        if (delivered != null ? !delivered.equals(that.delivered) : that.delivered != null) return false;
        if (estimated != null ? !estimated.equals(that.estimated) : that.estimated != null) return false;
        if (menu != null ? !menu.equals(that.menu) : that.menu != null) return false;
        if (ordered != null ? !ordered.equals(that.ordered) : that.ordered != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (menu != null ? menu.hashCode() : 0);
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (estimated != null ? estimated.hashCode() : 0);
        result = 31 * result + (ordered != null ? ordered.hashCode() : 0);
        result = 31 * result + (delivered != null ? delivered.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
