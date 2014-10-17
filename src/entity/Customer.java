package entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by tloehr on 08.10.14.
 */
@Entity
@Table(name = "customer")
public class Customer {
    private String abbrev;
    private String name;
    private String firstname;
    private String orgname;
    private String street;
    private String city;
    private String zip;
    private String tel;
    private String mobile;
    private String fax;
    private String email;
    private Customergroups group;
    private Collection<Menuweek2Customer> menuweek2Customers;

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
    @Column(name = "abbrev", nullable = false, insertable = true, updatable = true, length = 20)
    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "firstname", nullable = true, insertable = true, updatable = true, length = 200)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "orgname", nullable = true, insertable = true, updatable = true, length = 200)
    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    @Basic
    @Column(name = "street", nullable = true, insertable = true, updatable = true, length = 200)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Basic
    @Column(name = "city", nullable = true, insertable = true, updatable = true, length = 200)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "zip", nullable = true, insertable = true, updatable = true, length = 200)
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Basic
    @Column(name = "tel", nullable = true, insertable = true, updatable = true, length = 200)
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "mobile", nullable = true, insertable = true, updatable = true, length = 200)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Basic
    @Column(name = "fax", nullable = true, insertable = true, updatable = true, length = 200)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Basic
    @Column(name = "email", nullable = true, insertable = true, updatable = true, length = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @JoinColumn(name = "group", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    public Customergroups getGroup() {
        return group;
    }

    public void setGroup(Customergroups group) {
        this.group = group;
    }


    @JoinColumn(name = "customerid", referencedColumnName = "id", nullable = false)
    @OneToMany
    public Collection<Menuweek2Customer> getMenuweek2Customers() {
        return menuweek2Customers;
    }

    public void setMenuweek2Customers(Collection<Menuweek2Customer> menuweek2Customers) {
        this.menuweek2Customers = menuweek2Customers;
    }
}
