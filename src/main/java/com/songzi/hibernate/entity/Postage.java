package com.songzi.hibernate.entity;

import javax.persistence.*;

@Entity
public class Postage {
    private long id;
    private Integer type;
    private Double nums;
    private Package packageByPid;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "nums")
    public Double getNums() {
        return nums;
    }

    public void setNums(Double nums) {
        this.nums = nums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Postage postage = (Postage) o;

        if (id != postage.id) return false;
        if (type != null ? !type.equals(postage.type) : postage.type != null) return false;
        if (nums != null ? !nums.equals(postage.nums) : postage.nums != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (nums != null ? nums.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "pid", referencedColumnName = "id", nullable = false)
    public Package getPackageByPid() {
        return packageByPid;
    }

    public void setPackageByPid(Package packageByPid) {
        this.packageByPid = packageByPid;
    }
}
