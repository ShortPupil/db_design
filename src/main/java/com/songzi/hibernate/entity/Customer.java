package com.songzi.hibernate.entity;

import javax.persistence.*;

@Entity
public class Customer {
    private long id;
    private Double nums;
    private Double tel;
    private Double loFl;
    private Double doFl;
    private Double text;
    private Base baseByBid;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "nums")
    public Double getNums() {
        return nums;
    }

    public void setNums(Double nums) {
        this.nums = nums;
    }

    @Basic
    @Column(name = "tel")
    public Double getTel() {
        return tel;
    }

    public void setTel(Double tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "lo_fl")
    public Double getLoFl() {
        return loFl;
    }

    public void setLoFl(Double loFl) {
        this.loFl = loFl;
    }

    @Basic
    @Column(name = "do_fl")
    public Double getDoFl() {
        return doFl;
    }

    public void setDoFl(Double doFl) {
        this.doFl = doFl;
    }

    @Basic
    @Column(name = "text")
    public Double getText() {
        return text;
    }

    public void setText(Double text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (nums != null ? !nums.equals(customer.nums) : customer.nums != null) return false;
        if (tel != null ? !tel.equals(customer.tel) : customer.tel != null) return false;
        if (loFl != null ? !loFl.equals(customer.loFl) : customer.loFl != null) return false;
        if (doFl != null ? !doFl.equals(customer.doFl) : customer.doFl != null) return false;
        if (text != null ? !text.equals(customer.text) : customer.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (nums != null ? nums.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (loFl != null ? loFl.hashCode() : 0);
        result = 31 * result + (doFl != null ? doFl.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "bid", referencedColumnName = "id")
    public Base getBaseByBid() {
        return baseByBid;
    }

    public void setBaseByBid(Base baseByBid) {
        this.baseByBid = baseByBid;
    }
}
