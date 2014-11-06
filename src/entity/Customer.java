package entity;

import javax.persistence.*;

/**
 * Created by tloehr on 08.10.14.
 */
@Entity
@Table(name = "customer")
public class Customer {
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
    private String abbrev;

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 200)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "firstname", nullable = true, insertable = true, updatable = true, length = 200)
    private String firstname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "orgname", nullable = true, insertable = true, updatable = true, length = 200)
    private String orgname;

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    @Basic
    @Column(name = "street", nullable = true, insertable = true, updatable = true, length = 200)
    private String street;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Basic
    @Column(name = "city", nullable = true, insertable = true, updatable = true, length = 200)
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "zip", nullable = true, insertable = true, updatable = true, length = 200)
    private String zip;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Basic
    @Column(name = "tel", nullable = true, insertable = true, updatable = true, length = 200)
    private String tel;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "mobile", nullable = true, insertable = true, updatable = true, length = 200)
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Basic
    @Column(name = "fax", nullable = true, insertable = true, updatable = true, length = 200)
    private String fax;

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Basic
    @Column(name = "email", nullable = true, insertable = true, updatable = true, length = 200)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    @JoinColumn(name = "group", referencedColumnName = "id", nullable = false)
//    @ManyToOne(optional = false)
//    private Customergroups group;
//
//    public Customergroups getGroup() {
//        return group;
//    }
//
//    public void setGroup(Customergroups group) {
//        this.group = group;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (abbrev != null ? !abbrev.equals(customer.abbrev) : customer.abbrev != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (abbrev != null ? abbrev.hashCode() : 0);

        return result;
    }
}
