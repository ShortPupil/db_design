package com.songzi.hibernate.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Base {
    private long id;
    private Double tel;
    private Double loFl;
    private Double doFl;
    private Double text;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

        Base base = (Base) o;

        if (id != base.id) return false;
        if (tel != null ? !tel.equals(base.tel) : base.tel != null) return false;
        if (loFl != null ? !loFl.equals(base.loFl) : base.loFl != null) return false;
        if (doFl != null ? !doFl.equals(base.doFl) : base.doFl != null) return false;
        if (text != null ? !text.equals(base.text) : base.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (loFl != null ? loFl.hashCode() : 0);
        result = 31 * result + (doFl != null ? doFl.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
