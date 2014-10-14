package entity;

import javax.persistence.*;

/**
 * Created by tloehr on 14.10.14.
 */
@Entity
public class Menuweek2Customer {
    private Menuweek menuweek;
    private Customer customer;

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

    @JoinColumn(name = "menuweekid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Menuweek getMenuweek() {
        return menuweek;
    }

    public void setMenuweek(Menuweek menuweek) {
        this.menuweek = menuweek;
    }

    @JoinColumn(name = "customerid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    @Version
    @Column(name = "version")
    private Long version;



//     @Basic
//            @Column(name = "estimated", nullable = true, insertable = true, updatable = true, precision = 2)
//            public BigDecimal getEstimated() {
//                return estimated;
//            }
//
//            public void setEstimated(BigDecimal estimated) {
//                this.estimated = estimated;
//            }
//
//            @Basic
//            @Column(name = "ordered", nullable = true, insertable = true, updatable = true, precision = 2)
//            public BigDecimal getOrdered() {
//                return ordered;
//            }
//
//            public void setOrdered(BigDecimal ordered) {
//                this.ordered = ordered;
//            }
//
//            @Basic
//            @Column(name = "delivered", nullable = true, insertable = true, updatable = true, precision = 2)
//            public BigDecimal getDelivered() {
//                return delivered;
//            }
//
//            public void setDelivered(BigDecimal delivered) {
//                this.delivered = delivered;
//            }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menuweek2Customer that = (Menuweek2Customer) o;

        if (id != that.id) return false;
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        if (menuweek != null ? !menuweek.equals(that.menuweek) : that.menuweek != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = menuweek != null ? menuweek.hashCode() : 0;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
